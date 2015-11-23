package chat.haver.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.lang.Override;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

// TODO Get token lib

/**
 * WebSocketServer that puts Clients into Rooms and routes Messages from the Client to the Room.
 */
public class Router extends WebSocketServer {

	public final static JSONParser parser = new JSONParser();
	private HashMap<WebSocket, Client> clients = new HashMap<>();
	private HashMap<Client, Room> rooms = new HashMap<>();


	/**
	 * @throws UnknownHostException Config dun goof'd.
	 */
	public Router(String hostname, int port) throws UnknownHostException {
		super(new InetSocketAddress(hostname, port));
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		Client client = new Client();
		clients.put(conn, client);
        System.out.println("New connection (" + clients.size() + " connections): " + conn);
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		Client client = clients.get(conn);
		clients.remove(conn);
        if (rooms.get(client) != null) {
            rooms.get(client).close(conn);
            rooms.remove(client);
        }
        System.out.println("Connection closed(" + clients.size() + " remaining): " + conn);
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a specific websocket
			conn.close();
		}
	}

	/**
	 * Accepts Stringified JSON of Messages and routes them accordingly.
	 *
	 * @param conn The WebSocket the Message was recieved on.
	 * @param message The Stringified JSON Message.
	 */
	@Override
	public void onMessage(WebSocket conn, String message) {
		Client client = clients.get(conn);
		if (!client.addToQueue()) {
			System.out.println("Messages too frequent, rate limiting: " + client.getName());
			return;
		}

		Room room = rooms.get(client);
		System.out.println("Message from [" + conn + "]: " + message);

		JSONObject jsonObject = Message.jsonFromString(message);
		if (jsonObject == null) return; // invalid JSON
		Message.Type type = Message.typeFromJson(jsonObject);
		if (type == null) return; // invalid type

		if (room != null) {
			switch (type) {
				case LOCATION:
					Location location = Location.fromJSON(jsonObject);
					if (location == null) return;
					if (room.inRange(client.getLocation())) {
						room.updateLocation(client, location);
					} else {
						room.close(conn);
						client.setLocation(location);
						rooms.replace(client, getRoom(location));
					}
					break;

				case POST:
					room.send(Post.fromJSON(client, jsonObject));
					break;

				default:
					System.out.println("Message from ["+conn+"] was invalid");
					// Client dun goof'd
					break;
			}
		} else {
			switch (type) {
				case LOCATION:
                    Location location = Location.fromJSON(jsonObject);
                    if (location == null) return;
					client.setLocation(location);
					room = getRoom(location);
					if (room != null) {
						setRoom(conn, client, room);
					} else {
						conn.send(Message.Request.ROOM_INFO.request);
						System.out.println("Message to ["+conn+"]: <room info request>");
					}
					break;

				case ROOM_INFO:
					if (client.getLocation() != null) {
						RoomInfo roomInfo = RoomInfo.fromJSON(jsonObject);
                        if (roomInfo == null) return;
						room = new Room(roomInfo, client.getLocation()); // TODO User input is only asserted and not validated properly
						setRoom(conn, client, room);
					} else {
						// Client dun goof'd
						conn.send(Message.Request.LOCATION.request);
						System.out.println("Invalid room info from ["+conn+"]: <location request>");
					}
					break;

				default:
					// Client dun goof'd
					conn.send(Message.Request.LOCATION.request);
					System.out.println("Invalid message from ["+conn+"]: <location request>");
					break;
			}
		}
	}

	/**
	 * Helper method that deals with showing a client to their specified room.
	 *
	 * @param conn The WebSocket of the client.
	 * @param client The client that needs to be put in a room.
	 * @param room The room that the client needs to be put in.
	 */
	private void setRoom(WebSocket conn, Client client, Room room) {
        if (rooms.get(client) != null) {
            rooms.replace(client, room);
        } else {
            rooms.put(client, room);
        }
		room.addClient(conn, client);
	}

	public Room getRoom(Location location) {
		double closest = Double.MAX_VALUE;
		Room result = null;
		for(Room room : rooms.values()) {
			if(room.inRange(location)) {
				double distance = room.getCentre().distanceBetween(location); // TODO Get this value passed back from the previous method chain
				if(distance < closest) {
					closest = distance;
					result = room;
				}
			}
		}
		return result;
	}

	/**
	 * Sends the specified message to every Room
	 * @param post The Post to send
	 */
	public void broadcast(Post post) {
		for(Room room : rooms.values()) {
			room.send(post);
		}
	}
}