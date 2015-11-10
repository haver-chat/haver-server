import java.util.Arrays;
import java.util.Date;

public class CircularRateQueue {
	private final int rate;
	private final long[] array; // array of epoch timestamps, or -1l
	private int head = 0; // the index to insert the next element at
	private int tail = 0; // the last element index

	/**
	 * A queue of timestamps to be used in a rate limiting system.
	 * Efficient implementation using a circular array.
	 *
	 * @param capacity The maximum amount of messages that can be processed
	 * @param rate The time, in milliseconds, that the maximum amount of messages can be sent in
	 */
	public CircularRateQueue(int capacity, int rate) {
		array = new long[capacity];
		Arrays.fill(array, -1l);
		this.rate = rate;
	}

	/**
	 * Adds a timestamp to the queue.
	 *
	 * @return False if the queue is full (rate limit has been hit), true if added successfully.
	 */
	public boolean add() {
		final long now = new Date().getTime();
		if(isFull()) if (!clear(now)) return false;

		array[head] = now;
		head = (head + 1) % array.length;
		return true;
	}

	/**
	 * Checks to see if any space can be made in the array by moving expired timestamps.
	 *
	 * @param now The epoch timestamp now
	 * @return True if space was made, false otherwise
	 */
	public boolean clear(long now) {
		final long expireTime = now - rate;
		final int startIndex = (head - 1 + array.length) % array.length;
		int i = startIndex;
		do { // moving backwards through the circle from the head
			if(array[i] < expireTime) {
				remove(tail, i);
				tail = (i + 1) % array.length;
				return true;
			}
			i = (i - 1 + array.length) % array.length;
		} while(i != startIndex);

		return false;
	}

	/**
	 * Checks to see if any space can be made in the array by moving expired timestamps.
	 *
	 * @return True if space was made, false otherwise
	 */
	public boolean clear() {return clear(new Date().getTime());}

	/**
	 * Helper method to 'remove' (set to -1l) a range of elements.
	 * Takes into account the circular nature of the array.
	 *
	 * @param fromIndex the first index to remove
	 * @param toIndex the last index to remove
	 */
	private void remove(int fromIndex, int toIndex) {
		for(int i = fromIndex; i > toIndex; i = (i + 1) % array.length) {array[i] = -1l;}
	}

	/**
	 * @return true if the array is empty, false otherwise
	 */
	public boolean isEmpty() {return (head == tail) && (array[tail] == -1l);}

	/**
	 * @return true if the array is full, false otherwise
	 */
	public boolean isFull() {return (head == tail) && (array[tail] != -1l);}
}
