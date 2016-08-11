package fr.ycoupe.pronobike.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.pronostic.GameCreateFragment;
import fr.ycoupe.pronobike.utils.Logger;

/**
 * Login screen activity.
 */
public class GameCreateActivity extends BaseActivity {
    private final static String TAG = GameCreateActivity.class.getSimpleName();

    @BindView(R.id.game_create_toolbar)
    Toolbar toolbar;

    private GameCreateFragment gameCreateFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");

        setContentView(R.layout.game_create_activity);
        ButterKnife.bind(this);

        // Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.toolbar_back);
        toolbar.setTitleTextColor(getResources().getColor(R.color.green_1));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.green_1));

        addFragment();
    }

    private void addFragment(){
        gameCreateFragment = new GameCreateFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.game_create_fragment_container, gameCreateFragment).commit();
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
