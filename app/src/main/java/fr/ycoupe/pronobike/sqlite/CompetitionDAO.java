package fr.ycoupe.pronobike.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.ycoupe.pronobike.App;
import fr.ycoupe.pronobike.models.Competition;
import fr.ycoupe.pronobike.utils.Logger;

/**
 * Created by Yann on 22/02/16.
 */
public class CompetitionDAO {
    private final static String TAG = CompetitionDAO.class.getSimpleName();

    public static void saveCompetitions(Context context, JSONArray competitions){

        if(competitions == null) return;

        QueriesLibrary queriesLibrary = null;

        try {

            while (App.baseIsLocked) {
                SystemClock.sleep(100);
            }

            App.baseIsLocked = true;

            queriesLibrary = new QueriesLibrary(context);
            SQLiteDatabase base = queriesLibrary.getWritableDatabase();

            base.execSQL("BEGIN;");

            String requestSQL = "INSERT OR REPLACE INTO competition (";
            requestSQL += "id_competition, ";
            requestSQL += "name, ";
            requestSQL += "description, ";
            requestSQL += "current ";
            requestSQL += ") VALUES (?, ?, ?, ?)";

            for (int i=0; i < competitions.length(); i++) {

                JSONObject competitionsJson = competitions.getJSONObject(i);

                try {

                    Object[] args = new Object[4];
                    args[0] = competitionsJson.has("id_competition") ? competitionsJson.getInt("id_competition") : null;
                    args[1] = competitionsJson.has("name") ? competitionsJson.getString("name") : null;
                    args[2] = competitionsJson.has("description") ? competitionsJson.getString("description") : null;
                    args[3] = competitionsJson.has("current") ? competitionsJson.getInt("current") : null;

                    base.execSQL(requestSQL, args);

                } catch(Exception e){

                    Logger.log(Logger.Level.WARNING, TAG, "error while saving competitions : " + e);

                }

            }

            base.execSQL("COMMIT;");

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while saving competitions : " + e);
        } finally {

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while saving competitions : " + e);
                }
            }
        }

        App.baseIsLocked = false;

    }

    public static ArrayList<Competition> competitions(Context context){

        QueriesLibrary queriesLibrary = null;

        ArrayList<Competition> competitions = new ArrayList<Competition>();
        Cursor cursor = null;

        try {

            while (App.baseIsLocked) {
                SystemClock.sleep(100);
            }

            App.baseIsLocked = true;

            queriesLibrary = new QueriesLibrary(context);
            SQLiteDatabase base = queriesLibrary.getWritableDatabase();

            String request = "SELECT COALESCE(id_competition, 0), ";
            request += "COALESCE(name, ''), ";
            request += "COALESCE(description, ''), ";
            request += "COALESCE(current, 0) ";
            request += "FROM competition ";
            request += "WHERE current = 1 ";
            request += "ORDER BY id_competition ASC ";

            cursor = base.rawQuery(request, null);

            while (cursor.moveToNext()) {

                Competition competition = new Competition();
                competition.setIdCompetition(cursor.getInt(0));
                competition.setName(cursor.getString(1));
                competition.setDesc(cursor.getString(2));
                competition.setCurrent(cursor.getInt(3));
                competitions.add(competition);

            }

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while getting competitions : " + e);
        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while getting competitions : " + e);
                }
            }
        }

        App.baseIsLocked = false;

        return competitions;

    }

    public static void deleteCompetitions(Context context, JSONArray items){

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

            String requestSQL = "DELETE FROM competition ";
            requestSQL += "WHERE id_competition = ? ";

            for(int i = 0; i < items.length(); i++){

                int item = items.getInt(i);

                try {

                    Object[] args = new Object[1];
                    args[0] = item;

                    base.execSQL(requestSQL, args);

                } catch(Exception e){

                    Logger.log(Logger.Level.WARNING, TAG, "error while deleting competitions : " + e);

                }

            }

            base.execSQL("COMMIT;");

        } catch(Exception e){
            Logger.log(Logger.Level.WARNING, TAG, "error while deleting competitions : " + e);
        } finally {

            if (queriesLibrary != null) {

                try {
                    queriesLibrary.close();
                } catch (Exception e) {
                    Logger.log(Logger.Level.WARNING, TAG, "error while deleting competitions : " + e);
                }
            }
        }

        App.baseIsLocked = false;


    }

}
