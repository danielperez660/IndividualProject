package sc17dpc.individualproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.widget.Toast;

public class MainMenu extends AppCompatActivity {

    private TextView mTextMessage;
    private BluetoothAdapter mBluetoothAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.clear);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        // Bluetooth check to see if it exists and if not it closes app
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

        mTextMessage = findViewById(R.id.textView);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        Button buttonTest = findViewById(R.id.registerButton);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        buttonTest.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mTextMessage.append(String.valueOf(System.currentTimeMillis()) + "\n");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Requests permission from the user to use bluetooth
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
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

//    private void scanLeDevice(final boolean enable) {
//        if (enable) {
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//            mBluetoothAdapter.startLeScan(mLeScanCallback);
//        } else {
//            mBluetoothAdapter.stopLeScan(mLeScanCallback);
//        }
//    }
//
//    /**
//     * Device scan callback.
//     */
//    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
//        @Override
//        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
//            /**
//             * Package data into Eddystone
//             */
//            final Eddystone eddystone = EddystoneClass.fromScanData(device, rssi, scanRecord);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mLeDeviceListAdapter.addDevice(eddystone);
//                    mLeDeviceListAdapter.notifyDataSetChanged();
//                }
//            });
//        }
//    };

}
