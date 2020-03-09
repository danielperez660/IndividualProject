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
    private boolean status_test;
    SlackApiObject sApi;


    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, @android.support.annotation.Nullable Bundle savedInstanceState) {
        Log.d("HomeMade", "Home");
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sApi = new SlackApiObject();
        status = view.findViewById(R.id.statusText);
        statusImage = view.findViewById(R.id.statusImage);
        status_test = false;

        statusImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (status_test){
                    setOutOfOffice();
                }else{
                    setInOffice();
                }
            }
        });

        return view;
    }

    public void setInOffice(){
        sApi.sendPayload(true);
        statusImage.setImageResource(R.drawable.ic_in);
        status.setText("Status: In Office");
        status_test = true;
    }

    public void setOutOfOffice(){
        sApi.sendPayload(false);
        statusImage.setImageResource(R.drawable.ic_logout);
        status.setText("Status: Not in Office");
        status_test = false;
    }
}
