package net.xcreen.restsms.server;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import net.xcreen.restsms.AppContext;
import net.xcreen.restsms.R;

import java.net.BindException;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class ServerService extends Service {

    public static boolean isRunning = false;
    public static final String START_ACTION = "start";
    public static final String STOP_ACTION = "stop";
    private final int SERVER_SERVICE_ID = 10000;
    private final String NOTIFICATION_CHANNEL_ID = "sms_server_notification_channel";
    private final String NOTIFICATION_CHANNEL_NAME = "Sms-Server Service";
    AppContext appContext;

    @Override
    public void onCreate() {
        this.appContext = (AppContext) getApplication();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String intentAction = intent.getAction();
        if(intentAction != null && intentAction.equals(START_ACTION)) {
            //Check if Server is already running or in process
            if (!appContext.getSmsServer().isRunning() && !appContext.getSmsServer().isStopping()) {
                startService();
            }
        }
        else if(intentAction != null && intentAction.equals(STOP_ACTION)){
            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }

    /**
     * Start Server and Service in Foreground
     */
    public void startService(){
        //Set Port
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int serverPort = sharedPref.getInt("server_port", 8080);
        appContext.getSmsServer().setPort(serverPort);

        //Set Stop-Button
        Intent stopIntent = new Intent(this, ServerService.class);
        stopIntent.setAction(STOP_ACTION);
        PendingIntent pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, 0);

        //Setup Notification-Channel
        if(Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if(notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        //Create Notification
        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text, serverPort))
                .setSmallIcon(R.drawable.notification_icon)
                .setColor(getColor(R.color.colorPrimary))
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .addAction(R.drawable.notification_stop, getString(R.string.stop_server), pendingStopIntent)
                .build();

        //Start Serve in new Thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Start Server
                    String cacheDir = getCacheDir().getAbsolutePath();
                    appContext.getSmsServer().start(cacheDir);
                }
                catch(BindException bindEx){
                    //Failed to bind on the given port
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getResources().getText(R.string.server_failed_bindex), Toast.LENGTH_LONG).show();
                        }
                    });
                    appContext.getSmsServer().getServerLogging().log("error", "Server cant start up on this port (Bind-Exception)!");
                }
                catch (Exception ex){
                    ex.printStackTrace();
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getResources().getText(R.string.server_failed_to_start), Toast.LENGTH_LONG).show();
                        }
                    });
                    appContext.getSmsServer().getServerLogging().log("error", "Failed to start up server!");
                }
                finally {
                    //Stop Service
                    Intent serverIntent = new Intent(getApplicationContext(), ServerService.class);
                    serverIntent.setAction(STOP_ACTION);
                    startService(serverIntent);
                }
            }
        }).start();
        //Set in Foreground
        startForeground(SERVER_SERVICE_ID, notification);
    }

    @Override
    public void onDestroy() {
        Log.i("ServerService", "onDestroy()");
        try {
            if(appContext.getSmsServer().isRunning() && !appContext.getSmsServer().isStopping()) {
                appContext.getSmsServer().stop();
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        isRunning = false;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
