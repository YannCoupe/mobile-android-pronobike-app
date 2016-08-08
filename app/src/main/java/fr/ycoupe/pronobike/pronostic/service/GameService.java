package fr.ycoupe.pronobike.pronostic.service;

import com.google.gson.JsonElement;

import fr.ycoupe.pronobike.BuildConfig;
import fr.ycoupe.pronobike.authentication.bus.out.ProfileRequestFailedEvent;
import fr.ycoupe.pronobike.authentication.bus.out.ProfileRequestSuccessEvent;
import fr.ycoupe.pronobike.authentication.service.ProfileManager;
import fr.ycoupe.pronobike.models.Pronostic;
import fr.ycoupe.pronobike.pronostic.bus.out.BetFailedEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.BetSuccessEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.GameDeletedFailedEvent;
import fr.ycoupe.pronobike.pronostic.bus.out.GameDeletedSuccessEvent;
import fr.ycoupe.pronobike.utils.BusManager;
import fr.ycoupe.pronobike.utils.Logger;
import fr.ycoupe.pronobike.utils.RestUtils;
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
}
