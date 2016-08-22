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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.jakewharton.rxbinding.view.RxView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.models.Game;
import fr.ycoupe.pronobike.models.Pilot;
import fr.ycoupe.pronobike.models.Pronostic;
import fr.ycoupe.pronobike.profile.bus.out.RefreshEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.BetFailedEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.BetSuccessEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.GameCloseEvent;
import fr.ycoupe.pronobike.pronostic.service.GameService;
import fr.ycoupe.pronobike.sqlite.GameDAO;
import fr.ycoupe.pronobike.sqlite.PilotDAO;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;
import rx.internal.util.SubscriptionList;

/**
 * Created by yanncoupe on 18/07/2016.
 */
public class BetFragment extends Fragment {
    public final static String TAG = BetFragment.class.getSimpleName();

    public final static String GAME_EXTRA = TAG + ".GAME_EXTRA";

    @BindView(R.id.bet_button_send)
    Button send;
    @BindView(R.id.bet_rank_first)
    TextView first;
    @BindView(R.id.bet_rank_second)
    TextView second;
    @BindView(R.id.bet_rank_third)
    TextView third;
    @BindView(R.id.bet_loader)
    RelativeLayout loader;

    private SubscriptionList subscriptions;

    private Game game;
    private Pronostic pronostic;
    private ArrayList<Pilot> pilots;
    private Pilot firstPilot, secondPilot, thirdPilot;

    private GameService gameService;

    private boolean isBlocked = false;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup group, final Bundle savedInstanceState) {
        Logger.log(Logger.Level.DEBUG, TAG, "onCreateView");

        final View view = inflater.inflate(R.layout.bet_fragment, group, false);
        ButterKnife.bind(this, view);

        gameService = new GameService();

        game = getArguments().getParcelable(GAME_EXTRA);
        this.pilots = PilotDAO.pilotsWithIdGame(getActivity(), game.getIdGame());
        this.pronostic = GameDAO.pronosticWithIdGame(getActivity(), game.getIdGame(), game.getIdRace());

        RxView.clicks(first).subscribe(next -> pilot(1));
        RxView.clicks(second).subscribe(next -> pilot(2));
        RxView.clicks(third).subscribe(next -> pilot(3));
        RxView.clicks(send).subscribe(next -> send());

        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date gameDate = null;

        try {
            gameDate = dateFormatter.parse(game.getDateRace());
        } catch (ParseException e){
            Logger.log(Logger.Level.WARNING, TAG, "ParseException: " + e.getMessage());
        }

        if(gameDate != null && gameDate.getTime() < System.currentTimeMillis()){
            send.setVisibility(View.GONE);
            isBlocked = true;
        }

        if(pronostic != null){
            this.firstPilot = PilotDAO.pilotWithId(getActivity(), pronostic.getFirst());
            this.secondPilot = PilotDAO.pilotWithId(getActivity(), pronostic.getSecond());
            this.thirdPilot = PilotDAO.pilotWithId(getActivity(), pronostic.getThird());
            selectPilot(1, firstPilot);
            selectPilot(2, secondPilot);
            selectPilot(3, thirdPilot);
        }

        subscriptions = new SubscriptionList();
        subscriptions.add(BusManager.instance().observe(BetSuccessEvent.class, this::onBetSuccess));
        subscriptions.add(BusManager.instance().observe(BetFailedEvent.class, this::onBetFailed));

        return view;
    }

    /**
     * Show pilot selection
     * @param index the textfield number
     */
    private void pilot(final int index){
        if(isBlocked || (pilots == null || pilots.size() == 0)) return;
        String [] array = new String[pilots.size()];
        for(int i = 0; i < pilots.size(); i++){
            array[i] = pilots.get(i).getNumber() + " " + pilots.get(i).getFirstname() + " " + pilots.get(i).getLastname();
        }
        final String [] items = array;
        AlertDialog.Builder ad = new AlertDialog.Builder(
                getActivity());
        ad.setItems(items,
                (final DialogInterface d, final
                int choice) -> {
                    selectPilot(index, pilots.get(choice));
                });
        ad.show();
    }

    /**
     * Write pilot full name at the good textfield
     * @param index is the textfield number
     * @param pilot the {@link Pilot} selected
     */
    private void selectPilot(int index, Pilot pilot){
        TextView tv = null;
        switch (index){
            case 1:
                tv = first;
                this.firstPilot = pilot;
                break;
            case 2:
                tv = second;
                this.secondPilot = pilot;
                break;
            case 3:
                tv = third;
                this.thirdPilot = pilot;
                break;
        }
        tv.setTextColor(getResources().getColor(R.color.green_1));
        tv.setText(pilot.getNumber() + " " + pilot.getFirstname() + " " + pilot.getLastname());
    }

    /**
     * Send pronostic to Api
     */
    private void send(){
        if(firstPilot == null || secondPilot == null || thirdPilot == null){
            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.pronostic))
                    .setMessage(getString(R.string.renseigner_podium))
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();

            return;
        }
        if(pronostic == null){
            pronostic = new Pronostic();
            pronostic.setGameId(game.getIdGame());
            pronostic.setRaceId(game.getIdRace());
        }
        pronostic.setFirst(firstPilot.getIdPilot());
        pronostic.setSecond(secondPilot.getIdPilot());
        pronostic.setThird(thirdPilot.getIdPilot());

        gameService.bet(pronostic);
        showLoader(true);
    }

    private void onBetSuccess(final BetSuccessEvent event){
        Logger.log(Logger.Level.DEBUG, TAG, "onBetSuccess");
        showLoader(false);
        try {
            final JsonElement jsonElement = event.status;
            if (jsonElement != null) {
                final JSONObject prono = new JSONObject(jsonElement.toString());
                if (prono != null) {
                    if (prono.has("status")) {
                        int status = prono.getInt("status");
                        if (status == 0) {
                            showMessage(String.format(getString(R.string.felicitation_podium), game.getCircuitRace()), true);
                            return;
                        } else if (status == 1) {
                            showMessage(String.format(getString(R.string.course_commencee), game.getCircuitRace()), true);
                            return;
                        } else if (status == 2) {
                            showMessage(String.format(getString(R.string.partie_supprimee), game.getName()), true);
                            return;
                        }
                    }
                } else {
                    showMessage(getString(R.string.erreur), false);
                }
            }
        } catch (JSONException e){
            Logger.log(Logger.Level.WARNING, TAG, "JSONException: " + e);
        }
    }

    private void onBetFailed(final BetFailedEvent event){
        Logger.log(Logger.Level.DEBUG, TAG, "onBetFailed");
        showLoader(false);
        showMessage(getString(R.string.erreur), false);
    }

    // =============================================================================================
    // Error

    private void showMessage(final String error, final boolean refresh){
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.pronostic))
                .setMessage(error)
                .setNeutralButton(android.R.string.ok, (DialogInterface dialog, int which) -> {
                    dialog.cancel();
                    if(refresh){
                        BusManager.instance().send(new RefreshEvent());
                        BusManager.instance().send(new GameCloseEvent());
                        getActivity().finish();
                    }
                }).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.log(Logger.Level.DEBUG, TAG, "onDestroyView");

        subscriptions.unsubscribe();
    }

    private void showLoader(final boolean show){
        loader.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }
}
