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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ycoupe.pronobike.R;
import fr.ycoupe.pronobike.activities.LoginActivity;
import fr.ycoupe.pronobike.authentication.bus.out.CreateRequestFailedEvent;
import fr.ycoupe.pronobike.authentication.bus.out.CreateRequestSuccessEvent;
import fr.ycoupe.pronobike.authentication.bus.out.CreateSuccessEvent;
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
public class CreateFragment extends Fragment {
    private final static String TAG = CreateFragment.class.getSimpleName();

    @BindView(R.id.create_edittext_email)
    EditText emailEditText;
    @BindView(R.id.create_edittext_password)
    EditText passwordEditText;
    @BindView(R.id.create_edittext_confirm)
    EditText confirmEditText;
    @BindView(R.id.create_edittext_firstname)
    EditText firstnameEditText;
    @BindView(R.id.create_edittext_lastname)
    EditText lastnameEditText;
    @BindView(R.id.create_button_send)
    Button createButton;
    @BindView(R.id.create_loader)
    RelativeLayout loader;

    private ProfileService profileService;

    private SubscriptionList subscriptions;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.log(Logger.Level.DEBUG, TAG, "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup group, final Bundle savedInstanceState) {
        Logger.log(Logger.Level.DEBUG, TAG, "onCreateView");

        final View view = inflater.inflate(R.layout.create_fragment, group, false);
        ButterKnife.bind(this, view);

        profileService = new ProfileService();

        subscriptions = new SubscriptionList();

        subscriptions.add(BusManager.instance().observe(CreateRequestSuccessEvent.class, this::onCreateRequestSuccess));
        subscriptions.add(BusManager.instance().observe(CreateRequestFailedEvent.class, this::onCreateRequestFailed));
        subscriptions.add(RxView.clicks(createButton).subscribe(next -> create()));

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lastnameEditText.setOnEditorActionListener((final TextView v, final int actionId, final KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                create();
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

    private void create() {
        Logger.log(Logger.Level.DEBUG, TAG, "create");

        hideKeyboard();

        String error = null;

        if (StringUtils.isNullOrEmpty(emailEditText.getText()) ||
                StringUtils.isNullOrEmpty(passwordEditText.getText()) ||
                StringUtils.isNullOrEmpty(confirmEditText.getText()) ||
                StringUtils.isNullOrEmpty(firstnameEditText.getText()) ||
                StringUtils.isNullOrEmpty(lastnameEditText.getText())) {
            error = getString(R.string.renseigner_champs);
        }

        if (StringUtils.isNullOrEmpty(error) && !StringUtils.isValidEmailAddress(emailEditText.getText().toString())) {
            error = getString(R.string.email_non_valide);
        }

        if(StringUtils.isNullOrEmpty(error) && !confirmEditText.getText().toString().contentEquals(passwordEditText.getText().toString())){
            error = getString(R.string.mot_de_passe_differents);
        }

        if (StringUtils.isNullOrEmpty(error)) {
            // Call API
            try{
                showLoader(true);
                profileService.create(emailEditText.getText().toString(), StringUtils.md5(passwordEditText.getText().toString()), firstnameEditText.getText().toString(), lastnameEditText.getText().toString());

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

    private void onCreateRequestSuccess(final CreateRequestSuccessEvent event) {
        Logger.log(Logger.Level.DEBUG, TAG, "onCreateRequestSuccess");

        showLoader(false);

        try {
            final JsonElement jsonElement = event.element;
            if (jsonElement != null) {
                final JSONObject create = new JSONObject(jsonElement.toString());
                if (create.has("status")) {

                    int status = create.getInt("status");

                    if (status == 0) {

                        User user = new User(create.getInt("id_user"), create.getString("firstname"), create.getString("lastname"), create.getString("email"));

                        ProfileManager.create(user);

                        JSONArray games = null;

                        if (create.has("games")) games = create.getJSONArray("games");

                        GameDAO.saveGames(getActivity(), games);

                        PreferenceManager.getSecurePrefs(getContext()).edit()
                                .putString(LoginActivity.PREFS_LOGIN_EMAIL, emailEditText.getText().toString())
                                .putString(LoginActivity.PREFS_LOGIN_PASSWORD, passwordEditText.getText().toString())
                                .apply();

                        new AlertDialog.Builder(getActivity())
                                .setTitle(getString(R.string.creer_compte))
                                .setMessage(getString(R.string.votre_compte_cree))
                                .setNeutralButton(android.R.string.ok, (final DialogInterface dialog, final int which) -> {
                                    dialog.cancel();

                                    BusManager.instance().send(new CreateSuccessEvent());

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

    private void onCreateRequestFailed(final CreateRequestFailedEvent event) {
        Logger.log(Logger.Level.ERROR, TAG, "onCreateRequestFailed");

        showLoader(false);

        showError(getString(R.string.erreur));
    }

    // =============================================================================================
    // Keyboard

    private void hideKeyboard(){
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
                .setTitle(getString(R.string.creer_compte))
                .setMessage(error)
                .setNeutralButton(android.R.string.ok, (DialogInterface dialog, int which) -> {
                    dialog.cancel();
                }).show();
    }

}
