import org.json.simple.JSONObject;

public class RoomInfo extends Message {

	public final static String KEY_NAME = "name";
	public final static String KEY_RADIUS = "radius";
	public final static double RADIUS_MAX = 1000d; // TODO Decide on value
	public final static double RADIUS_MIN = 20d; // TODO Decide on value

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

	/**
	 * @param message
	 * @return True if the RoomInfo is valid
	 */
	@Override
	public boolean valid(JSONObject message) {
		double radius;
		return super.valid(message) &&
			message.get(KEY_NAME) instanceof String &&
			((String) message.get(KEY_NAME)).length() > 0 &&

			message.get(KEY_RADIUS) instanceof Long &&
			(radius = ((Long) message.get(KEY_RADIUS)).doubleValue()) >= RADIUS_MIN &&
			radius <= RADIUS_MAX;
	}
}
