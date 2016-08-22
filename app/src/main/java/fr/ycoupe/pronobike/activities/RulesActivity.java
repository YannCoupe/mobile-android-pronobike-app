package fr.ycoupe.pronobike.activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ycoupe.pronobike.App;
import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.utils.Logger;
import fr.ycoupe.pronobike.utils.StringUtils;
import fr.ycoupe.pronobike.utils.ViewUtils;

/**
 * Licences screen activity.
 */
public class RulesActivity extends BaseActivity {
    private final static String TAG = RulesActivity.class.getSimpleName();

    @BindView(R.id.rules_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");

        setContentView(R.layout.rules_activity);
        ButterKnife.bind(this);
        ViewUtils.setOverscrollColor(getResources(), R.color.green_1);

        // Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.toolbar_back);
        toolbar.setTitleTextColor(getResources().getColor(R.color.green_1));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.green_1));

        loadRules();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.log(Logger.Level.DEBUG, TAG, "onDestroy");
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadRules() {
        Logger.log(Logger.Level.DEBUG, TAG, "loadRules");

        final String raw = readRulesFile();
        final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
        final RulesData data = gson.fromJson(raw, RulesData.class);

        for (final RulesData.Rule r : data.rules) {
            addRule(r);
        }
    }

    private String readRulesFile() {
        Logger.log(Logger.Level.DEBUG, TAG, "readLicenseFile");

        try {
            final InputStream is = getResources().openRawResource(R.raw.rules);
            final BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF8"));

            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (final Resources.NotFoundException | IOException e) {
            Logger.log(Logger.Level.ERROR, TAG, e.getMessage() == null ? "Unknown error" : e.getMessage());
        }

        return StringUtils.EMPTY;
    }

    private void addRule(final RulesData.Rule rule) {
        Logger.log(Logger.Level.DEBUG, TAG, "addRule");

        // Title
        final View item = View.inflate(this, R.layout.rule_item, null);
        ((TextView) item.findViewById(R.id.rule_item_title)).setText(rule.title);

        final TextView tv = (TextView) item.findViewById(R.id.rule_item_text);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setText(rule.text);

        ((LinearLayout) findViewById(R.id.rules_container)).addView(item);
    }

    public class RulesData {
        public Rule[] rules;

        public class Rule {
            public String title;
            public String text;
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
