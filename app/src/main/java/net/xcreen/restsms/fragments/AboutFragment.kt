package net.xcreen.restsms.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import net.xcreen.restsms.R

class AboutFragment : Fragment() {
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager2? = null
    private var mainAppBarLayout: AppBarLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_about, container, false)
        if (activity != null) { //Set Tabs
            tabLayout = TabLayout(requireActivity())
            tabLayout!!.addTab(tabLayout!!.newTab()) // About-Me Tab
            tabLayout!!.addTab(tabLayout!!.newTab()) // App-Info Tab
            tabLayout!!.addTab(tabLayout!!.newTab()) // Third-Party-Info Tab
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
            mainAppBarLayout = requireActivity().findViewById(R.id.appbar_layout)
            mainAppBarLayout!!.addView(tabLayout, LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            //Set Page-Adapter to ViewPager
            val pagerAdapter = FragmentPageAdapter(this, tabLayout!!.tabCount)
            viewPager = rootView.findViewById(R.id.about_view_pager)
            viewPager!!.adapter = pagerAdapter
            TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->
                tab.text = when (position) {
                    0 -> resources.getText(R.string.about_about_me)
                    1 -> resources.getText(R.string.about_app_information)
                    2 -> resources.getText(R.string.about_third_party_librarys)
                    else -> resources.getText(R.string.about_about_me)
                }
            }.attach()
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

internal class FragmentPageAdapter(fragment: Fragment, private val numberOfTabs: Int) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AboutMeFragment()
            1 -> AboutAppFragment()
            2 -> AboutThirdPartyLibrarysFragment()
            else -> AboutMeFragment()
        }
    }

    override fun getItemCount(): Int {
        return numberOfTabs
    }
}