package chat.haver.server;

import java.util.Arrays;

/**
 * A queue of timestamps to be used in a rate limiting system.
 * Efficient implementation using a circular array.
 *
 * Handles system time being set forward by clearing early.
 * Does not handle system time being set backwards. May have a decreased capacity (or none).
 * Does not function correctly if system time is set to before {@link Long}.MIN_VALUE milliseconds before the Unix epoch or after {@link Long}.MAX_VALUE milliseconds after the Unix epoch.
 */
public class Queue {
    /**
     * The time, in milliseconds, that the maximum amount of messages can be sent in.
     */
    private final int rate;

    /**
     * An array of timestamps.
     */
    private final long[] array;

    /**
     * The index to insert the next element at.
     */
    private int head = 0;

    /**
     * The index of the last non-null element.
     */
    private int tail = 0;

    /**
     * A value used to represent 'null' in the array.
     */
    private static final long EMPTY = Long.MIN_VALUE;


    /**
     * @param capacity the maximum amount of messages that can be processed
     * @param rate the time, in milliseconds, that the maximum amount of messages can be sent in
     */
    public Queue(final int capacity, final int rate) {
        array = new long[capacity];
        Arrays.fill(array, EMPTY);
        this.rate = rate;
    }

    /**
     * Adds a timestamp to the queue.
     *
     * @return false if the queue is full (rate limit has been hit), true if added successfully
     */
    public boolean add() {
        final long now = System.currentTimeMillis();
        if(isFull() && !clear(now)) {return false;}

        array[head] = now;
        head = increment(head);
        return true;
    }

    /**
     * Checks to see if any space can be made in the queue by removing expired timestamps.
     *
     * @param now the number of milliseconds since the Unix epoch
     * @return true if space was made, false otherwise
     */
    public boolean clear(final long now) {
        final long expireTime = now - rate;
        final int startIndex = decrement(head);
        int i = startIndex;
        do { // moving backwards through the circle from the head
            if(array[i] < expireTime) {
                remove(tail, i);
                tail = increment(i);
                return true;
            }
            i = decrement(i);
        } while(i != startIndex);

        return false;
    }

    /**
     * Checks to see if any space can be made in the queue by removing expired timestamps.
     *
     * @return true if space was made, false otherwise
     */
    public boolean clear() {
        return clear(System.currentTimeMillis());
    }

    /**
     * Helper method to 'remove' (set to EMPTY) a range of elements.
     * Takes into account the circular nature of the array.
     *
     * @param fromIndex the first index to remove
     * @param toIndex the last index to remove
     */
    private void remove(final int fromIndex, final int toIndex) {
        for(int i = fromIndex; i > toIndex; i = increment(i)) {array[i] = EMPTY;}
    }

    /**
     * Helper method to increment the index of a circular array.
     *
     * @param index the index to increment
     * @return the incremented index
     */
    private int increment(int index) {
        return (index + 1) % array.length;
    }

    /**
     * Helper method to decrement the index of a circular array.
     *
     * @param index the index to decrement
     * @return the decremented index
     */
    private int decrement(int index) {
        return (index - 1 + array.length) % array.length;
    }

    /**
     * @return true if the array is empty (all elements are EMPTY), false otherwise
     */
    public boolean isEmpty() {
        return (head == tail) && (array[tail] == EMPTY);
    }

    /**
     * @return true if the array is full (no elements are EMPTY), false otherwise
     */
    public boolean isFull() {
        return (head == tail) && (array[tail] != EMPTY);
    }
}
