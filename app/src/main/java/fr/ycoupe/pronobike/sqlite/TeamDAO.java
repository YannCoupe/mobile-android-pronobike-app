package fr.ycoupe.pronobike.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import org.json.JSONArray;
import org.json.JSONObject;

import fr.ycoupe.pronobike.App;
import fr.ycoupe.pronobike.utils.Logger;

/**
 * Created by Yann on 22/02/16.
 */
public class TeamDAO {
    private final static String TAG = TeamDAO.class.getSimpleName();

    public static void saveTeams(Context context, JSONArray teams){

        if(teams == null) return;

        QueriesLibrary queriesLibrary = null;

        try {

            while (App.baseIsLocked) {
                SystemClock.sleep(100);
            }

            App.baseIsLocked = true;

            queriesLibrary = new QueriesLibrary(context);
            SQLiteDatabase base = queriesLibrary.getWritableDatabase();

            base.execSQL("BEGIN;");

            String requestSQL = "INSERT OR REPLACE INTO team (";
            requestSQL += "id_team, ";
            requestSQL += "name, ";
            requestSQL += "description ";
            requestSQL += ") VALUES (?, ?, ?)";

            for (int i=0; i < teams.length(); i++) {

                JSONObject raceJson = teams.getJSONObject(i);

                try {

                    Object[] args = new Object[3];
                    args[0] = raceJson.has("id_team") ? raceJson.getInt("id_team") : null;
                    args[1] = raceJson.has("name") ? raceJson.getString("name") : null;
                    args[2] = raceJson.has("description") ? raceJson.getString("description") : null;

                    base.execSQL(requestSQL, args);

                } catch(Exception e){

                    Logger.log(Logger.Level.WARNING, TAG, "error while saving teams : " + e);

                }

            }

            base.execSQL("COMMIT;");

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while saving teams : " + e);
        } finally {

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while saving teams : " + e);
                }
            }
        }

        App.baseIsLocked = false;

    }

    public static void deleteTeams(Context context, JSONArray items){

        if(items == null || items.length() == 0) return;

        QueriesLibrary queriesLibrary = null;

        try {

            while (App.baseIsLocked) {
                SystemClock.sleep(100);
            }

            App.baseIsLocked = true;

            queriesLibrary = new QueriesLibrary(context);
            SQLiteDatabase base = queriesLibrary.getWritableDatabase();

            base.execSQL("BEGIN;");

            String requestSQL = "DELETE FROM team ";
            requestSQL += "WHERE id_team = ? ";

            for(int i = 0; i < items.length(); i++){

                int item = items.getInt(i);

                try {

                    Object[] args = new Object[1];
                    args[0] = item;

                    base.execSQL(requestSQL, args);

                } catch(Exception e){

                    Logger.log(Logger.Level.WARNING, TAG, "error while deleting teams : " + e);

                }

            }

            base.execSQL("COMMIT;");

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while deleting teams : " + e);
        } finally {

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while deleting teams : " + e);
                }
            }
        }

        App.baseIsLocked = false;


    }

    public static void saveTeamsCompetitions(Context context, JSONArray teams){

        if(teams == null) return;

        QueriesLibrary queriesLibrary = null;

        try {

            while (App.baseIsLocked) {
                SystemClock.sleep(100);
            }

            App.baseIsLocked = true;

            queriesLibrary = new QueriesLibrary(context);
            SQLiteDatabase base = queriesLibrary.getWritableDatabase();

            base.execSQL("BEGIN;");

            String requestSQL = "INSERT OR REPLACE INTO team_competition (";
            requestSQL += "team_id, ";
            requestSQL += "competition_id ";
            requestSQL += ") VALUES (?, ?)";

            for (int i=0; i < teams.length(); i++) {

                JSONObject raceJson = teams.getJSONObject(i);

                try {

                    Object[] args = new Object[2];
                    args[0] = raceJson.has("team_id") ? raceJson.getInt("team_id") : null;
                    args[1] = raceJson.has("competition_id") ? raceJson.getInt("competition_id") : null;

                    base.execSQL(requestSQL, args);

                } catch(Exception e){

                    Logger.log(Logger.Level.WARNING, TAG, "error while saving teams competitions : " + e);

                }

            }

            base.execSQL("COMMIT;");

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while saving teams competitions : " + e);
        } finally {

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while saving teams : " + e);
                }
            }
        }

        App.baseIsLocked = false;

    }

    public static void deleteTeamsCompetitions(Context context, JSONArray teams){

        if(teams == null || teams.length() == 0) return;

        QueriesLibrary queriesLibrary = null;

        try {

            while (App.baseIsLocked) {
                SystemClock.sleep(100);
            }

            App.baseIsLocked = true;

            queriesLibrary = new QueriesLibrary(context);
            SQLiteDatabase base = queriesLibrary.getWritableDatabase();

            base.execSQL("BEGIN;");

            String requestSQL = "DELETE FROM team ";
            requestSQL += "WHERE team_id = ? AND competition_id = ? ";

            for (int i=0; i < teams.length(); i++) {

                JSONObject raceJson = teams.getJSONObject(i);

                try {

                    Object[] args = new Object[2];
                    args[0] = raceJson.has("team_id") ? raceJson.getInt("team_id") : null;
                    args[1] = raceJson.has("competition_id") ? raceJson.getInt("competition_id") : null;

                    base.execSQL(requestSQL, args);

                } catch(Exception e){

                    Logger.log(Logger.Level.WARNING, TAG, "error while deleting teams competitions : " + e);

                }

            }

            base.execSQL("COMMIT;");

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while deleting teams : " + e);
        } finally {

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while deleting teams : " + e);
                }
            }
        }

        App.baseIsLocked = false;


    }


}
