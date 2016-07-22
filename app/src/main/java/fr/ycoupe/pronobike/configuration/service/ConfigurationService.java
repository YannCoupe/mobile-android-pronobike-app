package fr.ycoupe.pronobike.configuration.service;

import com.google.gson.JsonElement;

import fr.ycoupe.pronobike.BuildConfig;
import fr.ycoupe.pronobike.authentication.bus.out.CreateRequestFailedEvent;
import fr.ycoupe.pronobike.authentication.bus.out.CreateRequestSuccessEvent;
import fr.ycoupe.pronobike.authentication.bus.out.PasswordRequestFailedEvent;
import fr.ycoupe.pronobike.authentication.bus.out.PasswordRequestSuccessEvent;
import fr.ycoupe.pronobike.authentication.bus.out.ProfileRequestFailedEvent;
import fr.ycoupe.pronobike.authentication.bus.out.ProfileRequestSuccessEvent;
import fr.ycoupe.pronobike.authentication.service.ProfileApi;
import fr.ycoupe.pronobike.configuration.bus.out.ConfigurationSuccessEvent;
import fr.ycoupe.pronobike.profile.bus.out.UpdateRequestFailedEvent;
import fr.ycoupe.pronobike.profile.bus.out.UpdateRequestSuccessEvent;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;
import fr.ycoupe.pronobike.utils.RestUtils;
import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yanncoupe on 14/07/2016.
 */
public class ConfigurationService {

    private final static String TAG = ConfigurationService.class.getSimpleName();

    private final ConfigurationApi configurationApi;

    public ConfigurationService() {
        final Retrofit retrofit = RestUtils.makeAdapter(BuildConfig.SERVICE_ENDPOINT);
        configurationApi = retrofit.create(ConfigurationApi.class);
    }

    /**
     * Get configuration of app
     *
     * @param lastupdate the lastupdate of configuration.
     */
    public void getConfiguration(final long lastupdate) {
        Logger.log(Logger.Level.DEBUG, TAG, "getConfiguration");

        configurationApi.getConfiguration(lastupdate)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        p -> onConfigurationRequestSucess(p),
                        this::onConfigurationRequestFailed
                );
    }

    private void onConfigurationRequestSucess(final JsonElement element) {
        Logger.log(Logger.Level.DEBUG, TAG, "onConfigurationRequestSucess");

        final ConfigurationSuccessEvent event = new ConfigurationSuccessEvent();
        event.element = element;
        BusManager.instance().send(event);
    }


    private void onConfigurationRequestFailed(final Throwable error) {
        Logger.log(Logger.Level.DEBUG, TAG, "onConfigurationRequestFailed");
    }
}
