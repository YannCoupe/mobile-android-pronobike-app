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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.authentication.service.ProfileManager;
import fr.ycoupe.pronobike.authentication.service.ProfileService;
import fr.ycoupe.pronobike.models.Game;
import fr.ycoupe.pronobike.models.User;
import fr.ycoupe.pronobike.pronostic.adapter.GameListAdapter;
import fr.ycoupe.pronobike.pronostic.adapter.GameRecyclerView;
import fr.ycoupe.pronobike.pronostic.service.bus.out.UserRequestFailedEvent;
import fr.ycoupe.pronobike.pronostic.service.bus.out.UserRequestSuccessEvent;
import fr.ycoupe.pronobike.sqlite.GameDAO;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;
import rx.internal.util.SubscriptionList;

/**
 * Created by yanncoupe on 18/07/2016.
 */
public class GameFragment extends Fragment {
    public final static String TAG = GameFragment.class.getSimpleName();

    private SubscriptionList subscriptions;

    private ProfileService profileService;

    private GameListAdapter gameListAdapter;

    @BindView(R.id.game_list)
    GameRecyclerView recyclerView;
    @BindView(R.id.game_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.game_loader)
    ProgressBar loader;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup group, final Bundle savedInstanceState) {
        Logger.log(Logger.Level.DEBUG, TAG, "onCreateView");

        final View view = inflater.inflate(R.layout.game_fragment, group, false);
        ButterKnife.bind(this, view);

        profileService = new ProfileService();

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

        gameListAdapter = new GameListAdapter(getActivity());

        recyclerView.setHasFixedSize(true);
        recyclerView.setGameEmptyView(view.findViewById(R.id.game_empty));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // Trick to enable pull to refresh only when the the first item is visible.
        // Because if you scrolling down in the list you cannot return at top, the refresh take focus
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                refreshLayout.setEnabled(topRowVerticalPosition >= 0);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        subscriptions = new SubscriptionList();
        subscriptions.add(BusManager.instance().observe(UserRequestSuccessEvent.class, this::onUserRequestSuccess));
        subscriptions.add(BusManager.instance().observe(UserRequestFailedEvent.class, this::onUserRequestFailed));

        reloadDatas();

        return view;
    }

    /*
     * Start get datas
     */
    private void reloadDatas(){
        Logger.log(Logger.Level.DEBUG, TAG, "reloadDatas");

        profileService.user(ProfileManager.instance().profile.getIdUser());
    }

    /*
     * Games are succesfully downloaded
     */
    private void onGamesUpdate() {
        Logger.log(Logger.Level.DEBUG, TAG, "onGamesUpdate");
        getActivity().runOnUiThread(() -> {

            // hide loader
            refreshLayout.setRefreshing(false);
            loader.setVisibility(View.GONE);

            final List<Game> games = GameDAO.gamesWithIdUser(getActivity(), ProfileManager.instance().profile.getIdUser());

            if (gameListAdapter == null || games == null) {
                return;
            }

            gameListAdapter.setGames(games);

            recyclerView.setAdapter(gameListAdapter);

        });
    }

    // =============================================================================================
    // Profile service

    private void onUserRequestSuccess(final UserRequestSuccessEvent event) {
        Logger.log(Logger.Level.DEBUG, TAG, "onUserRequestSuccess");

        try {
            final JsonElement jsonElement = event.element;
            if (jsonElement != null) {
                final JSONObject update = new JSONObject(jsonElement.toString());
                if (update.has("status")) {

                    int status = update.getInt("status");

                    if (status == 0) {

                        User user = new User(update.getInt("id_user"), update.getString("firstname"), update.getString("lastname"), update.getString("email"));

                        ProfileManager.create(user);

                        JSONArray games = null;

                        if (update.has("games")) games = update.getJSONArray("games");

                        GameDAO.saveGames(getActivity(), games);

                        onGamesUpdate();

                    }
                }
            }
        } catch (JSONException e){
            Logger.log(Logger.Level.WARNING, TAG, "JSONException: " + e);
        }
    }

    private void onUserRequestFailed(final UserRequestFailedEvent event) {
        Logger.log(Logger.Level.ERROR, TAG, "onUserRequestFailed");

        getActivity().runOnUiThread(() -> {
            // hide loader
            refreshLayout.setRefreshing(false);

            loader.setVisibility(View.GONE);

            recyclerView.setAdapter(gameListAdapter);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.log(Logger.Level.DEBUG, TAG, "onDestroyView");

        subscriptions.unsubscribe();
    }
}
