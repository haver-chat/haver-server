import org.json.simple.JSONObject;

public class RoomInfo extends Message {

	private final static String KEY_NAME = "name";
	private final static String KEY_RADIUS = "radius";

	public final String name;
	public final double radius;


	public RoomInfo(String name, double radius) {
		this.name = name;
		this.radius = radius;
	}

	public RoomInfo(JSONObject jsonObject) {
		// TODO Check if this errors
		this((String) jsonObject.get(KEY_NAME),
			((Long) jsonObject.get(KEY_RADIUS)).doubleValue());
	}

	public String getName() {
		return name;
	}

	public double getRadius() {
		return radius;
	}
}
