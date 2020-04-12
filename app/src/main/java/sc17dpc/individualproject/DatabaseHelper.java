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
    private static final int DATABASE_VERSION = 11;


    private static final String TABLE_BEACONS = "beacons";
    private static final String TABLE_BEACON_IMAGES = "beaconImages";

    private static final String KEY_BEACONS_BLUETOOTH_ID = "ID";
    private static final String KEY_BEACONS_NAME = "name";

    private static final String KEY_BEACON_ICON_BLUETOOTH_ID = "ID";
    private static final String KEY_BEACON_IMAGES_X = "x_coord";
    private static final String KEY_BEACON_IMAGES_Y = "y_coord";

    // Allows for the singel instance of the DB to be accessed
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creates the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createBeaconTable = "CREATE TABLE " + TABLE_BEACONS +
                "(" + KEY_BEACONS_BLUETOOTH_ID + " TEXT,"
                + KEY_BEACONS_NAME + " TEXT" + ")";

        String createBeaconImageTable = "CREATE TABLE " + TABLE_BEACON_IMAGES +
                "(" + KEY_BEACON_IMAGES_X + " FLOAT,"
                + KEY_BEACON_ICON_BLUETOOTH_ID + " TEXT,"
                + KEY_BEACON_IMAGES_Y + " FLOAT" + ")";

        db.execSQL(createBeaconTable);
        db.execSQL(createBeaconImageTable);

        Log.d("DataBaseNew", "DB Created");

    }

    // Checks to see if there is a new version of the database defined in the codebase
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEACONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEACON_IMAGES);
            onCreate(db);
        }
    }

    // Adds a single beacon entry to the database
    public Boolean addEntry(BeaconEntry beacon) {
        ContentValues values = new ContentValues();
        values.put(KEY_BEACONS_BLUETOOTH_ID, beacon.getBeaconID());
        values.put(KEY_BEACONS_NAME, beacon.getBeaconName());

        if (getEntry(beacon.getBeaconID()).getBeaconID() == null) {
            SQLiteDatabase db = this.getWritableDatabase();

            db.insert(TABLE_BEACONS, null, values);
            Log.d("DataBaseNew", "Beacon Added: " + beacon.getBeaconID());
            db.close();
        } else {
            return false;
        }

        return true;
    }

    // Fetches all registered beacons from the database
    public ArrayList<BeaconEntry> getAllEntries() {
        ArrayList<BeaconEntry> beacons = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_BEACONS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                BeaconEntry newBeacon = new BeaconEntry();

                newBeacon.setBeaconID(cursor.getString(cursor.getColumnIndex(KEY_BEACONS_BLUETOOTH_ID)));
                newBeacon.setBeaconName(cursor.getString(cursor.getColumnIndex(KEY_BEACONS_NAME)));

                beacons.add(newBeacon);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return beacons;
    }

    // Gets a single beacon from the database
    public BeaconEntry getEntry(String beaconID) {
        BeaconEntry beacon = new BeaconEntry();
        String query = "SELECT * FROM " + TABLE_BEACONS + " WHERE " + KEY_BEACONS_BLUETOOTH_ID + " = " + "'" + beaconID + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            beacon.setBeaconID(cursor.getString(cursor.getColumnIndex(KEY_BEACONS_BLUETOOTH_ID)));
            beacon.setBeaconName(cursor.getString(cursor.getColumnIndex(KEY_BEACONS_NAME)));
        }

        Log.d("DataBaseNew", "Fetched: " + beacon.getBeaconID() + " " + beacon.getBeaconName());
        cursor.close();
        db.close();

        return beacon;
    }

    // Gets all the icons existing in the database, relies on TABLE_BEACON_IMAGES
    public ArrayList<BeaconIconObject> getAllIcons() {
        ArrayList<BeaconIconObject> beacons = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_BEACON_IMAGES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                BeaconIconObject newBeacon = new BeaconIconObject();
                String ID = cursor.getString(cursor.getColumnIndex(KEY_BEACON_ICON_BLUETOOTH_ID));
                BeaconEntry beacon = getEntry(ID);

                float x = cursor.getInt(cursor.getColumnIndex(KEY_BEACON_IMAGES_X));
                float y = cursor.getInt(cursor.getColumnIndex(KEY_BEACON_IMAGES_Y));

                newBeacon.setBeacon(beacon);
                newBeacon.setIcon(null);
                newBeacon.setCoords(x, y);

                beacons.add(newBeacon);

                Log.d("DataBaseNew", "Fetched Icon: " + beacon.getBeaconID() + x + y);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        return beacons;
    }

    // Updates a single icon entry
    public void updateIcon(BeaconIconObject icon) {
        SQLiteDatabase db = this.getReadableDatabase();

        String ID = icon.getBeacon().getBeaconID();
        float x = icon.getCoords()[0];
        float y = icon.getCoords()[1];

        ContentValues cv = new ContentValues();
        cv.put(KEY_BEACON_ICON_BLUETOOTH_ID, ID);
        cv.put(KEY_BEACON_IMAGES_X, x);
        cv.put(KEY_BEACON_IMAGES_Y, y);

        Cursor allRows  = db.rawQuery("SELECT * FROM " + TABLE_BEACON_IMAGES, null);

        Log.d("DataBaseNew", "Table " + allRows.toString());

        Log.d("DataBaseNew", "Updating: " + ID + x + y);
        db.update(TABLE_BEACON_IMAGES, cv, KEY_BEACON_ICON_BLUETOOTH_ID + " = '" + ID + "'", null);
    }

    // Adds a single Icon entry to the database
    public Boolean addIconEntry(BeaconIconObject beacon) {
        ContentValues values = new ContentValues();
        values.put(KEY_BEACON_IMAGES_X, beacon.getCoords()[0]);
        values.put(KEY_BEACON_IMAGES_Y, beacon.getCoords()[1]);
        values.put(KEY_BEACON_ICON_BLUETOOTH_ID, beacon.getBeacon().getBeaconID());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_BEACON_IMAGES, null, values);

        Log.d("DataBaseNew", "Beacon Icon Added: " + beacon.getBeacon().getBeaconID() + beacon.getCoords()[0] + beacon.getCoords()[1]);
        db.close();

        return true;
    }

    // Clears the icons table
    public void clearIcons(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_BEACON_IMAGES);
    }
}
