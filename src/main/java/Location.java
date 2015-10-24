import org.json.simple.JSONObject;

import java.util.Calendar;

public class Location extends Message {

	public enum Key implements JSONKey {
		LATITUDE("latitude"),
		LONGITUDE("longitude"),
		ACCURACY("accuracy");

		public final String key;

		Key(String key) {
			this.key = key;
		}

		@Override
		public String toString() {return key;}
	}

	public enum Constraint {
		LATITUDE_MIN(-90),
		LATITUDE_MAX(90),
		LONGITUDE_MIN(-180),
		LONGITUDE_MAX(180),
		ACCURACY_MIN(0);

		public final int constraint;

		Constraint(int constraint) {
			this.constraint = constraint;
		}

		@Override
		public String toString() {return Integer.toString(constraint);}
	}

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
            if (jsonObject.size() != Key.values().length + 1) throw new Exception("Wrong number of keys");
            if (!(
                    jsonObject.containsKey(Key.LATITUDE) &&
                    jsonObject.containsKey(Key.LONGITUDE) &&
                    jsonObject.containsKey(Key.ACCURACY)
            )) throw new Exception("Wrong keys");
            if (!(
                    jsonObject.get(Key.LATITUDE) instanceof Number &&
                    jsonObject.get(Key.LONGITUDE) instanceof Number &&
                    jsonObject.get(Key.ACCURACY) instanceof Number
            )) throw new Exception("Values are wrong type");
            double latitude = Message.doubleFromJson(jsonObject, Key.LATITUDE);
            double longitude = Message.doubleFromJson(jsonObject, Key.LONGITUDE);
            int accuracy = Message.intFromJson(jsonObject, Key.ACCURACY);
            if (!(
                    latitude >= Constraint.LATITUDE_MIN.constraint &&
							latitude <= Constraint.LATITUDE_MAX.constraint &&
                    longitude >= Constraint.LONGITUDE_MIN.constraint &&
							longitude <= Constraint.LONGITUDE_MAX.constraint &&
                    accuracy >= Constraint.ACCURACY_MIN.constraint
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
