import org.java_websocket.WebSocket;

import java.util.Arrays;
import java.util.HashMap;

/**
 * A location-based chat room.
 */
public class Room {

	public final static String COMMAND_WHISPER = "whisper"; // TODO Move this command to client side?
	public final static String[] COLOURS = null;
	public final static String[] THINGS = null; // Change to read in from file instead of set in code later.

	private String name;
	private Location centre;
	private double radius;
	private HashMap<WebSocket, Client> clients = new HashMap<>();

	/**
	 * @param name The human-readable name of where the centre is.
	 * @param centre The Location of the centre of the Room.
	 * @param radius The distance in which a Client can join the Room.
	 */
	public Room(String name, Location centre, double radius) {
		this.name = name;
		this.centre = centre;
		this.radius = radius; // TODO Change as more people are added?
		// TODO Enforce min/max values
	}

	public Room(RoomInfo roomInfo, Location centre) {
		this(roomInfo.getName(), centre, roomInfo.getRadius());
	}

	/**
	 * Adds a specified Client to the Room.
	 *
	 * @param conn The WebSocket of the specified Client.
	 * @param client The Client to be added.
	 */
	public void addClient(WebSocket conn, Client client) {
		// Generate and give client an ID that's unique to this room
		clients.put(conn, client);
		centre = recalculateCentre(client.getLocation());
		send(new Post(client.getId(), "Some message saying a client has arrived"));
	}

	/**
	 * Generate a client ID that's unique to the room.
	 * A combination of objects and colours.
	 */
	private String generateID() {
		// TODO
		return null;
	}

	/**
	 * Handler method for connection close.
	 *
	 * @param client The client whose connection has closed.
	 */
	public void close(Client client) {
		clients.remove(client);
		send(new Post(client.getId(), "Some message saying a client has d/c'ed"));
	}

	/**
	 * Sends the specified Post to the correct Clients in the Room.
	 *
	 * @param post The message to be broadcast.
	 */
	public void send(Post post) {
		if(post.getContent().startsWith("/") &&
			(post = parseCommand(post)) == null) {return;}

		if(post.getTo().length == 0) { // TODO This may need changing
			for (WebSocket conn : clients.keySet()) {
				conn.send(post.toString());
			}
		} else {
			for(String id : post.getTo()) {
				for(WebSocket conn : clients.keySet()) {
					Client client = clients.get(conn);
					if(client.getId().equals(id) || client.getId().equals(post.getFrom())) {
						conn.send(post.toString());
						break;
					}
				}
			}
		}
	}

	public Post parseCommand(Post post) {
		String[] splitContent = post.getContent().substring(1).split("\\s");
		String command = splitContent[0].toLowerCase();
		String[] args = Arrays.copyOfRange(splitContent, 1, splitContent.length);

		switch(command) {
			// Implement commands here with a final static String
			case(COMMAND_WHISPER):
				if(!post.getFrom().equals(args[0])) {
					post.setTo(resolveName(args[0]));
					post.setContent(post.getContent().substring(1 + command.length() + 1 + args[0].length() + 1));
				} else {
					return null; // Cannot whisper to self
				}
				break;
			default:
				// No command found
				return null;
		}

		return post;
	}

	/**
	 * Helper method to resolve a display name into a Client ID.
	 *
	 * @param name The name to resolve.
	 * @return The client ID the display name is associated with.
	 */
	public String resolveName(String name) {
		// TODO Implement names for clients (and profile pictures)
		String id = name;
		return name;
	}

	/**
	 * Handler method for Client Location update.
	 *
	 * @param client The Client whose Location is updated.
	 * @param location The updated Location of the specified Client.
	 */
	public void updateLocation(Client client, Location location) {
		if (client.isValid()) {
			Location oldLocation = client.getLocation();
			client.setLocation(location);
			centre = recalculateCentre(oldLocation, location);
		} else {
			// Client dun goof'd?
		}
	}

	/**
	 * Helper method used to recalculate the centre of the room when a new client is added.
	 * Averages all the clients' locations.
	 * Accuracy is ignored.
	 *
	 * @param newLocation The Location of the new Client.
	 */
	private Location recalculateCentre(Location newLocation) {
		return new Location(centre.getLatitude() + (newLocation.getLatitude() / clients.size()),
				centre.getLongitude() + (newLocation.getLongitude() / clients.size()));
	}

	/**
	 * Helper method used to recalculate the centre of the room when a client updates its location.
	 * Averages all the clients' locations.
	 * Accuracy is ignored.
	 *
	 * @param oldLocation The old Location of the Client.
	 * @param updatedLocation The Client's new Location.
	 */
	private Location recalculateCentre(Location oldLocation, Location updatedLocation) {
		updatedLocation = recalculateCentre(updatedLocation);
		return new Location(updatedLocation.getLatitude() - (oldLocation.getLatitude() / clients.size()),
			updatedLocation.getLongitude() - (oldLocation.getLongitude() / clients.size()));
	}

	/**
	 * Helper method to check whether or not the specified location is within the range of the room.
	 *
	 * @param location The specified location.
	 * @return True if the specified location is within the radius of the room.
	 */
	public boolean inRange(Location location) {
		return centre.distanceBetween(location) <= radius;
	}

	/**
	 * The name and number of Clients in the Room.
	 *
	 * @return name (numberOfClients)
	 */
	@Override
	public String toString() {
		return name + '(' + clients.size() + ')';
	}

	public Location getCentre() {
		return centre;
	}
}
