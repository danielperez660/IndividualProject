package sc17dpc.individualproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BeaconRegisterActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DatabaseHelper.getInstance(getApplicationContext());

        setContentView(R.layout.activity_beacon_register);

        Button register = findViewById(R.id.register);
        Button cancel = findViewById(R.id.cancel);

        TextView beaconID = findViewById(R.id.beaconID);
        TextView beaconName = findViewById(R.id.beaconName);

        Bundle beacon = getIntent().getExtras();
        assert beacon != null;

        final String btID = beacon.getString("bluetoothID");
        beaconID.setText(btID);

        final String btName = beacon.getString("bluetoothName");
        beaconName.setText(btName);

        // Basic setup of return button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterNewBeacon(btID, btName);
            }
        });

    }

    private void RegisterNewBeacon(String ID, String Name){
        BeaconEntry newRegister = new BeaconEntry();

        newRegister.setBeaconName(Name);
        newRegister.setBeaconID(ID);
        newRegister.setPosition(null);

        if(!dbHelper.addEntry(newRegister)){
            Toast.makeText(getApplicationContext(), "Beacon Already Registered", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Beacon Registered", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
