import org.json.simple.JSONObject;

public class Post extends Message {

	public final static String KEY_CLIENT = "client";
	public final static String KEY_CONTENT = "content";

    private final Client client;
    private final String content;


    public Post(Client client, String content) {
        this.client = client;
        this.content = content;
    }

	public Post(JSONObject jsonObject) {
		this((Client) jsonObject.get(KEY_CLIENT),
			(String) jsonObject.get(KEY_CONTENT));
	}

    public String toJSON() {
        JSONObject obj = new JSONObject();
        obj.put(KEY_CLIENT, client.getId());
        obj.put(KEY_CONTENT, content);
        return obj.toJSONString();
    }
}
