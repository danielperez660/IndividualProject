package sc17dpc.individualproject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.service.ArmaRssiFilter;
import org.altbeacon.beacon.service.RunningAverageRssiFilter;

import java.util.Collection;

import androidx.annotation.Nullable;


public class BeaconControllerService extends Service implements BeaconConsumer {

    private org.altbeacon.beacon.BeaconManager beaconManager;
    private Thread mThread;

    // Stops searching for beacons when the service is stopped
    @Override
    public void onDestroy() {
        Log.d("HomeMade", "Stopped Search for beacons");
        beaconManager.unbind(this);
        mThread.interrupt();
        super.onDestroy();
    }

    // Does beacon search setup and starts search for UID
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mThread = new Thread() {
            public void run() {
            }
        };
        beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));

        beaconManager.bind(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onBeaconServiceConnect() {
        Log.d("HomeMade", "Searching for beacons");

        beaconManager.setRssiFilterImplClass(RunningAverageRssiFilter.class);
        RunningAverageRssiFilter.setSampleExpirationMilliseconds(5000l);

        beaconManager.setRegionStatePersistenceEnabled(false);
        beaconManager.addMonitorNotifier(new MonitorNotifier() {

            // Finds a beacon and commences to track its distance
            @Override
            public void didEnterRegion(Region region) {
                try {
                    Log.d("HomeMade", "entered: " + region.getBluetoothAddress());
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            // Sees that it left beacon range and stops tracking distance
            @Override
            public void didExitRegion(Region region) {
                try {
                    Log.d("HomeMade", "didExitRegion: I have just switched from seeing to not seeing beacons: " + region.getBluetoothAddress());
                    sendBeaconExit(region.getBluetoothAddress());
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.d("HomeMade", "didDetermineStateForRegion: I have just switched from seeing/not seeing beacons: " + region.getBluetoothAddress());
            }
        });

        // Starts tracking the range for a given region
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                for (Beacon b : beacons) {

                    if (b.getDistance() < 5.0) {
                        sendBeaconRange(b, b.getDistance());
                    }
                    sendBeaconFound(b);
                }
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("monitorID", null, null, null));
        } catch (RemoteException ignored) {
        }
    }

    private void sendBeaconRange(Beacon b, double distance) {
        Intent intent = new Intent("SendRange");

        intent.putExtra("distance", Double.toString(distance));
        intent.putExtra("id", b.getBluetoothAddress());

        Log.d("DistanceMonitor", "Distance: " + distance + " for: " + b.getBluetoothAddress());

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendBeaconExit(String b) {
        Intent intent = new Intent("SendExit");

        intent.putExtra("id", b);

        if (b != null) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private void sendBeaconFound(Beacon b) {
        Intent intent = new Intent("SendBeacon");
        intent.putExtra("address", b.getBluetoothAddress());
        intent.putExtra("name", b.getBluetoothName());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
