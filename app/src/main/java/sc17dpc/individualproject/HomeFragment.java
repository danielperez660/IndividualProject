package sc17dpc.individualproject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private TextView status;
    private ImageView statusImage;
    private boolean status_test = false;
    private Button updateButton;
    private EditText nameInput;
    private SharedPreferences pref;


    @android.support.annotation.Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, @android.support.annotation.Nullable Bundle savedInstanceState) {
        Log.d("HomeMade", "Home");
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        pref = Objects.requireNonNull(getContext()).getSharedPreferences("MyPref", 0);

        status = view.findViewById(R.id.statusText);
        statusImage = view.findViewById(R.id.statusImage);
        updateButton = view.findViewById(R.id.updateNameButton);
        nameInput = view.findViewById(R.id.nameInput);

        nameInput.setText(getCurrentName());

        assert getArguments() != null;
        unpack(getArguments());

        // Manages action of changing the name of the user
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName = nameInput.getText().toString();
                if(!updatedName.equals(null)) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("name", updatedName);
                    editor.apply();
                    Toast.makeText(getContext(), "Name Updated", Toast.LENGTH_SHORT).show();
                    hideKeyboardFrom(getContext(), view);
                }
            }
        });

        if (!status_test){
            setOutOfOffice();
        }else{
            setInOffice();
        }

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mStateReceiver, new IntentFilter("ChangeInState"));
        return view;
    }

    // receives information about state change in real time from the EntranceExit algorithm
    private BroadcastReceiver mStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean address = intent.getBooleanExtra("state", false);

            if(address){ setInOffice();}
            else{setOutOfOffice();}
        }
    };


    private void unpack(Bundle arguments) {
        try {
            status_test = arguments.getBoolean("status");
        }catch(Exception ignored){

        }
    }

    // Sets in office
    public void setInOffice(){
        statusImage.setImageResource(R.drawable.ic_in);
        status.setText("Status: In Office");
    }

    // Sets out of office
    public void setOutOfOffice(){
        statusImage.setImageResource(R.drawable.ic_logout);
        status.setText("Status: Not in Office");
    }

    // Fetches current name from the preference file
    private String getCurrentName(){
        String name = pref.getString("name", null);
        return name;
    }

    // Hides the keyboard once the button to update name is pressed
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
