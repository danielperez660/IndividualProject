package sc17dpc.individualproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BeaconRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_register);

        Button register = findViewById(R.id.register);
        Button cancel = findViewById(R.id.cancel);
        TextView beaconID = findViewById(R.id.beaconID);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle beacon = getIntent().getExtras();

        assert beacon != null;
        beaconID.setText(beacon.getString("bluetoothID"));

    }
}
