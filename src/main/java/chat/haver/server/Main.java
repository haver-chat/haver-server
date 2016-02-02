package chat.haver.server;

public class Main {
    public static final Environment ENVIRONMENT = Environment.DEVELOPMENT;

    private static String host = "127.0.0.1";
    private static int port = 8080;

    public enum Environment {PRODUCTION, DEVELOPMENT}

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
        try {
            parseArgs(args);
            new Router(host, port).start();
            Logger.info("Hosting new server on: " + host + ':' + port);
        } catch (Exception e) {
            Logger.severe(e);
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
