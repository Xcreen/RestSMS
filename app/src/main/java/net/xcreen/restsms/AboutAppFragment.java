package net.xcreen.restsms;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutAppFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about_app, container, false);

        //Set Version-Name
        TextView versionResultTV = rootView.findViewById(R.id.about_app_version_result_textview);
        versionResultTV.setText(BuildConfig.VERSION_NAME);

        //Set Version
        TextView versionNameResultTV = rootView.findViewById(R.id.about_app_versionname_result_textview);
        versionNameResultTV.setText(String.valueOf(BuildConfig.VERSION_CODE));

        //Check if has SMS-Permission
        TextView smsPermissionResultTV = rootView.findViewById(R.id.about_app_smspermission_result_textview);
        try {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                smsPermissionResultTV.setText(R.string.yes);
            } else {
                smsPermissionResultTV.setText(R.string.no);
            }
        }
        catch (Exception ex){
            smsPermissionResultTV.setText(R.string.unknown);
            ex.printStackTrace();
        }

        //Check Sim-State
        int primarySim = TelephonyManager.SIM_STATE_ABSENT;
        int secondarySim = TelephonyManager.SIM_STATE_ABSENT;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
            primarySim = telephonyManager.getSimState();
            if (Build.VERSION.SDK_INT >= 26) {
                primarySim = telephonyManager.getSimState(0);
                secondarySim = telephonyManager.getSimState(1);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        //Show Sim-State
        TextView simState1ResultTV = rootView.findViewById(R.id.about_app_sim1state_result_textview);
        int primarySimTextID = getTextIdBySimState(primarySim);
        if(primarySimTextID != 0) {
            simState1ResultTV.setText(primarySimTextID);
        }
        TextView simState2ResultTV = rootView.findViewById(R.id.about_app_sim2state_result_textview);
        int secondarySimTextID = getTextIdBySimState(secondarySim);
        if(secondarySimTextID != 0) {
            simState2ResultTV.setText(secondarySimTextID);
        }

        //Show Default-SMS-App
        String defaultSMSApp = Telephony.Sms.getDefaultSmsPackage(getContext());
        TextView defaultSMSAppResultTV = rootView.findViewById(R.id.about_app_defaultsmsapp_result_textview);
        if(defaultSMSApp != null){
            defaultSMSAppResultTV.setText(defaultSMSApp);
        }

        return rootView;
    }

    /**
     * Returns the String-ID for the Sim-Status
     * @param simState - SimState
     * @return id - Text-ID
     */
    private int getTextIdBySimState(int simState){
        switch (simState){
            case TelephonyManager.SIM_STATE_ABSENT:
                return R.string.error_sim_absent;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                return R.string.error_sim_pin;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                return R.string.error_sim_puk;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                return R.string.error_sim_network_pin;
            case TelephonyManager.SIM_STATE_READY:
                return R.string.error_sim_ready;
            case TelephonyManager.SIM_STATE_NOT_READY:
                return R.string.error_sim_not_ready;
            case TelephonyManager.SIM_STATE_PERM_DISABLED:
                return R.string.error_sim_disabled;
            case TelephonyManager.SIM_STATE_CARD_IO_ERROR:
                return R.string.error_sim_card_error;
            case TelephonyManager.SIM_STATE_CARD_RESTRICTED:
                return R.string.error_sim_restricted;
            default:
                return 0;
        }
    }
}
