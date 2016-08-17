package fr.ycoupe.pronobike.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ycoupe.pronobike.App;
import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.models.Game;
import fr.ycoupe.pronobike.pronostic.GameDetailFragment;
import fr.ycoupe.pronobike.pronostic.bus.out.BetOpenedEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.GameCloseEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.GameOpenEvent;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;

/**
 * Login screen activity.
 */
public class GameDetailActivity extends BaseActivity {
    private final static String TAG = GameDetailActivity.class.getSimpleName();

    public final static String GAME_EVENT_EXTRA = TAG + ".GAME_EVENT_EXTRA";

    @BindView(R.id.game_detail_toolbar)
    Toolbar toolbar;
    @BindView(R.id.game_detail_list)
    ImageButton list;

    private Game game;

    private GameDetailFragment gameDetailFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");

        setContentView(R.layout.game_detail_activity);
        ButterKnife.bind(this);

        final GameOpenEvent event = getIntent().getParcelableExtra(GAME_EVENT_EXTRA);
        game = event.game;

        // Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.toolbar_back);
        toolbar.setTitleTextColor(getResources().getColor(R.color.green_1));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.green_1));

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(game.getName());
        }

        addFragment();

        RxView.clicks(list).subscribe(next -> list());

        subscriptions.add(BusManager.instance().observe(BetOpenedEvent.class, this::onBetOpened));
        subscriptions.add(BusManager.instance().observe(GameCloseEvent.class, this::onClosed));

    }

    private void list(){
        final Intent intent = new Intent(this, PronosticsActivity.class);
        intent.putExtra(PronosticsActivity.PRONOSTICS_GAME_EXTRA, game);
        startActivity(intent);
    }

    private void addFragment(){
        gameDetailFragment = new GameDetailFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelable(GameDetailFragment.GAME_EXTRA, game);
        gameDetailFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.game_detail_fragment_container, gameDetailFragment).commit();
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

    // =============================================================================================
    // Bet events

    private void onBetOpened(final BetOpenedEvent event) {
        Logger.log(Logger.Level.DEBUG, TAG, "onBetOpened");

        final Intent intent = new Intent(this, BetActivity.class);
        intent.putExtra(BetActivity.BET_EVENT_EXTRA, event);
        startActivity(intent);
    }

    private void onClosed(final GameCloseEvent event) {
        Logger.log(Logger.Level.DEBUG, TAG, "onClosed");
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
