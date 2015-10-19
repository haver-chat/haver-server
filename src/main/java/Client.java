import org.java_websocket.WebSocket;

public class Client {
	private String id;
	private Location location;
	private final Object token;

	public Client() {
		// TODO Assign a client a name and profile picture?
		location = new Location(0d, 0d, 0d);
		this.token = generateToken();
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

	public static boolean validId(String id) {
		// TODO
		return true;
	}

}
