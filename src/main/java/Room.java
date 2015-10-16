import org.java_websocket.WebSocket;

import java.util.HashMap;

public class Room {
	private String name;
	private Location centre;
	private double radius;
	private HashMap<WebSocket, Client> clients;

	/**
	 * A location-based chat room.
	 *
	 * @param name The human-readable name of where the centre is.
	 * @param centre The Location of the centre of the Room.
	 * @param radius The distance in which a Client can join the Room.
	 */
	public Room(String name, Location centre, double radius) {
		this.name = name;
		this.centre = centre;
		this.radius = radius;
	}

	/**
	 * Helper method to check whether or not the specified location is within the range of the room.
	 *
	 * @param location The specified location.
	 * @return True if the specified locaiton is within the radius of the room.
	 */
	public boolean inRange(Location location) {
		return centre.distanceBetween(location) <= radius;
	}

	/**
	 * Handler method for connection close.
	 *
	 * @param client The client whose connection has closed.
	 * @param code
	 * @param reason
	 * @param remote
	 */
	public void onClose(Client client, int code, String reason, boolean remote) {
		broadcast(new Message(client, "Some message saying a client has d/c'ed"));
		clients.remove(client);
	}

	/**
	 * Sends the specified message to every Client in the Room.
	 * @param message The message to be broadcast.
	 */
	public void broadcast(Message message) {
		for(WebSocket conn : clients.keySet()) {
			conn.send(message.toJSON());
		}
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
			// TODO Client is giving location update to wrong room, what do?
		}
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
	 * The name and number of Clients in the Room.
	 *
	 * @return name (numberOfClients)
	 */
	@Override
	public String toString() {
		return name + '(' + clients.size() + ')';
	}
}
