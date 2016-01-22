package chat.haver.server;

import java.util.Date;

/**
 * Created by azertify on 22/01/2016.
 */
public class Logger {

    public static void info(String message) {
        out(format(message, "info"));
    }

    public static void warning(String message) {
        err(format(message, "warning"));
    }

    public static void sever(String message) {
        err(format(message, "sever"));
    }

    private static String format(String message, String type) {
        Date date = new Date();
        StackTraceElement ste = Thread.currentThread().getStackTrace()[3];
        String[] arr = ste.getClassName().split("\\.");
        String classDetails = arr[arr.length] + ":" + ste.getMethodName();
        if (type.equalsIgnoreCase("warning") || type.equalsIgnoreCase("sever")) {
            return "[" + date.toString() + "] " + type.toUpperCase() + " (" + classDetails + ") " + message;
        } else {
            return "[" + date.toString() + "] " + type.toUpperCase() + " " + message;
        }
    }

    private static void out(String message) {
        switch(Main.ENVIRONMENT) {
            case DEVELOPMENT:
                System.out.println(message);
                break;
            case TESTING:
                System.out.println(message);
                break;
            case PRODUCTION:
                System.out.println(message); // TODO: Sort out production logging
                break;
        }
    }

    private static void err(String message) {
        switch(Main.ENVIRONMENT) {
            case DEVELOPMENT:
                System.err.println(message);
                break;
            case TESTING:
                System.out.println(message);
                System.err.println(message);
                break;
            case PRODUCTION:
                System.out.println(message);
                System.err.println(message);
                break;
        }
    }
}
