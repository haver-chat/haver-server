import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/**
 * A location-based chat room.
 */
public class Room {

	public final static String COMMAND_WHISPER = "whisper"; // TODO Move this command to client side?

	private final static Random random = new Random();
	private String name;
	private Location centre;
	private double radius;
	private HashMap<WebSocket, Client> clients = new HashMap<>();
	private ArrayList<String> freeNames = new ArrayList<>(Arrays.asList(Client.NAMES));

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
		centre = recalculateCentre(client.getLocation());
		clients.put(conn, client);
		client.setName(generateName()); // After .put to keep thread safe
		send(new Post(client.getName(), "Some message saying a client has arrived"));
	}

	/**
	 * Generate a client name that's unique to the room.
	 * A combination of things and colours.
	 */
	private String generateName() {
		if(freeNames.size() == 0) { // TODO Actually handle this situation somehow
			System.err.println("ROOM IS OVER MAXIMUM LIMIT, OH SHIT SON");
			System.exit(69);
		}
		int index = random.nextInt(freeNames.size());
		String name = freeNames.get(index);
		freeNames.remove(index);
		return name;
	}

	/**
	 * Handler method for connection close.
	 *
	 * @param client The client whose connection has closed.
	 */
	public void close(Client client) {
		client.setName(null);
		freeNames.add(client.getName()); // Before .remove to keep thread safe
		clients.remove(client);
		send(new Post(client.getName(), "Some message saying a client has d/c'ed"));
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
				assert(validNames(post));
				conn.send(post.toString());
			}
		} else {
			for(String name : post.getTo()) {
				for(WebSocket conn : clients.keySet()) {
					Client client = clients.get(conn);
					if(client.getName().equals(name) || client.getName().equals(post.getFrom())) {
						assert(validNames(post));
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
					post.setTo(args[0]);
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

	/**
	 * Checks if a name is in use in this Room.
	 *
	 * @param name The name to verify.
	 * @return True if the name is being used in the room.
	 */
	public boolean validName(String name) {
		for(Client client : clients.values()) {
			if(client.getName().equals(name)) {return true;}
		}
		return false;

		// Alternate implementation, which I believe will be slower:
		//return Client.validName(name) &&
		//	!freeNames.contains(name);
	}

	/**
	 * Checks if an array of names are in use in this Room.
	 *
	 * @param names The names to verify.
	 * @return True if all the names are being used in the room.
	 */
	public boolean validNames(String[] names) {
		for(String name : names) {
			if(!validName(name)) {return false;}
		}
		return true;
	}

	/**
	 * Checks if the sender and receivers of a Post are in this Room.
	 *
	 * @param post The Post containing names to verify.
	 * @return True if all the names are being used in the room.
	 */
	public boolean validNames(Post post) {
		return validName(post.getFrom()) && validNames(post.getTo());
	}
}
