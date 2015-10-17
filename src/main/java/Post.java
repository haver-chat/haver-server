import org.json.simple.JSONObject;

public class Post extends Message {

	public final static String KEY_FROM = "from";
	public final static String KEY_CONTENT = "content";
	public final static String KEY_TO = "to";

    private final String from;
    private String content;
	private final String[] to; // If not specified (null), broadcast.


    public Post(String from, String content) {
        this.from = from;
        this.content = content;
		this.to = null;
    }

	public Post(String from, String content, String[] to) {
		this.from = from;
		this.content = content;
		this.to = to;
	}

	public Post(JSONObject jsonObject) {
		this((String) jsonObject.get(KEY_FROM),
			(String) jsonObject.get(KEY_CONTENT),
			(String[]) jsonObject.get(KEY_TO));
	}

	/**
	 * @return Stringified JSON.
	 */
	@Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
			jsonObject.put(KEY_FROM, from);
			jsonObject.put(KEY_CONTENT, content);
			//obj.put(KEY_TO, to); Not included as this is a BCC system
        return jsonObject.toJSONString();
    }

	public String setContent(String content) {
		this.content = content;
		return this.content;
	}

	public String getFrom() {
		return from;
	}

	public String getContent() {
		return content;
	}

	public String[] getTo() {
		return to;
	}
}
