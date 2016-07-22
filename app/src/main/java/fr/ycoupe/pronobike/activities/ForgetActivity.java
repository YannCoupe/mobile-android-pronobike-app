package fr.ycoupe.pronobike.activities;

import android.os.Bundle;

import fr.ycoupe.pronobike.authentication.ForgetFragment;
import fr.ycoupe.pronobike.authentication.bus.out.ForgetSuccessEvent;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;

/**
 * Login screen activity.
 */
public class ForgetActivity extends BaseActivity {
    private final static String TAG = ForgetActivity.class.getSimpleName();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");

        // Add info to bundle if coming after a logout
        final ForgetFragment forgetFragment = new ForgetFragment();

        subscriptions.add(BusManager.instance().observe(ForgetSuccessEvent.class, this::onForgetSuccessEvent));

        getSupportFragmentManager().beginTransaction().add(android.R.id.content, forgetFragment).commit();
    }

    private void onForgetSuccessEvent(final ForgetSuccessEvent event) {
        Logger.log(Logger.Level.DEBUG, TAG, "onForgetSuccessEvent");
        finish();
    }

}
