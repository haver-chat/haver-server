
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.lang.Override;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Collection;

/**
 * <insert description here>
 *
 * @author Edward Knight (<a href="http://www.pornhub.com/">website</a>, <a href="mailto:edw@rdknig.ht">email</a>)
 * @version 0.1
 * @since 1.8
 */
public class Router extends WebSocketServer {
	public Router( int port ) throws UnknownHostException {
		super( new InetSocketAddress( port ) );
	}

	public Router( InetSocketAddress address ) {
		super( address );
	}

	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        System.out.println("OHAI:::: " + handshake.getResourceDescriptor());
		this.sendToAll( "new connection: " + handshake.getResourceDescriptor() );
		System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		this.sendToAll( conn + " has left the room!" );
		System.out.println( conn + " has left the room!" );
	}

	@Override
	public void onMessage( WebSocket conn, String message ) {
		this.sendToAll( message );
		System.out.println( conn + ": " + message );
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
