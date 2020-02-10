package sc17dpc.individualproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SearchFragment extends Fragment {

//    TODO: Add beacon searching features
//    TODO: Change Icon so that its not shit that is unrelated
    private Intent intent;

    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, @android.support.annotation.Nullable Bundle savedInstanceState) {
        Log.d("HomeMade", "Search");

        // Starts the beacon tracking service
        intent = new Intent(getActivity(), BeaconController.class);
        getActivity().startService(intent);

        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("HomeMade", "done");

        getActivity().stopService(intent);
    }
}
