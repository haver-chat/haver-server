import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Collection;
import java.util.List;

/**
 * Message to the Client giving information about the Room and other Clients.
 * This class acts as a factory for stringified ClientInfo in JSON.
 */
public class ClientInfo extends Message {

	public final static String KEY_ROOM_NAME = "roomName";
	public final static String KEY_CLIENT_NAME = "clientName";
	public final static String KEY_CHANGE = "change";
	public final static String KEY_NAMES = "names";

	/**
	 * To be sent to the Client when they join a Room.
	 *
	 * @precondition roomName must have a length greater than 0.
	 * @precondition clientName must be a valid name.
	 * @precondition names must not be empty.
	 * @precondition names must contain only valid, unique, names.
	 * @precondition names must not be greater than the max size of a Room.
	 *
	 * @param roomName The name of the Room.
	 * @param clientName The name of the Client
	 * @param change True if Clients are being added, false otherwise.
	 * @param names A List of Client names that are being added/removed.
	 * @return Stringified JSON with TYPE_CLIENT_INFO and the specified data.
	 */
	public static String toString(String roomName, String clientName, boolean change, List<String> names) {
		return "{\"" +
				KEY_TYPE + "\": " + TYPE_CLIENT_INFO + ", \"" +
				KEY_ROOM_NAME + "\": " + roomName + ", \"" +
				KEY_CLIENT_NAME + "\": " + clientName + ", \"" +
				KEY_CHANGE + "\": " + change + ", \"" +
				KEY_NAMES + "\":" + JSONArray.toJSONString(names) + "}";
	}

	/**
	 * To be sent to the Client when they join a Room.
	 *
	 * @precondition roomName must have a length greater than 0.
	 * @precondition clientName must be a valid name.
	 * @precondition names must not be empty.
	 * @precondition names must contain only valid, unique, names.
	 * @precondition names must not be greater than the max size of a Room.
	 *
	 * @param roomName The name of the Room.
	 * @param clientName The name of the Client
	 * @param change True if Clients are being added, false otherwise.
	 * @param clients A List of Clients that are being added/removed.
	 * @return Stringified JSON with TYPE_CLIENT_INFO and the specified data.
	 */
	public static String toString(String roomName, String clientName, boolean change, Collection<Client> clients) {
		StringBuilder sb = new StringBuilder("{\"" +
				KEY_TYPE + "\": " + TYPE_CLIENT_INFO + ", \"" +
				KEY_ROOM_NAME + "\": \"" + roomName + "\", \"" +
				KEY_CLIENT_NAME + "\": \"" + clientName + "\", \"" +
				KEY_CHANGE + "\": " + change + ", \"" +
				KEY_NAMES + "\":[\"");
		for(Client client : clients) {
			sb.append(client.getName()).append("\", \"");
		}
		sb.delete((sb.length() - 1 - 2), (sb.length() - 1));
		sb.append("]}");
		return sb.toString();
	}

	/**
	 * To be sent to a Client when an other Client arrives or departs the Room.
	 *
	 * @precondition name must be a valid name.
	 *
	 * @param change True if Clients are being added, false otherwise.
	 * @param name The name of the Client that is being added/removed.
	 * @return Stringified JSON with TYPE_CLIENT_INFO and the specified data.
	 */
	public static String toString(boolean change, String name) {
		return "{\"" +
				KEY_TYPE + "\": " + TYPE_CLIENT_INFO + ", \"" +
				KEY_CHANGE + "\": " + change + ", \"" +
				KEY_NAMES + "\": \"" + name  + "\"}";
	}

	/**
	 * @param message
	 * @return True if the ClientInfo is valid
	 */
	@Override
	public boolean valid(JSONObject message) { // Will this ever be called? Since we don't receive a ClientInfo from the client ever.
		return super.valid(message) &&

				(!message.containsKey(KEY_ROOM_NAME) ||
						(message.get(KEY_ROOM_NAME) instanceof String &&
						Message.stringFromJson(message, KEY_ROOM_NAME).length() > 0)) &&

				message.get(KEY_CHANGE) instanceof Boolean &&

				message.get(KEY_NAMES) instanceof List &&
				validListOfNames(Message.listFromJson(message, KEY_NAMES));
	}

}
