package fr.ycoupe.pronobike.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class gathering string functions.
 */
public final class StringUtils {
    /**
     * Constant for empty string.
     */
    public final static String EMPTY = "";

    /**
     * Constant for space string.
     */
    public final static String SPACE = " ";

    /**
     * Constant for return string.
     */
    public final static String RETURN = "\n";

    /**
     * Constant for zero-width space string.
     */
    public final static String ZERO_WIDTH_SPACE = "\u200B";

    private StringUtils() {
    }

    /**
     * Get all indexes of the given character in the given string.
     *
     * @param c The char to look for.
     * @param s The string to look into.
     * @return A list filled with indexes of the given char into the given string.
     */
    public static List<Integer> getCharIndexes(final char c, final String s) {
        final List<Integer> result = new ArrayList<>();

        int index = s.indexOf(c);
        while (index >= 0) {
            result.add(index);
            index = s.indexOf(c, index + 1);
        }
        return result;
    }

    /**
     * Check if the two given Strings are equal.
     *
     * @param a A string.
     * @param b A string.
     * @return {@code true} if the two given String are equal.
     */
    public static boolean equals(final String a, final String b) {
        return a == null ? b == null : a.equals(b);
    }

    /**
     * Check if a string is {@code null} or empty.
     *
     * @param s A string.
     * @return {@code true} if the given string is {@code null} or empty.
     */
    public static boolean isNullOrEmpty(final String s) {
        return s == null || s.isEmpty() || s.trim().isEmpty();
    }

    /**
     * Check if a CharSequence is {@code null} or empty.
     *
     * @param cs A CharSequence.
     * @return {@code true} if the given CharSequence is {@code null} or empty.
     */
    public static boolean isNullOrEmpty(final CharSequence cs) {
        return cs == null || cs.toString().isEmpty() || cs.toString().trim().isEmpty();
    }

    /**
     * Check if a character represents a number
     *
     * @param character A character.
     * @return {@code true} if the given char is a number.
     */
    public static boolean isNumber(final char character) {
        return character >= '0' && character <= '9';
    }

    /**
     * Check if a character is a letter.
     *
     * @param character A character.
     * @param lowerCase {@code true} to search for lower case letter, {@code false} for upper case.
     * @return {@code true} if the given char is a letter in the given case.
     */
    public static boolean isLetter(final char character, final boolean lowerCase) {
        if (lowerCase) {
            return character >= 'a' && character <= 'z';
        } else {
            return character >= 'A' && character <= 'Z';
        }
    }

    /**
     * Check if a character is a letter or number.
     *
     * @param character A character.
     * @return {@code true} if the given char is a letter (any case) or number.
     */
    public static boolean isAlphanumeric(final char character) {
        return isNumber(character) || isLetter(character, true) || isLetter(character, false);
    }

    /**
     * Capitalize the first letter of the given String if possible.
     *
     * @param s A String.
     * @return A capitalized String of #EMPTY if s is {@code null} or empty.
     */
    public static String capitalize(final String s) {
        if (isNullOrEmpty(s)) {
            return StringUtils.EMPTY;
        }

        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /*
     * Check if email adresse is well formated
     */
    public static boolean isValidEmailAddress(String emailAddress) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
    }

    public static final String md5(String s) throws NoSuchAlgorithmException {
        char hexdigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        byte[] strTemp = s.getBytes();
        MessageDigest mdTemp = MessageDigest.getInstance("MD5");
        mdTemp.update(strTemp);
        byte[] md = mdTemp.digest();
        int j = md.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = md[i];
            str[k++] = hexdigits[byte0 >>> 4 & 0xf];
            str[k++] = hexdigits[byte0 & 0xf];
        }
        return new String(str);
    }
}
