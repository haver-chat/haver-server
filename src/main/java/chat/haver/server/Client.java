package chat.haver.server;

import java.util.Arrays;
import java.util.List;

public class Client {
	// Change to read in from file instead of set in code later.
	public static final String[] NAMES = {
			"Blue Battleship", "Blue Boot", "Blue Dog", "Blue Iron", "Blue Racecar", "Blue Thimble", "Blue Tophat", "Blue Wheelbarrow",
			"Green Battleship", "Green Boot", "Green Dog", "Green Iron", "Green Racecar", "Green Thimble", "Green Tophat", "Green Wheelbarrow",
			"Orange Battleship", "Orange Boot", "Orange Dog", "Orange Iron", "Orange Racecar", "Orange Thimble", "Orange Tophat", "Orange Wheelbarrow",
			"Purple Battleship", "Purple Boot", "Purple Dog", "Purple Iron", "Purple Racecar", "Purple Thimble", "Purple Tophat", "Purple Wheelbarrow",
			"Red Battleship", "Red Boot", "Red Dog", "Red Iron", "Red Racecar", "Red Thimble", "Red Tophat", "Red Wheelbarrow",
			"Yellow Battleship", "Yellow Boot", "Yellow Dog", "Yellow Iron", "Yellow Racecar", "Yellow Thimble", "Yellow Tophat", "Yellow Wheelbarrow"};

	private String name;
	private Location location;
	private final Object token;
	private final Queue queue;
	private static final int MESSAGES = 10;
	private static final int MILLISECONDS = 5000;

	public Client() {
		this.token = generateToken();
        this.queue = new Queue(MESSAGES, MILLISECONDS);
	}

	//TODO Generate tokens
	private Object generateToken() {
		return null;
	}

	//TODO Token validation
	public boolean isValid() {
		return true;
	}

    public boolean addToQueue() {
        return queue.add();
    }

	public Location getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public Object getToken() {return token;}

	public void setName(final String name) {
		if (Main.DEBUG && !(validName(name))) {System.err.println("Client:setName() : Invalid name");} // TODO: null check and fix error message
		this.name = name;
	}

	public void setLocation(final Location location) {this.location = location;}

	/**
	 * Tests if the specified name is valid.
	 *
	 * @param name The name to validate.
	 * @return True if the name is recognised.
	 */
	public static boolean validName(final String name) {
		return Arrays.binarySearch(NAMES, name) >= 0;
	}

	/**
	 * Tests if the specified names are valid.
	 *
	 * @param names The names to validate.
	 * @return True if all names are recognised.
	 */
	public static boolean validNames(final List<String> names) {
		for(String name : names) {
			if (!validName(name)) return false;
		}
		return true;
	}
}
