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
	public final static double ACCURACY_MIN = 0d;

	public final double latitude;
	public final double longitude;
	public final double accuracy;
	public final long time; // epoch timestamp


	public Location(double latitude, double longitude, double accuracy) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.accuracy = accuracy;
        this.time = Calendar.getInstance().getTimeInMillis();
	}

	public Location(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.accuracy = 0d;
		this.time = Calendar.getInstance().getTimeInMillis();
	}

	public Location (JSONObject jsonObject) {
        this(
                Message.parseJSON(jsonObject.get(KEY_LATITUDE)),
                Message.parseJSON(jsonObject.get(KEY_LONGITUDE)),
                Message.parseJSON(jsonObject.get(KEY_ACCURACY))
        );
	}

	/**
	 * Helper method for finding the distance between two locations.
	 * Currently ignores accuracy.
	 *
	 * @param location
	 * @return The distance between two locations.
	 */
	// TODO: Actually implement this method correctly
	// This may help: http://www.movable-type.co.uk/scripts/latlong.html
	/*public double distanceBetween(Location location) {
		double distance =
                Math.sqrt(
                        Math.pow(this.latitude - location.latitude, 2) +
                        Math.pow(this.longitude - location.longitude, 2)
                );
		return distance;
	}*/

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

	/**
	 * @param message
	 * @return True if the Location is valid
	 */
	@Override
	public boolean valid(JSONObject message) {
		double latitude;
		double longitude;
		return super.valid(message) &&
			message.get(KEY_LATITUDE) instanceof Number &&
			(latitude = (Message.numberFromJson(message, KEY_LATITUDE))) >= LATITUDE_MIN &&
			latitude <= LATITUDE_MAX &&

			message.get(KEY_LONGITUDE) instanceof Number &&
			(longitude = (Message.numberFromJson(message, KEY_LONGITUDE))) >= LONGITUDE_MIN &&
			longitude <= LONGITUDE_MAX &&

			message.get(KEY_ACCURACY) instanceof Number &&
			Message.numberFromJson(message, KEY_ACCURACY) >= ACCURACY_MIN;
	}
}
