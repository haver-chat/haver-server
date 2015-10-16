import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.lang.Override;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;

// TODO get JSON lib
// TODO get token lib

public class Router extends WebSocketServer {

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

	//TODO Generate IDs
	private String generateID(WebSocket conn) {
		return null;
	}

	//TODO Generate tokens
	private Object generateToken(WebSocket conn) {
		return null;
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		this.sendToAll( conn + " has left the room!" );
		System.out.println( conn + " has left the room!" );
	}

	@Override
	public void onMessage( WebSocket conn, String message ) {
		rooms.get(clients.get(conn)).onMessage(conn, message);


	}

	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
		if( conn != null ) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}

	public void sendToAll( String text ) {
		Collection<WebSocket> con = connections();
		synchronized ( con ) {
			for( WebSocket c : con ) {
				c.send( text );
			}
		}
	}

    public static void main(String[] args) throws Exception {
        WebSocketImpl.DEBUG = true;
        Router router = new Router(8080);
        router.start();
        SocClient client = new SocClient(new URI("ws://127.0.0.1:8080/"));
        client.connect();
    }
}
