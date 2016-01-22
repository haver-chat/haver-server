package chat.haver.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Basic logging class
 */
public class Logger {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final StringWriter STRING_WRITER = new StringWriter();
    private static final PrintWriter PRINT_WRITER = new PrintWriter(STRING_WRITER);

    public static void printStackTrace(Exception e) {
        e.printStackTrace(PRINT_WRITER);
        for (String line : STRING_WRITER.toString().split("\\r?\\n")) {
            severe(line);
        }
    }

    public static void info(String message) {
        out(format(message, "info"));
    }

    public static void warning(String message) {
        err(format(message, "warning"));
    }

    public static void severe(String message) {
        err(format(message, "severe"));
    }

    /**
     * Formats the message to be printed to log
     * @param message To be logged
     * @param type The type of message: info, warning, or severe
     * @return Formatted message
     */
    private static String format(String message, String type) {
        Date date = new Date();
        StackTraceElement ste = Thread.currentThread().getStackTrace()[3];
        String[] arr = ste.getClassName().split("\\.");
        String classDetails = arr[arr.length - 1] + ":" + ste.getMethodName();
        String dateString = dateFormat.format(date);
        if (type.equalsIgnoreCase("info")) {
            return "[" + dateString + "] " + type.toUpperCase() + " " + message;
        } else {
            return "[" + dateString + "] " + type.toUpperCase() + " (" + classDetails + ") " + message;
        }
    }

    /**
     * Prints to standard out
     * TODO: Use ENVIRONMENT to determine whether to print
     * @param message to output
     */
    private static void out(String message) {
        System.out.println(message);
    }

    /**
     * Prints to standard error
     * TODO: Use ENVIRONMENT to determine whether to print
     * @param message to output
     */
    private static void err(String message) {
        System.err.println(message);
    }
}
