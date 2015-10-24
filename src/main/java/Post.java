import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Post extends Message {

	public final static String KEY_FROM = "from";
	public final static String KEY_CONTENT = "content";
	public final static String KEY_TO = "to";

    private String from;
    private String content;
	private List<String> to; // If not specified (empty), broadcast.

	public Post(String from, String content, List<String> to) {
		setFrom(from);
		this.content = content;
		setTo(to);
	}

	public static Post fromJSON(Client client, JSONObject jsonObject) {
        try {
            if (jsonObject.size() != 3) throw new Exception("Wrong number of keys");
            if (!(jsonObject.containsKey(KEY_CONTENT) && jsonObject.containsKey(KEY_TO))) throw new Exception("Wrong keys");
            if (!(jsonObject.get(KEY_CONTENT) instanceof String && jsonObject.get(KEY_TO) instanceof List))
                throw new Exception("Values are wrong type");
            String content = Message.stringFromJson(jsonObject, KEY_CONTENT);
            List<String> to = Message.listFromJson(jsonObject, KEY_TO);
            if (!(content.length() > 0 && validListOfNames(to))) throw new Exception("Values' content is invalid");
            return new Post(client.getName(), content, to);
        } catch(Exception e) {
            System.err.println("Post:fromJSON : " + e.getMessage());
            return null;
        }
    }

	/**
	 * @return Stringified JSON.
	 */
	@Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Message.KEY_TYPE, Message.TYPE_POST);
        jsonObject.put(KEY_FROM, from);
        jsonObject.put(KEY_CONTENT, content);
        jsonObject.put(KEY_TO, to); // NB: All recipients know all other recipients.
        return jsonObject.toJSONString();
    }

	public void setFrom(String from) {
		// if (Main.DEBUG && !(Client.validName(from))) { System.err.println("Post:setFrom() : Name not valid"); } // Name should always be valid since set by server
		this.from = from;
	}

	public String setContent(String content) {
		this.content = content;
		return this.content;
	}

	/**
	 * Enforces name uniqueness.
	 * @param to
	 */
	public void setTo(List<String> to) {
		if (Main.DEBUG && !(Client.validNames(to))) { System.err.println("Post:setTo() : Names not in list"); } // TODO: null check and fix error message
		this.to = to.stream().distinct().collect(Collectors.toList());
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

	/**
	 * @return
	 */
	public List<String> getTo() {
		return to;
	}
}
