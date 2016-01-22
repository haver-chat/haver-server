package chat.haver.server;

import java.net.UnknownHostException;

public class Main {
    @Deprecated
    public static final boolean DEBUG = true; // To be removed and replaced with Environment enum
    public static final Environment ENVIRONMENT = Environment.DEVELOPMENT;

    private static String host = "127.0.0.1";
    private static int port = 8080;

    public enum Environment {
        DEVELOPMENT("development"),
        TESTING("testing"),
        PRODUCTION("production");

        public final String env;

        Environment(final String env) {
            this.env = env;
        }

        @Override
        public String toString() {
            return env;
        }
    }

    /**
     * Empty private constructor to prevent creation of Main.
     */
    private Main() {
    }

    /**
     * Parses command line arguments then creates and runs a {@link Router server}.
     *
     * @param args Command line arguments.
     */
    public static void main(final String[] args) {
        parseArgs(args);
        try {
            Router router = new Router(host, port);
            router.start();
            Logger.info("Hosting new server on: " + router.getAddress());
        } catch (UnknownHostException e) {
            Logger.sever("Invalid host: " + host + ':' + port);
        }
    }

    /**
     * Helper method to parse command line arguments.
     * Expected: [host] [port]
     * Unexpected arguments are ignored.
     *
     * @param args The command line arguments passed to {@link #main(String[]) main}.
     */
    private static void parseArgs(final String[] args) {
        if(args.length > 0) {
            host = args[0];
            if(args.length > 1) {
                port = Integer.parseInt(args[1]);
            }
        }
    }
}
