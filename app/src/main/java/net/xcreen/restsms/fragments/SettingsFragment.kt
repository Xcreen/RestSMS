package net.xcreen.restsms.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import net.xcreen.restsms.R

class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_settings, container, false)
        val currentContext: Context = context as Context
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(currentContext)
        val portEditText = rootView.findViewById<EditText>(R.id.settings_port_edittext)
        val openBrowserCheckBox = rootView.findViewById<CheckBox>(R.id.settings_open_browser_checkbox)
        val disableLoggingCheckBox = rootView.findViewById<CheckBox>(R.id.settings_disable_logging_checkbox)
        val saveBtn = rootView.findViewById<Button>(R.id.settings_save_btn)
        saveBtn.setOnClickListener { v ->
            var saved = false
            val editor = sharedPref.edit()
            //Save Port
            if (portEditText.text.isNotEmpty()) {
                var newPort = 0
                try {
                    newPort = portEditText.text.toString().toInt()
                } catch (ex: Exception) {
                    Toast.makeText(v.context, resources.getText(R.string.setting_invalid_port), Toast.LENGTH_SHORT).show()
                }
                if (newPort in 1..65534) { //Set Port
                    editor.putInt("server_port", newPort)
                    editor.apply()
                    saved = true
                } else {
                    Toast.makeText(v.context, resources.getText(R.string.setting_invalid_port_range), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(v.context, resources.getText(R.string.setting_invalid_port), Toast.LENGTH_SHORT).show()
            }
            //Save Open-Browser after Server-Start
            editor.putBoolean("open_browser_serverstart", openBrowserCheckBox.isChecked)
            editor.apply()
            //Save Disable-Logging-Option
            editor.putBoolean("disable_logging", disableLoggingCheckBox.isChecked)
            editor.apply()
            if (saved) {
                Toast.makeText(v.context, resources.getText(R.string.setting_saved), Toast.LENGTH_SHORT).show()
            }
        }
        //Set current Port
        portEditText.setText(sharedPref.getInt("server_port", 8080).toString())
        //Set current "Open-Browser after Server-Start"-Option
        if (sharedPref.getBoolean("open_browser_serverstart", true)) {
            openBrowserCheckBox.isChecked = true
        }
        //Set current "Disable Logging"-Option
        if (sharedPref.getBoolean("disable_logging", false)) {
            disableLoggingCheckBox.isChecked = true
        }
        return rootView
    }
}