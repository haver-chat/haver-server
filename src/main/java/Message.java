import org.json.simple.JSONObject;

/**
 * All Messages have a constructor with the signature:
 * 	public CLASS(JSONObject jsonObject).
 * Fields must be primitives ONLY if they are to be stringifiable and override toString.
 */
public abstract class Message {
	public final static String KEY_TYPE = "type";
}
