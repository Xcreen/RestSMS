package net.xcreen.restsms.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import net.xcreen.restsms.R
import java.io.BufferedReader
import java.io.File

class LoggingDetailFragment : Fragment() {
    private var logFilePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            logFilePath = requireArguments().getString("logfilepath")
        }
        catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_logging_detail, container, false)
        val logFile = File(logFilePath!!)
        val headlineTextview = rootView.findViewById<TextView>(R.id.logging_detail_headline_textview)
        headlineTextview.text = resources.getString(R.string.logging_detail_headline, logFile.name)
        val loggingTextview = rootView.findViewById<TextView>(R.id.logging_detail_textview)
        try {
            val fileInputStream = File(logFile.toURI()).inputStream()
            val logFileText = fileInputStream.bufferedReader().use(BufferedReader::readText)
            loggingTextview.text = logFileText
        }
        catch (ex: Exception) {
            ex.printStackTrace()
        }
        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(logFilePath: String?): LoggingDetailFragment {
            val fragment = LoggingDetailFragment()
            val bundle = Bundle(1)
            bundle.putString("logfilepath", logFilePath)
            fragment.arguments = bundle
            return fragment
        }
    }
}