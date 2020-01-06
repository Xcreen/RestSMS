package net.xcreen.restsms.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.xcreen.restsms.R;

import java.util.ArrayList;

public class AboutThirdPartyLibrarysFragment extends Fragment {

    private ArrayList<DataModel> dataModels;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about_third_party_librarys, container, false);

        dataModels = new ArrayList<>();
        //Set Data
        dataModels.add(new DataModel("FontAwesome", "4.7.0 / Used for Navigation-Icons", "https://fontawesome.com/"));
        dataModels.add(new DataModel("Jetty", "9.2.26.v20180806 / Used for the HTTP-Server", "https://www.eclipse.org/jetty/"));
        dataModels.add(new DataModel("GSON", "2.8.5 / Used for JSON/Java-Object serialization/deserialization", "https://github.com/google/gson"));
        dataModels.add(new DataModel("libphonenumber", "8.9.14 / Used validating/parsing phone-numbers.", "https://github.com/googlei18n/libphonenumber"));
        dataModels.add(new DataModel("Slf4j-simple", "1.7.25 / Used Jetty-Logging", "https://www.slf4j.org/"));

        CustomListViewAdapter customListViewAdapter = new CustomListViewAdapter(dataModels, getActivity());
        ListView listView = rootView.findViewById(R.id.about_third_party_list_view);
        listView.setAdapter(customListViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(getResources().getColor(R.color.colorDarkBlack, null));
                CustomTabsIntent customTabsIntent = builder.build();

                //Open Library Website
                String url = dataModels.get(position).getUrl();
                if(url != null && getContext() != null){
                    customTabsIntent.launchUrl(getContext(), Uri.parse(url));
                }
            }
        });

        return rootView;
    }
}

class DataModel{

    private String name;
    private String version;
    private String url;

    public DataModel(String name, String version, String url){
        this.name = name;
        this.version = version;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getUrl() {
        return url;
    }
}

class CustomListViewAdapter extends ArrayAdapter<DataModel> {


    private static class ViewHolder {
        TextView nameTextView;
        TextView versionTextView;
    }

    public CustomListViewAdapter(ArrayList<DataModel> dataset, Context context) {
        super(context, R.layout.about_third_party_list_item, dataset);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DataModel dataModel = getItem(position);
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

        if(dataModel != null) {
            viewHolder.nameTextView.setText(dataModel.getName());
            viewHolder.versionTextView.setText(dataModel.getVersion());
        }
        return convertView;
    }
}

