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
public class RankDAO {
    private final static String TAG = RankDAO.class.getSimpleName();

    public static void saveRanks(Context context, JSONArray ranks){

        if(ranks == null) return;

        QueriesLibrary queriesLibrary = null;

        try {

            while (App.baseIsLocked) {
                SystemClock.sleep(100);
            }

            App.baseIsLocked = true;

            queriesLibrary = new QueriesLibrary(context);
            SQLiteDatabase base = queriesLibrary.getWritableDatabase();

            base.execSQL("BEGIN;");

            String requestSQL = "INSERT OR REPLACE INTO rank (";
            requestSQL += "id_rank, ";
            requestSQL += "first, ";
            requestSQL += "second, ";
            requestSQL += "third, ";
            requestSQL += "race_id ";
            requestSQL += ") VALUES (?, ?, ?, ?, ?)";

            for (int i=0; i < ranks.length(); i++) {

                JSONObject raceJson = ranks.getJSONObject(i);

                try {

                    Object[] args = new Object[5];
                    args[0] = raceJson.has("id_rank") ? raceJson.getInt("id_rank") : null;
                    args[1] = raceJson.has("first") ? raceJson.getInt("first") : null;
                    args[2] = raceJson.has("second") ? raceJson.getInt("second") : null;
                    args[3] = raceJson.has("third") ? raceJson.getInt("third") : null;
                    args[4] = raceJson.has("race_id") ? raceJson.getInt("race_id") : null;

                    base.execSQL(requestSQL, args);

                } catch(Exception e){

                    Logger.log(Logger.Level.WARNING, TAG, "error while saving ranks : " + e);

                }

            }

            base.execSQL("COMMIT;");

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while saving ranks : " + e);
        } finally {

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while saving ranks : " + e);
                }
            }
        }

        App.baseIsLocked = false;

    }

    public static void deleteRanks(Context context, JSONArray items){

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

            String requestSQL = "DELETE FROM rank ";
            requestSQL += "WHERE id_rank = ? ";

            for(int i = 0; i < items.length(); i++){

                int item = items.getInt(i);

                try {

                    Object[] args = new Object[1];
                    args[0] = item;

                    base.execSQL(requestSQL, args);

                } catch(Exception e){

                    Logger.log(Logger.Level.WARNING, TAG, "error while deleting ranks : " + e);

                }

            }

            base.execSQL("COMMIT;");

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while deleting ranks : " + e);
        } finally {

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while deleting ranks : " + e);
                }
            }
        }

        App.baseIsLocked = false;


    }

}
