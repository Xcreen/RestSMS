package net.xcreen.restsms.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import net.xcreen.restsms.BuildConfig
import net.xcreen.restsms.R

class AboutAppFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_about_app, container, false)
        //Set Version-Name
        val versionResultTV = rootView.findViewById<TextView>(R.id.about_app_version_result_textview)
        versionResultTV.text = BuildConfig.VERSION_NAME
        //Set Version
        val versionNameResultTV = rootView.findViewById<TextView>(R.id.about_app_versionname_result_textview)
        versionNameResultTV.text = BuildConfig.VERSION_CODE.toString()
        //Check if has SMS-Permission
        val smsPermissionResultImageView = rootView.findViewById<ImageView>(R.id.about_app_smspermission_result_iv)
        try {
            smsPermissionResultImageView.setColorFilter(requireContext().getColor(R.color.colorError), PorterDuff.Mode.SRC_ATOP)
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                smsPermissionResultImageView.setImageDrawable(requireContext().getDrawable(R.drawable.check_yes))
                smsPermissionResultImageView.setColorFilter(requireContext().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        //Check Sim-State
        var primarySim = TelephonyManager.SIM_STATE_ABSENT
        var secondarySim = TelephonyManager.SIM_STATE_ABSENT
        try {
            val telephonyManager = requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            primarySim = telephonyManager.simState
            if (Build.VERSION.SDK_INT >= 26) {
                primarySim = telephonyManager.getSimState(0)
                secondarySim = telephonyManager.getSimState(1)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        //Show Sim-State
        val simState1ResultTV = rootView.findViewById<TextView>(R.id.about_app_sim1state_result_textview)
        val primarySimTextID = getTextIdBySimState(primarySim)
        if (primarySimTextID != 0) {
            simState1ResultTV.setText(primarySimTextID)
        }
        val simState2ResultTV = rootView.findViewById<TextView>(R.id.about_app_sim2state_result_textview)
        val secondarySimTextID = getTextIdBySimState(secondarySim)
        if (secondarySimTextID != 0) {
            simState2ResultTV.setText(secondarySimTextID)
        }
        //Show Default-SMS-App
        val defaultSMSApp = Telephony.Sms.getDefaultSmsPackage(context)
        val defaultSMSAppResultTV = rootView.findViewById<TextView>(R.id.about_app_defaultsmsapp_result_textview)
        if (defaultSMSApp != null) {
            defaultSMSAppResultTV.text = defaultSMSApp
        }
        return rootView
    }

    /**
     * Returns the String-ID for the Sim-Status
     * @param simState - SimState
     * @return id - Text-ID
     */
    private fun getTextIdBySimState(simState: Int): Int {
        return when (simState) {
            TelephonyManager.SIM_STATE_ABSENT -> R.string.error_sim_absent
            TelephonyManager.SIM_STATE_PIN_REQUIRED -> R.string.error_sim_pin
            TelephonyManager.SIM_STATE_PUK_REQUIRED -> R.string.error_sim_puk
            TelephonyManager.SIM_STATE_NETWORK_LOCKED -> R.string.error_sim_network_pin
            TelephonyManager.SIM_STATE_READY -> R.string.error_sim_ready
            TelephonyManager.SIM_STATE_NOT_READY -> R.string.error_sim_not_ready
            TelephonyManager.SIM_STATE_PERM_DISABLED -> R.string.error_sim_disabled
            TelephonyManager.SIM_STATE_CARD_IO_ERROR -> R.string.error_sim_card_error
            TelephonyManager.SIM_STATE_CARD_RESTRICTED -> R.string.error_sim_restricted
            else -> 0
        }
    }
}