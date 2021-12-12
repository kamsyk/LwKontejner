package com.lindewiemann.lwkontejner;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContainerDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "LwKontejner.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LwKontejnerDbDict.OdpadEntry.TABLE_NAME + " ("
                    + LwKontejnerDbDict.OdpadEntry._ID + " INTEGER PRIMARY KEY,"
                    + LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_PROJEKT + " TEXT,"
                    + LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_CHYBA_NAME + " TEXT,"
                    + LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_CHYBA_ID + " INTEGER,"
                    + LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_KS + " INTEGER,"
                    + LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_DATUM + " TEXT,"
                    + LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_USER_CODE + " INTEGER"
                    + ")";

    //private static final String SQL_DELETE_ENTRIES =
    //        "DROP TABLE IF EXISTS " + LwKontejnerDbDict.OdpadEntry.TABLE_NAME;


    public ContainerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DbUpgrade(db);
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL(SQL_DELETE_ENTRIES);
        //onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private void DbUpgrade(SQLiteDatabase db) {
        if(!IsExistsColumnInTable(db,
                LwKontejnerDbDict.OdpadEntry.TABLE_NAME,
                LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_USER_CODE)) {
            String sql = "ALTER TABLE "
                + LwKontejnerDbDict.OdpadEntry.TABLE_NAME
                + " ADD COLUMN " + LwKontejnerDbDict.OdpadEntry.COLUMN_NAME_USER_CODE
                + " INTEGER";
            db.execSQL(sql);
        }
    }

    private boolean IsExistsColumnInTable(SQLiteDatabase lwkDb, String lwkTable, String columnToCheck) {
        Cursor mCursor = null;
        try {
            // Query 1 row
            mCursor = lwkDb.rawQuery("SELECT * FROM " + lwkTable + " LIMIT 0", null);

            // getColumnIndex() gives us the index (0 to ...) of the column - otherwise we get a -1
            if (mCursor.getColumnIndex(columnToCheck) != -1)
                return true;
            else
                return false;

        } catch (Exception Exp) {
            // Something went wrong. Missing the database? The table?
            //Log.d("... - existsColumnInTable", "When checking whether a column exists in the table, an error occurred: " + Exp.getMessage());
            return false;
        } finally {
            if (mCursor != null) mCursor.close();
        }
    }
}