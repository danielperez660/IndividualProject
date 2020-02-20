package sc17dpc.individualproject;

import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StatusFragment extends Fragment {

    SQLiteOpenHelper dbHelper;

    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("HomeMade", "Status");

        View view = inflater.inflate(R.layout.fragment_status, container, false);
        TextView current = view.findViewById(R.id.registered_beacons);

        dbHelper = DatabaseHelper.getInstance(getContext());

        for (BeaconEntry i : ((DatabaseHelper) dbHelper).getAllEntries()) {
            String id = i.getBeaconID() + "\n";
            current.setText(id);
        }

        return view;
    }
}
