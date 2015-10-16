import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by azertify on 16/10/15.
 */
public class SocClient extends WebSocketClient {
    public SocClient(URI uri) {
        super(uri);
    }

    @Override
    public void onMessage( String message ) {
    }

    @Override
    public void onOpen( ServerHandshake handshake ) {
    }

    @Override
    public void onClose( int code, String reason, boolean remote ) {
    }

    @Override
    public void onError( Exception ex ) {
    }
}
