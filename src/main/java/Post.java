import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Post extends Message {

	public final static String KEY_FROM = "from";
	public final static String KEY_CONTENT = "content";
	public final static String KEY_TO = "to";

    private String from;
    private String content;
	private List<String> to; // If not specified (null), broadcast.

	public Post(String from, String content, List<String> to) {
		setFrom(from);
		this.content = content;
		setTo(to);
	}

    public Post(String from, String content) {
        this(from, content, new ArrayList<>());
    }

	public Post(JSONObject jsonObject) {
		// TODO Check if this errors
		this(
                Message.stringFromJson(jsonObject, KEY_FROM),
                Message.stringFromJson(jsonObject, KEY_CONTENT),
                Message.listFromJson(jsonObject, KEY_TO)
        );
	}

	/**
	 * @return Stringified JSON.
	 */
	@Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
			jsonObject.put(KEY_FROM, from);
			jsonObject.put(KEY_CONTENT, content);
			jsonObject.put(KEY_TO, to); // NB: All recipients know all other recipients.
        return jsonObject.toJSONString();
    }

	public void setFrom(String from) {
		if (Main.DEBUG && !(Client.validName(from))) { System.err.println("Assert error"); } // TODO: null check and fix error message
		this.from = from;
	}

	public String setContent(String content) {
		this.content = content;
		return this.content;
	}

	public void setTo(List<String> to) {
		if (Main.DEBUG && !(Client.validNames(to))) { System.err.println("Assert error"); } // TODO: null check and fix error message
		this.to = to;
	}

	public void setTo(String to) {
		List<String> list = new ArrayList<>();
		list.add(to);
		setTo(list);
	}

	public String getFrom() {
		return from;
	}

	public String getContent() {
		return content;
	}

	public List<String> getTo() {
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
			message.get(KEY_FROM) instanceof String &&
			Client.validName(Message.stringFromJson(message, KEY_FROM)) &&

			message.get(KEY_CONTENT) instanceof String &&
			Message.stringFromJson(message, KEY_CONTENT).length() > 0 &&

			message.get(KEY_TO) instanceof String[])) {return false;}

		// Correctly allows for an empty array
		for(String name : (String[]) message.get(KEY_TO)) {
			if(!Client.validName(name)) {return false;}
		}

		return true;
	}
}
