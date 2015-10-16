import org.json.simple.JSONObject;

/**
 * All Messages have a constructor with the signature:
 * 	public CLASS(JSONObject jsonObject)
 */
public abstract class Message {
	public final static String KEY_TYPE = "type";
}
