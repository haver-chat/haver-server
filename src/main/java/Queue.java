import java.util.Date;

/**
 * Created by azertify on 27/10/15.
 */
@Deprecated
public class Queue {
    private static final int MESSAGES = 10;
    private static final int SECONDS = 5;
    private long[] arr;
    private int pos;

    public Queue() {
        arr = new long[MESSAGES];
        pos = 0;
    }

    public boolean add() {
        long now = new Date().getTime();
        if (pos == MESSAGES) clear(now);
        if (pos == MESSAGES) return false;
        arr[pos++] = new Date().getTime();
        return true;
    }

    public void clear(long now) {
        long then = now - (SECONDS * 1000);
        int i;
        for(i = 0; i < MESSAGES; i++) {
            if (arr[i] > then) break;
        }
        long[] newarr = new long[MESSAGES];
        for (pos = 0; i < MESSAGES; i++) newarr[pos++] = arr[i];
        arr = newarr;
    }
}
