package fr.ycoupe.pronobike.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.utils.Logger;
import fr.ycoupe.pronobike.utils.ViewUtils;
import rx.internal.util.SubscriptionList;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends AppCompatActivity {
    private final static String TAG = BaseActivity.class.getSimpleName();

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    protected SubscriptionList subscriptions;

    @Override
    protected void attachBaseContext(final Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");

        ViewUtils.setOverscrollColor(getResources(), R.color.green_1);
        subscriptions = new SubscriptionList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.log(Logger.Level.DEBUG, TAG, "onDestroy");

        subscriptions.unsubscribe();
    }
}
