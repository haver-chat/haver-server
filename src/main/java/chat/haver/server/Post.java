package chat.haver.server;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Post extends Message {

    public enum Key implements JSONKey {
        FROM("from"),
        CONTENT("content"),
        TO("to");

        public final String key;

        Key(final String key) {
            this.key = key;
        }

        @Override
        public String toString() {return key;}
    }

    private String from;
    private String content;
    private List<String> to; // If not specified (empty), broadcast.

    public Post(final String from, final String content, final List<String> to) {
        setFrom(from);
        this.content = content;
        setTo(to);
    }

    @SuppressWarnings("unchecked")
    public static Post fromJSON(final Client client, final JSONObject jsonObject) {
        try {
            if (jsonObject.size() != Key.values().length + 1 - 1) throw new Exception("Wrong number of keys");
            if (!(jsonObject.containsKey(Key.CONTENT.key) && jsonObject.containsKey(Key.TO.key))) throw new Exception("Wrong keys");
            if (!(jsonObject.get(Key.CONTENT.key) instanceof String && jsonObject.get(Key.TO.key) instanceof List))
                throw new Exception("Values are wrong type");
            String content = Message.stringFromJson(jsonObject, Key.CONTENT);
            List to = Message.listFromJson(jsonObject, Key.TO);
            if (!(content.length() > 0 && validListOfNames(to))) throw new Exception("Values' content is invalid");
            return new Post(client.getName(), content, to); // This is a checked cast because validListOfNames(List) returned true.
        } catch(Exception e) {
            System.err.println("Post:fromJSON : " + e.getMessage());
            return null;
        }
    }

    /**
     * @return Stringified JSON.
     */
    @Override
    @SuppressWarnings("unchecked")
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Message.Key.TYPE.key, Type.POST.type);
        jsonObject.put(Key.FROM.key, from);
        jsonObject.put(Key.CONTENT.key, content);
        jsonObject.put(Key.TO.key, to); // NB: All recipients know all other recipients.
        return jsonObject.toJSONString();
    }

    public void setFrom(final String from) {
        // if (Main.DEBUG && !(Client.validName(from))) { System.err.println("Post:setFrom() : Name not valid"); } // Name should always be valid since set by server
        this.from = from;
    }

    public String setContent(final String content) {
        this.content = content;
        return this.content;
    }

    /**
     * Enforces name uniqueness.
     * @param to
     */
    public void setTo(final List<String> to) {
        if (Main.DEBUG && !(Client.validNames(to))) System.err.println("Post:setTo() : Names not in list"); // TODO: null check and fix error message
        this.to = to.stream().distinct().collect(Collectors.toList());
    }

    public void setTo(final String to) {
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
