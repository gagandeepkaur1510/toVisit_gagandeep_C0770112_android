package com.example.tovisit_gagandeep_c0770112_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PLACE_DB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "favourite_places";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_LNG = "lng";
    private static final String COLUMN_VISITED = "visited";

    public DatabaseHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER CONSTRAINT employee_pk PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TITLE + " VARCHAR(100) NOT NULL, " +
                COLUMN_LAT + " REAL NOT NULL, " +
                COLUMN_LNG + " REAL NOT NULL, " +
                COLUMN_VISITED + " INTEGER DEFAULT 0 )";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

    public boolean insertPlace(String title, double lat, double lng, boolean visited) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_LAT, lat);
        contentValues.put(COLUMN_LNG, lng);
        if(visited)
        {
            contentValues.put(COLUMN_VISITED, 1);
        }
        else
        {
            contentValues.put(COLUMN_VISITED, 0);
        }

        return db.insert(TABLE_NAME, null, contentValues) != -1;
    }

    public Cursor getAllPlaces() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public boolean updatePlace(int id, String title, double lat, double lng, boolean visited) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE, title);
        contentValues.put(COLUMN_LAT, lat);
        contentValues.put(COLUMN_LNG, lng);
        if(visited)
        {
            contentValues.put(COLUMN_VISITED, 1);
        }
        else
        {
            contentValues.put(COLUMN_VISITED, 0);
        }

        return db.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deletePlace(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }
}
