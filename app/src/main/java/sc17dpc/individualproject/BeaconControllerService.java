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

import java.io.Serializable;
import java.util.Collection;

import androidx.annotation.Nullable;


public class BeaconControllerService extends Service implements BeaconConsumer {

    private org.altbeacon.beacon.BeaconManager beaconManager;
    private Thread mThread;
    // private Collection<Beacon> beaconList;

    // Stops searching for beacons when the service is stopped
    @Override
    public void onDestroy() {
        Log.d("HomeMade", "Stopped Search for beacons" );
        beaconManager.unbind(this);
        mThread.interrupt();
        super.onDestroy();
    }

    // Does beacon search setup and starts search for UID
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mThread = new Thread(){
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
        final Region region = new Region("DanielBeaconNew",null, null, null);
        Log.d("HomeMade", "Searching for beacons");

        beaconManager.addMonitorNotifier(new MonitorNotifier()  {

            // Finds a beacon and commences to track its distance
            @Override
            public void didEnterRegion(Region region) {
                try {
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            // Sees that it left beacon range and stops tracking distance
            @Override
            public void didExitRegion(Region region) {
                try {
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.d("HomeMade", "didDetermineStateForRegion: I have just switched from seeing/not seeing beacons: "+state);
            }
        });

        // Starts tracking the range for a given region
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                for (Beacon b: beacons ) {
                    Log.d("HomeMade", b.getBluetoothAddress());
                    sendBeaconAddress(b.getBluetoothAddress());
//                    sendBeaconData(b);
                }
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException ignored) {    }
    }

    private void sendBeaconAddress(String b){
        Intent intent = new Intent("SendBeacon");
        intent.putExtra("address", b);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

//    private void sendBeaconData(Beacon b){
//        Intent intent = new Intent("SendBeacon");
//        intent.putExtra("beacon", (Serializable) b);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
