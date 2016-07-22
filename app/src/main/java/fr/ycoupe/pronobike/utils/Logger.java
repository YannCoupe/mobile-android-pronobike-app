package fr.ycoupe.pronobike.utils;

import android.util.Log;

import fr.ycoupe.pronobike.BuildConfig;

/**
 * Utility class to concentrate logging information.
 */
public final class Logger {

    private static String name;
    private static boolean enableCrashlytics;

    /**
     * Level of log
     */
    public enum Level {
        // Order by importance (needed for logger filter)
        VERBOSE, DEBUG, INFO, WARNING, ERROR
    }

    private static Level LEVEL = BuildConfig.DEBUG ? Level.DEBUG : Level.WARNING;

    /**
     * Force the logger level.
     *
     * @param level The wanted level of log.
     * @see Level
     */
    public static void setLevel(final Level level) {
        LEVEL = level;
    }

    /**
     * Log a message with Android default logger.
     *
     * @param level The level of the message.
     * @param tag   The tag to use.
     * @param msg   The message to display.
     */
    public static void log(final Level level, final String tag, final String msg) {

        if (level.ordinal() >= LEVEL.ordinal()) {
            switch (level) {
                case VERBOSE:
                    Log.v(tag, msg);
                    break;
                case DEBUG:
                    Log.d(tag, msg);
                    break;
                case INFO:
                    Log.i(tag, msg);
                    break;
                case WARNING:
                    Log.w(tag, msg);
                    break;
                case ERROR:
                    Log.e(tag, msg);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Log a message with Android default logger with the stack trace of the call.
     *
     * @param level The level of the message.
     * @param tag   The tag to use.
     * @param msg   The message to display.
     */
    public static void logWithTrace(final Level level, final String tag, final String msg) {
        if (level.ordinal() >= LEVEL.ordinal()) {
            switch (level) {
                case VERBOSE:
                    Log.v(tag, msg + Log.getStackTraceString(new Exception("logWithTrace")));
                    break;
                case DEBUG:
                    Log.d(tag, msg + Log.getStackTraceString(new Exception("logWithTrace")));
                    break;
                case INFO:
                    Log.i(tag, msg + Log.getStackTraceString(new Exception("logWithTrace")));
                    break;
                case WARNING:
                    Log.w(tag, msg + Log.getStackTraceString(new Exception("logWithTrace")));
                    break;
                case ERROR:
                    Log.e(tag, msg + Log.getStackTraceString(new Exception("logWithTrace")));
                    break;
                default:
                    break;
            }
        }
    }
}
