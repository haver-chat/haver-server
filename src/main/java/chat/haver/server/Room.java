package chat.haver.server;

import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * A location-based chat room.
 * Maximum size is defined by Client.NAMES.length.
 */
public class Room {

    public static final String COMMAND_WHISPER = "whisper"; // TODO Move this command to client side?

    private static final Random RANDOM = new Random();
    private final String name;
    private Location centre;
    private final double radius;
    private HashMap<WebSocket, Client> clients = new HashMap<>();
    private ArrayList<String> freeNames = new ArrayList<>(Arrays.asList(Client.NAMES));

    /**
     * @param name The human-readable name of where the centre is.
     * @param centre The Location of the centre of the Room.
     * @param radius The distance in which a Client can join the Room.
     */
    public Room(final String name, final Location centre, final double radius) {
        if (name.length() <= 0) { Logger.warning("Empty Name"); } // TODO: null check and fix error message
        this.name = name;
        if (centre == null) { Logger.warning("Centre is null"); } // TODO: null check and fix error message
        this.centre = centre;
        if (!RoomInfo.validRadius(radius)) { Logger.warning("Invalid Radius"); } // TODO: null check and fix error message
        this.radius = radius; // TODO Change as more people are added?
    }

    public Room(final RoomInfo roomInfo, final Location centre) {
        this(roomInfo.getName(), centre, roomInfo.getRadius());
    }

    /**
     * Adds a specified Client to the Room.
     *
     * @param conn The WebSocket of the specified Client.
     * @param client The Client to be added.
     */
    public void addClient(final WebSocket conn, final Client client) {
        clients.put(conn, client);
        client.setName(generateName()); // After .put to keep thread safe
        centre = recalculateCentre(client.getLocation());
        conn.send(ClientInfo.toString(name, client.getName(), true, clients.values()));
        String newClientString = ClientInfo.toString(true, client.getName());
        clients.forEach((k, v) -> {
            if (k != conn) k.send(newClientString);
        });
        Logger.info("Added conn [" + conn + "] to room [" + this + "]: <client info request>");
    }

    /**
     * Generate a client name that's unique to the room.
     * A combination of things and colours.
     */
    private String generateName() {
        if(freeNames.size() == 0) { // TODO Actually handle this situation somehow
            Logger.severe(new Exception("ROOM IS OVER MAXIMUM LIMIT, RIP IN KILL"));
            System.exit(-2011);
        }
        int index = RANDOM.nextInt(freeNames.size());
        String name = freeNames.get(index);
        freeNames.remove(index);
        return name;
    }

    /**
     * Handler method for connection close.
     *
     * @param conn The socket which has been closed.
     */
    public void close(final WebSocket conn) {
        Client client = clients.get(conn);
        freeNames.add(client.getName()); // Before .remove to keep thread safe
        clients.remove(conn);
        String removeClientString = ClientInfo.toString(false, client.getName());
        clients.forEach((k, v) -> k.send(removeClientString));
    }

    /**
     * Sends the specified Post to the correct Clients in the Room.
     *
     * @param post The message to be broadcast.
     */
    @SuppressWarnings("checkstyle:finalparameters")
    public void send(Post post) {
        if(post.getContent().startsWith("/") &&
            (post = parseCommand(post)) == null) {return;}
        // post may now be considered final

        if (post.getTo().size() == 0) {
            for (WebSocket conn : clients.keySet()) {
                if (!(validNames(post.getTo()) && conn.isOpen())) Logger.warning("Either name not valid or connection closed"); // TODO: null check and fix error message
                conn.send(post.toString());
            }
        } else {
            for(String name : post.getTo()) {
                for(WebSocket conn : clients.keySet()) {
                    Client client = clients.get(conn);
                    if (client.getName().equals(name) || client.getName().equals(post.getFrom())) {
                        if (!(validNames(post.getTo()) && conn.isOpen())) Logger.warning("Either name not valid or connection closed (2)"); // TODO: null check and fix error message
                        conn.send(post.toString());
                        break;
                    }
                }
            }
        }
    }

    public Post parseCommand(final Post post) {
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
    public void updateLocation(final Client client, final Location location) {
        if (client.isValid()) {
            //Location oldLocation = client.getLocation();
            client.setLocation(location);
            //centre = recalculateCentre(oldLocation, location);
        } else {
            // Client dun goof'd?
        }
    }

    /**
     * Helper method used to recalculate the centre of the room when a new client is added.
     * Averages all the clients' locations.
     * Accuracy is ignored.
     *
     * @param location The Location of the new Client.
     */
    private Location recalculateCentre(final Location location) {
        if (centre == null) {
            return location;
        } else {
            return Location.getCentre(
                    clients
                            .values()
                            .stream()
                            .map(Client::getLocation)
                            .collect(Collectors.toList())
            );
        }
    }

    /**
     * Helper method used to recalculate the centre of the room when a client updates its location.
     * Averages all the clients' locations.
     * Accuracy is ignored.
     *
     * @param oldLocation The old Location of the Client.
     * @param updatedLocation The Client's new Location.
     */
    @Deprecated
    @SuppressWarnings("checkstyle:finalparameters")
    private Location recalculateCentre(final Location oldLocation, Location updatedLocation) {
        updatedLocation = recalculateCentre(updatedLocation);
        // updatedLocation can now be considered final
        return new Location(updatedLocation.getLatitude() - (oldLocation.getLatitude() / clients.size()),
            updatedLocation.getLongitude() - (oldLocation.getLongitude() / clients.size()));
    }

    /**
     * Helper method to check whether or not the specified location is within the range of the room.
     *
     * @param location The specified location.
     * @return True if the specified location is within the radius of the room.
     */
    public boolean inRange(final Location location) {
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
    public boolean validName(final String name) {
        for(Client client : clients.values()) {
            if(client.getName().equals(name)) {return true;}
        }
        return false;

        // Alternate implementation, which I believe will be slower:
        // return Client.validName(name) &&
        // !freeNames.contains(name);
    }

    /**
     * Checks if an array of names are in use in this Room.
     *
     * @param names The names to verify.
     * @return True if all the names are being used in the room.
     */
    public boolean validNames(final List<String> names) {
        for(String name : names) {
            if(!validName(name)) {return false;}
        }
        return true;
    }

    /**
     * Getter for radius
     * @return Radius
     */
    public double getRadius() {
        return radius;
    }
}
