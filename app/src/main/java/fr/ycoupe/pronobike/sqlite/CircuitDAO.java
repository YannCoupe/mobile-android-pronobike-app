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
public class CircuitDAO {
    private final static String TAG = CircuitDAO.class.getSimpleName();

    public static void saveCircuits(Context context, JSONArray circuits){

        if(circuits == null) return;

        QueriesLibrary queriesLibrary = null;

        try {

            while (App.baseIsLocked) {
                SystemClock.sleep(100);
            }

            App.baseIsLocked = true;

            queriesLibrary = new QueriesLibrary(context);
            SQLiteDatabase base = queriesLibrary.getWritableDatabase();

            base.execSQL("BEGIN;");

            String requestSQL = "INSERT OR REPLACE INTO circuit (";
            requestSQL += "id_circuit, ";
            requestSQL += "name, ";
            requestSQL += "localisation, ";
            requestSQL += "country_code ";
            requestSQL += ") VALUES (?, ?, ?, ?)";

            for (int i=0; i < circuits.length(); i++) {

                JSONObject circuitJson = circuits.getJSONObject(i);

                try {

                    Object[] args = new Object[4];
                    args[0] = circuitJson.has("id_circuit") ? circuitJson.getInt("id_circuit") : null;
                    args[1] = circuitJson.has("name") ? circuitJson.getString("name") : null;
                    args[2] = circuitJson.has("localisation") ? circuitJson.getString("localisation") : null;
                    args[3] = circuitJson.has("country") ? circuitJson.getString("country") : null;

                    base.execSQL(requestSQL, args);

                } catch(Exception e){

                    Logger.log(Logger.Level.WARNING, TAG, "error while saving circuits : " + e);

                }

            }

            base.execSQL("COMMIT;");

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while saving circuits : " + e);
        } finally {

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while saving circuits : " + e);
                }
            }
        }

        App.baseIsLocked = false;

    }

    public static void deleteCircuits(Context context, JSONArray items){

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

            String requestSQL = "DELETE FROM circuit ";
            requestSQL += "WHERE id_circuit = ? ";

            for(int i = 0; i < items.length(); i++){

                int item = items.getInt(i);

                try {

                    Object[] args = new Object[1];
                    args[0] = item;

                    base.execSQL(requestSQL, args);

                } catch(Exception e){

                    Logger.log(Logger.Level.WARNING, TAG, "error while deleting circuits : " + e);

                }

            }

            base.execSQL("COMMIT;");

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while deleting circuits : " + e);
        } finally {

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while deleting circuits : " + e);
                }
            }
        }

        App.baseIsLocked = false;


    }

}
