package com.example.nickgao.database;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.nickgao.database.RCMDataStore;

public class RCMSettingsDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "[RC]RCMSettingsDbHelper";

    private static final String DB_NAME = "settings.db";
    private static final int DB_VERSION = 1;

    public RCMSettingsDbHelper(final Context context) {
        super(context, getDatabaseDirectory(context) + DB_NAME, null, DB_VERSION);
    }
    
    public static String getDatabaseDirectory(Context context) {
        return context.getFilesDir().getAbsolutePath() + File.separator;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            db.beginTransaction();
            for (RCMDbTable table : RCMDataStore.sRCMSettingsDbTables.values()) {
                table.onCreate(db);
            }
            db.setTransactionSuccessful();
        } catch (Throwable e) {

            throw new RuntimeException("DB creation failed: " + e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // add update logic if necessary
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // do nothing
    }

}
