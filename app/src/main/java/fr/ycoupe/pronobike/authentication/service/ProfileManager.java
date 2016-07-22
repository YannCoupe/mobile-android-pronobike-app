package fr.ycoupe.pronobike.authentication.service;

import fr.ycoupe.pronobike.models.User;
import fr.ycoupe.pronobike.utils.Logger;

/**
 * Utility singleton for user profile
 */
public class ProfileManager {
    private final static String TAG = ProfileManager.class.getSimpleName();

    private static ProfileManager instance;
    public User profile;

    private ProfileManager() {
    }

    public static void create(final User p) {
        Logger.log(Logger.Level.DEBUG, TAG, "create");

        instance = new ProfileManager();
        instance().profile = p;
    }

    public static ProfileManager instance() {
        return instance;
    }

    public static void reset(){
        instance = null;
    }
}
