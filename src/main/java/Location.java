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
		this(((Long) jsonObject.get(KEY_LATITUDE)).doubleValue(),
			((Long) jsonObject.get(KEY_LONGITUDE)).doubleValue(),
			((Long) jsonObject.get(KEY_ACCURACY)).doubleValue());
	}

	/**
	 * Helper method for finding the distance between two locations.
	 * Currently ignores accuracy.
	 *
	 * @param location
	 * @return The distance between two locations.
	 */
	public double distanceBetween(Location location) {
		return Math.sqrt(Math.pow(Math.abs(this.latitude - location.getLatitude()), 2) +
			Math.pow(Math.abs(this.longitude - location.getLongitude()), 2));
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
	 * @return True if the location is valid
	 */
	@Override
	public boolean valid(JSONObject message) {
		double latitude;
		double longitude;
		return super.valid(message) &&
			message.get(KEY_LATITUDE) instanceof Long &&
			(latitude = ((Long) message.get(KEY_LATITUDE)).doubleValue()) >= LATITUDE_MIN &&
			latitude <= LATITUDE_MAX &&

			message.get(KEY_LONGITUDE) instanceof Long &&
			(longitude = ((Long) message.get(KEY_LONGITUDE)).doubleValue()) >= LONGITUDE_MIN &&
			longitude <= LONGITUDE_MAX &&

			message.get(KEY_ACCURACY) instanceof Long &&
			(((Long) message.get(KEY_ACCURACY)).doubleValue()) >= ACCURACY_MIN;
	}
}
