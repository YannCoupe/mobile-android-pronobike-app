package fr.ycoupe.pronobike.pronostic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.jakewharton.rxbinding.view.RxView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.profile.bus.out.RefreshEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.JoinFailedEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.JoinSuccessEvent;
import fr.ycoupe.pronobike.pronostic.service.GameService;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;
import fr.ycoupe.pronobike.utils.StringUtils;
import fr.ycoupe.pronobike.utils.ViewUtils;
import rx.internal.util.SubscriptionList;

/**
 * Created by yanncoupe on 11/08/2016.
 */
public class JoinFragment extends Fragment {
    public final static String TAG = JoinFragment.class.getSimpleName();

    private SubscriptionList subscriptions;

    @BindView(R.id.join_edittext)
    EditText joinEditText;
    @BindView(R.id.join_button_send)
    Button send;
    @BindView(R.id.join_loader)
    RelativeLayout loader;

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

        final View view = inflater.inflate(R.layout.join_fragment, group, false);
        ButterKnife.bind(this, view);

        gameService = new GameService();

        subscriptions = new SubscriptionList();

        RxView.clicks(send).subscribe(next -> join());

        subscriptions = new SubscriptionList();
        subscriptions.add(BusManager.instance().observe(JoinSuccessEvent.class, this::onJoinSuccess));
        subscriptions.add(BusManager.instance().observe(JoinFailedEvent.class, this::onJoinFailed));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        joinEditText.setOnEditorActionListener((final TextView v, final int actionId, final KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                join();
                return true;
            }
            return false;
        });
    }

    /**
     * Call api to join a game
     */
    private void join(){
        Logger.log(Logger.Level.DEBUG, TAG, "join");
        hideKeyboard();

        String error = null;

        if (StringUtils.isNullOrEmpty(joinEditText.getText())) {
            error = getString(R.string.veuillez_renseigner_token);
        }

        if (StringUtils.isNullOrEmpty(error)) {
            // Call API
            gameService.join(joinEditText.getText().toString());
            showLoader(true);
        } else {
            // Show error
            showError(error);
        }
    }

    private void onJoinSuccess(final JoinSuccessEvent event){
        Logger.log(Logger.Level.DEBUG, TAG, "onJoinSuccess");
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
                                    .setTitle(getString(R.string.rejoindre_partie))
                                    .setMessage(getString(R.string.felicitation_partie))
                                    .setNeutralButton(android.R.string.ok, (DialogInterface dialog, int which) -> {
                                        dialog.cancel();
                                        BusManager.instance().send(new RefreshEvent());
                                        getActivity().finish();
                                    }).show();
                            return;
                        } else if (status == 1) {
                            showError(getString(R.string.aucune_partie_token));
                            return;
                        } else if (status == 3) {
                            showError(getString(R.string.deja_inscris));
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

    private void onJoinFailed(final JoinFailedEvent event){
        Logger.log(Logger.Level.DEBUG, TAG, "onJoinFailed");
        showLoader(false);
        showError(getString(R.string.erreur));
    }

    private void showError(final String error){
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.rejoindre_partie))
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
        ViewUtils.closeKeyboard(getActivity(), joinEditText.getWindowToken());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.log(Logger.Level.DEBUG, TAG, "onDestroyView");

        subscriptions.unsubscribe();
    }
}
