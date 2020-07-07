package net.xcreen.restsms.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import net.xcreen.restsms.R
import net.xcreen.restsms.fragments.LoggingDetailFragment.Companion.newInstance
import java.io.File
import java.io.FileReader
import java.io.LineNumberReader
import java.util.*

class LoggingFragment : Fragment() {
    var dataModels = ArrayList<LoggingDataModel>()
    private var customListViewAdapter: LoggingCustomListViewAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_logging, container, false)
        try {
            dataModels.clear()
            //Get all Logs
            val logDirPath = requireContext().filesDir.absolutePath + File.separator + "logs"
            val logDir = File(logDirPath)
            val logFiles = logDir.listFiles()
            if (logFiles != null) { //Add Log-files
                for (logFile in logFiles){
                    if (logFile.isFile) {
                        //Read Line-Count
                        val lineNumberReader = LineNumberReader(FileReader(logFile.absolutePath))
                        while(lineNumberReader.skip(Int.MAX_VALUE.toLong()) > 0)
                        //Add Log-File
                        dataModels.add(LoggingDataModel(logFile.name, logFile.absolutePath, lineNumberReader.lineNumber))
                    }
                }
            }
        }
        catch (ex: Exception) {
            ex.printStackTrace()
        }
        customListViewAdapter = LoggingCustomListViewAdapter(dataModels, activity)
        val listView = rootView.findViewById<ListView>(R.id.logging_list_view)
        listView.adapter = customListViewAdapter
        listView.setOnItemClickListener { _, _, position, _ ->
            //Show LogFile
            val filePath = dataModels[position].path
            val loggingFragment: Fragment = newInstance(filePath)
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.main_framelayout, loggingFragment).addToBackStack("fragBack").commit()
        }
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val filePath = dataModels[position].path
            val logFile = File(filePath)
            val dialogClickListener = DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE ->  //Delete File
                        if (logFile.delete()) { //Remove Item from ListView
                            dataModels.removeAt(position)
                            customListViewAdapter!!.notifyDataSetChanged()
                            Toast.makeText(context, getString(R.string.logging_successful_deleted), Toast.LENGTH_LONG).show()
                        } else { //Failed to delete File
                            Toast.makeText(context, getString(R.string.logging_failed_to_delete), Toast.LENGTH_LONG).show()
                        }
                    DialogInterface.BUTTON_NEGATIVE -> {
                    }
                }
            }
            val builder = AlertDialog.Builder(context)
            builder.setMessage(getString(R.string.logging_delete, logFile.name))
                    .setTitle(getString(R.string.logging_delete_headline))
                    .setPositiveButton(getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(R.string.no), dialogClickListener)
                    .show()
            true
        }
        return rootView
    }
}

class LoggingDataModel(val name: String, val path: String, val lineCount: Int)

internal class LoggingCustomListViewAdapter(dataset: ArrayList<LoggingDataModel>?, context: Context?) : ArrayAdapter<LoggingDataModel?>(context!!, R.layout.about_third_party_list_item, dataset!! as List<LoggingDataModel?>) {
    private class ViewHolder {
        var nameTextView: TextView? = null
        var versionTextView: TextView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var returnConvertView = convertView
        val dataModel = getItem(position)
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
        if (dataModel != null) {
            viewHolder.nameTextView!!.text = dataModel.name
            viewHolder.versionTextView!!.text = context.resources.getString(R.string.logging_log_entries, dataModel.lineCount)
        }
        return returnConvertView!!
    }
}