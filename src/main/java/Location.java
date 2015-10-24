import org.json.simple.JSONObject;

import java.util.Calendar;

public class Location extends Message {

	public final static String KEY_LATITUDE = "latitude";
	public final static double LATITUDE_MAX = 90d;
	public final static double LATITUDE_MIN = -90d;
	public final static String KEY_LONGITUDE = "longitude";
	public final static double LONGITUDE_MAX = 180d;
	public final static double LONGITUDE_MIN = -180d;
	public final static String KEY_ACCURACY = "accuracy";
	public final static int ACCURACY_MIN = 0;

	public final double latitude;
	public final double longitude;
	public final int accuracy;
	public final long time; // epoch timestamp

	public Location(double latitude, double longitude, int accuracy) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.accuracy = accuracy;
        this.time = Calendar.getInstance().getTimeInMillis();
	}

	public Location(double latitude, double longitude) {
		this(latitude, longitude, 0);
	}

    public static Location fromJSON(JSONObject jsonObject) {
        try {
            if (jsonObject.size() != 4) throw new Exception("Wrong number of keys");
            if (!(
                    jsonObject.containsKey(KEY_LATITUDE) &&
                    jsonObject.containsKey(KEY_LONGITUDE) &&
                    jsonObject.containsKey(KEY_ACCURACY)
            )) throw new Exception("Wrong keys");
            if (!(
                    jsonObject.get(KEY_LATITUDE) instanceof Number &&
                    jsonObject.get(KEY_LONGITUDE) instanceof Number &&
                    jsonObject.get(KEY_ACCURACY) instanceof Number
            )) throw new Exception("Values are wrong type");
            double latitude = Message.doubleFromJson(jsonObject, KEY_LATITUDE);
            double longitude = Message.doubleFromJson(jsonObject, KEY_LONGITUDE);
            int accuracy = Message.intFromJson(jsonObject, KEY_ACCURACY);
            if (!(
                    latitude >= LATITUDE_MIN && latitude <= LATITUDE_MAX &&
                    longitude >= LONGITUDE_MIN && longitude <= LONGITUDE_MAX &&
                    accuracy >= ACCURACY_MIN
            )) throw new Exception("Values' content is invalid");

            return new Location(latitude, longitude, accuracy);
        } catch(Exception e) {
            System.err.println("Location:fromJSON : " + e.getMessage());
            return null;
        }
    }

    /**
     * Finds distance in meters between 2 locations
     * Haversine formula
     * @param location
     * @return
     */
    public double distanceBetween(Location location) {
        double R = 6378.137;
        double dLat = (location.latitude - latitude) * Math.PI / 180d;
        double dLong = (location.longitude - longitude) * Math.PI / 180d;
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(latitude * Math.PI / 180d) *
                Math.cos(location.latitude * Math.PI / 180d) *
                Math.sin(dLong/2) * Math.sin(dLong/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c * 1000;
        System.out.println("Locations are " + d + " meters apart.");
        return d;

    }

	public double getLatitude() {return latitude;}

	public double getLongitude() {return longitude;}

	public double getAccuracy() {return accuracy;}

	/**
	 * @return standard Java epoch time when the location data was received from the client
	 */
	public long getTime() {return time;}
}
