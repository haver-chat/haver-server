package chat.haver.server;

import org.json.simple.JSONObject;

public class RoomInfo extends Message {

    public enum Key implements JSONKey {
        NAME("name"),
        RADIUS("radius");

        public final String key;

        Key(final String key) {
            this.key = key;
        }

        @Override
        public String toString() {return key;}
    }

    public enum Constraint {
        RADIUS_MIN(100), // TODO Decide on value.
        RADIUS_MAX(1000); // TODO Decide on value.

        public final int constraint;

        Constraint(final int constraint) {
            this.constraint = constraint;
        }

        @Override
        public String toString() {return Integer.toString(constraint);}
    }

    public final String name;
    public final double radius;

    public RoomInfo(final String name, final double radius) {
        this.name = name;
        this.radius = radius;
    }

    public static RoomInfo fromJSON(final JSONObject jsonObject) {
        try {
            if (jsonObject.size() != Key.values().length + 1) throw new Exception("Wrong number of keys");
            if (!(jsonObject.containsKey(Key.NAME.key) && jsonObject.containsKey(Key.RADIUS.key)))
                throw new Exception("Wrong keys");
            if (!(jsonObject.get(Key.NAME.key) instanceof String && jsonObject.get(Key.RADIUS.key) instanceof Number))
                throw new Exception("Values are wrong type");
            String name = Message.stringFromJson(jsonObject, Key.NAME);
            double radius = Message.doubleFromJson(jsonObject, Key.RADIUS);
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
    public static boolean validRadius(final double radius) {
        return radius >= Constraint.RADIUS_MIN.constraint &&
                radius <= Constraint.RADIUS_MAX.constraint;
    }
}
