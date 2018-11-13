package net.xcreen.restsms.server;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
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
            if (!appContext.smsServer.isRunning() && !appContext.smsServer.isStopping()) {
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
        appContext.smsServer.setPort(serverPort);

        //Set Stop-Button
        Intent stopIntent = new Intent(this, ServerService.class);
        stopIntent.setAction("stop");
        PendingIntent pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, 0);

        //Create Notification
        Notification notification = new NotificationCompat.Builder(this, "TESTID")
                .setSmallIcon(R.drawable.launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text, serverPort))
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
                    appContext.smsServer.start(cacheDir);
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
                    appContext.smsServer.getServerLogging().log("error", "Server cant start up on this port (Bind-Exception)!");
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
                    appContext.smsServer.getServerLogging().log("error", "Failed to start up server!");
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
        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        Log.i("ServerService", "onDestroy()");
        try {
            if(appContext.smsServer.isRunning() && !appContext.smsServer.isStopping()) {
                appContext.smsServer.stop();
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
