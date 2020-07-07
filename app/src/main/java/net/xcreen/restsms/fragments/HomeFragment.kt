package net.xcreen.restsms.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import net.xcreen.restsms.AppContext
import net.xcreen.restsms.R
import net.xcreen.restsms.server.ServerLogging
import net.xcreen.restsms.server.ServerService

class HomeFragment : Fragment() {
    private var toggleServerBtn: Button? = null
    private var appContext: AppContext? = null
    private val SMS_PERMISSION_REQUEST = 100

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        val context = requireContext();
        appContext = requireActivity().application as AppContext
        //Set Server-Logging for Server
        val serverLogging = ServerLogging(context.filesDir.absolutePath, context)
        appContext!!.smsServer.serverLogging = serverLogging
        toggleServerBtn = rootView.findViewById(R.id.toggle_server_btn)
        toggleServerBtn!!.setOnClickListener { v ->
            //Check if SMS-Permission is grant
            if (ActivityCompat.checkSelfPermission(v.context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) { //Show Error Toast
                Toast.makeText(v.context, resources.getText(R.string.no_sms_permission), Toast.LENGTH_LONG).show()
                //Request Permission
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.SEND_SMS), SMS_PERMISSION_REQUEST)
                return@setOnClickListener
            }
            //Check if Device has a Sim-Card
            try {
                val telephonyManager = v.context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                var primarySim = telephonyManager.simState
                var secondarySim = TelephonyManager.SIM_STATE_ABSENT
                if (Build.VERSION.SDK_INT >= 26) {
                    primarySim = telephonyManager.getSimState(0)
                    secondarySim = telephonyManager.getSimState(1)
                }
                if (primarySim != TelephonyManager.SIM_STATE_READY) {
                    if (secondarySim != TelephonyManager.SIM_STATE_READY) {
                        //Device has not Sim-Card which is ready
                        Toast.makeText(v.context, resources.getText(R.string.invalid_sim), Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                Toast.makeText(v.context, resources.getText(R.string.invalid_sim), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            //Get Port
            val sharedPref = PreferenceManager.getDefaultSharedPreferences(v.context)
            val serverPort = sharedPref.getInt("server_port", 8080)
            //Set Intent
            val serverIntent = Intent(v.context, ServerService::class.java)
            if (ServerService.isRunning) {
                serverIntent.action = ServerService.STOP_ACTION
                ServerService.isRunning = false
            } else {
                serverIntent.action = ServerService.START_ACTION
                ServerService.isRunning = true
                //Check if browser should be opened
                if (sharedPref.getBoolean("open_browser_serverstart", true)) { //Wait till Server is started
                    Thread(Runnable {
                        while (!appContext!!.smsServer.isRunning) {
                            try {
                                Thread.sleep(100)
                            }
                            catch (ex: Exception) { }
                        }
                        //Open Browser
                        val serverUrl = "http://127.0.0.1:$serverPort"
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(serverUrl))
                        startActivity(browserIntent)
                    }).start()
                }
            }
            //Start Service
            v.context.startService(serverIntent)
            refreshButtonText()
        }
        //Refresh Button every sec
        Thread(Runnable {
            try {
                while (true) {
                    Thread.sleep(1000)
                    requireActivity().runOnUiThread { refreshButtonText() }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }).start()
        return rootView
    }

    /*
     * Check if Service is running and set correct Button-Text
     */
    private fun refreshButtonText() {
        if (ServerService.isRunning) {
            toggleServerBtn!!.text = resources.getText(R.string.stop_server)
        } else {
            toggleServerBtn!!.text = resources.getText(R.string.start_server)
        }
    }
}