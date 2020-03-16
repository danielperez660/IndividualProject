package sc17dpc.individualproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;


public class EntranceExitDetector {
    private ArrayList<BeaconIconObject> beaconIcons;
    private Intent intent;

    private Context context;

    private SlackApiObject sApi = new SlackApiObject();

    private BeaconEntry interior;
    private BeaconEntry exterior;

    private boolean interiorSeen = false;
    private boolean exteriorSeen = false;

    private double interiorDistance;
    private double exteriorDistance;

    private boolean entranceThreshold;
    private boolean exitThreshold;
    private int firstThreshold = -1;

    private boolean status = false;

    boolean getStatus() {
        Log.d("test", "Getting status " + Boolean.toString(status));
        return status;
    }

    void pause(Context context) {
        this.context = context;
        context.stopService(intent);
    }

    public void start(Context context) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
        this.context = context;

        beaconIcons = dbHelper.getAllIcons();

        float max = 0;

        for (BeaconIconObject icon : beaconIcons) {
            BeaconEntry beacon = icon.getBeacon();

            if (icon.getCoords()[1] > max) {
                exterior = interior;
                interior = beacon;
                max = icon.getCoords()[1];
            } else {
                exterior = beacon;
            }
        }

        intent = new Intent(context, BeaconControllerService.class);
        Objects.requireNonNull(context.startService(intent));

        context.startService(intent);
        LocalBroadcastManager.getInstance(context).registerReceiver(beaconRanger, new IntentFilter("SendRange"));
        LocalBroadcastManager.getInstance(context).registerReceiver(beaconExit, new IntentFilter("SendExit"));
    }

    private BroadcastReceiver beaconRanger = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String distance = intent.getStringExtra("distance");
            String ID = intent.getStringExtra("id");

            beaconRangeReceived(Double.parseDouble(distance), ID);
            checkForEntranceOrExit();
        }
    };

    private BroadcastReceiver beaconExit = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String ID = intent.getStringExtra("id");
            exitedBeaconRange(ID);
        }
    };

    private void beaconRangeReceived(double distance, String ID) {
        if (interior == null || exterior == null) {
            return;
        }

        if (ID.equals(interior.getBeaconID())) {
            interiorSeen = true;
            interiorDistance = distance;
        } else if (ID.equals(exterior.getBeaconID())) {
            exteriorSeen = true;
            exteriorDistance = distance;
        }
    }

    private void exitedBeaconRange(String id) {
        Log.d("EntranceStatus", "exited " + id);
        if (id.equals(interior.getBeaconID())) {
            interiorSeen = false;
            interiorDistance = Double.NaN;
        } else if (id.equals(exterior.getBeaconID())) {
            exteriorSeen = false;
            exteriorDistance = Double.NaN;
        }
    }

    private void checkForEntranceOrExit() {

        if (!exteriorSeen || !interiorSeen) {
            return;
        }

        if (exteriorDistance < 1) {
            entranceThreshold = true;

            if (firstThreshold == -1){
                firstThreshold = 1;
            }

            Log.d("PhysicalTest", "Entrance");

//            Toast toast = Toast.makeText(context, "foundEntrance", Toast.LENGTH_SHORT);
//            toast.show();
        }

        if (interiorDistance < 1) {
            exitThreshold = true;

            if (firstThreshold == -1){
                firstThreshold = 0;
            }
            Log.d("PhysicalTest", "exit");


//            Toast toast = Toast.makeText(context, "foundExit", Toast.LENGTH_SHORT);
//            toast.show();

        }

        // firstThreshold 1: door is reached first, so entering building
        if (entranceThreshold && exitThreshold) {
            if (firstThreshold == 1) {
                sApi.sendPayload(true);
                status = true;

            }else if (firstThreshold == 0){
                sApi.sendPayload(false);
                status = false;
            }

            refresh();

        }
    }

    private void refresh() {
        entranceThreshold = false;
        exitThreshold = false;
        firstThreshold =  -1;

        interiorSeen = false;
        exteriorSeen = false;

        interiorDistance = Double.NaN;
        exteriorDistance = Double.NaN;
    }
}
