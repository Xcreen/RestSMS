package net.xcreen.restsms;

import android.net.Uri;
import android.os.Bundle;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class AboutMeFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about_me, container, false);

        Button donateBtn = rootView.findViewById(R.id.about_me_donate_btn);
        donateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(getResources().getColor(R.color.colorDarkBlack, null));
                CustomTabsIntent customTabsIntent = builder.build();

                //Open Paypal Website
                customTabsIntent.launchUrl(getContext(), Uri.parse("https://www.paypal.me/xcreen"));
            }
        });

        Button githubBtn = rootView.findViewById(R.id.about_me_github_btn);
        githubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(getResources().getColor(R.color.colorDarkBlack, null));
                CustomTabsIntent customTabsIntent = builder.build();

                //Open Github Website
                customTabsIntent.launchUrl(getContext(), Uri.parse("https://github.com/Xcreen"));
            }
        });

        return rootView;
    }
}
