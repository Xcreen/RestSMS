package net.xcreen.restsms;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

public class AboutFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AppBarLayout mainAppBarLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        //Set Tabs
        tabLayout = new TabLayout(getActivity());
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getText(R.string.about_about_me)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getText(R.string.about_app_information)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getText(R.string.about_third_party_librarys)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorDarkBlack, null));
        tabLayout.setTabTextColors(getResources().getColor(R.color.colorWhite, null), getResources().getColor(R.color.colorLightBlue, null));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        //Add TabLayout to the AppBarLayout
        mainAppBarLayout = getActivity().findViewById(R.id.appbar_layout);
        mainAppBarLayout.addView(tabLayout, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //Set Page-Adapter to ViewPager
        FragmentPageAdapter pagerAdapter = new FragmentPageAdapter(getFragmentManager(), tabLayout.getTabCount());
        viewPager = rootView.findViewById(R.id.about_view_pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Remove Tab-Layout
        mainAppBarLayout.removeView(tabLayout);
    }
}

class FragmentPageAdapter extends FragmentStatePagerAdapter {
    private int numberOfTabs;

    public FragmentPageAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.numberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AboutMeFragment();
            case 1:
                return new AboutMeFragment();
            case 2:
                return new AboutThirdPartyLibrarysFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
