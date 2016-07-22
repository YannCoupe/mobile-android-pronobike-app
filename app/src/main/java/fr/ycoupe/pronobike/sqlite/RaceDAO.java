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
public class RaceDAO {
    private final static String TAG = RaceDAO.class.getSimpleName();

    public static void saveRaces(Context context, JSONArray races){

        if(races == null) return;

        QueriesLibrary queriesLibrary = null;

        try {

            while (App.baseIsLocked) {
                SystemClock.sleep(100);
            }

            App.baseIsLocked = true;

            queriesLibrary = new QueriesLibrary(context);
            SQLiteDatabase base = queriesLibrary.getWritableDatabase();

            base.execSQL("BEGIN;");

            String requestSQL = "INSERT OR REPLACE INTO race (";
            requestSQL += "id_race, ";
            requestSQL += "date, ";
            requestSQL += "circuit_id, ";
            requestSQL += "competition_id ";
            requestSQL += ") VALUES (?, ?, ?, ?)";

            for (int i=0; i < races.length(); i++) {

                JSONObject raceJson = races.getJSONObject(i);

                try {

                    Object[] args = new Object[4];
                    args[0] = raceJson.has("id_race") ? raceJson.getInt("id_race") : null;
                    args[1] = raceJson.has("date") ? raceJson.getString("date") : null;
                    args[2] = raceJson.has("circuit_id") ? raceJson.getInt("circuit_id") : null;
                    args[3] = raceJson.has("competition_id") ? raceJson.getInt("competition_id") : null;

                    base.execSQL(requestSQL, args);

                } catch(Exception e){

                    Logger.log(Logger.Level.WARNING, TAG, "error while saving races : " + e);

                }

            }

            base.execSQL("COMMIT;");

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while saving races : " + e);
        } finally {

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while saving races : " + e);
                }
            }
        }

        App.baseIsLocked = false;

    }

    public static void deleteRaces(Context context, JSONArray items){

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

            String requestSQL = "DELETE FROM race ";
            requestSQL += "WHERE id_race = ? ";

            for(int i = 0; i < items.length(); i++){

                int item = items.getInt(i);

                try {

                    Object[] args = new Object[1];
                    args[0] = item;

                    base.execSQL(requestSQL, args);

                } catch(Exception e){

                    Logger.log(Logger.Level.WARNING, TAG, "error while deleting races : " + e);

                }

            }

            base.execSQL("COMMIT;");

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while deleting races : " + e);
        } finally {

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while deleting races : " + e);
                }
            }
        }

        App.baseIsLocked = false;


    }

}
