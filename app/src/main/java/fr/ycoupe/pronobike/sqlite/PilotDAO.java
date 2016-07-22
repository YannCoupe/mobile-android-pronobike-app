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
import fr.ycoupe.pronobike.models.Pilot;
import fr.ycoupe.pronobike.utils.Logger;

/**
 * Created by Yann on 22/02/16.
 */
public class PilotDAO {
    private final static String TAG = PilotDAO.class.getSimpleName();

    public static void savePilots(Context context, JSONArray pilots){

        if(pilots == null) return;

        QueriesLibrary queriesLibrary = null;

        try {

            while (App.baseIsLocked) {
                SystemClock.sleep(100);
            }

            App.baseIsLocked = true;

            queriesLibrary = new QueriesLibrary(context);
            SQLiteDatabase base = queriesLibrary.getWritableDatabase();

            base.execSQL("BEGIN;");

            String requestSQL = "INSERT OR REPLACE INTO pilot (";
            requestSQL += "id_pilot, ";
            requestSQL += "firstname, ";
            requestSQL += "lastname, ";
            requestSQL += "nickname, ";
            requestSQL += "number, ";
            requestSQL += "country_code, ";
            requestSQL += "team_id ";
            requestSQL += ") VALUES (?, ?, ?, ?, ?, ?, ?)";

            for (int i=0; i < pilots.length(); i++) {

                JSONObject pilotsJson = pilots.getJSONObject(i);

                try {

                    Object[] args = new Object[7];
                    args[0] = pilotsJson.has("id_pilot") ? pilotsJson.getInt("id_pilot") : null;
                    args[1] = pilotsJson.has("firstname") ? pilotsJson.getString("firstname") : null;
                    args[2] = pilotsJson.has("lastname") ? pilotsJson.getString("lastname") : null;
                    args[3] = pilotsJson.has("nickname") ? pilotsJson.getString("nickname") : null;
                    args[4] = pilotsJson.has("number") ? pilotsJson.getInt("number") : null;
                    args[5] = pilotsJson.has("country") ? pilotsJson.getString("country") : null;
                    args[6] = pilotsJson.has("team_id") ? pilotsJson.getInt("team_id") : null;

                    base.execSQL(requestSQL, args);

                } catch(Exception e){

                    Logger.log(Logger.Level.WARNING, TAG, "error while saving pilots : " + e);

                }

            }

            base.execSQL("COMMIT;");

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while saving pilots : " + e);
        } finally {

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while saving pilots : " + e);
                }
            }
        }

        App.baseIsLocked = false;

    }

    public static ArrayList<Pilot> pilotsWithIdGame(Context context, int idGame){

        QueriesLibrary queriesLibrary = null;

        ArrayList<Pilot> pilots = new ArrayList<Pilot>();
        Cursor cursor = null;

        try {

            while (App.baseIsLocked) {
                SystemClock.sleep(100);
            }

            App.baseIsLocked = true;

            queriesLibrary = new QueriesLibrary(context);
            SQLiteDatabase base = queriesLibrary.getWritableDatabase();

            String request = "SELECT COALESCE(p.id_pilot, 0), ";
            request += "COALESCE(p.firstname, ''), ";
            request += "COALESCE(p.lastname, ''), ";
            request += "COALESCE(p.nickname, ''), ";
            request += "COALESCE(p.number, 0), ";
            request += "COALESCE(p.country_code, '') ";
            request += "FROM pilot p ";
            request += "LEFT JOIN team_competition tc ON p.team_id = tc.team_id ";
            request += "LEFT JOIN game g ON tc.competition_id = g.competition_id ";
            request += "WHERE g.id_game = ? ";

            String args[] = new String[1];
            args[0] = String.valueOf(idGame);

            cursor = base.rawQuery(request, args);

            while (cursor.moveToNext()) {

                Pilot pilot = new Pilot();
                pilot.setIdPilot(cursor.getInt(0));
                pilot.setFirstname(cursor.getString(1));
                pilot.setLastname(cursor.getString(2));
                pilot.setNickname(cursor.getString(3));
                pilot.setNumber(cursor.getInt(4));
                pilot.setCountryCode(cursor.getString(5));
                pilots.add(pilot);

            }

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while getting pilots : " + e);
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while getting pilots : " + e);
                }
            }
        }

        App.baseIsLocked = false;

        return pilots;

    }

    public static void deletePilots(Context context, JSONArray items){

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

            String requestSQL = "DELETE FROM pilot ";
            requestSQL += "WHERE id_pilot = ? ";

            for(int i = 0; i < items.length(); i++){

                int item = items.getInt(i);

                try {

                    Object[] args = new Object[1];
                    args[0] = item;

                    base.execSQL(requestSQL, args);

                } catch(Exception e){

                    Logger.log(Logger.Level.WARNING, TAG, "error while deleting pilots : " + e);

                }

            }

            base.execSQL("COMMIT;");

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while deleting pilots : " + e);
        } finally {

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while deleting pilots : " + e);
                }
            }
        }

        App.baseIsLocked = false;


    }

    public static Pilot pilotWithId(Context context, int idPilot){

        QueriesLibrary queriesLibrary = null;

        Pilot pilot = null;
        Cursor cursor = null;

        try {

            while (App.baseIsLocked) {
                SystemClock.sleep(100);
            }

            App.baseIsLocked = true;

            queriesLibrary = new QueriesLibrary(context);
            SQLiteDatabase base = queriesLibrary.getWritableDatabase();

            String request = "SELECT COALESCE(p.id_pilot, 0), ";
            request += "COALESCE(p.firstname, ''), ";
            request += "COALESCE(p.lastname, ''), ";
            request += "COALESCE(p.nickname, ''), ";
            request += "COALESCE(p.number, 0), ";
            request += "COALESCE(p.country_code, '') ";
            request += "FROM pilot p ";
            request += "WHERE p.id_pilot = ? ";

            String args[] = new String[1];
            args[0] = String.valueOf(idPilot);

            cursor = base.rawQuery(request, args);

            while (cursor.moveToNext()) {

                pilot = new Pilot();
                pilot.setIdPilot(cursor.getInt(0));
                pilot.setFirstname(cursor.getString(1));
                pilot.setLastname(cursor.getString(2));
                pilot.setNickname(cursor.getString(3));
                pilot.setNumber(cursor.getInt(4));
                pilot.setCountryCode(cursor.getString(5));

            }

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while getting pilot with id : " + e);
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while getting pilot with id : " + e);
                }
            }
        }

        App.baseIsLocked = false;

        return pilot;

    }

}
