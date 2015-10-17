import org.json.simple.JSONObject;

import java.util.Calendar;

public class Location extends Message {

	public final static String KEY_LATITUDE = "latitude";
	public final static String KEY_LONGITUDE = "longitude";
	public final static String KEY_ACCURACY = "accuracy";

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
		// TODO Check if this errors
		this((double) jsonObject.get(KEY_LATITUDE),
			(double) jsonObject.get(KEY_LONGITUDE),
			(double) jsonObject.get(KEY_ACCURACY));
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
}
