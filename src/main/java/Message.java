import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * All Messages have a constructor with the signature:
 * 	public CLASS(JSONObject jsonObject).
 * Fields must be primitives ONLY if they are to be stringifiable and override toString.
 */
public abstract class Message {
	public final static int TYPE_LOCATION = 0;
	public final static int TYPE_POST = 1;
    public final static int TYPE_ROOM_INFO = 2;
	public final static int TYPE_CLIENT_INFO = 3;
    public final static int[] TYPES = {TYPE_LOCATION, TYPE_POST, TYPE_ROOM_INFO, TYPE_CLIENT_INFO};
	public final static String KEY_TYPE = "type";
	public final static String ROOM_INFO_REQUEST = "{\"" + KEY_TYPE + "\": " + TYPE_ROOM_INFO + '}';
	public final static String LOCATION_REQUEST = "{\"" + KEY_TYPE + "\": " + TYPE_LOCATION + '}';
	public final static String POST_REQUEST = "{\"" + KEY_TYPE + "\": " + TYPE_POST + '}';

	/**
	 * @param message
	 * @return True if the message is valid.
	 */
	public static int getType(JSONObject message) {
        if (message.containsKey(KEY_TYPE) && message.get(KEY_TYPE) instanceof Number) {
            int type = intFromJson(message, KEY_TYPE);
            for (int i : TYPES) {
                if (type == i) return type;
            }
        }
        return -1;
	}


    public static JSONObject jsonFromString(String jsonString) {
        try {
            return (JSONObject) Router.parser.parse(jsonString);
        } catch(ParseException e) {
            if (Main.DEBUG) {
                System.err.println("Message:jsonFromString : Parse Exception");
            }
            return null;
        }
    }

	/**
	 *
	 * NB: Does not enforce unique names. This should be enforced when storing the List (see: {@link Post#setTo(List) Post:setTo(List}).
	 * @param names
	 * @return True if the List is empty or all elements are valid names. False if List is longer than room max size.
	 */
	public static boolean validListOfNames(List names) {
		// Correctly allows for an empty array
		if(names.size() == 0) {return true;} // Empty Lists are safe
		if(names.size() > Client.NAMES.length) {return false;} // Cannot be larger than room max size
		// ONLY check contents below here

		for(Object name : names.stream().distinct().toArray()) { // To remove DOS attack chance.
			if (!(name instanceof String)) {return false;}
			if (!Client.validName((String) (name))) {return false;}
		}
		return true;
	}

	/**
	 * Helper method that encapsulates the casting of Numbers from JSONObjects.
	 *
	 * @precondition jsonObject must contain key of type Number.
	 * @param jsonObject The entire JSONObject containing the key/value pair to extract and cast.
	 * @param key The key that maps to a Number in the jsonObject.
	 * @return The specified value as a double.
	 */
    protected static double doubleFromJson(JSONObject jsonObject, String key) {
		return ((Number) jsonObject.get(key)).doubleValue();
    }

    /**
     * Helper method that encapsulates the casting of Numbers from JSONObjects.
     *
     * @precondition jsonObject must contain key of type Number.
     * @param jsonObject The entire JSONObject containing the key/value pair to extract and cast.
     * @param key The key that maps to a Number in the jsonObject.
     * @return The specified value as a int.
     */
    protected static int intFromJson(JSONObject jsonObject, String key) {
        return ((Number) jsonObject.get(key)).intValue();
    }

	/**
	 * Helper method that encapsulates the casting of Strings from JSONObjects.
	 *
	 * @precondition jsonObject must contain key of type String.
	 * @param jsonObject The entire JSONObject containing the key/value pair to extract and cast.
	 * @param key The key that maps to a String in the jsonObject.
	 * @return The specified value as a String.
	 */
    protected static String stringFromJson(JSONObject jsonObject, String key) {
		return (String) jsonObject.get(key);
    }

	/**
	 * Helper method that encapsulates the casting of Lists from JSONObjects.
	 *
	 * @precondition jsonObject must contain key of type List.
	 * @param jsonObject The entire JSONObject containing the key/value pair to extract and cast.
	 * @param key The key that maps to a List in the jsonObject.
	 * @return The specified value as a List.
	 */
    protected static List listFromJson(JSONObject jsonObject, String key) {
        return (List) jsonObject.get(key);
    }
}
