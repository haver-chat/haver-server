import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Main {
    public static final boolean DEBUG = true;
    private static final String CONFIG = "config.txt";

    public static void main(String[] args) {
        try {
            File f = new File(CONFIG);
            String host = "127.0.0.1";
            int port = 8080;
            if (f.exists() && !f.isDirectory()) {
                BufferedReader r = new BufferedReader(new FileReader(CONFIG));
                String file = "";
                String line;
                while ((line = r.readLine()) != null) file += line;
                host = file.split(":")[0];
                port = Integer.valueOf(file.split(":")[1]);
            }
            Router router = new Router(host, port);
            router.start();
            System.out.println("Hosting new server on: " + router.getAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
