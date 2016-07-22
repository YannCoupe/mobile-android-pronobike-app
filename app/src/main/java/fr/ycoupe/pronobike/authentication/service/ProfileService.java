package fr.ycoupe.pronobike.authentication.service;

import com.google.gson.JsonElement;

import java.net.IDN;

import fr.ycoupe.pronobike.BuildConfig;
import fr.ycoupe.pronobike.authentication.bus.out.CreateRequestFailedEvent;
import fr.ycoupe.pronobike.authentication.bus.out.CreateRequestSuccessEvent;
import fr.ycoupe.pronobike.authentication.bus.out.PasswordRequestFailedEvent;
import fr.ycoupe.pronobike.authentication.bus.out.PasswordRequestSuccessEvent;
import fr.ycoupe.pronobike.authentication.bus.out.ProfileRequestFailedEvent;
import fr.ycoupe.pronobike.authentication.bus.out.ProfileRequestSuccessEvent;
import fr.ycoupe.pronobike.profile.bus.out.UpdateRequestFailedEvent;
import fr.ycoupe.pronobike.profile.bus.out.UpdateRequestSuccessEvent;
import fr.ycoupe.pronobike.pronostic.service.bus.out.UserRequestFailedEvent;
import fr.ycoupe.pronobike.pronostic.service.bus.out.UserRequestSuccessEvent;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;
import fr.ycoupe.pronobike.utils.RestUtils;
import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yanncoupe on 14/07/2016.
 */
public class ProfileService {

    private final static String TAG = ProfileService.class.getSimpleName();

    private final ProfileApi profileApi;

    public ProfileService() {
        final Retrofit retrofit = RestUtils.makeAdapter(BuildConfig.SERVICE_ENDPOINT);
        profileApi = retrofit.create(ProfileApi.class);
    }

    /**
     * Log a user with a login/mdp.
     *
     * @param email The user email.
     * @param password The user password.
     */
    public void getProfile(final String email, final String password) {
        Logger.log(Logger.Level.DEBUG, TAG, "getProfile");

        profileApi.getProfile(email, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        p -> onProfileRequestSuccess(p),
                        this::onProfileRequestFailed
                );
    }

    private void onProfileRequestSuccess(final JsonElement element) {
        Logger.log(Logger.Level.DEBUG, TAG, "onProfileRequestSuccess");

        final ProfileRequestSuccessEvent event = new ProfileRequestSuccessEvent();
        event.profile = element;
        BusManager.instance().send(event);
    }


    private void onProfileRequestFailed(final Throwable error) {
        Logger.log(Logger.Level.DEBUG, TAG, "onProfileRequestFailed");

        final ProfileRequestFailedEvent event = new ProfileRequestFailedEvent();
        BusManager.instance().send(event);
    }

    /**
     * Generate a new password for user
     *
     * @param email The user email.
     */
    public void getPassword(final String email) {
        Logger.log(Logger.Level.DEBUG, TAG, "getPassword");

        profileApi.getPassword(email)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        p -> onPasswordRequestSuccess(p),
                        this::onPasswordRequestFailed
                );
    }

    private void onPasswordRequestSuccess(final JsonElement element) {
        Logger.log(Logger.Level.DEBUG, TAG, "onPasswordRequestSuccess");

        final PasswordRequestSuccessEvent event = new PasswordRequestSuccessEvent();
        event.element = element;
        BusManager.instance().send(event);
    }


    private void onPasswordRequestFailed(final Throwable error) {
        Logger.log(Logger.Level.DEBUG, TAG, "onPasswordRequestFailed");

        final PasswordRequestFailedEvent event = new PasswordRequestFailedEvent();
        BusManager.instance().send(event);
    }

    /**
     * Create a new account for user
     *
     * @param email The user email.
     * @param password The user password.
     * @param firstname The user firstname.
     * @param lastname The user lastname.
     */
    public void create(final String email, final String password, final String firstname, final String lastname) {
        Logger.log(Logger.Level.DEBUG, TAG, "create");

        profileApi.create(email, password, firstname, lastname)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        p -> onCreateRequestSuccess(p),
                        this::onCreateRequestFailed
                );
    }

    private void onCreateRequestSuccess(final JsonElement element) {
        Logger.log(Logger.Level.DEBUG, TAG, "onCreateRequestSuccess");

        final CreateRequestSuccessEvent event = new CreateRequestSuccessEvent();
        event.element = element;
        BusManager.instance().send(event);
    }


    private void onCreateRequestFailed(final Throwable error) {
        Logger.log(Logger.Level.DEBUG, TAG, "onCreateRequestFailed");

        final CreateRequestFailedEvent event = new CreateRequestFailedEvent();
        BusManager.instance().send(event);
    }

    /**
     * Update an account for user
     *
     * @param idUser The user id.
     * @param email The user email.
     * @param password The user password.
     * @param firstname The user firstname.
     * @param lastname The user lastname.
     */
    public void update(final int idUser, final String email, final String password, final String firstname, final String lastname) {
        Logger.log(Logger.Level.DEBUG, TAG, "create");
        profileApi.update(idUser, email, password, firstname, lastname)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        p -> onUpdateRequestSuccess(p),
                        this::onUpdateRequestFailed
                );
    }

    private void onUpdateRequestSuccess(final JsonElement element) {
        Logger.log(Logger.Level.DEBUG, TAG, "onUpdateRequestSuccess");
        final UpdateRequestSuccessEvent event = new UpdateRequestSuccessEvent();
        event.element = element;
        BusManager.instance().send(event);
    }


    private void onUpdateRequestFailed(final Throwable error) {
        Logger.log(Logger.Level.DEBUG, TAG, "onUpdateRequestFailed");

        final UpdateRequestFailedEvent event = new UpdateRequestFailedEvent();
        BusManager.instance().send(event);
    }

    /**
     * Get user informations
     *
     * @param idUser The user id.
     */
    public void user(final int idUser) {
        Logger.log(Logger.Level.DEBUG, TAG, "create");
        profileApi.user(idUser)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        p -> onUserRequestSuccess(p),
                        this::onUserRequestFailed
                );
    }

    private void onUserRequestSuccess(final JsonElement element) {
        Logger.log(Logger.Level.DEBUG, TAG, "onUserRequestSuccess");
        final UserRequestSuccessEvent event = new UserRequestSuccessEvent();
        event.element = element;
        BusManager.instance().send(event);
    }


    private void onUserRequestFailed(final Throwable error) {
        Logger.log(Logger.Level.DEBUG, TAG, "onUserRequestFailed");

        final UserRequestFailedEvent event = new UserRequestFailedEvent();
        BusManager.instance().send(event);
    }
}
