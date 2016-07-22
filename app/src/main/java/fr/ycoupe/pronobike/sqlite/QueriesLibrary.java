package fr.ycoupe.pronobike.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lievremont on 22/10/15.
 */
public class QueriesLibrary extends SQLiteOpenHelper {

    public static String DB_PATH = "/data/data/fr.ycoupe.pronobike/databases/";
    private static String DB_ASSET_NAME = "pronobike.sqlite";
    private static String DB_NAME = "pronobike.sqlite";

    public QueriesLibrary(Context contextParam) {
        super(contextParam, DB_NAME, null, 1);
    }

    public static void open(Context context) {
        // Copy if database does not exist
        File fichierBase = new File(DB_PATH + DB_NAME);
        if (!fichierBase.exists())
            creerBase(context);

    }

    private static void creerBase(Context context) {
        // Copy database
        try {

            // Reading stream
            InputStream myInput = context.getAssets().open(DB_ASSET_NAME);

            // Create directory
            File rep = new File(DB_PATH);
            rep.mkdir();

            // Create file
            File fichierBase = new File(DB_PATH + DB_NAME);
            fichierBase.createNewFile();

            // Writing stream
            OutputStream myOutput = new FileOutputStream(fichierBase);

            // Copy datas
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            // Close buffers
            myOutput.flush();
            myOutput.close();
            myInput.close();


        } catch (IOException e) {
            e.printStackTrace();
            throw new Error("error while copying database : " + e.getLocalizedMessage());
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
