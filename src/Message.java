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

    public String toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("user", client.getId());
        obj.put("content", content);
        return obj.toJSONString();
    }
}
