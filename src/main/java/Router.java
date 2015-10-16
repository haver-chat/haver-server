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

	private final static JSONParser parser = new JSONParser();
	private final static int PORT = 8080;
	private HashMap<WebSocket, Client> clients = new HashMap<>();
	private HashMap<Client, Room> rooms = new HashMap<>();

	public Router() throws UnknownHostException {
		super(new InetSocketAddress(PORT));
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		clients.put(conn, new Client(generateID(conn), generateToken(conn)));
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		Client client = clients.get(conn);
		rooms.get(client).onClose(client, code, reason, remote);
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		Client client = clients.get(conn);
		Room room = rooms.get(client);
		JSONObject jsonObject = null; // TODO init to null?

		try {
			jsonObject = (JSONObject) parser.parse(message);

			switch((int) jsonObject.get(Location.KEY_TYPE)) {
				case TYPE_LOCATION:
					if (room != null) {
						room.updateLocation(client, new Location(jsonObject));
					} else {
						// Give the client a room
						room = null;
						// Add to the rooms hashmap
						rooms.put(client, room);
						room.addClient(conn, client);
					}
					break;

				case TYPE_MESSAGE:
					if (room != null) {
						room.broadcast(new Message(jsonObject));
					} else {
						// Client has dun-goofed and sent a message when it isn't in a room
						// TODO Decide what to do here. Ignore?
					}
					break;
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

	/**
	 * Sends the specified Message to every Client
	 * @param message The message to send
	 */
	public void broadcast(Message message) {
		for(Room room : rooms.values()) {
			room.broadcast(message);
		}
	}

    public static void main(String[] args) throws Exception {
        WebSocketImpl.DEBUG = true;
        Router router = new Router();
        router.start();
        SocClient client = new SocClient(new URI("ws://127.0.0.1:" + PORT + "/"));
        client.connect();
    }
}
