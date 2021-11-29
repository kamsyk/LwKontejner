package com.lindewiemann.lwkontejner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContainerDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "LwKontejner.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LwKontejnerDbDict.OdpadEntry.TABLE_NAME + " (" +
                    LwKontejnerDbDict.OdpadEntry._ID + " INTEGER PRIMARY KEY," +
                    LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_PROJEKT + " TEXT," +
                    LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_CHYBA_NAME + " TEXT," +
                    LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_CHYBA_ID + " INTEGER," +
                    LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_KS + " INTEGER," +
                    LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_DATUM + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + LwKontejnerDbDict.OdpadEntry.TABLE_NAME;

    public ContainerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}