import org.java_websocket.WebSocket;

import java.util.HashMap;

public class Room {
	private String name;
	private Location centre;
	private double radius;
	private HashMap<WebSocket, Client> clients;

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

	public void onClose(Client client, int code, String reason, boolean remote) {
		broadcast(client, new Message(client, "Some message saying a client has d/c'ed"));
		clients.remove(client);
	}

	public void broadcast(Client client, Message message) {

	}

	public void addClient(WebSocket conn, Client client) {
		clients.put(conn, client);
        centre = recalculateCentre(client.getLocation());
	}

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
}
