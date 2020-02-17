package sc17dpc.individualproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper sInstance;

    private static final String DATABASE_NAME = "beaconsRegistered";
    private static final int DATABASE_VERSION = 1;


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
                                    "(" + KEY_BEACONS_BLUETOOTH_ID + "TEXT,"
                                    + KEY_BEACONS_NAME + "TEXT,"
                                    + KEY_BEACONS_POSITION + "TEXT" + ")";
        db.execSQL(createBeaconTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEACONS);
            onCreate(db);
        }
    }

    public void addEntrie(){

    }

    public List<BeaconEntry> getAllEntries(){
        List<BeaconEntry> beacons = new ArrayList<>();

        return beacons;
    }

    public BeaconEntry getEntry(String beaconID){
        BeaconEntry beacon = new BeaconEntry();
        return beacon;
    }
}
