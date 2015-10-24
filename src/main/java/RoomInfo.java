import org.json.simple.JSONObject;

public class RoomInfo extends Message {

	public final static String KEY_NAME = "name";
	public final static String KEY_RADIUS = "radius";
	public final static double RADIUS_MAX = 1000d; // TODO Decide on value
	public final static double RADIUS_MIN = 100d; // TODO Decide on value

	public final String name;
	public final double radius;

	public RoomInfo(String name, double radius) {
		this.name = name;
		this.radius = radius;
	}

	public static RoomInfo fromJSON(JSONObject jsonObject) {
        try {
            if (jsonObject.size() != 3) throw new Exception("Wrong number of keys");
            if (!(jsonObject.containsKey(KEY_NAME) && jsonObject.containsKey(KEY_RADIUS)))
                throw new Exception("Wrong keys");
            if (!(jsonObject.get(KEY_NAME) instanceof String && jsonObject.get(KEY_RADIUS) instanceof Number))
                throw new Exception("Values are wrong type");
            String name = Message.stringFromJson(jsonObject, KEY_NAME);
            double radius = Message.doubleFromJson(jsonObject, KEY_RADIUS);
            if (!(name.length() > 0 && validRadius(radius))) throw new Exception("Values' content is invalid");

            return new RoomInfo(name, radius);
        } catch(Exception e) {
            System.err.println("RoomInfo:fromJSON : " + e.getMessage());
            return null;
        }
	}

	public String getName() {
		return name;
	}

	public double getRadius() {
		return radius;
	}

	/**
	 * Checks if the specified radius is within sensible bounds as defined by RADIUS_MIN and RADIUS_MAX.
	 *
	 * @param radius The radius to verify.
	 * @return True if the radius is within the defined bounds.
	 */
	public static boolean validRadius(double radius) {
		return radius >= RADIUS_MIN && radius <= RADIUS_MAX;
	}
}
