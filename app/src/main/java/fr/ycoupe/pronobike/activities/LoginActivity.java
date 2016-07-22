package fr.ycoupe.pronobike.activities;

import android.content.Intent;
import android.os.Bundle;

import fr.ycoupe.pronobike.authentication.LoginFragment;
import fr.ycoupe.pronobike.authentication.bus.out.AuthenticationSuccessEvent;
import fr.ycoupe.pronobike.authentication.bus.out.CreateEvent;
import fr.ycoupe.pronobike.authentication.bus.out.ForgetEvent;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;

/**
 * Login screen activity.
 */
public class LoginActivity extends BaseActivity {
    private final static String TAG = LoginActivity.class.getSimpleName();

    public final static String PREFS_LOGIN_EMAIL = TAG + ".PRONO_LOGIN_EMAIL";
    public final static String PREFS_LOGIN_PASSWORD = TAG + ".PRONO_LOGIN_PASSWORD";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");

        // Add info to bundle if coming after a logout
        final LoginFragment loginFragment = new LoginFragment();

        subscriptions.add(BusManager.instance().observe(AuthenticationSuccessEvent.class, this::onAuthenticationSuccess));
        subscriptions.add(BusManager.instance().observe(ForgetEvent.class, this::showForget));
        subscriptions.add(BusManager.instance().observe(CreateEvent.class, this::showCreate));

        getSupportFragmentManager().beginTransaction().add(android.R.id.content, loginFragment).commit();
    }

    private void onAuthenticationSuccess(final AuthenticationSuccessEvent event) {
        Logger.log(Logger.Level.DEBUG, TAG, "authSuccess");
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void showForget(final ForgetEvent event) {
        Logger.log(Logger.Level.DEBUG, TAG, "showForget");

        startActivity(new Intent(LoginActivity.this, ForgetActivity.class));
    }

    private void showCreate(final CreateEvent event) {
        Logger.log(Logger.Level.DEBUG, TAG, "showCreate");

        startActivity(new Intent(LoginActivity.this, CreateActivity.class));
    }

}
