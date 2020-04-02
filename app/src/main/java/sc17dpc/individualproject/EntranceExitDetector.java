package sc17dpc.individualproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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

        LocalBroadcastManager.getInstance(context).unregisterReceiver(beaconExit);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(beaconRanger);

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
                interior = exterior;
                exterior = beacon;
                max = icon.getCoords()[1];
            } else {
                interior = beacon;
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
            if (!interiorSeen) {
                Log.d("PhysicalTest", "Seeing interior now: " + distance);
                interiorSeen = true;
            }

            interiorDistance = distance;

            if (interiorDistance < 2 && !exitThreshold) {
                exitThreshold = true;
                Log.d("PhysicalTest", "interior in range: " + distance);

                if (firstThreshold == -1) {
                    firstThreshold = 0;
                }

                checkForEntranceOrExit();
            }
        } else if (ID.equals(exterior.getBeaconID())) {
            if (!exteriorSeen) {
                Log.d("PhysicalTest", "Seeing exterior now: " + distance);
                exteriorSeen = true;
            }
            exteriorDistance = distance;

            if (exteriorDistance < 2 && !entranceThreshold) {
                entranceThreshold = true;
                Log.d("PhysicalTest", "exterior in range: " + distance);

                if (firstThreshold == -1) {
                    firstThreshold = 1;
                }
                checkForEntranceOrExit();
            }
        }
    }

    private void exitedBeaconRange(String id) {
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

        // firstThreshold 1: door is reached first, so entering building
        if (entranceThreshold && exitThreshold) {
            if (firstThreshold == 1) {
                sApi.sendPayload(true, getName());
                status = true;
            } else if (firstThreshold == 0) {
                sApi.sendPayload(false, getName());
                status = false;
            }

            try {
                refresh();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void refresh() throws InterruptedException {
        Log.d("PhysicalTest", "CLEARED");

        entranceThreshold = false;
        exitThreshold = false;
        firstThreshold = -1;

        interiorSeen = false;
        exteriorSeen = false;

        interiorDistance = Double.NaN;
        exteriorDistance = Double.NaN;

        LocalBroadcastManager.getInstance(context).unregisterReceiver(beaconRanger);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(beaconExit);

        Thread.sleep(10000);

        LocalBroadcastManager.getInstance(context).registerReceiver(beaconRanger, new IntentFilter("SendRange"));
        LocalBroadcastManager.getInstance(context).registerReceiver(beaconExit, new IntentFilter("SendExit"));
    }

    private String getName(){
        String name = null;
        SharedPreferences pref = Objects.requireNonNull(context).getSharedPreferences("MyPref", 0);
        try{
            name = pref.getString("name", null);

        }catch(Exception ignored){

        }
        return name;
    }
}
