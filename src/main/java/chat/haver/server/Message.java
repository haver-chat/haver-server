package chat.haver.server;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.List;

/**
 * All Messages have a constructor with the signature:
 *     public CLASS(JSONObject jsonObject).
 * Fields must be primitives ONLY if they are to be stringifiable and override toString.
 */
public abstract class Message {

    public interface JSONKey {}

    public enum Key implements JSONKey {
        TYPE("type");

        public final String key;

        Key(final String key) {
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

        Type(final int type) {
            this.type = type;
        }

        @Override
        public String toString() {return Integer.toString(type);}
    }

    public enum Request {
        LOCATION("{\"" + Key.TYPE + "\": " + Type.LOCATION + '}'),
        ROOM_INFO("{\"" + Key.TYPE + "\": " + Type.ROOM_INFO + '}');

        public final String request;

        Request(final String request) {
            this.request = request;
        }

        @Override
        public String toString() {return request;}
    }

    public static Type typeFromJson(final JSONObject message) {
        if (message.containsKey(Key.TYPE.key) && message.get(Key.TYPE.key) instanceof Number) {
            int typeNumber = intFromJson(message, Key.TYPE);
            for (Type type : Type.values()) {
                if (typeNumber == type.type) return type;
            }
        }
        return null;
    }


    public static JSONObject jsonFromString(final String jsonString) {
        try {
            return (JSONObject) Router.PARSER.parse(jsonString);
        } catch(ParseException e) {
            if (Main.DEBUG) {
                System.err.println("Message:jsonFromString : Parse Exception");
                e.printStackTrace();
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
    public static boolean validListOfNames(final List names) {
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
     * @haver.precondition jsonObject must contain key of type Number.
     * @param jsonObject The entire JSONObject containing the key/value pair to extract and cast.
     * @param key The key that maps to a Number in the jsonObject.
     * @return The specified value as a double.
     */
    protected static double doubleFromJson(final JSONObject jsonObject, final JSONKey key) {
        return ((Number) jsonObject.get(key.toString())).doubleValue();
    }

    /**
     * Helper method that encapsulates the casting of Numbers from JSONObjects.
     *
     * @haver.precondition jsonObject must contain key of type Number.
     * @param jsonObject The entire JSONObject containing the key/value pair to extract and cast.
     * @param key The key that maps to a Number in the jsonObject.
     * @return The specified value as a int.
     */
    protected static int intFromJson(final JSONObject jsonObject, final JSONKey key) {
        return ((Number) jsonObject.get(key.toString())).intValue();
    }

    /**
     * Helper method that encapsulates the casting of Strings from JSONObjects.
     *
     * @haver.precondition jsonObject must contain key of type String.
     * @param jsonObject The entire JSONObject containing the key/value pair to extract and cast.
     * @param key The key that maps to a String in the jsonObject.
     * @return The specified value as a String.
     */
    protected static String stringFromJson(final JSONObject jsonObject, final JSONKey key) {
        return (String) jsonObject.get(key.toString());
    }

    /**
     * Helper method that encapsulates the casting of Lists from JSONObjects.
     *
     * @haver.precondition jsonObject must contain key of type List.
     * @param jsonObject The entire JSONObject containing the key/value pair to extract and cast.
     * @param key The key that maps to a List in the jsonObject.
     * @return The specified value as a List.
     */
    protected static List listFromJson(final JSONObject jsonObject, final JSONKey key) {
        return (List) jsonObject.get(key.toString());
    }
}
