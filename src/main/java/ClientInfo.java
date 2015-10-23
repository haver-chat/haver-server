import org.json.simple.JSONObject;

import java.util.List;
// TODO: Replace weird Posts and Post Req shenanigans with this class
public class ClientInfo extends Message {

	public final static String KEY_ROOM_NAME = "roomName";
	public final static String KEY_CHANGE = "change";
	public final static String KEY_NAMES = "names";

	/**
	 * @param message
	 * @return True if the ClientInfo is valid
	 */
	@Override
	public boolean valid(JSONObject message) { // TODO: Will this ever be called? Since we don't receive a ClientInfo from the client ever.
		if(!(super.valid(message) &&

				(!message.containsKey(KEY_ROOM_NAME) ||
						(message.get(KEY_ROOM_NAME) instanceof String &&
								Message.stringFromJson(message, KEY_ROOM_NAME).length() > 0)) &&

				message.get(KEY_CHANGE) instanceof Boolean &&

				message.get(KEY_NAMES) instanceof List)) {return false;}

		// TODO: REMOVE DUPE CODE
		List names;
		// Correctly allows for an empty array
		if((names = ((List) message.get(KEY_NAMES))).size() == 0) {return true;} // Empty Lists are safe
		if(names.size() > Client.NAMES.length) {return false;} // Cannot be larger than room max size
		// ONLY check contents of To array below here

		// TODO: Move element type verification elsewhere
		for(Object name : names.stream().distinct().toArray()) { // To remove DOS attack chance. Uniqueness is enforced in setTo().
			if (!(name instanceof String)) {return false;}
			if (!Client.validName((String) (name))) {return false;}
		}

		return true;
	}

}
