import org.java_websocket.WebSocket;

public class Client {
	private final String id;
	private Location location;
	private final Object token;

	public Client() {
		this.id = generateID();
		// TODO Assign a client a name and profile picture?
		location = new Location(0d, 0d, 0d);
		this.token = generateToken();
	}

	//TODO Generate IDs
	private String generateID() {
		return null;
	}

	//TODO Generate tokens
	private Object generateToken() {
		return null;
	}

	//TODO Token validation
	public boolean isValid() {
		return true;
	}

	public Location getLocation() {
		return location;
	}

	public String getId() {
		return id;
	}

	public Object getToken() {
		return token;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

}
