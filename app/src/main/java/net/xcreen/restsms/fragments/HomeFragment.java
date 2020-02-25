package net.xcreen.restsms.fragments;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import net.xcreen.restsms.AppContext;
import net.xcreen.restsms.R;
import net.xcreen.restsms.server.ServerLogging;
import net.xcreen.restsms.server.ServerService;

public class HomeFragment extends Fragment {

    private Button toggleServerBtn;
    private AppContext appContext;
    private int SMS_PERMISSION_REQUEST = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        appContext = (AppContext) getActivity().getApplication();
        //Set Server-Logging for Server
        ServerLogging serverLogging = new ServerLogging(getContext().getFilesDir().getAbsolutePath(), getContext());
        appContext.getSmsServer().setServerLogging(serverLogging);

        toggleServerBtn = rootView.findViewById(R.id.toggle_server_btn);
        toggleServerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if SMS-Permission is grant
                if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    //Show Error Toast
                    Toast.makeText(v.getContext(), getResources().getText(R.string.no_sms_permission), Toast.LENGTH_LONG).show();
                    //Request Permission
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST);
                    return;
                }
                //Check if Device has a Sim-Card
                try {
                    TelephonyManager telephonyManager = (TelephonyManager) v.getContext().getSystemService(Context.TELEPHONY_SERVICE);
                    if(telephonyManager != null) {
                        int primarySim = telephonyManager.getSimState();
                        int secondarySim = TelephonyManager.SIM_STATE_ABSENT;
                        if (Build.VERSION.SDK_INT >= 26) {
                            primarySim = telephonyManager.getSimState(0);
                            secondarySim = telephonyManager.getSimState(1);
                        }
                        if (primarySim != TelephonyManager.SIM_STATE_READY) {
                            if (secondarySim != TelephonyManager.SIM_STATE_READY) {
                                //Device has not Sim-Card which is ready
                                Toast.makeText(v.getContext(), getResources().getText(R.string.invalid_sim), Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(v.getContext(), getResources().getText(R.string.invalid_sim), Toast.LENGTH_LONG).show();
                    return;
                }

                //Get Port
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(v.getContext());
                final int serverPort = sharedPref.getInt("server_port", 8080);

                //Set Intent
                Intent serverIntent = new Intent(v.getContext(), ServerService.class);
                if(ServerService.isRunning) {
                    serverIntent.setAction(ServerService.STOP_ACTION);
                    ServerService.isRunning = false;
                }
                else{
                    serverIntent.setAction(ServerService.START_ACTION);
                    ServerService.isRunning = true;

                    //Check if browser should be opened
                    if (sharedPref.getBoolean("open_browser_serverstart", true)) {
                        //Wait till Server is started
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while(!appContext.getSmsServer().isRunning()){
                                    try {
                                        Thread.sleep(100);
                                    }
                                    catch (Exception ex){}
                                }
                                //Open Browser
                                String serverUrl = "http://127.0.0.1:" + serverPort;
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(serverUrl));
                                startActivity(browserIntent);
                            }
                        }).start();
                    }
                }
                //Start Service
                v.getContext().startService(serverIntent);
                refreshButtonText();
            }
        });

        //Refresh Button every sec
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        Thread.sleep(1000);
                        if(getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshButtonText();
                                }
                            });
                        }
                    }
                }
                catch (Exception ex){ex.printStackTrace();}
            }
        }).start();

        return rootView;
    }

    /*
     * Check if Service is running and set correct Button-Text
     */
    private void refreshButtonText(){
        if(ServerService.isRunning){
            toggleServerBtn.setText(getResources().getText(R.string.stop_server));
        }
        else{
            toggleServerBtn.setText(getResources().getText(R.string.start_server));
        }
    }
}
