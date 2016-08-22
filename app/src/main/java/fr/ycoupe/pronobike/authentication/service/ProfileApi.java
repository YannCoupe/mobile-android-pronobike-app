package fr.ycoupe.pronobike.authentication.service;

import com.google.gson.JsonElement;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * API definition for user profile service.
 */
public interface ProfileApi {

    /**
     * Get the profile of the user defined by the given login/mdp.
     *
     * @param email The user email.
     * @param password The user password.
     * @return The associated profile.
     */
    @FormUrlEncoded
    @POST("/login")
    Observable<JsonElement> getProfile(
            @Field("email") final String email,
            @Field("password") final String password);

    /**
     * Generate a new password
     *
     * @param email The user email.
     * @return the status
     */
    @FormUrlEncoded
    @POST("/forget")
    Observable<JsonElement> getPassword(
            @Field("email") final String email);

    /**
     * Create a new account
     *
     * @param email The user email.
     * @param password The user password.
     * @param firstname The user firstname.
     * @param lastname The user lastname.
     * @return the status
     */
    @FormUrlEncoded
    @POST("/user")
    Observable<JsonElement> create(
            @Field("email") final String email,
            @Field("password") final String password,
            @Field("firstname") final String firstname,
            @Field("lastname") final String lastname);

    /**
     * Update an account
     *
     * @param idUser The user id.
     * @param email The user email.
     * @param password The user password.
     * @param firstname The user firstname.
     * @param lastname The user lastname.
     * @return the status
     */
    @FormUrlEncoded
    @POST("/user")
    Observable<JsonElement> update(
            @Field("id_user") final int idUser,
            @Field("email") final String email,
            @Field("password") final String password,
            @Field("firstname") final String firstname,
            @Field("lastname") final String lastname);

    /**
     * Get user informations
     *
     * @param idUser The user id.
     * @return the status
     */
    @FormUrlEncoded
    @POST("/user")
    Observable<JsonElement> user(
            @Field("id_user") final int idUser);
}
