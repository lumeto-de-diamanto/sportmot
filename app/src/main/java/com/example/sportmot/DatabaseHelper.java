package com.example.sportmot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.osmdroid.util.GeoPoint;


/**
 * This class manages the tournament location. The SQLite database stores the location that
 * is made out of latitude and longitude. Here we save, update and retrieve the location of the
 * tournament by using the tournament ID.
 * */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SportmotDatabase.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOCATIONS_TABLE = "CREATE TABLE locations (" +
                "tournament_id INTEGER PRIMARY KEY, " +
                "latitude REAL, " +
                "longitude REAL)";
        db.execSQL(CREATE_LOCATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS locations");
        onCreate(db);
    }

    // Save or update location in the database
    public void saveOrUpdateLocation(double latitude, double longitude, int tournamentID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tournament_id", tournamentID);
        values.put("latitude", latitude);
        values.put("longitude", longitude);

        int rowsUpdated = db.update("locations", values, "tournament_id = ?", new String[]{String.valueOf(tournamentID)});

        if (rowsUpdated == 0) {
            db.insert("locations", null, values);
            Log.d("DatabaseHelper", "Location SAVED: " + latitude + ", " + longitude + " for tournament " + tournamentID);
        } else {
            Log.d("DatabaseHelper", "Location UPDATED: " + latitude + ", " + longitude + " for tournament " + tournamentID);
        }

        db.close();
    }


    public double[] getTournamentLocation(int tournamentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        double[] location = null;

        Cursor cursor = db.rawQuery("SELECT latitude, longitude FROM locations WHERE tournament_id = ?",
                new String[]{String.valueOf(tournamentId)});

        if (cursor.moveToFirst()) {
            double latitude = cursor.getDouble(0);
            double longitude = cursor.getDouble(1);
            location = new double[]{latitude, longitude};
        }

        cursor.close();
        db.close();
        return location;
    }



}
