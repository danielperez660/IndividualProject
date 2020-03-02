package sc17dpc.individualproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeFragment extends Fragment {

    private TextView status;
    private ImageView statusImage;


    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, @android.support.annotation.Nullable Bundle savedInstanceState) {
        Log.d("HomeMade", "Home");
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        status = view.findViewById(R.id.statusText);
        statusImage = view.findViewById(R.id.statusImage);

        statusImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                setInOffice();
            }
        });

        return view;
    }

    public void setInOffice(){
        statusImage.setImageResource(R.drawable.ic_in);
        status.setText("Status: In Office");
    }

    public void setOutOfOffice(){
        statusImage.setImageResource(R.drawable.ic_logout);
        status.setText("Status: Not in Office");

    }
}
