import org.json.simple.JSONObject;

/**
 * All Messages have a constructor with the signature:
 * 	public CLASS(JSONObject jsonObject).
 * Fields must be primitives ONLY if they are to be stringifiable and override toString.
 */
public abstract class Message {
	public final static String KEY_TYPE = "type";
	public final static int TYPE_ROOM_INFO = -1;
	public final static String ROOM_INFO_REQUEST = "{\"" + KEY_TYPE + "\":" + TYPE_ROOM_INFO + '}';
	public final static int TYPE_LOCATION = 0;
	public final static String LOCATION_REQUEST = "{\"" + KEY_TYPE + "\":" + TYPE_LOCATION + '}';
	public final static int TYPE_POST = 1;
	public final static String POST_REQUEST = "{\"" + KEY_TYPE + "\":" + TYPE_POST + '}'; // TODO change to JSON object so that the name can be added dynamically

	/**
	 * @param message
	 * @return True if the message is valid.
	 */
	public boolean valid(JSONObject message) {
		int type;
		return message.get(KEY_TYPE) instanceof Long &&
			((type = ((Long) message.get(KEY_TYPE)).intValue()) == TYPE_ROOM_INFO ||
				type == TYPE_LOCATION ||
				type == TYPE_POST);
	}
}
