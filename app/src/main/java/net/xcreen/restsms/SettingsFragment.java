package net.xcreen.restsms;

import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        final EditText portEditText = rootView.findViewById(R.id.settings_port_edittext);
        final CheckBox openBrowserCheckBox = rootView.findViewById(R.id.settings_open_browser_checkbox);

        Button saveBtn = rootView.findViewById(R.id.settings_save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean saved = false;
                SharedPreferences.Editor editor = sharedPref.edit();

                //Save Port
                if(portEditText.getText().length() > 0){
                    int newPort = 0;
                    try{
                        newPort = Integer.parseInt(portEditText.getText().toString());
                    }
                    catch (Exception ex){
                        Toast.makeText(v.getContext(), getResources().getText(R.string.setting_invalid_port), Toast.LENGTH_SHORT).show();
                    }
                    if(newPort > 0 && newPort < 65535){
                        //Set Port
                        editor.putInt("server_port", newPort);
                        editor.apply();
                        saved = true;
                    }
                    else{
                        Toast.makeText(v.getContext(), getResources().getText(R.string.setting_invalid_port_range), Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(v.getContext(), getResources().getText(R.string.setting_invalid_port), Toast.LENGTH_SHORT).show();
                }

                //Save Open-Browser after Server-Start
                editor.putBoolean("open_browser_serverstart", openBrowserCheckBox.isChecked());
                editor.apply();

                if(saved){
                    Toast.makeText(v.getContext(), getResources().getText(R.string.setting_saved), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Set current Port
        portEditText.setText(String.valueOf(sharedPref.getInt("server_port", 8080)));

        //Set current "Open-Browser after Server-Start"-Option
        if(sharedPref.getBoolean("open_browser_serverstart", true)){
            openBrowserCheckBox.setChecked(true);
        }

        return rootView;
    }
}
