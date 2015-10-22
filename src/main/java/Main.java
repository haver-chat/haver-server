import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by azertify on 20/10/15.
 */
public class Main {
    public static final boolean DEBUG = true;

    public static void main(String[] args) {
        try {
            BufferedReader r = new BufferedReader(new FileReader("config.txt"));
            String file = "";
            String line;
            while((line = r.readLine()) != null) file += line;
            Router router = new Router(file.split(":")[0], Integer.valueOf(file.split(":")[1]));
            router.start();
            System.out.println("Hosting new server on: " + router.getAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
