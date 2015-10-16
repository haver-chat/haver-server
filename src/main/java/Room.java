import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <insert description here>
 *
 * @author Edward Knight (<a href="http://www.edwardknig.ht/">website</a>, <a href="mailto:edw@rdknig.ht">email</a>)
 * @version 0.1
 * @since 1.8
 */
public class Room {
	private String name;
	private Location centre;
	private float radius;
	private HashMap<Object, Client> validClients; // <Object token, Client client>

	public Room(String name, Location centre, float radius) {
		this.name = name;
		this.centre = centre;
		this.radius = radius;
	}

	public void onClose(Client client, int code, String reason, boolean remote) {
		broadcast(client, new Message(client, "Some message saying a client has d/c'ed"));
		validClients.remove(client);
	}

	public void broadcast(Client client, Message message) {

	}

	public void addClient(Client client) {validClients.put(client.getToken(), client);}

	public void updateLocation(Client client, Location location) {
		for(Client validClient : validClients.values()) {
			if(client.getToken() == validClient.getToken()) { // Valid client
				validClients.replace(client.getToken(), validClient, client);
				break;
			}
		}
	}
}
