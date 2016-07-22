package fr.ycoupe.pronobike.configuration.service;

import com.google.gson.JsonElement;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * API definition for user profile service.
 */
public interface ConfigurationApi {

    /**
     * Get the configuration of the app
     *
     * @param lastupdate The lastupdate of the call configratioj.
     * @return The configuration.
     */
    @FormUrlEncoded
    @POST("/configuration")
    Observable<JsonElement> getConfiguration(
            @Field("lastupdate") final long lastupdate);
}
