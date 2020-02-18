package sc17dpc.individualproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper sInstance;

    private static final String DATABASE_NAME = "beaconsRegistered";
    private static final int DATABASE_VERSION = 3;


    private static final String TABLE_BEACONS = "beacons";

    private static final String KEY_BEACONS_BLUETOOTH_ID = "ID";
    private static final String KEY_BEACONS_NAME = "name";
    private static final String KEY_BEACONS_POSITION = "position";


    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createBeaconTable = "CREATE TABLE " + TABLE_BEACONS +
                "(" + KEY_BEACONS_BLUETOOTH_ID + " TEXT,"
                + KEY_BEACONS_NAME + " TEXT,"
                + KEY_BEACONS_POSITION + " TEXT" + ")";
        db.execSQL(createBeaconTable);
        Log.d("DataBaseNew", "DB Created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEACONS);
            onCreate(db);
        }
    }

    public void addEntry(BeaconEntry beacon) {
        ContentValues values = new ContentValues();
        values.put(KEY_BEACONS_BLUETOOTH_ID, beacon.getBeaconID());
        values.put(KEY_BEACONS_NAME, beacon.getBeaconName());
        values.put(KEY_BEACONS_POSITION, beacon.getPosition());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_BEACONS, null, values);

        Log.d("DataBaseNew", "Beacon Added: " + beacon.getBeaconID());

        db.close();
    }

    public List<BeaconEntry> getAllEntries() {
        List<BeaconEntry> beacons = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_BEACONS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        Log.d("DataBaseNew", "Attempting to fetch all: " + cursor.getColumnNames()[0] +cursor.getColumnNames()[1] + cursor.getColumnNames()[2]);

        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()) {
                BeaconEntry newBeacon = new BeaconEntry();

                newBeacon.beaconID = cursor.getString(cursor.getColumnIndex(KEY_BEACONS_BLUETOOTH_ID));
                newBeacon.beaconName = cursor.getString(cursor.getColumnIndex(KEY_BEACONS_NAME));
                newBeacon.position = cursor.getString(cursor.getColumnIndex(KEY_BEACONS_POSITION));

                beacons.add(newBeacon);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return beacons;
    }

    public BeaconEntry getEntry(String beaconID) {
        BeaconEntry beacon = new BeaconEntry();
        String query = "SELECT * FROM " + TABLE_BEACONS + " WHERE " + KEY_BEACONS_BLUETOOTH_ID + " = " + "'" + beaconID + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            beacon.beaconID = cursor.getString(cursor.getColumnIndex(KEY_BEACONS_BLUETOOTH_ID));
            beacon.beaconName = cursor.getString(cursor.getColumnIndex(KEY_BEACONS_NAME));
            beacon.position = cursor.getString(cursor.getColumnIndex(KEY_BEACONS_POSITION));
        }

        Log.d("DataBaseNew", beacon.getBeaconID() + " " + beacon.getBeaconName());
        cursor.close();
        db.close();

        return beacon;
    }
}
