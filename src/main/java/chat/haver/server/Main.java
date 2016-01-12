package chat.haver.server;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static final boolean DEBUG = true;
    public static final Map<String, String> CONFIG = new HashMap<>();

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

    private static void makeConfig(String[] args) {
        CONFIG.put("host", "127.0.0.1");
        CONFIG.put("port", "8080");
        CONFIG.put("env", Environment.DEVELOPMENT.toString());
        if (args.length > 1) {
            CONFIG.put("host", args[0]);
            CONFIG.put("port", args[1]);
        } else if (args.length == 1) {
            CONFIG.put("host", args[0]);
        }
    }

    public static void main(final String[] args) {
        try {
            makeConfig(args);
            Router router = new Router(CONFIG.get("host"), Integer.valueOf(CONFIG.get("port")));
            router.start();
            System.out.println("Hosting new server on: " + router.getAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
