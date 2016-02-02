package chat.haver.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

/**
 * Basic logging class
 */
public class Logger {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Logs a {@link Level}.INFO level message.
     *
     * @param message to be logged
     */
    public static void info(final String message) {
        out(format(message, Level.INFO));
    }

    /**
     * Logs a {@link Level}.WARNING level message.
     *
     * @param message to be logged
     */
    public static void warning(final String message) {
        err(format(message, Level.WARNING));
    }

    /**
     * Logs a {@link Level}.SEVERE level message.
     *
     * @param e the exception to log
     */
    public static void severe(final Exception e) {
        err(format(formatStackTrace(e), Level.SEVERE));
    }

    /**
     * Helper method to format a stack trace for logging
     *
     * @param e the exception to extract the stack trace from
     * @return a stack trace formatted identically to {@link Exception#printStackTrace()}
     */
    private static String formatStackTrace(final Exception e) {
        StringBuilder sb = new StringBuilder(e.getClass().getCanonicalName() + ": " + e.getMessage());
        for(StackTraceElement ste : e.getStackTrace()) {
            sb.append("\n\tat ").append(ste.toString());
        }
        return sb.toString();
    }

    /**
     * Formats the message to be printed to log
     *
     * @param message to be logged
     * @param level the logging level
     * @return formatted message
     */
    private static String format(final String message, final Level level) {
        return '[' + DATE_FORMAT.format(new Date()) + "] " + level + ' ' + message;
    }

    /**
     * Prints to standard out and logs.
     *
     * @param message to output
     */
    private static void out(final String message) {
        System.out.println(message);
        if(Main.ENVIRONMENT == Main.Environment.PRODUCTION) {
            log(message);
        }
    }

    /**
     * Prints to standard error and logs.
     *
     * @param message to output
     */
    private static void err(final String message) {
        System.err.println(message);
        if(Main.ENVIRONMENT == Main.Environment.PRODUCTION) {
            log(message);
        }
    }

    /**
     * Helper method to log messages to a logfile.
     *
     * @param message to be logged
     */
    private static void log(final String message) {
        // TODO implement log file
    }
}
