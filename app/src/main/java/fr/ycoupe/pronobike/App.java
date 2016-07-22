package fr.ycoupe.pronobike;

import android.app.Application;

import fr.ycoupe.pronobike.sqlite.QueriesLibrary;
import fr.ycoupe.pronobike.utils.Logger;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Custom application entry point.
 */
public class App extends Application {
    private final static String TAG = App.class.getSimpleName();

    public static boolean baseIsLocked = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");

        // open database
        QueriesLibrary.open(this);

        // Calligraphy
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/ProximaNova-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }
}
