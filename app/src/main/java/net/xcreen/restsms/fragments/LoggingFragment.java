package net.xcreen.restsms.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.xcreen.restsms.R;

import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;

public class LoggingFragment extends Fragment {

    private ListView listView;
    ArrayList<LoggingDataModel> dataModels;
    private LoggingCustomListViewAdapter customListViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_logging, container, false);

        dataModels = new ArrayList<>();
        try {
            //Get all Logs
            String logDirPath = getContext().getFilesDir().getAbsolutePath() + File.separator + "logs";
            File logDir = new File(logDirPath);
            File[] logFiles = logDir.listFiles();
            //Add Log-files
            for(File logFile : logFiles)
                if (logFile.isFile()) {
                    //Read Line-Count
                    FileReader fileReader = new FileReader(logFile.getAbsolutePath());
                    LineNumberReader lineNumberReader = new LineNumberReader(fileReader);
                    while(lineNumberReader.skip(Long.MAX_VALUE) > 0){}
                    //Add Log-File
                    dataModels.add(new LoggingDataModel(logFile.getName(), logFile.getAbsolutePath(), lineNumberReader.getLineNumber()));
                }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        customListViewAdapter = new LoggingCustomListViewAdapter(dataModels, getActivity());
        listView = rootView.findViewById(R.id.logging_list_view);
        listView.setAdapter(customListViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Show LogFile
                String filePath = dataModels.get(position).getPath();
                Fragment loggingFragment = LoggingDetailFragment.newInstance(filePath);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_framelayout, loggingFragment).addToBackStack("fragBack").commit();
            }
        });

        return rootView;
    }
}

class LoggingDataModel{

    private String name;
    private String path;
    private int lineCount;

    public LoggingDataModel(String name, String path, int lineCount){
        this.name = name;
        this.path = path;
        this.lineCount = lineCount;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getLineCount() {
        return lineCount;
    }
}

class LoggingCustomListViewAdapter extends ArrayAdapter<LoggingDataModel> {

    private ArrayList<LoggingDataModel> dataset;
    private Context context;

    private static class ViewHolder {
        TextView nameTextView;
        TextView versionTextView;
    }

    public LoggingCustomListViewAdapter(ArrayList<LoggingDataModel> dataset, Context context) {
        super(context, R.layout.about_third_party_list_item, dataset);
        this.dataset = dataset;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LoggingDataModel dataModel = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.about_third_party_list_item, parent, false);
            viewHolder.nameTextView = convertView.findViewById(R.id.about_third_party_library_name);
            viewHolder.versionTextView = convertView.findViewById(R.id.about_third_party_library_version);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            viewHolder.nameTextView.setText(dataModel.getName());
            viewHolder.versionTextView.setText(getContext().getResources().getString(R.string.logging_log_entries, dataModel.getLineCount()));
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return convertView;
    }
}
