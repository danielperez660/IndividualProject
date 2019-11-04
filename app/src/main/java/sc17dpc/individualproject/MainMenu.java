package sc17dpc.individualproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.widget.Toast;

public class MainMenu extends AppCompatActivity {

    private TextView mTextMessage;
    private BluetoothAdapter mBluetoothAdapter;

    private static final int PERMISSION_REQUEST_BLUETOOTH = 1;

    // Creates the bottom menu items and associates actions with the change to those specific section
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.clear);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.clear);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.clear);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

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
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        // --------------------------------------------- Button BS ---------------------------------------------------------- //

        mTextMessage = findViewById(R.id.textView);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        Button buttonTest = findViewById(R.id.registerButton);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        buttonTest.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO: Add list of BLE devices in the range
                    mTextMessage.append("Start" + "\n");
                    mTextMessage.append("Done" + "\n");
            }

        });
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // User chose not to enable Bluetooth.
        if (requestCode == 1 && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}
