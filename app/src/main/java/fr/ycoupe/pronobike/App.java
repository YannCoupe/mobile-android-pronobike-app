package fr.ycoupe.pronobike;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import fr.ycoupe.pronobike.sqlite.QueriesLibrary;
import fr.ycoupe.pronobike.utils.Logger;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Custom application entry point.
 */
public class App extends Application {
    private final static String TAG = App.class.getSimpleName();

    private Tracker tracker;

    public static boolean baseIsLocked = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");

        // Tracking


        // open database
        QueriesLibrary.open(this);

        // Calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/ProximaNova-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(R.xml.global_tracker);
        }
        return tracker;
    }
}
