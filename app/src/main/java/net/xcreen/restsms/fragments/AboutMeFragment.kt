package net.xcreen.restsms.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.fragment.app.Fragment
import androidx.browser.customtabs.CustomTabsIntent

import net.xcreen.restsms.R

class AboutMeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_about_me, container, false)
        val customTabColorSchemeParams = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(resources.getColor(R.color.colorDarkBlack, null))
            .setNavigationBarColor(resources.getColor(R.color.colorDarkBlack, null))
            .build()

        val donateBtn = rootView.findViewById<Button>(R.id.about_me_donate_btn)
        donateBtn.setOnClickListener { v ->
            val builder = CustomTabsIntent.Builder()
            builder.setDefaultColorSchemeParams(customTabColorSchemeParams)
            val customTabsIntent = builder.build()
            if (context != null) {
                //Open Paypal Website
                customTabsIntent.launchUrl(v.context, android.net.Uri.parse("https://www.paypal.me/xcreen"))
            }
        }

        val githubBtn = rootView.findViewById<Button>(R.id.about_me_github_btn)
        githubBtn.setOnClickListener { v->
            val builder = CustomTabsIntent.Builder()
            builder.setDefaultColorSchemeParams(customTabColorSchemeParams)
            val customTabsIntent = builder.build()
            if (context != null) {
                //Open Github Website
                customTabsIntent.launchUrl(v.context, android.net.Uri.parse("https://github.com/Xcreen"))
            }
        }
        return rootView
    }
}