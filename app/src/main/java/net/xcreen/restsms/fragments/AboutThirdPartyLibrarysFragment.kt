package net.xcreen.restsms.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import net.xcreen.restsms.R
import java.util.*

class AboutThirdPartyLibrarysFragment : Fragment() {
    private var thirdPartyLibraryDataModels = ArrayList<ThirdPartyLibraryDataModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_about_third_party_librarys, container, false)
        //Set Data
        thirdPartyLibraryDataModels.add(ThirdPartyLibraryDataModel("FontAwesome", "4.7.0 / Used for Navigation-Icons", "https://fontawesome.com/"))
        thirdPartyLibraryDataModels.add(ThirdPartyLibraryDataModel("Jetty", "9.2.30.v20200428 / Used for the HTTP-Server", "https://www.eclipse.org/jetty/"))
        thirdPartyLibraryDataModels.add(ThirdPartyLibraryDataModel("GSON", "2.9.1 / Used for JSON/Java-Object serialization/deserialization", "https://github.com/google/gson"))
        thirdPartyLibraryDataModels.add(ThirdPartyLibraryDataModel("libphonenumber", "8.12.55 / Used validating/parsing phone-numbers.", "https://github.com/googlei18n/libphonenumber"))
        thirdPartyLibraryDataModels.add(ThirdPartyLibraryDataModel("Slf4j-simple", "1.7.36 / Used Jetty-Logging", "https://www.slf4j.org/"))
        val thirdPartyLibraryCustomListViewAdapter = ThirdPartyLibraryCustomListViewAdapter(thirdPartyLibraryDataModels, activity)
        val listView = rootView.findViewById<ListView>(R.id.about_third_party_list_view)
        listView.adapter = thirdPartyLibraryCustomListViewAdapter
        listView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            val customTabColorSchemeParams = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(resources.getColor(R.color.colorDarkBlack, null))
                .setNavigationBarColor(resources.getColor(R.color.colorDarkBlack, null))
                .build()
            val builder = CustomTabsIntent.Builder()
            builder.setDefaultColorSchemeParams(customTabColorSchemeParams)
            val customTabsIntent = builder.build()
            //Open Library Website
            val url = thirdPartyLibraryDataModels[position].url
            if (context != null) {
                customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
            }
        }
        return rootView
    }
}

internal class ThirdPartyLibraryDataModel(val name: String, val version: String, val url: String)

internal class ThirdPartyLibraryCustomListViewAdapter(dataset: ArrayList<ThirdPartyLibraryDataModel>?, context: Context?) : ArrayAdapter<ThirdPartyLibraryDataModel?>(context!!, R.layout.about_third_party_list_item, dataset!! as List<ThirdPartyLibraryDataModel?>) {

    private class ViewHolder {
        var nameTextView: TextView? = null
        var versionTextView: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var returnConvertView = convertView
        val thirdPartyLibraryDataModel = getItem(position)
        val viewHolder: ViewHolder
        if (convertView == null) {
            viewHolder = ViewHolder()
            val inflater = LayoutInflater.from(context)
            returnConvertView = inflater.inflate(R.layout.about_third_party_list_item, parent, false)
            viewHolder.nameTextView = returnConvertView.findViewById(R.id.about_third_party_library_name)
            viewHolder.versionTextView = returnConvertView.findViewById(R.id.about_third_party_library_version)
            returnConvertView.tag = viewHolder
        } else {
            viewHolder = returnConvertView!!.tag as ViewHolder
        }
        if (thirdPartyLibraryDataModel != null) {
            viewHolder.nameTextView!!.text = thirdPartyLibraryDataModel.name
            viewHolder.versionTextView!!.text = thirdPartyLibraryDataModel.version
        }
        return returnConvertView!!
    }
}