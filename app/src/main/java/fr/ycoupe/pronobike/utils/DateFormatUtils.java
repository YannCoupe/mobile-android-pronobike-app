package fr.ycoupe.pronobike.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class gathering date format functions.
 */
public class DateFormatUtils {

    /**
     * Format of date
     */
    public enum DateFormat {
        DATE_TIME_FORMAT_ISO_8601("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

        private String format;

        DateFormat(final String format) {
            this.format = format;
        }

        /**
         * Retrieve the format value
         *
         * @return the format value
         */
        public String getFormat() {
            return this.format;
        }
    }

    /**
     * Retrieve a date formatted
     *
     * @param date   the date to format
     * @param format the format
     * @return the formatted date
     */
    public static String format(final Date date, final DateFormat format) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format.getFormat());
        return simpleDateFormat.format(date);
    }
}
