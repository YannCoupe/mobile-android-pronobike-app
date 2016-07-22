package fr.ycoupe.pronobike.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.securepreferences.SecurePreferences;

/**
 * Utility singleton for app preferences centralization
 */
public class PreferenceManager {
    private static SharedPreferences securePrefs;

    /**
     * Retrieve a "secured" version of preferences where keys and values are obfuscated.
     *
     * @param c A {@link Context}
     * @return Secured {@link SharedPreferences}
     */
    public static SharedPreferences getSecurePrefs(final Context c) {
        if (securePrefs == null) {
            securePrefs = new SecurePreferences(c.getApplicationContext(), PreferenceManager.class.getName(), null);
        }
        return securePrefs;
    }

    /**
     * Retrieve default app preferences.
     *
     * @param c A {@link Context}
     * @return Default {@link SharedPreferences}
     */
    public static SharedPreferences getPrefs(final Context c) {
        return android.preference.PreferenceManager.getDefaultSharedPreferences(c);
    }
}
