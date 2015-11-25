package chat.haver.server;

import org.json.simple.JSONArray;

import java.util.Collection;
import java.util.List;

/**
 * Message to the Client giving information about the Room and other Clients.
 * This class acts as a factory for stringified ClientInfo in JSON.
 */
public class ClientInfo extends Message {

    public enum Key implements JSONKey {
        ROOM_NAME("roomName"),
        CLIENT_NAME("clientName"),
        CHANGE("change"),
        NAMES("names");

        public final String key;

        Key(final String key) {
            this.key = key;
        }

        @Override
        public String toString() {return key;}
    }

    /**
     * To be sent to the Client when they join a Room.
     *
     * @haver.precondition roomName must have a length greater than 0.
     * @haver.precondition clientName must be a valid name.
     * @haver.precondition names must not be empty.
     * @haver.precondition names must contain only valid, unique, names.
     * @haver.precondition names must not be greater than the max size of a Room.
     *
     * @param roomName The name of the Room.
     * @param clientName The name of the Client
     * @param change True if Clients are being added, false otherwise.
     * @param names A List of Client names that are being added/removed.
     * @return Stringified JSON with TYPE_CLIENT_INFO and the specified data.
     */
    public static String toString(final String roomName, final String clientName, final boolean change, final List<String> names) {
        return "{\"" +
                Message.Key.TYPE + "\": " + Type.CLIENT_INFO + ", \"" +
                Key.ROOM_NAME + "\": " + roomName + ", \"" +
                Key.CLIENT_NAME + "\": " + clientName + ", \"" +
                Key.CHANGE + "\": " + change + ", \"" +
                Key.NAMES + "\":" + JSONArray.toJSONString(names) + "}";
    }

    /**
     * To be sent to the Client when they join a Room.
     *
     * @haver.precondition roomName must have a length greater than 0.
     * @haver.precondition clientName must be a valid name.
     * @haver.precondition names must not be empty.
     * @haver.precondition names must contain only valid, unique, names.
     * @haver.precondition names must not be greater than the max size of a Room.
     *
     * @param roomName The name of the Room.
     * @param clientName The name of the Client
     * @param change True if Clients are being added, false otherwise.
     * @param clients A List of Clients that are being added/removed.
     * @return Stringified JSON with TYPE_CLIENT_INFO and the specified data.
     */
    public static String toString(final String roomName, final String clientName, final boolean change, final Collection<Client> clients) {
        StringBuilder sb = new StringBuilder("{\"" +
                Message.Key.TYPE + "\": " + Type.CLIENT_INFO + ", \"" +
                Key.ROOM_NAME + "\": \"" + roomName + "\", \"" +
                Key.CLIENT_NAME + "\": \"" + clientName + "\", \"" +
                Key.CHANGE + "\": " + change + ", \"" +
                Key.NAMES + "\":[\"");
        for(Client client : clients) {
            sb.append(client.getName()).append("\", \"");
        }
        sb.delete((sb.length() - 1 - 3), (sb.length() - 1));
        sb.append("]}");
        return sb.toString();
    }

    /**
     * To be sent to a Client when an other Client arrives or departs the Room.
     *
     * @haver.precondition name must be a valid name.
     *
     * @param change True if Clients are being added, false otherwise.
     * @param name The name of the Client that is being added/removed.
     * @return Stringified JSON with TYPE_CLIENT_INFO and the specified data.
     */
    public static String toString(final boolean change, final String name) {
        return "{\"" +
                Message.Key.TYPE + "\": " + Type.CLIENT_INFO + ", \"" +
                Key.CHANGE + "\": " + change + ", \"" +
                Key.NAMES + "\": [\"" + name  + "\"]}";
    }
}
