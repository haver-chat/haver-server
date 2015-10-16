import org.json.simple.JSONObject;

import java.util.Calendar;

/**
 * <insert description here>
 *
 * @author Edward Knight (<a href="http://www.edwardknig.ht/">website</a>, <a href="mailto:edw@rdknig.ht">email</a>)
 * @version 0.1
 * @since 1.8
 */
public class Location {
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

	public static Location fromJSON(JSONObject jsonObject) {
		return new Location(
				(double) jsonObject.get("latitude"),
				(double) jsonObject.get("longitude"),
				(double) jsonObject.get("accuracy")
		);
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getAccuracy() {
		return accuracy;
	}

	/**
	 * @return standard Java epoch time when the location data was recieved from the client
	 */
	public long getTime() {
		return time;
	}
}
