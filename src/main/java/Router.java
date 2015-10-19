import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.lang.Override;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

// TODO Get token lib

/**
 * WebSocketServer that puts Clients into Rooms and routes Messages from the Client to the Room.
 */
public class Router extends WebSocketServer {

	private final static int TYPE_ROOM_INFO = -1;
	private final static int TYPE_LOCATION = 0;
	private final static int TYPE_POST = 1;
	private final static String ROOM_INFO_REQUEST = "{\"type\":" + TYPE_ROOM_INFO + '}';
	private final static String LOCATION_REQUEST = "{\"type\":" + TYPE_LOCATION + '}';
	private final static String POST_REQUEST = "{\"type\":" + TYPE_POST + '}';

	private final static JSONParser parser = new JSONParser();
    private final static String HOSTNAME = "localhost";
	private final static int PORT = 8080;
	private HashMap<WebSocket, Client> clients = new HashMap<>();
	private HashMap<Client, Room> rooms = new HashMap<>();


	/**
	 * @throws UnknownHostException Config dun goof'd.
	 */
	public Router() throws UnknownHostException {
		super(new InetSocketAddress(HOSTNAME, PORT));
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		Client client = new Client();
		clients.put(conn, client);
        System.out.println("New connection: " + client.getId());
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		Client client = clients.get(conn);
		clients.remove(conn);
        if (rooms.get(client) != null) {
            rooms.get(client).close(client);
            rooms.remove(client);
        }
        System.out.println("Connection closed: " + client.getId());
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a specific websocket
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
		Room room = rooms.get(client);
		System.out.println("Message from [" + client.getId() + "]: " + message);

		try {
			JSONObject jsonObject = (JSONObject) parser.parse(message);
			int type = ((Long) jsonObject.get(Location.KEY_TYPE)).intValue();

			if(room != null) {
				switch (type) {
					case TYPE_LOCATION:
						if (room.inRange(client.getLocation())) {
							room.updateLocation(client, new Location(jsonObject));
						} else {
							room.close(client);
							Location location = new Location(jsonObject);
							client.setLocation(location);
							rooms.replace(client, getRoom(location));
						}
						break;

					case TYPE_POST:
						room.send(new Post(jsonObject));
						break;

					default:
						// Client dun goof'd
						break;
				}
			} else {
				switch (type) {
					case TYPE_LOCATION:
						Location location = new Location(jsonObject);
						client.setLocation(location);
						room = getRoom(location);
						if(room != null) {
							setRoom(conn, client, room);
						} else {
							conn.send(ROOM_INFO_REQUEST);
						}
						break;

					case TYPE_ROOM_INFO:
						if(client.getLocation() != null) {
							RoomInfo roomInfo = new RoomInfo(jsonObject);
							room = new Room(roomInfo, client.getLocation());
							setRoom(conn, client, room);
						} else {
							// Client dun goof'd
							conn.send(LOCATION_REQUEST);
						}
						break;

					default:
						// Client dun goof'd
						conn.send(LOCATION_REQUEST);
						break;
				}
			}
		} catch(ParseException e) {
			// Client dun goof'd
			e.printStackTrace();
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
		rooms.replace(client, room);
		room.addClient(conn, client);
		conn.send(POST_REQUEST); // Receipt of a Post request tells the Client it has been allocated to a valid room
	}

	public Room getRoom(Location location) {
		double closest = -1d;
		Room result = null;

		for(Room room : rooms.values()) {
			if(room.inRange(location)) {
				double distance = room.getCentre().distanceBetween(location);
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

    public static void main(String[] args) {
        try {
            Router router = new Router();
            router.start();
            System.out.println("Hosting new server on: " + router.getAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
