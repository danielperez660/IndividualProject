package sc17dpc.individualproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.widget.Toast;

public class LaunchActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private EntranceExitDetector entranceExitManager;

    private boolean status = false;

    private static final int PERMISSION_REQUEST_BLUETOOTH = 1;

    // Creates the bottom menu items and associates actions with the change to those specific section
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = new BeaconManagerFragment();
                    entranceExitManager.pause(getApplicationContext());
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                case R.id.navigation_status:
                    selectedFragment = new HomeFragment();
                    entranceExitManager.start(getApplicationContext());
                    Bundle args = new Bundle();
                    args.putBoolean("status", entranceExitManager.getStatus());

                    selectedFragment.setArguments(args);

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                case R.id.navigation_notifications:
                    selectedFragment = new SearchFragment();
                    entranceExitManager.pause(getApplicationContext());
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // runs the object that will compute if we are in the region or not
        entranceExitManager = new EntranceExitDetector();
        entranceExitManager.start(getApplicationContext());

        // allows us to access the required permissions for background
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // sets startup fragment
        Fragment selectedFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

        // Bluetooth permission stuff
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Request location services
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        navigation.getMenu().getItem(0).setChecked(false);
        navigation.getMenu().getItem(1).setChecked(true);
    }

    //Checks if on resume the application still has bluetooth
    @Override
    protected void onResume() {
        super.onResume();

        // Requests permission from the user to use bluetooth
        if (!mBluetoothAdapter.isEnabled()) {

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, PERMISSION_REQUEST_BLUETOOTH);
            }
        }
    }

    // Requests location permissions
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        entranceExitManager.start(getApplicationContext());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // User chose not to enable Bluetooth.
        if (requestCode == 1 && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}
