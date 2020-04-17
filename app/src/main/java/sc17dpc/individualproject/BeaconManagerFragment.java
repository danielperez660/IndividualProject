package sc17dpc.individualproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class BeaconManagerFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private ArrayList<BeaconIconObject> beaconIcons;
    private ArrayList<BeaconEntry> beacons;
    private View view;
    private ImageView entranceImage;
    private SharedPreferences pref;

    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("HomeMade", "Status");

        view = inflater.inflate(R.layout.fragment_beacon_manage, container, false);
        pref = Objects.requireNonNull(getContext()).getSharedPreferences("MyPref", 0);

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

        changeRoomOption(getRoom());
        checkInitialRoom(getRoom());

        //Sets the options for the room type
        roomSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.straightEntrance:
                        changeRoomOption("Straight Entrance");
                        break;
                    case R.id.leftEntrance:
                        changeRoomOption("Left Entrance");
                        break;
                    case R.id.tEntrance:
                        changeRoomOption("T Entrance");
                        break;
                    case R.id.rightEntrance:
                        changeRoomOption("Right Entrance");
                        break;
                }
            }
        });

        // Manages the action of adding a beacon
        addButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {

                // Error message if you have used all your given beacons
                if (beaconIcons.size() == beacons.size()) {
                    Toast.makeText(getActivity(), "No more registered beacons", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Changes the fragment in view to the beaconManagementDialogue
                FragmentTransaction ft = getFragmentManager().beginTransaction();

                BeaconManagementDialogFragment df = BeaconManagementDialogFragment.newInstance(beacons);
                df.setTargetFragment(BeaconManagerFragment.this, 111);
                df.show(ft, "dialog");
            }
        });

        // Manages the actions for the clear button
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

    // Does intitial set up of room
    private void checkInitialRoom(String room) {

        HashMap<String, Integer> rooms = new HashMap<>();
        rooms.put("Straight Entrance", R.id.straightEntrance);
        rooms.put("Left Entrance", R.id.leftEntrance);
        rooms.put("Right Entrance", R.id.rightEntrance);
        rooms.put("T Entrance", R.id.tEntrance);

        RadioButton clicked = view.findViewById(rooms.get(room));
        clicked.toggle();
    }

    // Gets returned data from the dialogue as to which beacon has been added
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 111:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        String beaconId = data.getStringExtra("beaconID");

                        // Ensuring you only put the same beacon in once
                        for(BeaconIconObject i : beaconIcons){
                            if(i.getBeacon().getBeaconID().equals(beaconId)){
                                Toast.makeText(getContext(), "Beacon Already Present on Entrance", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                            createNewIcon(beaconId);
                    }
                }
                break;
        }
    }

    // Manages the radio buttons for changing room type
    private void changeRoomOption(String newRoom) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("room", newRoom);
        editor.apply();


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

    // Manages the creation and appending of new icon images onto the screen
    private void createNewIcon(String id) {

        ImageView newImage = new ImageView(getActivity());

        RelativeLayout.LayoutParams layouP = new RelativeLayout.LayoutParams(128, 128);
        layouP.addRule(RelativeLayout.CENTER_IN_PARENT);

        newImage.setLayoutParams(layouP);
        newImage.setImageResource(R.drawable.ic_bluetooth);
        newImage.setTag("Beacon Object");

        ((ViewGroup) view).addView(newImage);

        // Creating the beaconIconObject and setting params
        BeaconIconObject newIcon = new BeaconIconObject();
        for (BeaconEntry b : beacons) {
            if (b.getBeaconID().equals(id)) {
                newIcon.setBeacon(b);
                Log.d("Added: ", b.getBeaconID());
            }
        }
        newIcon.setIcon(newImage);
        newIcon.setCoords(500,745);

        beaconIcons.add(newIcon);
        dbHelper.addIconEntry(newIcon);

        // Gives the image the option to get dragged around
        newImage.setOnTouchListener(onTouch());
    }

    // Populates the map at the start with existing beacon configurations
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

    // Does set up for image dragging feature
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

    // Manages the outcome of the beacon dragging action
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

                                //Relative changes due to our constraints in the layout
                                float x = event.getX()-55;
                                float y = event.getY()+314;

                                Log.d("Debug", "onDrag: " + event.toString() +x +" " + y);

                                currentBeacon.setY(y);
                                currentBeacon.setX(x);


                                for (BeaconIconObject b : beaconIcons) {
                                    if (b.getIcon() == currentBeacon) {
                                        b.setCoords(x, y);

                                        // updates database information about icon position
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

    // Queries the preference file about the room type which has been previously assigned
    public String getRoom(){
        String room = pref.getString("room", "Straight Entrance");
        return room;
    }
}
