import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.lang.Override;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashMap;

// TODO get token lib

public class Router extends WebSocketServer {

	private final static int TYPE_LOCATION = 0;
	private final static int TYPE_MESSAGE = 1;
	private final static int TYPE_ROOM_INFO = 2;

	private final static JSONParser parser = new JSONParser();
    private final static String HOSTNAME = "localhost";
	private final static int PORT = 8080;
	private HashMap<WebSocket, Client> clients = new HashMap<>();
	private HashMap<Client, Room> rooms = new HashMap<>();

	public Router() throws UnknownHostException {
		super(new InetSocketAddress(HOSTNAME, PORT));
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		clients.put(conn, new Client(generateID(conn), generateToken(conn)));
        System.out.println("New connection: " + conn);
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		Client client = clients.get(conn);
		clients.remove(conn);
        if (rooms.get(client) != null) {
            rooms.get(client).close(client);
            rooms.remove(client);
        }
        System.out.println("Connection closed: " + conn);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
        System.out.println("Message from [" + conn + ": " + message);

		Client client = clients.get(conn);
		Room room = rooms.get(client);
		JSONObject jsonObject = null; // TODO init to null?

		try {
			jsonObject = (JSONObject) parser.parse(message);
			int type = (int) jsonObject.get(Location.KEY_TYPE);

			if(room != null) {
				switch (type) {
					case TYPE_LOCATION:
						room.updateLocation(client, new Location(jsonObject));
						break;
					case TYPE_MESSAGE:
						room.broadcast(new Post(jsonObject));
						break;
					default:
						// TODO DUN GOOF'D
						break;
				}
			} else {
				switch (type) {
					case TYPE_LOCATION:
						Location location = new Location(jsonObject);
						client.setLocation(location);
						rooms.replace(client, getRoom(location));
						break;
					case TYPE_ROOM_INFO:
						if(client.getLocation() != null) {
							RoomInfo roomInfo = new RoomInfo(jsonObject);
							room = new Room(roomInfo, client.getLocation());
							rooms.replace(client, room);
							room.addClient(conn, client);
						} else {
							// Client has given room information but no location beforehand
						}
						break;
					default:
						// TODO DUN GOOF'D
						break;
				}
			}
		} catch(ParseException e) {
			// TODO Client gave me bad JSON, wut do? =(
		}
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		ex.printStackTrace();
		if (conn != null) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}

	public static JSONObject serialise(String message) {
		return null;
	}

	//TODO Generate IDs
	private String generateID(WebSocket conn) {
		return null;
	}

	//TODO Generate tokens
	private Object generateToken(WebSocket conn) {
		return null;
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
	 * Sends the specified message to every Client
	 * @param post The message to send
	 */
	public void broadcast(Post post) {
		for(Room room : rooms.values()) {
			room.broadcast(post);
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
