package fr.ycoupe.pronobike.activities;

import android.os.Bundle;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import fr.ycoupe.pronobike.App;
import fr.ycoupe.pronobike.authentication.CreateFragment;
import fr.ycoupe.pronobike.authentication.bus.out.AuthenticationSuccessEvent;
import fr.ycoupe.pronobike.authentication.bus.out.CreateSuccessEvent;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;

/**
 * Login screen activity.
 */
public class CreateActivity extends BaseActivity {
    private final static String TAG = CreateActivity.class.getSimpleName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");

        subscriptions.add(BusManager.instance().observe(CreateSuccessEvent.class, this::onCreateSuccessEvent));

        final CreateFragment createFragment = new CreateFragment();
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, createFragment).commit();
    }

    private void onCreateSuccessEvent(final CreateSuccessEvent event) {
        Logger.log(Logger.Level.DEBUG, TAG, "onCreateSuccessEvent");
        BusManager.instance().send(new AuthenticationSuccessEvent());
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
