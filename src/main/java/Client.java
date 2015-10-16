/**
 * <insert description here>
 *
 * @author Edward Knight (<a href="http://www.edwardknig.ht/">website</a>, <a href="mailto:edw@rdknig.ht">email</a>)
 * @version 0.1
 * @since 1.8
 */
public class Client {
	private final String id;
	private Location location;
	private final  Object token;

	public void setLocation(Location location) {
		this.location = location;
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

	public Client(String id, Object token) {
		this.id = id;
		location = new Location(0d, 0d, 0);
		this.token = token;
	}

	public boolean isValid() {
		return true;
	}
}
