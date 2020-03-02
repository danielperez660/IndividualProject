package sc17dpc.individualproject;

import android.content.ClipData;
import android.content.ClipDescription;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static java.lang.String.valueOf;

public class BeaconManagerFragment extends Fragment {

    SQLiteOpenHelper dbHelper;
    ArrayList<BeaconIconObject> beacons;
    View view;
    ImageView map;

    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("HomeMade", "Status");

        view = inflater.inflate(R.layout.fragment_beacon_manage, container, false);

        final Button addButton = view.findViewById(R.id.AddButton);
        Button removeButton = view.findViewById(R.id.RemoveButton);

        beacons = new ArrayList<>();
        map = view.findViewById(R.id.EntryView);

        map.setOnDragListener(onDrag());

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView newImage = new ImageView(getActivity());

                RelativeLayout.LayoutParams layouP = new RelativeLayout.LayoutParams(56, 56);
                layouP.addRule(RelativeLayout.CENTER_IN_PARENT);

                newImage.setLayoutParams(layouP);
                newImage.setImageResource(R.drawable.ic_bluetooth);
                newImage.setTag("Beacon Object");

                ((ViewGroup) view).addView(newImage);


                BeaconIconObject newIcon = new BeaconIconObject();

                newIcon.setImage(newImage);
                newIcon.setCoords(newImage.getLeft(), newImage.getTop());

                beacons.add(newIcon);

                newImage.setOnTouchListener(onTouch());
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

//                ImageView dragged = view.findViewById(R.id.)

                switch (event.getAction()) {

                    //signal for the start of a drag and drop operation.
                    case DragEvent.ACTION_DRAG_STARTED:

                        break;

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
                            Log.d("ImageMove", valueOf( event.getX()));
                        }

                        Toast.makeText(getActivity(), "Drag dropped", Toast.LENGTH_SHORT).show();
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
