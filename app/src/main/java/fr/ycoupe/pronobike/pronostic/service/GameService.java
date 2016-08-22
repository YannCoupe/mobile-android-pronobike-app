package fr.ycoupe.pronobike.pronostic.service;

import com.google.gson.JsonElement;

import java.util.ArrayList;

import fr.ycoupe.pronobike.BuildConfig;
import fr.ycoupe.pronobike.authentication.service.ProfileManager;
import fr.ycoupe.pronobike.models.Competition;
import fr.ycoupe.pronobike.models.Game;
import fr.ycoupe.pronobike.models.Pronostic;
import fr.ycoupe.pronobike.pronostic.bus.out.BetFailedEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.BetSuccessEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.GameCreatedFailedEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.GameCreatedSuccessEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.GameDeletedFailedEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.GameDeletedSuccessEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.JoinFailedEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.JoinSuccessEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.PronosticsFailedEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.PronosticsSuccessEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.RankFailedEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.RankSuccessEvent;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;
import fr.ycoupe.pronobike.utils.RestUtils;
import fr.ycoupe.pronobike.utils.StringUtils;
import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by yanncoupe on 08/08/2016.
 */
public class GameService {

    private final static String TAG = GameService.class.getSimpleName();

    private final GameApi gameApi;

    public GameService() {
        final Retrofit retrofit = RestUtils.makeAdapter(BuildConfig.SERVICE_ENDPOINT);
        gameApi = retrofit.create(GameApi.class);
    }

    /**
     * Delete game
     *
     * @param idGame The game id
     */
    public void deleteGame(final int idGame) {
        Logger.log(Logger.Level.DEBUG, TAG, "deleteGame");

        gameApi.delete(idGame)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        p -> onGameDeletedSuccess(p),
                        this::onGameDeletedFailed
                );
    }

    private void onGameDeletedSuccess(final JsonElement element) {
        Logger.log(Logger.Level.DEBUG, TAG, "onGameDeletedSuccess");

        final GameDeletedSuccessEvent event = new GameDeletedSuccessEvent();
        event.status = element;
        BusManager.instance().send(event);
    }


    private void onGameDeletedFailed(final Throwable error) {
        Logger.log(Logger.Level.DEBUG, TAG, "onGameDeletedFailed");

        final GameDeletedFailedEvent event = new GameDeletedFailedEvent();
        BusManager.instance().send(event);
    }

    /**
     * Bet on a game
     *
     * @param pronostic The pronostic of the game
     */
    public void bet(final Pronostic pronostic) {
        Logger.log(Logger.Level.DEBUG, TAG, "bet");

        gameApi.bet(
                pronostic.getFirst(),
                pronostic.getSecond(),
                pronostic.getThird(),
                pronostic.getRaceId(),
                pronostic.getGameId(),
                ProfileManager.instance().profile.getIdUser(),
                pronostic.getIdPronostic() > 0 ? pronostic.getIdPronostic() : null)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        p -> onBetSuccess(p),
                        this::onBetFailed
                );
    }

    private void onBetSuccess(final JsonElement element) {
        Logger.log(Logger.Level.DEBUG, TAG, "onBetSuccess");

        final BetSuccessEvent event = new BetSuccessEvent();
        event.status = element;
        BusManager.instance().send(event);
    }


    private void onBetFailed(final Throwable error) {
        Logger.log(Logger.Level.DEBUG, TAG, "onBetFailed");

        final BetFailedEvent event = new BetFailedEvent();
        BusManager.instance().send(event);
    }

    /**
     * Get rank with firends
     *
     * @param games: List of common games
     */
    public void rank(ArrayList<Game> games) {
        Logger.log(Logger.Level.DEBUG, TAG, "bet");

        final ArrayList<String> ids = new ArrayList<>();

        for(final Game game : games){
            ids.add(String.valueOf(game.getIdGame()));
        }

        gameApi.rank(
                StringUtils.join(ids, ","))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        p -> onRankSuccess(p),
                        this::onRankFailed
                );
    }

    private void onRankSuccess(final JsonElement element) {
        Logger.log(Logger.Level.DEBUG, TAG, "onRankSuccess");

        final RankSuccessEvent event = new RankSuccessEvent();
        event.element = element;
        BusManager.instance().send(event);
    }


    private void onRankFailed(final Throwable error) {
        Logger.log(Logger.Level.DEBUG, TAG, "onRankFailed");

        final RankFailedEvent event = new RankFailedEvent();
        BusManager.instance().send(event);
    }

    /**
     * Join a game
     *
     * @param token The game identifiant
     */
    public void join(final String token) {
        Logger.log(Logger.Level.DEBUG, TAG, "join");

        gameApi.join(ProfileManager.instance().profile.getIdUser(), token)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        p -> onJoinSuccess(p),
                        this::onJoinFailed
                );
    }

    private void onJoinSuccess(final JsonElement element) {
        Logger.log(Logger.Level.DEBUG, TAG, "onJoinSuccess");

        final JoinSuccessEvent event = new JoinSuccessEvent();
        event.status = element;
        BusManager.instance().send(event);
    }


    private void onJoinFailed(final Throwable error) {
        Logger.log(Logger.Level.DEBUG, TAG, "onJoinFailed");

        final JoinFailedEvent event = new JoinFailedEvent();
        BusManager.instance().send(event);
    }

    /**
     * Create a game
     *
     * @param name The game name
     * @param competition The {@link Competition}
     */
    public void create(final String name, final Competition competition) {
        Logger.log(Logger.Level.DEBUG, TAG, "create");

        gameApi.create(ProfileManager.instance().profile.getIdUser(), name, competition.getIdCompetition())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        p -> onCreateSuccess(p),
                        this::onCreateFailed
                );
    }

    private void onCreateSuccess(final JsonElement element) {
        Logger.log(Logger.Level.DEBUG, TAG, "onCreateSuccess");

        final GameCreatedSuccessEvent event = new GameCreatedSuccessEvent();
        event.status = element;
        BusManager.instance().send(event);
    }


    private void onCreateFailed(final Throwable error) {
        Logger.log(Logger.Level.DEBUG, TAG, "onCreateFailed");

        final GameCreatedFailedEvent event = new GameCreatedFailedEvent();
        BusManager.instance().send(event);
    }

    /**
     * Get list of all previous pronostics
     *
     * @param idGame The game identifiant
     */
    public void pronostics(final int idGame) {
        Logger.log(Logger.Level.DEBUG, TAG, "pronostics");

        gameApi.pronostics(idGame)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        p -> onPronosticsSuccess(p),
                        this::onPronosticsFailed
                );
    }

    private void onPronosticsSuccess(final JsonElement element) {
        Logger.log(Logger.Level.DEBUG, TAG, "onPronosticsSuccess");

        final PronosticsSuccessEvent event = new PronosticsSuccessEvent();
        event.element = element;
        BusManager.instance().send(event);
    }


    private void onPronosticsFailed(final Throwable error) {
        Logger.log(Logger.Level.DEBUG, TAG, "onPronosticsFailed");

        final PronosticsFailedEvent event = new PronosticsFailedEvent();
        BusManager.instance().send(event);
    }
}
