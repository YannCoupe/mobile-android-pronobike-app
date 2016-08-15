package fr.ycoupe.pronobike.pronostic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.models.Game;
import fr.ycoupe.pronobike.profile.bus.out.RefreshEvent;
import fr.ycoupe.pronobike.pronostic.adapter.RankListAdapter;
import fr.ycoupe.pronobike.pronostic.bus.out.BetOpenedEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.GameDeletedFailedEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.GameDeletedSuccessEvent;
import fr.ycoupe.pronobike.pronostic.service.GameService;
import fr.ycoupe.pronobike.sqlite.GameDAO;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;
import rx.internal.util.SubscriptionList;

/**
 * Created by yanncoupe on 18/07/2016.
 */
public class GameDetailFragment extends Fragment {
    public final static String TAG = GameDetailFragment.class.getSimpleName();

    public final static String GAME_EXTRA = TAG + ".GAME_EXTRA";

    private SubscriptionList subscriptions;

    @BindView(R.id.game_detail_button_bet)
    Button bet;
    @BindView(R.id.game_detail_button_delete)
    Button delete;
    @BindView(R.id.game_detail_button_divider)
    View divider;
    @BindView(R.id.game_detail_title)
    TextView title;
    @BindView(R.id.game_detail_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.game_detail_loader)
    RelativeLayout loader;

    private RankListAdapter rankListAdapter;

    private GameService gameService;

    private Game game;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup group, final Bundle savedInstanceState) {
        Logger.log(Logger.Level.DEBUG, TAG, "onCreateView");

        final View view = inflater.inflate(R.layout.game_detail_fragment, group, false);
        ButterKnife.bind(this, view);

        gameService = new GameService();

        game = getArguments().getParcelable(GAME_EXTRA);

        final boolean isAdmin = game.getAdmin() == 1;
        divider.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        delete.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

        title.setText(String.format(getResources().getString(R.string.token_partie), game.getToken()));

        rankListAdapter = new RankListAdapter(getActivity());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        rankListAdapter.setRanks(GameDAO.gameWithId(getActivity(), game.getIdGame()));

        recyclerView.setAdapter(rankListAdapter);

        RxView.clicks(bet).subscribe(next -> bet());
        RxView.clicks(delete).subscribe(next -> delete());

        subscriptions = new SubscriptionList();
        subscriptions.add(BusManager.instance().observe(GameDeletedSuccessEvent.class, this::onGameDeletedSuccess));
        subscriptions.add(BusManager.instance().observe(GameDeletedFailedEvent.class, this::onGameDeletedFailed));

        return view;
    }

    private void bet(){
        final BetOpenedEvent event = new BetOpenedEvent();
        event.game = game;
        BusManager.instance().send(event);
    }

    private void delete(){
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.suppression_partie))
                .setMessage(String.format(getString(R.string.etes_vous_supprimer_partie), game.getName()))
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (final DialogInterface dialog, final int which) -> {
                    showLoader(true);
                    gameService.deleteGame(game.getIdGame());
                }).show();
    }

    private void onGameDeletedSuccess(final GameDeletedSuccessEvent event){
        Logger.log(Logger.Level.DEBUG, TAG, "onGameDeletedSuccess");
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
                                    .setTitle(getString(R.string.suppression_partie))
                                    .setMessage(String.format(getString(R.string.vous_venez_supprimer_partie), game.getName()))
                                    .setNeutralButton(android.R.string.ok, (DialogInterface dialog, int which) -> {
                                            dialog.cancel();
                                            BusManager.instance().send(new RefreshEvent());
                                            getActivity().finish();
                                    }).show();
                            return;
                        }
                    }
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.suppression_partie))
                            .setMessage(getString(R.string.erreur))
                            .setNeutralButton(android.R.string.ok, (DialogInterface dialog, int which) -> {
                                    dialog.cancel();
                            }).show();
                }
            }
        } catch (JSONException e){
            Logger.log(Logger.Level.WARNING, TAG, "JSONException: " + e);
        }

    }

    private void onGameDeletedFailed(final GameDeletedFailedEvent event){
        Logger.log(Logger.Level.DEBUG, TAG, "onGameDeletedSuccess");
        showLoader(false);
        showError(getString(R.string.erreur));
    }

    private void showError(final String error){
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.suppression_partie))
                .setMessage(error)
                .setNeutralButton(android.R.string.ok, (DialogInterface dialog, int which) -> {
                    dialog.cancel();
                }).show();
    }

    private void showLoader(final boolean show){
        loader.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.log(Logger.Level.DEBUG, TAG, "onDestroyView");

        subscriptions.unsubscribe();
    }
}
