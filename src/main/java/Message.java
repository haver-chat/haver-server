import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.List;

/**
 * All Messages have a constructor with the signature:
 * 	public CLASS(JSONObject jsonObject).
 * Fields must be primitives ONLY if they are to be stringifiable and override toString.
 */
public abstract class Message {

	public interface JSONKey {}

	public enum Key implements JSONKey {
		TYPE("type");

		public final String key;

		Key(String key) {
			this.key = key;
		}

		@Override
		public String toString() {return key;}
	}

	public enum Type {
		LOCATION(0),
		POST(1),
		ROOM_INFO(2),
		CLIENT_INFO(3);

		public final int type;

		Type(int type) {
			this.type = type;
		}

		@Override
		public String toString() {return Integer.toString(type);}
	}

	public final static String ROOM_INFO_REQUEST = "{\"" + Key.TYPE + "\": " + Type.ROOM_INFO + '}';
	public final static String LOCATION_REQUEST = "{\"" + Key.TYPE + "\": " + Type.LOCATION + '}';
	public final static String POST_REQUEST = "{\"" + Key.TYPE + "\": " + Type.POST + '}';

	public static Type getType(JSONObject message) {
        if (!message.containsKey(Key.TYPE.key)) System.err.println("Message:getType : Key not found");
        if (!(message.get(Key.TYPE.key) instanceof Number)) System.err.println("Message:getType : GFDI");
        if (message.containsKey(Key.TYPE) && message.get(Key.TYPE) instanceof Number) {
            int typeNumber = intFromJson(message, Key.TYPE);
            for (Type type : Type.values()) {
                if (typeNumber == type.type) return type;
            }
        }
        return null;
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
    protected static double doubleFromJson(JSONObject jsonObject, JSONKey key) {
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
    protected static int intFromJson(JSONObject jsonObject, JSONKey key) {
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
    protected static String stringFromJson(JSONObject jsonObject, JSONKey key) {
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
    protected static List listFromJson(JSONObject jsonObject, JSONKey key) {
        return (List) jsonObject.get(key);
    }
}
