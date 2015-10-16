import org.json.simple.JSONObject;

/**
 * Created by azertify on 16/10/15.
 */
public class Message {
    private final Client client;
    private final String content;

    public Message(Client client, String content) {
        this.client = client;
        this.content = content;
    }

	// TODO Decide whether or not to replace second constructor and toJSON by making Message extend JSONObject,
	// keeping the internal data structure as JSON and just providing accessor methods.
	public Message(JSONObject jsonObject) {
		this((Client) jsonObject.get("client"),
			(String) jsonObject.get("content"));
	}

    public String toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("user", client.getId());
        obj.put("content", content);
        return obj.toJSONString();
    }
}
