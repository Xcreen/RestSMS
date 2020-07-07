package net.xcreen.restsms.server

import android.content.Context
import androidx.preference.PreferenceManager
import android.util.Log
import java.io.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ServerLogging(filepath: String, private val context: Context) {
    private val filepath: String = filepath + File.separator + "logs"

    /**
     * Write Log-Entry to File
     * @param type - String Log-Type (error,warning,info)
     * @param message - String Log-Message
     */
    fun log(type: String, message: String) {
        //Stop processing, when logging is disabled
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        if (sharedPref.getBoolean("disable_logging", false)) {
            return
        }
        //Create and get Log-File
        val path = setupLogging()

        //Create Entry
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date()
        val entry = dateFormat.format(date) + " - Type: " + type + " - Message: " + message
        when (type) {
            "error" -> Log.e("Server-Logging", entry)
            "warning" -> Log.w("Server-Logging", entry)
            "debug" -> Log.d("Server-Logging", entry)
            "info" -> Log.i("Server-Logging", entry)
            else -> Log.i("Server-Logging", entry)
        }

        //Write to Log-File
        if (path != null) {
            try {
                FileWriter(path, true).use {
                    fw -> BufferedWriter(fw).use {
                        bw -> PrintWriter(bw).use {
                            out -> out.println(entry)
                        }
                    }
                }
            }
            catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        else {
            Log.i("Server-Logging", "Failed to write to log!")
        }
    }

    /**
     * Get File for Logging
     * @return String - Log-File-Path
     */
    private fun setupLogging(): String? {
        //Create Filename
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = Date()
        val filename = dateFormat.format(date) + "-server.log"
        //Create File
        val path = filepath + File.separator + filename
        val logFile = File(path)
        //Create dir/file
        try {
            logFile.parentFile?.mkdirs()
            logFile.createNewFile()
        }
        catch (ex: Exception) {
            ex.printStackTrace()
        }
        return path
    }
}