/**
 * Created by azertify on 20/10/15.
 */
public class Main {
    public static final boolean DEBUG = true;

    public static void main(String[] args) {
        try {
            Router router = new Router();
            router.start();
            System.out.println("Hosting new server on: " + router.getAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
