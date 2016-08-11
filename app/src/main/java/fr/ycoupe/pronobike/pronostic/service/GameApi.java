package fr.ycoupe.pronobike.pronostic.service;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by yanncoupe on 08/08/2016.
 */
public interface GameApi {

    /**
     * Delete game
     *
     * @param idGame The game id
     * @return The status
     */
    @FormUrlEncoded
    @POST("/delete")
    Observable<JsonElement> delete(
            @Field("id_game") final int idGame);

    /**
     * Bet on a game
     *
     * @param first First pilot id
     * @param second Second pilot id
     * @param third Third pilot id
     * @param idRace Race id
     * @param idUser User id
     * @param idGame The game id
     * @param idPronostic The current pronostic id
     * @return The status
     */
    @FormUrlEncoded
    @POST("/bet")
    Observable<JsonElement> bet(
            @Field("first") final int first,
            @Field("second") final int second,
            @Field("third") final int third,
            @Field("race_id") final int idRace,
            @Field("game_id") final int idGame,
            @Field("user_id") final int idUser,
            @Field("id_pronostic") final Object idPronostic);

    /**
     * Get rank of friends
     *
     * @param ids Array of common games with friends
     * @return Rank
     */
    @FormUrlEncoded
    @POST("/rank")
    Observable<JsonElement> rank(
            @Field("ids") final String ids);

    /**
     * Join a game
     *
     * @param idUser user identifiant
     * @param token game identifiant
     * @return status
     */
    @FormUrlEncoded
    @POST("/join")
    Observable<JsonElement> join(
            @Field("id_user") final int idUser,
            @Field("token") final String token);

    /**
     * Create a game
     *
     * @param idUser user identifiant
     * @param name name of the game
     * @param competitionId id of the competition
     * @return status
     */
    @FormUrlEncoded
    @POST("/create")
    Observable<JsonElement> create(
            @Field("id_user") final int idUser,
            @Field("name") final String name,
            @Field("id_competition") final int competitionId);
}
