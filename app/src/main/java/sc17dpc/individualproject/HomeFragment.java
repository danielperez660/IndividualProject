package sc17dpc.individualproject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
        return view;
    }

    private void unpack(Bundle arguments) {
        try {
            status_test = arguments.getBoolean("status");
        }catch(Exception ignored){

        }
    }

    public void setInOffice(){
        statusImage.setImageResource(R.drawable.ic_in);
        status.setText("Status: In Office");
    }

    public void setOutOfOffice(){
        statusImage.setImageResource(R.drawable.ic_logout);
        status.setText("Status: Not in Office");
    }

    private String getCurrentName(){
        String name = pref.getString("name", null);
        return name;
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
