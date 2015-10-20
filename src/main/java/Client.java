import java.util.Arrays;

public class Client {
	// Change to read in from file instead of set in code later.
	public final static String[] NAMES = {
		"BlueBattleship", "BlueDog", "BlueHat", "BlueIron", "BlueRacecar", "BlueShoe", "BlueThimble", "BlueWheelbarrow",
		"GreenBattleship", "GreenDog", "GreenHat", "GreenIron", "GreenRacecar", "GreenShoe", "GreenThimble", "GreenWheelbarrow",
		"OrangeBattleship", "OrangeDog", "OrangeHat", "OrangeIron", "OrangeRacecar", "OrangeShoe", "OrangeThimble", "OrangeWheelbarrow",
		"PurpleBattleship", "PurpleDog", "PurpleHat", "PurpleIron", "PurpleRacecar", "PurpleShoe", "PurpleThimble", "PurpleWheelbarrow",
		"RedBattleship", "RedDog", "RedHat", "RedIron", "RedRacecar", "RedShoe", "RedThimble", "RedWheelbarrow",
		"YellowBattleship", "YellowDog", "YellowHat", "YellowIron", "YellowRacecar", "YellowShoe", "YellowThimble", "YellowWheelbarrow"};

	private String name;
	private Location location;
	private final Object token;

	public Client() {
		this.token = generateToken();
	}

	//TODO Generate tokens
	private Object generateToken() {
		return null;
	}

	//TODO Token validation
	public boolean isValid() {
		return true;
	}

	public Location getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public Object getToken() {return token;}

	public void setName(String name) {
		assert(validName(name));
		this.name = name;
	}

	public void setLocation(Location location) {this.location = location;}

	/**
	 * Tests if the specified name is valid.
	 *
	 * @param name The name to validate.
	 * @return True if the name is recognised.
	 */
	public static boolean validName(String name) {
		return Arrays.binarySearch(NAMES, name) >= 0;
	}

	/**
	 * Tests if the specified names are valid.
	 *
	 * @param names The names to validate.
	 * @return True if all names are recognised.
	 */
	public static boolean validNames(String[] names) {
		for(String name : names) {
			if(!(Arrays.binarySearch(NAMES, name) >= 0)) {return false;}
		}
		return true;
	}
}
