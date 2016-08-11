package fr.ycoupe.pronobike.pronostic;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.authentication.service.ProfileManager;
import fr.ycoupe.pronobike.authentication.service.ProfileService;
import fr.ycoupe.pronobike.models.Game;
import fr.ycoupe.pronobike.models.RankGame;
import fr.ycoupe.pronobike.models.User;
import fr.ycoupe.pronobike.profile.bus.out.RefreshEvent;
import fr.ycoupe.pronobike.pronostic.adapter.GameListAdapter;
import fr.ycoupe.pronobike.pronostic.adapter.GameRecyclerView;
import fr.ycoupe.pronobike.pronostic.adapter.RankListAdapter;
import fr.ycoupe.pronobike.pronostic.bus.out.GameDeletedFailedEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.GameDeletedSuccessEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.RankFailedEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.RankSuccessEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.UserRequestFailedEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.UserRequestSuccessEvent;
import fr.ycoupe.pronobike.pronostic.service.GameService;
import fr.ycoupe.pronobike.sqlite.GameDAO;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;
import rx.internal.util.SubscriptionList;

/**
 * Created by yanncoupe on 18/07/2016.
 */
public class RankFragment extends Fragment {
    public final static String TAG = RankFragment.class.getSimpleName();

    private SubscriptionList subscriptions;

    private GameService gameService;

    @BindView(R.id.rank_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.rank_loader)
    ProgressBar loader;
    @BindView(R.id.rank_list)
    RecyclerView recyclerView;

    private RankListAdapter rankListAdapter;

    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup group, final Bundle savedInstanceState) {
        Logger.log(Logger.Level.DEBUG, TAG, "onCreateView");

        final View view = inflater.inflate(R.layout.rank_fragment, group, false);

        ButterKnife.bind(this, view);

        gameService = new GameService();

        // Set color if < Lollipop
        if (Build.VERSION.SDK_INT < 21) {
            // Call some material design APIs here
            loader.getIndeterminateDrawable().setColorFilter(0xFF01B3A5, android.graphics.PorterDuff.Mode.SRC_ATOP);
        }

        // color progress bar
        refreshLayout.setColorSchemeResources(
                R.color.green_1);

        // action when you pull
        refreshLayout.setOnRefreshListener(() -> reloadDatas());

        rankListAdapter = new RankListAdapter(getActivity());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        subscriptions = new SubscriptionList();
        subscriptions.add(BusManager.instance().observe(RefreshEvent.class, this::refresh));
        subscriptions.add(BusManager.instance().observe(RankSuccessEvent.class, this::onRankSuccess));
        subscriptions.add(BusManager.instance().observe(RankFailedEvent.class, this::onRankFailed));

        reloadDatas();

        return view;
    }

    private void refresh(final RefreshEvent event){
        reloadDatas();
    }

    /*
     * Start get datas
     */
    private void reloadDatas(){
        Logger.log(Logger.Level.DEBUG, TAG, "reloadDatas");

        gameService.rank(GameDAO.gamesWithIdUser(getActivity(), ProfileManager.instance().profile.getIdUser()));
    }

    private void onRankSuccess(final RankSuccessEvent event){
        Logger.log(Logger.Level.DEBUG, TAG, "onRankSuccess");

        // hide loader
        refreshLayout.setRefreshing(false);
        loader.setVisibility(View.GONE);

        try {
            if(event.element != null) {
                final JSONObject ranksObject = new JSONObject(event.element.toString());
                if (ranksObject != null) {
                    if (ranksObject.has("rank")) {
                        final JSONArray ranks = ranksObject.getJSONArray("rank");
                        final ArrayList<RankGame> rankGames = new ArrayList<>();
                        for(int i = 0; i < ranks.length(); i++){
                            rankGames.add(new RankGame(ranks.getJSONObject(i)));
                        }
                        rankListAdapter.setRanks(rankGames);
                        recyclerView.setAdapter(rankListAdapter);
                    }
                }
            }
        } catch (JSONException e) {
            Logger.log(Logger.Level.WARNING, TAG, "JSONException:" + e.getMessage());
        }
    }

    private void onRankFailed(final RankFailedEvent event){
        Logger.log(Logger.Level.DEBUG, TAG, "onRankFailed");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.log(Logger.Level.DEBUG, TAG, "onDestroyView");

        subscriptions.unsubscribe();
    }
}
