package fr.ycoupe.pronobike.pronostic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.models.Game;
import fr.ycoupe.pronobike.pronostic.adapter.EmptyRecyclerView;
import fr.ycoupe.pronobike.pronostic.adapter.PronosticListAdapter;
import fr.ycoupe.pronobike.pronostic.bus.out.PronosticsFailedEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.PronosticsSuccessEvent;
import fr.ycoupe.pronobike.pronostic.service.GameService;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;
import rx.internal.util.SubscriptionList;

/**
 * Created by yanncoupe on 18/07/2016.
 */
public class PronosticsFragment extends Fragment {
    public final static String TAG = PronosticsFragment.class.getSimpleName();

    public final static String GAME_EXTRA = TAG + ".GAME_EXTRA";

    @BindView(R.id.pronostics_title)
    TextView title;
    @BindView(R.id.pronostics_recycler)
    EmptyRecyclerView recyclerView;
    @BindView(R.id.pronostics_loader)
    RelativeLayout loader;

    private SubscriptionList subscriptions;

    private Game game;

    private GameService gameService;

    private PronosticListAdapter pronosticListAdapter;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup group, final Bundle savedInstanceState) {
        Logger.log(Logger.Level.DEBUG, TAG, "onCreateView");

        final View view = inflater.inflate(R.layout.pronostics_fragment, group, false);
        ButterKnife.bind(this, view);

        title.setVisibility(View.GONE);

        gameService = new GameService();

        game = getArguments().getParcelable(GAME_EXTRA);

        gameService.pronostics(game.getIdGame());

        pronosticListAdapter = new PronosticListAdapter(getActivity());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setEmptyView(view.findViewById(R.id.pronostic_empty));

        subscriptions = new SubscriptionList();
        subscriptions.add(BusManager.instance().observe(PronosticsSuccessEvent.class, this::onPronosticsSuccess));
        subscriptions.add(BusManager.instance().observe(PronosticsFailedEvent.class, this::onPronosticsFailed));

        return view;
    }

    private void onPronosticsSuccess(final PronosticsSuccessEvent event) {
        Logger.log(Logger.Level.DEBUG, TAG, "onPronosticsSuccess");
        showLoader(false);

        try {
            final JsonElement jsonElement = event.element;
            if (jsonElement != null) {
                final JSONObject list = new JSONObject(jsonElement.toString());
                if (list != null) {
                    if (list.has("status")) {
                        int status = list.getInt("status");
                        if (status == 0) {
                            title.setText(String.format(getString(R.string.derniers_pronostics_desc), list.getString("circuit")));
                            if (pronosticListAdapter == null) {
                                return;
                            }

                            final int[] ranks = new int[3];
                            ranks[0] = list.getInt("first");
                            ranks[1] = list.getInt("second");
                            ranks[2] = list.getInt("third");

                            title.setVisibility(View.VISIBLE);
                            pronosticListAdapter.setPronostics(list.getJSONArray("pronostics"), ranks);
                            recyclerView.setAdapter(pronosticListAdapter);
                            return;
                        }
                    }
                }
                recyclerView.setAdapter(pronosticListAdapter);
            }
        } catch (JSONException e){
            Logger.log(Logger.Level.WARNING, TAG, "JSONException: " + e);
        }

    }

    private void onPronosticsFailed(final PronosticsFailedEvent event) {
        Logger.log(Logger.Level.DEBUG, TAG, "onPronosticsFailed");
        showLoader(false);
        recyclerView.setAdapter(pronosticListAdapter);
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
