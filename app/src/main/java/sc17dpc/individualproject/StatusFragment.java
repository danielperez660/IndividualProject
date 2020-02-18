package sc17dpc.individualproject;

import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

public class StatusFragment extends Fragment {

    SQLiteOpenHelper dbHelper;

    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, @android.support.annotation.Nullable Bundle savedInstanceState) {
        Log.d("HomeMade", "Status");

        TextView current = Objects.requireNonNull(getActivity()).findViewById(R.id.registered);

        dbHelper = DatabaseHelper.getInstance(getContext());

        for(BeaconEntry i : ((DatabaseHelper) dbHelper).getAllEntries()){
            Log.d("HomeMade", "Fetched: " + i.getBeaconID());
            try{
                current.append(i.getBeaconID() + "\n");
            }catch(Exception ignored){}
        }

        return inflater.inflate(R.layout.fragment_status, container, false);
    }
}
