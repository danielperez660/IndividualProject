package sc17dpc.individualproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BeaconManagerFragment extends Fragment {

    DatabaseHelper dbHelper;
    ArrayList<BeaconIconObject> beaconIcons;
    ArrayList<BeaconEntry> beacons;
    View view;
    ImageView entranceImage;

    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("HomeMade", "Status");

        view = inflater.inflate(R.layout.fragment_beacon_manage, container, false);

        dbHelper = DatabaseHelper.getInstance(getContext());

        final Button addButton = view.findViewById(R.id.AddButton);
        Button removeButton = view.findViewById(R.id.RemoveButton);

        RadioGroup roomSelector = view.findViewById(R.id.RoomOptions);

        beaconIcons = new ArrayList<>();
        beacons = new ArrayList<>();

        entranceImage = view.findViewById(R.id.EntryView);
        entranceImage.setOnDragListener(onDrag());

        beaconIcons = dbHelper.getAllIcons();

        beacons.addAll(dbHelper.getAllEntries());

        roomSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.StraightRoom:
                        changeRoomOption("Straight Entrance");
                        break;
                    case R.id.LeftRoom:
                        changeRoomOption("Left Entrance");
                        break;
                    case R.id.TRoom:
                        changeRoomOption("T Entrance");
                        break;
                    case R.id.RightRoom:
                        changeRoomOption("Right Entrance");
                        break;
                }
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {

                if (beaconIcons.size() == beacons.size()) {
                    Toast.makeText(getActivity(), "No more registered beacons", Toast.LENGTH_SHORT).show();
                    return;
                }

                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                Fragment prev = getFragmentManager().findFragmentByTag("dialog");

                BeaconManagementDialogFragment df = BeaconManagementDialogFragment.newInstance(beacons);
                df.setTargetFragment(BeaconManagerFragment.this, 111);
                df.show(ft, "dialog");
            }
        });


        removeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                for (BeaconIconObject b : beaconIcons) {
                    ImageView temp = b.getIcon();
                    temp.setVisibility(View.GONE);

                    b.setIcon(temp);
                }

                beaconIcons.clear();
                dbHelper.clearIcons();
            }
        });

        if (beaconIcons.size() != 0) {
            createExistingIcon();
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 111:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        createNewIcon(data.getStringExtra("beaconID"));
                    }
                }
                break;
        }
    }

    private void changeRoomOption(String newRoom) {
        switch (newRoom) {
            case "Straight Entrance":
                entranceImage.setImageResource(R.drawable.ic_straight_room);
                Log.d("RoomChange", newRoom);
                entranceImage.setOnDragListener(onDrag());
                break;
            case "Left Entrance":
                entranceImage.setImageResource(R.drawable.ic_left_bend_room);
                Log.d("RoomChange", newRoom);
                entranceImage.setOnDragListener(onDrag());
                break;
            case "Right Entrance":
                entranceImage.setImageResource(R.drawable.ic_right_bend_room);
                Log.d("RoomChange", newRoom);
                entranceImage.setOnDragListener(onDrag());
                break;
            case "T Entrance":
                entranceImage.setImageResource(R.drawable.ic_t_shaped_room);
                Log.d("RoomChange", newRoom);
                entranceImage.setOnDragListener(onDrag());
                break;
        }


    }

    private void createNewIcon(String id) {

        ImageView newImage = new ImageView(getActivity());

        RelativeLayout.LayoutParams layouP = new RelativeLayout.LayoutParams(128, 128);
        layouP.addRule(RelativeLayout.CENTER_IN_PARENT);

        newImage.setLayoutParams(layouP);
        newImage.setImageResource(R.drawable.ic_bluetooth);
        newImage.setTag("Beacon Object");

        ((ViewGroup) view).addView(newImage);

        BeaconIconObject newIcon = new BeaconIconObject();

        for (BeaconEntry b : beacons) {
            if (b.getBeaconID().equals(id)) {
                newIcon.setBeacon(b);
                Log.d("Added: ", b.getBeaconID());
            }
        }

        newIcon.setIcon(newImage);
        newIcon.setCoords(newImage.getLeft(), newImage.getTop());

        beaconIcons.add(newIcon);
        dbHelper.addIconEntry(newIcon);

        newImage.setOnTouchListener(onTouch());
    }

    private void createExistingIcon() {
        for (BeaconIconObject icon : beaconIcons) {
            ImageView newImage = new ImageView(getActivity());
            RelativeLayout.LayoutParams layouP = new RelativeLayout.LayoutParams(128, 128);

            newImage.setLayoutParams(layouP);
            newImage.setImageResource(R.drawable.ic_bluetooth);
            newImage.setTag("Beacon Object");

            newImage.setY(icon.getCoords()[1]);
            newImage.setX(icon.getCoords()[0]);

            icon.setIcon(newImage);
            ((ViewGroup) view).addView(newImage);
            newImage.setOnTouchListener(onTouch());
        }
    }

    private View.OnTouchListener onTouch() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                View.DragShadowBuilder mShadow = new View.DragShadowBuilder(v);
                ClipData.Item item = new ClipData.Item(v.getTag().toString());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData data = new ClipData(v.getTag().toString(), mimeTypes, item);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(data, mShadow, v, 0);
                }
                return false;
            }
        };
    }

    private View.OnDragListener onDrag() {
        return new View.OnDragListener() {

            @Override
            public boolean onDrag(View v, DragEvent event) {
                boolean inside = true;

                switch (event.getAction()) {

                    //the drag point has entered the bounding box of the View
                    case DragEvent.ACTION_DRAG_ENTERED:
                        inside = true;
                        break;

                    //the user has moved the drag shadow outside the bounding box of the View
                    case DragEvent.ACTION_DRAG_EXITED:
                        inside = false;
                        break;

                    //drag shadow has been released,the drag point is within the bounding box of the View
                    case DragEvent.ACTION_DROP:
                        if (inside) {
                            ImageView currentBeacon = (ImageView) event.getLocalState();

                            if (currentBeacon != null) {
                                currentBeacon.setY(event.getY());
                                currentBeacon.setX(event.getX());
                                for (BeaconIconObject b : beaconIcons) {
                                    if (b.getIcon() == currentBeacon) {
                                        b.setCoords(event.getX(), event.getY());
                                        dbHelper.updateIcon(b);
                                    }
                                }
                            }
                        }
                        break;

                    //the drag and drop operation has concluded.
                    case DragEvent.ACTION_DRAG_ENDED:

                    default:
                        break;
                }
                return true;
            }
        };
    }
}
