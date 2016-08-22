package fr.ycoupe.pronobike.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import fr.ycoupe.pronobike.App;
import fr.ycoupe.pronobike.configuration.SplashFragment;
import fr.ycoupe.pronobike.configuration.bus.out.StartEvent;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;

/**
 * Splash screen activity.
 */
public class SplashActivity extends BaseActivity {
    private final static String TAG = SplashActivity.class.getSimpleName();

    public final static String PREFS_CONFIGURATION = TAG + ".PRONO_CONFIGURATION";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");

        subscriptions.add(BusManager.instance().observe(StartEvent.class, this::onStart));

        final SplashFragment splashFragment = new SplashFragment();

        getSupportFragmentManager().beginTransaction().add(android.R.id.content, splashFragment).commit();
    }

    private void onStart(final StartEvent event){
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Obtain the shared Tracker instance.
        final App application = (App) getApplication();
        final Tracker tracker = application.getDefaultTracker();

        tracker.setScreenName(TAG);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
