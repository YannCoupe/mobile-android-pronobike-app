package fr.ycoupe.pronobike.profile;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.activities.LoginActivity;
import fr.ycoupe.pronobike.authentication.bus.out.CreateSuccessEvent;
import fr.ycoupe.pronobike.authentication.service.ProfileManager;
import fr.ycoupe.pronobike.authentication.service.ProfileService;
import fr.ycoupe.pronobike.models.User;
import fr.ycoupe.pronobike.profile.bus.out.UpdateRequestFailedEvent;
import fr.ycoupe.pronobike.profile.bus.out.UpdateRequestSuccessEvent;
import fr.ycoupe.pronobike.sqlite.GameDAO;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;
import fr.ycoupe.pronobike.utils.PreferenceManager;
import fr.ycoupe.pronobike.utils.StringUtils;
import fr.ycoupe.pronobike.utils.ViewUtils;
import rx.internal.util.SubscriptionList;

public class ProfileFragment extends Fragment {
    public final static String TAG = ProfileFragment.class.getSimpleName();

    @BindView(R.id.profile_edittext_email)
    EditText emailEditText;
    @BindView(R.id.profile_edittext_password)
    EditText passwordEditText;
    @BindView(R.id.profile_edittext_confirm)
    EditText confirmEditText;
    @BindView(R.id.profile_edittext_firstname)
    EditText firstnameEditText;
    @BindView(R.id.profile_edittext_lastname)
    EditText lastnameEditText;
    @BindView(R.id.profile_button_send)
    Button createButton;
    @BindView(R.id.profile_loader)
    RelativeLayout loader;

    private ProfileService profileService;

    private SubscriptionList subscriptions;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        Logger.log(Logger.Level.DEBUG, TAG, "onCreateView");

        final View view = inflater.inflate(R.layout.profile_fragment, container, false);

        ButterKnife.bind(this, view);

        profileService = new ProfileService();

        subscriptions = new SubscriptionList();

        subscriptions.add(BusManager.instance().observe(UpdateRequestSuccessEvent.class, this::onUpdateRequestSuccess));
        subscriptions.add(BusManager.instance().observe(UpdateRequestFailedEvent.class, this::onUpdateRequestFailed));
        subscriptions.add(RxView.clicks(createButton).subscribe(next -> update()));

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lastnameEditText.setOnEditorActionListener((final TextView v, final int actionId, final KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                update();
                return true;
            }
            return false;
        });
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

    private void loadValues(){
        Logger.log(Logger.Level.DEBUG, TAG, "loadValues");

        if(ProfileManager.instance() != null){

            User profile = ProfileManager.instance().profile;

            emailEditText.setText(profile.getEmail());
            firstnameEditText.setText(profile.getFirstname());
            lastnameEditText.setText(profile.getLastname());
        }

    }

    // =============================================================================================
    // Actions

    private void update() {
        Logger.log(Logger.Level.DEBUG, TAG, "update");

        hideKeyboard();

        String error = null;

        if (StringUtils.isNullOrEmpty(emailEditText.getText()) ||
                StringUtils.isNullOrEmpty(firstnameEditText.getText()) ||
                StringUtils.isNullOrEmpty(lastnameEditText.getText())) {
            error = getString(R.string.renseigner_champs);
        }

        if(StringUtils.isNullOrEmpty(error) && !StringUtils.isNullOrEmpty(confirmEditText.getText()) || !StringUtils.isNullOrEmpty(passwordEditText.getText())){
            if(!confirmEditText.getText().toString().contentEquals(passwordEditText.getText().toString())){
                error = getString(R.string.mot_de_passe_differents);
            }
        }

        if (StringUtils.isNullOrEmpty(error) && !StringUtils.isValidEmailAddress(emailEditText.getText().toString())) {
            error = getString(R.string.email_non_valide);
        }

        if (StringUtils.isNullOrEmpty(error)) {
            // Call API
            try{
                showLoader(true);

                String email = emailEditText.getText().toString();
                String firstname = firstnameEditText.getText().toString();
                String lastname = lastnameEditText.getText().toString();
                String password = confirmEditText.getText().toString().length() > 0 ? StringUtils.md5(confirmEditText.getText().toString()) : null;

                if(email.contentEquals(ProfileManager.instance().profile.getEmail())) email = null;
                if(firstname.contentEquals(ProfileManager.instance().profile.getFirstname())) firstname = null;
                if(lastname.contentEquals(ProfileManager.instance().profile.getLastname())) lastname = null;

                profileService.update(ProfileManager.instance().profile.getIdUser(), email, password, firstname, lastname);

            } catch (NoSuchAlgorithmException e){
                Logger.log(Logger.Level.WARNING, TAG, "NoSuchAlgorithmException: " + e);
            }
        } else {
            // Show error
            showError(error);
            return;
        }
    }

    // =============================================================================================
    // Profile service

    private void onUpdateRequestSuccess(final UpdateRequestSuccessEvent event) {
        Logger.log(Logger.Level.DEBUG, TAG, "onUpdateRequestSuccess");

        showLoader(false);

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

                        PreferenceManager.getSecurePrefs(getContext()).edit()
                                .putString(LoginActivity.PREFS_LOGIN_EMAIL, emailEditText.getText().toString())
                                .putString(LoginActivity.PREFS_LOGIN_PASSWORD, passwordEditText.getText().toString())
                                .apply();

                        new AlertDialog.Builder(getActivity())
                                .setTitle(getString(R.string.modifier_compte))
                                .setMessage(getString(R.string.votre_modifications_prises))
                                .setNeutralButton(android.R.string.ok, (DialogInterface dialog, int which) -> {
                                    dialog.cancel();
                                    loadValues();
                                }).show();
                        return;

                    } else if (status == 1) {
                        showError(getString(R.string.adresse_utilisee));
                        return;
                    }
                }
            }

            showError(getString(R.string.erreur));

        } catch (JSONException e){
            Logger.log(Logger.Level.WARNING, TAG, "JSONException: " + e);
        }
    }

    private void onUpdateRequestFailed(final UpdateRequestFailedEvent event) {
        Logger.log(Logger.Level.ERROR, TAG, "onUpdateRequestFailed");

        showLoader(false);

        showError(getString(R.string.erreur));
    }

    // =============================================================================================
    // Keyboard

    private void hideKeyboard(){
        if(emailEditText == null) return;
        ViewUtils.closeKeyboard(getActivity(), emailEditText.getWindowToken());
        ViewUtils.closeKeyboard(getActivity(), passwordEditText.getWindowToken());
        ViewUtils.closeKeyboard(getActivity(), confirmEditText.getWindowToken());
        ViewUtils.closeKeyboard(getActivity(), firstnameEditText.getWindowToken());
        ViewUtils.closeKeyboard(getActivity(), lastnameEditText.getWindowToken());
    }

    // =============================================================================================
    // Error

    private void showError(final String error){

        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.modifier_compte))
                .setMessage(error)
                .setNeutralButton(android.R.string.ok, (DialogInterface dialog, int which) -> {
                    dialog.cancel();
                }).show();
    }

    /**
     * Notify this {@link Fragment} that it is visible/hidden.
     *
     * @param visible {@code true} if visible.
     */
    public void setVisible(final boolean visible) {
        Logger.log(Logger.Level.DEBUG, TAG, "setVisible " + visible);

        if (visible) {
            loadValues();
        } else {
            hideKeyboard();
        }
    }
}
