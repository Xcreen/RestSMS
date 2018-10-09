package net.xcreen.restsms;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.net.BindException;

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

        toggleServerBtn = rootView.findViewById(R.id.toggle_server_btn);
        toggleServerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if SMS-Permission is grant
                if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    //Check if Device has a Sim-Card
                    try {
                        TelephonyManager telephonyManager = (TelephonyManager) v.getContext().getSystemService(Context.TELEPHONY_SERVICE);
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
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(v.getContext(), getResources().getText(R.string.invalid_sim), Toast.LENGTH_LONG).show();
                        return;
                    }

                    //Check if Server is running
                    if (appContext.smsServer.isRunning() && !appContext.smsServer.isStopping()) {
                        //Stop Server
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    appContext.smsServer.stop();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }).start();
                        //Switch Button-Text
                        toggleServerBtn.setText(getResources().getText(R.string.start_server));
                    } else if (!appContext.smsServer.isStopping()) {
                        final String cacheDir = v.getContext().getCacheDir().getAbsolutePath();

                        //Set Port
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(v.getContext());
                        appContext.smsServer.setPort(sharedPref.getInt("server_port", 8080));

                        //Start Server
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    appContext.smsServer.start(cacheDir);
                                }
                                catch(BindException bindEx){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Toast.makeText(getContext(), getResources().getText(R.string.server_failed_bindex), Toast.LENGTH_LONG).show();
                                                toggleServerBtn.setText(getResources().getText(R.string.start_server));
                                            }
                                            catch (Exception ex){ ex.printStackTrace(); }
                                        }
                                    });
                                }
                                catch (Exception ex) {
                                    ex.printStackTrace();
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Toast.makeText(getContext(), getResources().getText(R.string.server_failed_to_start), Toast.LENGTH_LONG).show();
                                                toggleServerBtn.setText(getResources().getText(R.string.start_server));
                                            }
                                            catch (Exception ex){ ex.printStackTrace(); }
                                        }
                                    });
                                }
                            }
                        }).start();

                        //Switch Button-Text
                        toggleServerBtn.setText(getResources().getText(R.string.stop_server));
                    } else {
                        //Server is stopping
                        Toast.makeText(v.getContext(), getResources().getText(R.string.server_is_stopping), Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    //Show Error Toast
                    Toast.makeText(v.getContext(), getResources().getText(R.string.no_sms_permission), Toast.LENGTH_LONG).show();
                    //Request Permission
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_REQUEST);
                }
            }
        });

        //Check Server is running, to set correct Button-Text
        if(appContext.smsServer.isRunning()){
            toggleServerBtn.setText(getResources().getText(R.string.stop_server));
        }

        return rootView;
    }
}
