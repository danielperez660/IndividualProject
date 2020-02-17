package sc17dpc.individualproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class BeaconRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_register);

        Button register = findViewById(R.id.register);
        Button cancel = findViewById(R.id.cancel);
        TextView beaconID = findViewById(R.id.beaconID);

        Bundle beacon = getIntent().getExtras();
        assert beacon != null;
        final String btID = beacon.getString("bluetoothID");
        beaconID.setText(btID);

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
                RegisterNewBeacon(btID);
            }
        });

    }

    private void RegisterNewBeacon(String ID){
        Toast toast = Toast.makeText(getApplicationContext(), "Beacon Registered", Toast.LENGTH_SHORT);
        toast.show();
    }
}
