package fr.ycoupe.pronobike.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.ycoupe.pronobike.App;
import fr.ycoupe.pronobike.models.Game;
import fr.ycoupe.pronobike.models.Pronostic;
import fr.ycoupe.pronobike.models.RankGame;
import fr.ycoupe.pronobike.utils.Logger;

/**
 * Created by Yann on 22/02/16.
 */
public class GameDAO {
    private final static String TAG = GameDAO.class.getSimpleName();

    public static void saveGames(Context context, JSONArray games){

        QueriesLibrary queriesLibrary = null;

        try {

            while (App.baseIsLocked) {
                SystemClock.sleep(100);
            }

            App.baseIsLocked = true;

            queriesLibrary = new QueriesLibrary(context);
            SQLiteDatabase base = queriesLibrary.getWritableDatabase();

            base.execSQL("BEGIN;");

            String requestSQL = "DELETE FROM game";

            base.execSQL(requestSQL);

            requestSQL = "DELETE FROM rank_game";

            base.execSQL(requestSQL);

            requestSQL = "DELETE FROM pronostic";

            base.execSQL(requestSQL);

            if(games != null && games.length() > 0){

                String requestGame = "INSERT INTO game (";
                requestGame += "id_game, ";
                requestGame += "name, ";
                requestGame += "token, ";
                requestGame += "competition_id, ";
                requestGame += "admin ";
                requestGame += ") VALUES (?, ?, ?, ?, ?)";

                String requestRank = "INSERT INTO rank_game (";
                requestRank += "game_id, ";
                requestRank += "user_id, ";
                requestRank += "firstname, ";
                requestRank += "lastname, ";
                requestRank += "first, ";
                requestRank += "second, ";
                requestRank += "third, ";
                requestRank += "total, ";
                requestRank += "position ";
                requestRank += ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                String requestProno = "INSERT INTO pronostic (";
                requestProno += "id_pronostic, ";
                requestProno += "game_id, ";
                requestProno += "race_id, ";
                requestProno += "first, ";
                requestProno += "second, ";
                requestProno += "third ";
                requestProno += ") VALUES (?, ?, ?, ?, ?, ?)";

                for (int i=0; i < games.length(); i++) {

                    JSONObject gameJon = games.getJSONObject(i);

                    try {

                        Object[] args = new Object[5];
                        args[0] = gameJon.has("id_game") ? gameJon.getInt("id_game") : null;
                        args[1] = gameJon.has("name") ? gameJon.getString("name") : null;
                        args[2] = gameJon.has("token") ? gameJon.getString("token") : null;
                        args[3] = gameJon.has("competition_id") ? gameJon.getInt("competition_id") : null;
                        args[4] = gameJon.has("admin") ? gameJon.getInt("admin") : null;

                        base.execSQL(requestGame, args);

                        JSONArray ranksArray = gameJon.has("ranks") ? gameJon.getJSONArray("ranks") : null;

                        if(ranksArray != null){

                            for (int r=0; r < ranksArray.length(); r++) {

                                JSONObject rankJson = ranksArray.getJSONObject(r);

                                Object[] argsRank = new Object[9];
                                argsRank[0] = args[0];
                                argsRank[1] = rankJson.has("user_id") ? rankJson.getInt("user_id") : null;
                                argsRank[2] = rankJson.has("firstname") ? rankJson.getString("firstname") : null;
                                argsRank[3] = rankJson.has("lastname") ? rankJson.getString("lastname") : null;
                                argsRank[4] = rankJson.has("first") ? rankJson.getInt("first") : null;
                                argsRank[5] = rankJson.has("second") ? rankJson.getInt("second") : null;
                                argsRank[6] = rankJson.has("third") ? rankJson.getInt("third") : null;
                                argsRank[7] = rankJson.has("total") ? rankJson.getInt("total") : null;
                                argsRank[8] = rankJson.has("position") ? rankJson.getInt("position") : null;

                                base.execSQL(requestRank, argsRank);

                            }


                        }

                        JSONArray pronosArray = gameJon.has("pronostics") ? gameJon.getJSONArray("pronostics") : null;

                        if(pronosArray != null){

                            for (int p=0; p < pronosArray.length(); p++) {

                                JSONObject pronoJson = pronosArray.getJSONObject(p);

                                Object[] argsPronos = new Object[6];
                                argsPronos[0] = pronoJson.has("id_pronostic") ? pronoJson.getInt("id_pronostic") : null;
                                argsPronos[1] = pronoJson.has("game_id") ? pronoJson.getInt("game_id") : null;
                                argsPronos[2] = pronoJson.has("race_id") ? pronoJson.getInt("race_id") : null;
                                argsPronos[3] = pronoJson.has("first") ? pronoJson.getInt("first") : null;
                                argsPronos[4] = pronoJson.has("second") ? pronoJson.getInt("second") : null;
                                argsPronos[5] = pronoJson.has("third") ? pronoJson.getInt("third") : null;

                                base.execSQL(requestProno, argsPronos);

                            }


                        }

                    } catch(Exception e){

                        Logger.log(Logger.Level.WARNING, TAG, "error while saving games : " + e);

                    }

                }

            }

            base.execSQL("COMMIT;");

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while saving games : " + e);
        } finally {

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while saving games : " + e);
                }
            }
        }

        App.baseIsLocked = false;

    }

    public static void deleteGames(Context context){

        QueriesLibrary queriesLibrary = null;

        try {

            while (App.baseIsLocked) {
                SystemClock.sleep(100);
            }

            App.baseIsLocked = true;

            queriesLibrary = new QueriesLibrary(context);
            SQLiteDatabase base = queriesLibrary.getWritableDatabase();

            base.execSQL("BEGIN;");

            String requestSQL = "DELETE FROM game ";

            base.execSQL(requestSQL);

            base.execSQL("COMMIT;");

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while deleting games : " + e);
        } finally {

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while deleting games : " + e);
                }
            }
        }

        App.baseIsLocked = false;


    }

    public static ArrayList<Game> gamesWithIdUser(Context context, int idUser){

        QueriesLibrary queriesLibrary = null;

        ArrayList<Game> games = new ArrayList<Game>();
        Cursor cursor = null;

        try {

            while (App.baseIsLocked) {
                SystemClock.sleep(100);
            }

            App.baseIsLocked = true;

            queriesLibrary = new QueriesLibrary(context);
            SQLiteDatabase base = queriesLibrary.getWritableDatabase();

            String request = "SELECT COALESCE(g.id_game, 0), ";
            request += "COALESCE(g.name, ''), ";
            request += "COALESCE(g.token, ''), ";
            request += "COALESCE(g.admin, 0), ";
            request += "COALESCE(co.name, ''), ";
            request += "COALESCE(rg.position, 0), ";
            request += "(SELECT r0.id_race FROM race r0 WHERE r0.competition_id = co.id_competition AND r0.date > date('now') ORDER BY r0.date ASC LIMIT 1) as id_race, ";
            request += "(SELECT r1.date FROM race r1 WHERE r1.competition_id = co.id_competition AND r1.date > date('now') ORDER BY r1.date ASC LIMIT 1) as date_race, ";
            request += "(SELECT c1.name FROM race r2 LEFT JOIN circuit c1 ON r2.circuit_id = c1.id_circuit WHERE r2.competition_id = co.id_competition AND r2.date > date('now') ORDER BY r2.date ASC LIMIT 1) as circuit_race ";
            request += "FROM game g ";
            request += "LEFT JOIN competition co ON co.id_competition = g.competition_id ";
            request += "LEFT JOIN rank_game rg ON rg.game_id = g.id_game AND rg.user_id = ? ";
            request += "GROUP BY g.id_game";

            String args[] = new String[1];
            args[0] = String.valueOf(idUser);

            cursor = base.rawQuery(request, args);

            while (cursor.moveToNext()) {

                Game game = new Game();
                game.setIdGame(cursor.getInt(0));
                game.setName(cursor.getString(1));
                game.setToken(cursor.getString(2));
                game.setAdmin(cursor.getInt(3));
                game.setCompetitionRace(cursor.getString(4));
                game.setPositionUser(cursor.getInt(5));
                if(!cursor.isNull(6)) game.setIdRace(cursor.getInt(6));
                if(!cursor.isNull(7)) game.setDateRace(cursor.getString(7));
                if(!cursor.isNull(8)) game.setCircuitRace(cursor.getString(8));

                games.add(game);

            }

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while getting games : " + e);
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while getting games : " + e);
                }
            }
        }

        App.baseIsLocked = false;

        return games;

    }

    public static ArrayList<RankGame> gameWithId(Context context, int idGame){

        QueriesLibrary queriesLibrary = null;

        ArrayList<RankGame> ranks = new ArrayList<RankGame>();
        Cursor cursor = null;

        try {

            while (App.baseIsLocked) {
                SystemClock.sleep(100);
            }

            App.baseIsLocked = true;

            queriesLibrary = new QueriesLibrary(context);
            SQLiteDatabase base = queriesLibrary.getWritableDatabase();

            String request = "SELECT COALESCE(user_id, 0), ";
            request += "COALESCE(firstname, ''), ";
            request += "COALESCE(lastname, ''), ";
            request += "COALESCE(first, 0), ";
            request += "COALESCE(second, 0), ";
            request += "COALESCE(third, 0), ";
            request += "COALESCE(total, 0), ";
            request += "COALESCE(position, 0) ";
            request += "FROM rank_game ";
            request += "WHERE game_id = ? ";

            String args[] = new String[1];
            args[0] = String.valueOf(idGame);

            cursor = base.rawQuery(request, args);

            while (cursor.moveToNext()) {

                RankGame game = new RankGame();
                game.setUserId(cursor.getInt(0));
                game.setFirstname(cursor.getString(1));
                game.setLastname(cursor.getString(2));
                game.setFirst(cursor.getInt(3));
                game.setSecond(cursor.getInt(4));
                game.setThird(cursor.getInt(5));
                game.setTotal(cursor.getInt(6));
                game.setPosition(cursor.getInt(7));
                ranks.add(game);

            }

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while getting ranks : " + e);
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while getting ranks : " + e);
                }
            }
        }

        App.baseIsLocked = false;

        return ranks;

    }

    public static Pronostic pronosticWithIdGame(Context context, int idGame, int idRace){

        QueriesLibrary queriesLibrary = null;

        Pronostic prono = null;
        Cursor cursor = null;

        try {

            while (App.baseIsLocked) {
                SystemClock.sleep(100);
            }

            App.baseIsLocked = true;

            queriesLibrary = new QueriesLibrary(context);
            SQLiteDatabase base = queriesLibrary.getWritableDatabase();

            String request = "SELECT COALESCE(id_pronostic, 0), ";
            request += "COALESCE(game_id, 0), ";
            request += "COALESCE(race_id, 0), ";
            request += "COALESCE(first, 0), ";
            request += "COALESCE(second, 0), ";
            request += "COALESCE(third, 0) ";
            request += "FROM pronostic ";
            request += "WHERE game_id = ? AND race_id = ?";

            String args[] = new String[2];
            args[0] = String.valueOf(idGame);
            args[1] = String.valueOf(idRace);

            cursor = base.rawQuery(request, args);

            while (cursor.moveToNext()) {

                prono = new Pronostic();
                prono.setIdPronostic(cursor.getInt(0));
                prono.setGameId(cursor.getInt(1));
                prono.setRaceId(cursor.getInt(2));
                prono.setFirst(cursor.getInt(3));
                prono.setSecond(cursor.getInt(4));
                prono.setThird(cursor.getInt(5));

            }

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while getting pronostics : " + e);
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while getting pronostics : " + e);
                }
            }
        }

        App.baseIsLocked = false;

        return prono;

    }

}
