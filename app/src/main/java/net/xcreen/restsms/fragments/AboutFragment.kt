package net.xcreen.restsms.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import net.xcreen.restsms.R

class AboutFragment : Fragment() {
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    private var mainAppBarLayout: AppBarLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_about, container, false)
        if (activity != null) { //Set Tabs
            tabLayout = TabLayout(activity!!)
            tabLayout!!.addTab(tabLayout!!.newTab().setText(resources.getText(R.string.about_about_me)))
            tabLayout!!.addTab(tabLayout!!.newTab().setText(resources.getText(R.string.about_app_information)))
            tabLayout!!.addTab(tabLayout!!.newTab().setText(resources.getText(R.string.about_third_party_librarys)))
            tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL
            tabLayout!!.setBackgroundColor(resources.getColor(R.color.colorDarkBlack, null))
            tabLayout!!.setTabTextColors(resources.getColor(R.color.colorWhite, null), resources.getColor(R.color.colorLightBlue, null))
            tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager!!.currentItem = tab.position
                }
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
            //Add TabLayout to the AppBarLayout
            mainAppBarLayout = activity!!.findViewById(R.id.appbar_layout)
            mainAppBarLayout!!.addView(tabLayout, LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            //Set Page-Adapter to ViewPager
            val pagerAdapter = FragmentPageAdapter(fragmentManager, tabLayout!!.tabCount)
            viewPager = rootView.findViewById(R.id.about_view_pager)
            viewPager!!.adapter = pagerAdapter
            viewPager!!.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        }
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            //Remove Tab-Layout
            mainAppBarLayout!!.removeView(tabLayout)
        }
        catch (ex: Exception){
            ex.printStackTrace()
        }
    }
}

internal class FragmentPageAdapter(fm: FragmentManager?, private val numberOfTabs: Int) : FragmentStatePagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> AboutAppFragment()
            2 -> AboutThirdPartyLibrarysFragment()
            0 -> AboutMeFragment()
            else -> AboutMeFragment()
        }
    }

    override fun getCount(): Int {
        return numberOfTabs
    }
}