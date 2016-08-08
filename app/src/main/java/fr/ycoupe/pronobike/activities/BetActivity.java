package fr.ycoupe.pronobike.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.models.Game;
import fr.ycoupe.pronobike.pronostic.BetFragment;
import fr.ycoupe.pronobike.pronostic.GameDetailFragment;
import fr.ycoupe.pronobike.pronostic.bus.out.BetOpenedEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.GameOpenEvent;
import fr.ycoupe.pronobike.utils.Logger;

/**
 * Login screen activity.
 */
public class BetActivity extends BaseActivity {
    private final static String TAG = BetActivity.class.getSimpleName();

    public final static String BET_EVENT_EXTRA = TAG + ".BET_EVENT_EXTRA";

    private Toolbar toolbar;

    private Game game;

    private BetFragment betFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");

        setContentView(R.layout.bet_activity);

        final BetOpenedEvent event = getIntent().getParcelableExtra(BET_EVENT_EXTRA);
        game = event.game;

        // Toolbar
        toolbar = (Toolbar) findViewById(R.id.bet_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.toolbar_back);
        toolbar.setTitleTextColor(getResources().getColor(R.color.green_1));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.green_1));

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(game.getCircuitRace());
        }

        addFragment();
    }

    private void addFragment(){
        betFragment = new BetFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelable(BetFragment.GAME_EXTRA, game);
        betFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.bet_fragment_container, betFragment).commit();
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

}
