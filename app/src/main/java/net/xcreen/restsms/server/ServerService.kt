package net.xcreen.restsms.server

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import net.xcreen.restsms.AppContext
import net.xcreen.restsms.R
import java.net.BindException

class ServerService : Service() {
    private val SERVER_SERVICE_ID = 10000
    private val NOTIFICATION_CHANNEL_ID = "sms_server_notification_channel"
    private val NOTIFICATION_CHANNEL_NAME = "Sms-Server Service"
    private var appContext: AppContext? = null

    companion object {
        var isRunning = false
        const val START_ACTION = "start"
        const val STOP_ACTION = "stop"
    }

    override fun onCreate() {
        appContext = application as AppContext
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val intentAction = intent.action
        if (intentAction != null && intentAction == START_ACTION) {
            //Check if Server is already running or in process
            if (!appContext?.smsServer?.isRunning!! && !appContext?.smsServer?.isStopping!!) {
                startService()
            }
        }
        else if (intentAction != null && intentAction == STOP_ACTION) {
            stopForeground(true)
            stopSelf()
        }
        return START_STICKY
    }

    /**
     * Start Server and Service in Foreground
     */
    private fun startService() {
        //Set Port
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val serverPort = sharedPref.getInt("server_port", 8080)
        appContext?.smsServer?.port = serverPort
        //Set Auth params
        val goodToken = sharedPref.getString("server_token", "") ?: ""
        val authEnabled = sharedPref.getBoolean("enable_auth", false)
        appContext?.smsServer?.goodToken = goodToken
        appContext?.smsServer?.authEnabled = authEnabled

        //Set Stop-Button
        val stopIntent = Intent(this, ServerService::class.java)
        stopIntent.action = STOP_ACTION
        var pendingStopIntent: PendingIntent
        if (Build.VERSION.SDK_INT >= 31) {
            pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_MUTABLE)
        }
        else {
            pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_MUTABLE)
        }

        //Setup Notification-Channel
        if (Build.VERSION.SDK_INT >= 26) {
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
        //Create Notification
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text, serverPort))
                .setSmallIcon(R.drawable.notification_icon)
                .setColor(getColor(R.color.colorPrimary))
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .addAction(R.drawable.notification_stop, getString(R.string.stop_server), pendingStopIntent)
                .build()

        //Start Serve in new Thread
        Thread(Runnable {
            try {
                //Start Server
                val cacheDir = cacheDir.absolutePath
                appContext?.smsServer?.start(cacheDir)
            } catch (bindEx: BindException) {
                //Failed to bind on the given port
                val handler = Handler(Looper.getMainLooper())
                handler.post { Toast.makeText(applicationContext, resources.getText(R.string.server_failed_bindex), Toast.LENGTH_LONG).show() }
                appContext?.smsServer?.serverLogging!!.log("error", "Server cant start up on this port (Bind-Exception)!")
            } catch (ex: Exception) {
                ex.printStackTrace()
                val handler = Handler(Looper.getMainLooper())
                handler.post { Toast.makeText(applicationContext, resources.getText(R.string.server_failed_to_start), Toast.LENGTH_LONG).show() }
                appContext?.smsServer?.serverLogging!!.log("error", "Failed to start up server!")
            } finally {
                //Stop Service
                val serverIntent = Intent(applicationContext, ServerService::class.java)
                serverIntent.action = STOP_ACTION
                startService(serverIntent)
            }
        }).start()
        //Set in Foreground
        startForeground(SERVER_SERVICE_ID, notification)
    }

    override fun onDestroy() {
        Log.i("ServerService", "onDestroy()")
        try {
            if (appContext?.smsServer?.isRunning!! && !appContext?.smsServer?.isStopping!!) {
                appContext?.smsServer?.stop()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        isRunning = false
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}