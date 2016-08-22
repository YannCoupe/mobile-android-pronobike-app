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
import com.jakewharton.rxbinding.widget.RxTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.activities.LoginActivity;
import fr.ycoupe.pronobike.authentication.bus.out.AuthenticationSuccessEvent;
import fr.ycoupe.pronobike.authentication.bus.out.CreateEvent;
import fr.ycoupe.pronobike.authentication.bus.out.ForgetEvent;
import fr.ycoupe.pronobike.authentication.bus.out.ProfileRequestFailedEvent;
import fr.ycoupe.pronobike.authentication.bus.out.ProfileRequestSuccessEvent;
import fr.ycoupe.pronobike.authentication.service.ProfileManager;
import fr.ycoupe.pronobike.authentication.service.ProfileService;
import fr.ycoupe.pronobike.models.User;
import fr.ycoupe.pronobike.sqlite.GameDAO;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;
import fr.ycoupe.pronobike.utils.PreferenceManager;
import fr.ycoupe.pronobike.utils.StringUtils;
import fr.ycoupe.pronobike.utils.ViewUtils;
import rx.internal.util.SubscriptionList;

/**
 * {@link Fragment} managing the display of the login form and the user authentication to the
 * platform.
 */
public class LoginFragment extends Fragment {
    private final static String TAG = LoginFragment.class.getSimpleName();

    private SubscriptionList subscriptions;

    @BindView(R.id.auth_edittext_email)
    EditText emailEditText;
    @BindView(R.id.auth_edittext_password)
    EditText passwordEditText;
    @BindView(R.id.auth_button_connect)
    Button connectButton;
    @BindView(R.id.auth_button_create)
    Button createButton;
    @BindView(R.id.auth_button_forget)
    Button forgetButton;
    @BindView(R.id.auth_loader)
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

        final View view = inflater.inflate(R.layout.login_fragment, group, false);
        ButterKnife.bind(this, view);

        profileService = new ProfileService();

        subscriptions = new SubscriptionList();

        subscriptions.add(BusManager.instance().observe(ProfileRequestSuccessEvent.class, this::onProfileRequestSuccess));
        subscriptions.add(BusManager.instance().observe(ProfileRequestFailedEvent.class, this::onProfileRequestFailed));
        subscriptions.add(RxTextView.editorActionEvents(passwordEditText).subscribe(next -> connectButton.performClick()));
        subscriptions.add(RxView.clicks(connectButton).subscribe(next -> login()));
        subscriptions.add(RxView.clicks(createButton).subscribe(next -> create()));
        subscriptions.add(RxView.clicks(forgetButton).subscribe(next -> forget()));

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        passwordEditText.setOnEditorActionListener((final TextView v, final int actionId, final KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login();
                return true;
            }
            return false;
        });

        loadCredentials();
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

    private void login() {
        Logger.log(Logger.Level.DEBUG, TAG, "login");

        hideKeyboard();

        String error = null;

        if (StringUtils.isNullOrEmpty(emailEditText.getText()) || StringUtils.isNullOrEmpty(passwordEditText.getText())) {
            error = getString(R.string.renseigner_champs);
        }

        if (StringUtils.isNullOrEmpty(error) && !StringUtils.isValidEmailAddress(emailEditText.getText().toString())) {
            error = getString(R.string.email_non_valide);
        }

        if (StringUtils.isNullOrEmpty(error)) {
            // Call API
            try{
                showLoader(true);
                profileService.getProfile(emailEditText.getText().toString(), StringUtils.md5(passwordEditText.getText().toString()));
            } catch (NoSuchAlgorithmException e){
                Logger.log(Logger.Level.WARNING, TAG, "NoSuchAlgorithmException: " + e);
            }
        } else {
            // Show error
            showError(error);

            return;
        }
    }

    private void create() {
        Logger.log(Logger.Level.DEBUG, TAG, "create");

        // Notify that user want to show create screen
        final CreateEvent event = new CreateEvent();
        BusManager.instance().send(event);
    }

    private void forget() {
        Logger.log(Logger.Level.DEBUG, TAG, "foget");

        // Notify that user want to show forget screen
        final ForgetEvent event = new ForgetEvent();
        BusManager.instance().send(event);
    }
    
    // =============================================================================================
    // Credentials

    /**
     * Save credentials into preferences
     */
    private void saveCredentials() {
        Logger.log(Logger.Level.DEBUG, TAG, "saveCredentials");

        PreferenceManager.getSecurePrefs(getContext()).edit()
                .putString(LoginActivity.PREFS_LOGIN_EMAIL, emailEditText.getText().toString())
                .putString(LoginActivity.PREFS_LOGIN_PASSWORD, passwordEditText.getText().toString())
                .apply();
    }

    /**
     * Load saved login/password into textfields
     */
    private void loadCredentials() {
        Logger.log(Logger.Level.DEBUG, TAG, "loadCredentials");

        emailEditText.setText(PreferenceManager.getSecurePrefs(getContext()).getString(LoginActivity.PREFS_LOGIN_EMAIL, ""));
        passwordEditText.setText(PreferenceManager.getSecurePrefs(getContext()).getString(LoginActivity.PREFS_LOGIN_PASSWORD, ""));
    }

    // =============================================================================================
    // Profile service

    private void onProfileRequestSuccess(final ProfileRequestSuccessEvent event) {
        Logger.log(Logger.Level.DEBUG, TAG, "onProfileRequestSuccess");

        showLoader(false);

        try {

            final JsonElement jsonElement = event.profile;

            if (jsonElement != null) {

                final JSONObject profile = new JSONObject(jsonElement.toString());

                if (profile.has("status")) {
                    int status = profile.getInt("status");
                    if (status == 0) {
                        User user = new User(
                                profile.getInt("id_user"),
                                profile.getString("firstname"),
                                profile.getString("lastname"),
                                profile.getString("email"));

                        ProfileManager.create(user);

                        JSONArray games = null;

                        if (profile.has("games")) games = profile.getJSONArray("games");

                        GameDAO.saveGames(getActivity(), games);

                        saveCredentials();

                        BusManager.instance().send(new AuthenticationSuccessEvent());
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

    private void onProfileRequestFailed(final ProfileRequestFailedEvent event) {
        Logger.log(Logger.Level.ERROR, TAG, "onProfileRequestFailed");

        showLoader(false);

        showError(getString(R.string.erreur));
    }

    // =============================================================================================
    // Keyboard

    private void hideKeyboard(){
        ViewUtils.closeKeyboard(getActivity(), emailEditText.getWindowToken());
        ViewUtils.closeKeyboard(getActivity(), passwordEditText.getWindowToken());
    }

    // =============================================================================================
    // Error

    private void showError(final String error){

        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.connexion))
                .setMessage(error)
                .setNeutralButton(android.R.string.ok, (DialogInterface dialog, int which) -> {
                        dialog.cancel();
                }).show();
    }
}
