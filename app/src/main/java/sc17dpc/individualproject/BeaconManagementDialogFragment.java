package sc17dpc.individualproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class BeaconManagementDialogFragment extends DialogFragment {

    private BeaconEntry selected = null;
    private ArrayList<BeaconEntry> beacons;

    // Creates a fragment dialog box and gets information about the beacons
    public static BeaconManagementDialogFragment newInstance(ArrayList<BeaconEntry> beaconList) {
        BeaconManagementDialogFragment frag = new BeaconManagementDialogFragment();
        frag.beacons = beaconList;
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog, container, false);

        List<String> beaconIDs = new ArrayList<>();

        for (BeaconEntry i : beacons) {
            beaconIDs.add(i.getBeaconID());
        }

        Button select = view.findViewById(R.id.select_button);
        Button cancel = view.findViewById(R.id.cancel_button);
        final Spinner beaconSelector = view.findViewById(R.id.beacon_option);

        // General set up of the spinner in the dialogue
        ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, beaconIDs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        beaconSelector.setAdapter(adapter);

        // When a beacon item is selected from the spinner it will notify the main beaconmanagerfragment
        beaconSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String beaconID = beaconSelector.getItemAtPosition(position).toString();

                for (BeaconEntry i : beacons) {
                    if (i.getBeaconID().equals(beaconID)) {
                        setSelected(i);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putExtra("beaconID", selected.getBeaconID());

                getTargetFragment().onActivityResult(
                        getTargetRequestCode(), Activity.RESULT_OK, intent);
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void setSelected(BeaconEntry selected) {
        this.selected = selected;
    }

}
