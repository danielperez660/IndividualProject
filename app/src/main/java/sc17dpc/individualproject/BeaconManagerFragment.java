package sc17dpc.individualproject;

import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class BeaconManagerFragment extends Fragment {

    SQLiteOpenHelper dbHelper;
    ArrayList<BeaconIconObject> beacons;

    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("HomeMade", "Status");

        final View view = inflater.inflate(R.layout.fragment_beacon_manage, container, false);

        final Button addButton = view.findViewById(R.id.AddButton);
        Button removeButton = view.findViewById(R.id.RemoveButton);

        beacons = new ArrayList<>();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView newImage = new ImageView(getActivity());

                RelativeLayout.LayoutParams layouP = new RelativeLayout.LayoutParams(56,56);
                layouP.addRule(RelativeLayout.CENTER_IN_PARENT);

                newImage.setLayoutParams(layouP);
                newImage.setImageResource(R.drawable.ic_bluetooth);

                ((ViewGroup)view).addView(newImage);


                BeaconIconObject newIcon = new BeaconIconObject();

                newIcon.setImage(newImage);
                newIcon.setCoords(newImage.getLeft(), newImage.getTop());

                beacons.add(newIcon);
            }
        });

//        TextView current = view.findViewById(R.id.registered_beacons);
//
//
//        dbHelper = DatabaseHelper.getInstance(getContext());
//
//        for (BeaconEntry i : ((DatabaseHelper) dbHelper).getAllEntries()) {
//            String id = i.getBeaconID() + "\n";
//            current.append(id);
//        }

        return view;
    }

    private View.OnTouchListener onTouch(){
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        };
    }
}
