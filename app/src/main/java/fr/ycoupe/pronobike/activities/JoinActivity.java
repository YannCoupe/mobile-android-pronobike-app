package fr.ycoupe.pronobike.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.pronostic.JoinFragment;
import fr.ycoupe.pronobike.utils.Logger;

/**
 * Login screen activity.
 */
public class JoinActivity extends BaseActivity {
    private final static String TAG = JoinActivity.class.getSimpleName();

    @BindView(R.id.join_toolbar)
    Toolbar toolbar;

    private JoinFragment joinFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");

        setContentView(R.layout.join_activity);
        ButterKnife.bind(this);

        // Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.toolbar_back);
        toolbar.setTitleTextColor(getResources().getColor(R.color.green_1));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.green_1));

        addFragment();
    }

    private void addFragment(){
        joinFragment = new JoinFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.join_fragment_container, joinFragment).commit();
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
