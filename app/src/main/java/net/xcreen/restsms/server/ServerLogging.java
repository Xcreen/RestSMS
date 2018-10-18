package net.xcreen.restsms.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ServerLogging {

    private String filepath = null;
    private Context context;

    public ServerLogging(String filepath, Context context){
        this.filepath = filepath + File.separator + "logs";
        this.context = context;
    }

    /**
     * Write Log-Entry to File
     * @param type - String Log-Type (error,warning,info)
     * @param message - String Log-Message
     */
    public void log(String type, String message){
        //Stop processing, when logging is disabled
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if(sharedPref.getBoolean("disable_logging", false)){
            return;
        }
        //Create and get Log-File
        String path = setupLogging();

        //Create Entry
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String entry = dateFormat.format(date) + " - Type: " + type + " - Message: " + message;

        //Write Log-Output
        switch (type){
            case "error":
                Log.e("Server-Logging", entry);
                break;
            case "warning":
                Log.w("Server-Logging", entry);
                break;
            case "debug":
                Log.d("Server-Logging", entry);
                break;
            case "info":
                Log.i("Server-Logging", entry);
                break;
            default:
                Log.i("Server-Logging", entry);
                break;
        }

        //Write to Log-File
        if(path != null) {
            try (FileWriter fw = new FileWriter(path, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println(entry);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else{
            Log.i("Server-Logging", "Failed to write to log!");
        }
    }

    /**
     * Get File for Logging
     * @return String - Log-File-Path
     */
    private String setupLogging(){
        if(filepath != null){
            //Create Filename
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date();
            String filename = dateFormat.format(date) + "-server.log";
            //Create File
            String path = filepath + File.separator + filename;
            File logFile = new File(path);
            //Create dir/file
            try {
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            return path;
        }
        return null;
    }

}
