package fr.ycoupe.pronobike.configuration;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.activities.SplashActivity;
import fr.ycoupe.pronobike.configuration.bus.out.ConfigurationSuccessEvent;
import fr.ycoupe.pronobike.configuration.bus.out.StartEvent;
import fr.ycoupe.pronobike.configuration.service.ConfigurationService;
import fr.ycoupe.pronobike.sqlite.CircuitDAO;
import fr.ycoupe.pronobike.sqlite.CompetitionDAO;
import fr.ycoupe.pronobike.sqlite.PilotDAO;
import fr.ycoupe.pronobike.sqlite.RaceDAO;
import fr.ycoupe.pronobike.sqlite.RankDAO;
import fr.ycoupe.pronobike.sqlite.TeamDAO;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;
import fr.ycoupe.pronobike.utils.PreferenceManager;
import rx.internal.util.SubscriptionList;

/**
 * Created by yanncoupe on 19/07/2016.
 */
public class SplashFragment extends Fragment {
    private final static String TAG = SplashFragment.class.getSimpleName();

    private SubscriptionList subscriptions;

    private ConfigurationService configurationService;

    @BindView(R.id.splash_version)
    TextView versionTextView;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup group, final Bundle savedInstanceState) {
        Logger.log(Logger.Level.DEBUG, TAG, "onCreateView");

        final View view = inflater.inflate(R.layout.splash_fragment, group, false);
        ButterKnife.bind(this, view);

        subscriptions = new SubscriptionList();
        subscriptions.add(BusManager.instance().observe(ConfigurationSuccessEvent.class, this::onConfigurationSuccess));

        try {
            PackageManager manager = getActivity().getPackageManager();
            PackageInfo info = manager.getPackageInfo(
                    getActivity().getPackageName(), 0);

            versionTextView.setText(String.format(getString(R.string.version), info.versionName));

        } catch (PackageManager.NameNotFoundException e){
            Logger.log(Logger.Level.WARNING, TAG, "NameNotFoundException: " + e.getMessage());
        }

        new Handler().postDelayed(() -> {
            BusManager.instance().send(new StartEvent());
        }, 3000);

        configurationService = new ConfigurationService();

        configurationService.getConfiguration(PreferenceManager.getSecurePrefs(getContext()).getLong(SplashActivity.PREFS_CONFIGURATION, 0));

        return view;
    }

    private void onConfigurationSuccess(final ConfigurationSuccessEvent event){
        Logger.log(Logger.Level.DEBUG, TAG, "onConfigurationSuccess");
        try {
            if(event.element != null){

                final JSONObject configuration = new JSONObject(event.element.toString());

                if(configuration != null){
                    final SaveDatas task = new SaveDatas();
                    task.setConfiguration(configuration);
                    task.startTask();
                }
            }

        } catch (JSONException e) {
            Logger.log(Logger.Level.WARNING, TAG, "JSONException:" + e.getMessage());
        }

    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.log(Logger.Level.DEBUG, TAG, "onDestroyView");

        subscriptions.unsubscribe();
    }

    private class SaveDatas extends fr.ycoupe.pronobike.utils.AsyncTask {

        JSONObject configuration = null;

        public void setConfiguration(JSONObject configuration) {
            this.configuration = configuration;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {

                JSONArray circuits = null;
                JSONArray competitions = null;
                JSONArray pilots = null;
                JSONArray races = null;
                JSONArray ranks = null;
                JSONArray teams = null;
                JSONArray teamsCompetitions = null;

                JSONArray circuitsDeleted = null;
                JSONArray competitionsDeleted = null;
                JSONArray pilotsDeleted = null;
                JSONArray racesDeleted = null;
                JSONArray ranksDeleted = null;
                JSONArray teamsDeleted = null;
                JSONArray teamsCompetitionsDeleted = null;

                if (configuration.has("circuits")) circuits = configuration.getJSONArray("circuits");
                if (configuration.has("competitions")) competitions = configuration.getJSONArray("competitions");
                if (configuration.has("pilots")) pilots = configuration.getJSONArray("pilots");
                if (configuration.has("races")) races = configuration.getJSONArray("races");
                if (configuration.has("ranks")) ranks = configuration.getJSONArray("ranks");
                if (configuration.has("teams")) teams = configuration.getJSONArray("teams");
                if (configuration.has("teams_competitions")) teamsCompetitions = configuration.getJSONArray("teams_competitions");

                if (configuration.has("circuits_deleted")) circuitsDeleted = configuration.getJSONArray("circuits_deleted");
                if (configuration.has("competitions_deleted")) competitionsDeleted = configuration.getJSONArray("competitions_deleted");
                if (configuration.has("pilots_deleted")) pilotsDeleted = configuration.getJSONArray("pilots_deleted");
                if (configuration.has("races_deleted")) racesDeleted = configuration.getJSONArray("races_deleted");
                if (configuration.has("ranks_deleted")) ranksDeleted = configuration.getJSONArray("ranks_deleted");
                if (configuration.has("teams_deleted")) teamsDeleted = configuration.getJSONArray("teams_deleted");
                if (configuration.has("teams_competitions_deleted")) teamsCompetitionsDeleted = configuration.getJSONArray("teams_competitions_deleted");

                if(circuits != null && circuits.length() > 0) CircuitDAO.saveCircuits(getActivity(), circuits);
                if(competitions != null && competitions.length() > 0) CompetitionDAO.saveCompetitions(getActivity(), competitions);
                if(pilots != null && pilots.length() > 0) PilotDAO.savePilots(getActivity(), pilots);
                if(races != null && races.length() > 0) RaceDAO.saveRaces(getActivity(), races);
                if(ranks != null && ranks.length() > 0) RankDAO.saveRanks(getActivity(), ranks);
                if(teams != null && teams.length() > 0) TeamDAO.saveTeams(getActivity(), teams);
                if(teamsCompetitions != null && teamsCompetitions.length() > 0) TeamDAO.saveTeamsCompetitions(getActivity(), teamsCompetitions);

                if(circuitsDeleted != null && circuitsDeleted.length() > 0) CircuitDAO.deleteCircuits(getActivity(), circuitsDeleted);
                if(competitionsDeleted != null && competitionsDeleted.length() > 0) CompetitionDAO.deleteCompetitions(getActivity(), competitionsDeleted);
                if(pilotsDeleted != null && pilotsDeleted.length() > 0) PilotDAO.deletePilots(getActivity(), pilotsDeleted);
                if(racesDeleted != null && racesDeleted.length() > 0) RaceDAO.deleteRaces(getActivity(), racesDeleted);
                if(ranksDeleted != null && ranksDeleted.length() > 0) RankDAO.deleteRanks(getActivity(), ranksDeleted);
                if(teamsDeleted != null && teamsDeleted.length() > 0) TeamDAO.deleteTeams(getActivity(), teamsDeleted);
                if(teamsCompetitionsDeleted != null && teamsCompetitionsDeleted.length() > 0) TeamDAO.deleteTeamsCompetitions(getActivity(), teamsCompetitionsDeleted);
            } catch (JSONException e) {
                Logger.log(Logger.Level.WARNING, TAG, "JSONException:" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if(configuration != null){
                try {
                    long lastupdate = configuration.has("lastupdate") ? configuration.getLong("lastupdate") : 0;

                    PreferenceManager.getSecurePrefs(getContext()).edit()
                            .putLong(SplashActivity.PREFS_CONFIGURATION, lastupdate)
                            .apply();
                } catch (JSONException e) {
                    Logger.log(Logger.Level.WARNING, TAG, "JSONException:" + e.getMessage());
                }
            }
        }
    }
}
