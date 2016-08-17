package fr.ycoupe.pronobike.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ycoupe.pronobike.App;
import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.models.Game;
import fr.ycoupe.pronobike.pronostic.PronosticsFragment;
import fr.ycoupe.pronobike.utils.Logger;

/**
 * Login screen activity.
 */
public class PronosticsActivity extends BaseActivity {
    private final static String TAG = PronosticsActivity.class.getSimpleName();

    public final static String PRONOSTICS_GAME_EXTRA = TAG + ".PRONOSTICS_GAME_EXTRA";

    @BindView(R.id.pronostics_toolbar)
    Toolbar toolbar;

    private PronosticsFragment pronosticsFragment;

    private Game game;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");

        setContentView(R.layout.pronostics_activity);
        ButterKnife.bind(this);

        game = getIntent().getParcelableExtra(PRONOSTICS_GAME_EXTRA);

        // Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.toolbar_back);
        toolbar.setTitleTextColor(getResources().getColor(R.color.green_1));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.green_1));

        addFragment();
    }

    private void addFragment(){
        pronosticsFragment = new PronosticsFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelable(PronosticsFragment.GAME_EXTRA, game);
        pronosticsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.pronostics_fragment_container, pronosticsFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        Logger.log(Logger.Level.DEBUG, TAG, "onOptionsItemSelected");

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
