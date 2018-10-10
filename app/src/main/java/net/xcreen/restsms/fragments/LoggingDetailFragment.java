package net.xcreen.restsms.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.xcreen.restsms.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class LoggingDetailFragment extends Fragment {

    private String logFilePath;

    public static LoggingDetailFragment newInstance(String logFilePath) {
        LoggingDetailFragment fragment = new LoggingDetailFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString("logfilepath", logFilePath);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            logFilePath = getArguments().getString("logfilepath");
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_logging_detail, container, false);

        File logFile = new File(logFilePath);

        TextView headlineTextview = rootView.findViewById(R.id.logging_detail_headline_textview);
        headlineTextview.setText(getResources().getString(R.string.logging_detail_headline, logFile.getName()));

        TextView loggingTextview = rootView.findViewById(R.id.logging_detail_textview);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(logFile));
            String line = "";
            String logFileText = "";
            while((line = bufferedReader.readLine()) != null){
                logFileText += line  + "\n";
            }
            loggingTextview.setText(logFileText);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return rootView;
    }
}
