package chat.haver.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static final boolean DEBUG = true;
    public static final Map<String, String> CONFIG = new HashMap<>();
    private static final String CONFIG_FILE = "config.txt";

    public enum Environment {
        DEVELOPMENT("development"),
        TESTING("testing"),
        PRODUCTION("production");

        public final String env;

        Environment(final String env) {
            this.env = env;
        }

        @Override
        public String toString() {return env;}
    }

    private Main(){}

    private static Map<String, String> makeConfig() throws IOException {
        CONFIG.put("host", "127.0.0.1");
        CONFIG.put("port", "8080");
        CONFIG.put("env", Environment.DEVELOPMENT.toString());
        File file = new File(CONFIG_FILE);
        if (file.exists() && !file.isDirectory()) {
            BufferedReader r = new BufferedReader(new FileReader(file));
            String line;
            while ((line = r.readLine()) != null) {
                CONFIG.put(line.split(":")[0].trim(), line.split(":")[1].trim());
            }
        }
        return CONFIG;
    }

    public static void main(final String[] args) {
        try {
            makeConfig();
            Router router = new Router(CONFIG.get("host"), Integer.valueOf(CONFIG.get("port")));
            router.start();
            System.out.println("Hosting new server on: " + router.getAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
