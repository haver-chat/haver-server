import org.json.simple.JSONObject;

public class Post extends Message {

	public final static String KEY_FROM = "from";
	public final static String KEY_CONTENT = "content";
	public final static String KEY_TO = "to";

    private String from;
    private String content;
	private String[] to; // If not specified (null), broadcast.


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
		// TODO Check if this errors
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
			jsonObject.put(KEY_TO, to); // NB: All recipiants know all other recipiants.
        return jsonObject.toJSONString();
    }

	public void setFrom(String from) {
		this.from = from;
	}

	public String setContent(String content) {
		this.content = content;
		return this.content;
	}

	public void setTo(String[] to) {
		this.to = to;
	}

	public void setTo(String to) {
		this.to = new String[]{to};
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

	/**
	 * @param message
	 * @return True if the Post is valid
	 */
	@Override
	public boolean valid(JSONObject message) {
		String from;
		String content;
		String[] to;

		if(!(super.valid(message) &&
			message.get(KEY_TO) instanceof String &&
			Client.validId((String) message.get(KEY_TO)) &&

			message.get(KEY_CONTENT) instanceof String &&
			((String) message.get(KEY_CONTENT)).length() > 0 &&

			message.get(KEY_FROM) instanceof String[])) {return false;}

		for(String id : (String[]) message.get(KEY_FROM)) {
			if(!Client.validId(id)) {return false;}
		}

		return true;
	}
}
