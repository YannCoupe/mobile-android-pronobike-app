package fr.ycoupe.pronobike.pronostic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.jakewharton.rxbinding.view.RxView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.models.Competition;
import fr.ycoupe.pronobike.profile.bus.out.RefreshEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.GameCreatedFailedEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.GameCreatedSuccessEvent;
import fr.ycoupe.pronobike.pronostic.service.GameService;
import fr.ycoupe.pronobike.sqlite.CompetitionDAO;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;
import fr.ycoupe.pronobike.utils.StringUtils;
import fr.ycoupe.pronobike.utils.ViewUtils;
import rx.internal.util.SubscriptionList;

/**
 * Created by yanncoupe on 11/08/2016.
 */
public class GameCreateFragment extends Fragment {
    public final static String TAG = GameCreateFragment.class.getSimpleName();

    private SubscriptionList subscriptions;

    @BindView(R.id.game_create_competition)
    TextView competition;
    @BindView(R.id.game_create_name)
    EditText name;
    @BindView(R.id.game_create_button_send)
    Button send;
    @BindView(R.id.game_create_loader)
    RelativeLayout loader;

    private Competition currentCompetition;
    private ArrayList<Competition> competitions;

    private GameService gameService;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup group, final Bundle savedInstanceState) {
        Logger.log(Logger.Level.DEBUG, TAG, "onCreateView");

        final View view = inflater.inflate(R.layout.game_create_fragment, group, false);
        ButterKnife.bind(this, view);

        gameService = new GameService();

        competitions = CompetitionDAO.competitions(getActivity());

        subscriptions = new SubscriptionList();

        RxView.clicks(competition).subscribe(next -> selectCompetition());
        RxView.clicks(send).subscribe(next -> create());

        subscriptions = new SubscriptionList();
        subscriptions.add(BusManager.instance().observe(GameCreatedSuccessEvent.class, this::onCreateGameSuccess));
        subscriptions.add(BusManager.instance().observe(GameCreatedFailedEvent.class, this::onCreateGameFailed));

        return view;
    }

    /**
     * Call api to create a game
     */
    private void create(){
        Logger.log(Logger.Level.DEBUG, TAG, "create");
        hideKeyboard();

        String error = null;

        if (StringUtils.isNullOrEmpty(name.getText()) || currentCompetition == null) {
            error = getString(R.string.renseigner_champs);
        }

        if (StringUtils.isNullOrEmpty(error)) {
            // Call API
            gameService.create(name.getText().toString(), currentCompetition);
            showLoader(true);
        } else {
            // Show error
            showError(error);
        }
    }

    private void selectCompetition(){
        Logger.log(Logger.Level.DEBUG, TAG, "selectCompetition");

        if(competitions == null || competitions.size() == 0) return;

        String [] array = new String[competitions.size()];

        for(int i = 0; i < competitions.size(); i++){
            array[i] = competitions.get(i).getName();
        }

        final String [] items = array;

        AlertDialog.Builder ad = new AlertDialog.Builder(
                getActivity());
        ad.setItems(items, (DialogInterface d, int choice) -> {
            competition.setTextColor(getResources().getColor(R.color.green_1));
            competition.setText(items[choice]);

            currentCompetition = competitions.get(choice);
        });
        ad.show();

    }

    private void onCreateGameSuccess(final GameCreatedSuccessEvent event){
        Logger.log(Logger.Level.DEBUG, TAG, "onCreateGameSuccess");
        showLoader(false);
        try {
            final JsonElement jsonElement = event.status;
            if (jsonElement != null) {
                final JSONObject delete = new JSONObject(jsonElement.toString());
                if (delete != null) {
                    if (delete.has("status")) {
                        int status = delete.getInt("status");
                        if (status == 0) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(getString(R.string.creer_partie))
                                    .setMessage(String.format(getString(R.string.felicitation_creation_partie), delete.getString("token")))
                                    .setNeutralButton(android.R.string.ok, (DialogInterface dialog, int which) -> {
                                        dialog.cancel();
                                        BusManager.instance().send(new RefreshEvent());
                                        getActivity().finish();
                                    }).show();
                            return;
                        }
                    }
                } else {
                    showError(getString(R.string.erreur));
                }
            }
        } catch (JSONException e){
            Logger.log(Logger.Level.WARNING, TAG, "JSONException: " + e);
        }

    }

    private void onCreateGameFailed(final GameCreatedFailedEvent event){
        Logger.log(Logger.Level.DEBUG, TAG, "onCreateGameFailed");
        showLoader(false);
        showError(getString(R.string.erreur));
    }

    private void showError(final String error){
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.creer_partie))
                .setMessage(error)
                .setNeutralButton(android.R.string.ok, (DialogInterface dialog, int which) -> {
                    dialog.cancel();
                }).show();
    }

    private void showLoader(final boolean show){
        loader.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    // =============================================================================================
    // Keyboard

    private void hideKeyboard(){
        ViewUtils.closeKeyboard(getActivity(), name.getWindowToken());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.log(Logger.Level.DEBUG, TAG, "onDestroyView");

        subscriptions.unsubscribe();
    }
}
