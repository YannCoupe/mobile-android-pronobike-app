package fr.ycoupe.pronobike.authentication;

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
import fr.ycoupe.pronobike.authentication.bus.out.ForgetSuccessEvent;
import fr.ycoupe.pronobike.authentication.bus.out.PasswordRequestFailedEvent;
import fr.ycoupe.pronobike.authentication.bus.out.PasswordRequestSuccessEvent;
import fr.ycoupe.pronobike.authentication.service.ProfileService;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;
import fr.ycoupe.pronobike.utils.StringUtils;
import fr.ycoupe.pronobike.utils.ViewUtils;
import rx.internal.util.SubscriptionList;

/**
 * {@link Fragment} managing the display of the login form and the user authentication to the
 * platform.
 */
public class ForgetFragment extends Fragment {
    private final static String TAG = ForgetFragment.class.getSimpleName();

    private SubscriptionList subscriptions;

    @BindView(R.id.forget_edittext_email)
    EditText emailEditText;
    @BindView(R.id.forget_button_send)
    Button forgetButton;
    @BindView(R.id.forget_loader)
    RelativeLayout loader;

    private ProfileService profileService;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup group, final Bundle savedInstanceState) {
        Logger.log(Logger.Level.DEBUG, TAG, "onCreateView");

        final View view = inflater.inflate(R.layout.forget_fragment, group, false);
        ButterKnife.bind(this, view);

        profileService = new ProfileService();

        subscriptions = new SubscriptionList();

        subscriptions.add(BusManager.instance().observe(PasswordRequestSuccessEvent.class, this::onPasswordRequestSuccess));
        subscriptions.add(BusManager.instance().observe(PasswordRequestFailedEvent.class, this::onPasswordRequestFailed));
        subscriptions.add(RxView.clicks(forgetButton).subscribe(next -> forget()));

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailEditText.setOnEditorActionListener((final TextView v, final int actionId, final KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                forget();
                return true;
            }
            return false;
        });

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

    // =============================================================================================
    // Actions

    private void forget() {
        Logger.log(Logger.Level.DEBUG, TAG, "forget");

        hideKeyboard();

        String error = null;

        if (StringUtils.isNullOrEmpty(emailEditText.getText())) {
            error = getString(R.string.renseigner_champs);
        }

        if (StringUtils.isNullOrEmpty(error) && !StringUtils.isValidEmailAddress(emailEditText.getText().toString())) {
            error = getString(R.string.email_non_valide);
        }

        if (StringUtils.isNullOrEmpty(error)) {
            // Call API
            showLoader(true);
            profileService.getPassword(emailEditText.getText().toString());
        } else {
            // Show error
            showError(error);
            return;
        }
    }

    // =============================================================================================
    // Profile service

    private void onPasswordRequestSuccess(final PasswordRequestSuccessEvent event) {
        Logger.log(Logger.Level.DEBUG, TAG, "onPasswordRequestSuccess");

        showLoader(false);

        try {
            final JsonElement jsonElement = event.element;
            if (jsonElement != null) {
                final JSONObject forget = new JSONObject(jsonElement.toString());
                if (forget.has("status")) {
                    int status = forget.getInt("status");
                    if (status == 0) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle(getString(R.string.changement_mot_de_passe))
                                .setMessage(getString(R.string.un_email_vient_etre_envoye))
                                .setNeutralButton(android.R.string.ok, (DialogInterface dialog, int which) -> {
                                    dialog.cancel();
                                    BusManager.instance().send(new ForgetSuccessEvent());
                                }).show();
                        return;
                    }
                }
                showError(getString(R.string.verifier_identifiants));
                return;

            } else {
                showError(getString(R.string.erreur));
            }
        } catch (JSONException e){
            Logger.log(Logger.Level.WARNING, TAG, "JSONException: " + e);
        }
    }

    private void onPasswordRequestFailed(final PasswordRequestFailedEvent event) {
        Logger.log(Logger.Level.ERROR, TAG, "onPasswordRequestFailed");

        showLoader(false);

        showError(getString(R.string.erreur));
    }

    // =============================================================================================
    // Keyboard

    private void hideKeyboard(){
        ViewUtils.closeKeyboard(getActivity(), emailEditText.getWindowToken());
    }

    // =============================================================================================
    // Error

    private void showError(final String error){

        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.changement_mot_de_passe))
                .setMessage(error)
                .setNeutralButton(android.R.string.ok, (DialogInterface dialog, int which) -> {
                    dialog.cancel();
                }).show();
    }
}
