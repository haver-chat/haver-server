import org.java_websocket.WebSocket;

import java.util.Arrays;
import java.util.HashMap;

/**
 * A location-based chat room.
 */
public class Room {
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
		clients.put(conn, client);
		centre = recalculateCentre(client.getLocation());
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
		if(post.getContent().startsWith("/")) {
			if(post.setContent(parseCommand(post.getContent().substring(1))) == null) {return;}
		}

		if(post.getTo() == null) {
			for (WebSocket conn : clients.keySet()) {
				conn.send(post.toString());
			}
		} else {
			for(String id : post.getTo()) {
				for(WebSocket conn : clients.keySet()) {
					Client client = clients.get(conn);
					if(client.getId().equals(id)) {
						conn.send(post.toString());
						break;
					}
				}
			}
		}
	}

	public String parseCommand(String message) {
		String[] splitMessage = message.split("\\s");
		String command = splitMessage[0];
		String[] args = Arrays.copyOfRange(splitMessage, 1, splitMessage.length);

		switch(command) {
			// Implement commands here with a final static String
			default:
				// No command found
				return null;
		}
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
	 * @param newLocation
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
	 * @param oldLocation
	 * @param updatedLocation
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
