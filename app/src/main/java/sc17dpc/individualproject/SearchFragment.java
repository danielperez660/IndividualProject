package sc17dpc.individualproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    //    TODO: Change Icon so that its not shit that is unrelated
    private Intent intent;
    private ArrayList<String> beacons = new ArrayList<>();
    private ListView beaconList;
    private ArrayAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("HomeMade", "Search");

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        Button searchButton = view.findViewById(R.id.search);
        beaconList = view.findViewById(R.id.beaconList);

        adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, beacons);
        beaconList.setAdapter(adapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("HomeMade", "Search Started");

                // Starts the beacon tracking service
                intent = new Intent(getActivity(), BeaconController.class);
                getActivity().startService(intent);
            }
        });

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBeaconReceiver, new IntentFilter("SendBeacon"));
        return view;
    }

    private void beaconFound(String beacon) {
        if (!beacons.contains(beacon)) {
            beacons.add(beacon);
            adapter.notifyDataSetChanged();
        }
    }

    private BroadcastReceiver mBeaconReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("address");
//            Bundle beacon = intent.getBundleExtra("beacon");
//            Object test = beacon.get("id1");

//            Log.d("HomeMade", "Beacon: " + test.toString());
            Log.d("HomeMade", "Message Received " + data);

            beaconFound(data);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("HomeMade", "done");
        getActivity().stopService(intent);
        beacons.clear();
    }
}
