package com.sparkzi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TextView;

import com.sparkzi.fragment.DummyFragment;
import com.sparkzi.fragment.ProfileEssentialsfragment;
import com.sparkzi.fragment.ProfileQuestionsFragment;
import com.sparkzi.lazylist.ImageLoader;
import com.sparkzi.model.UserCred;
import com.sparkzi.utility.SparkziApplication;
import com.sparkzi.utility.Utility;
import com.viewpagerindicator.TabPageIndicator;

public class ProfileActivity extends FragmentActivity {
    
    private static final String   TAG        = ProfileActivity.class.getSimpleName();
    private static final String[] CONTENT    = new String[] { "Questions", "Essentials" };
    
    private static final String[] looking_for = new String[] {"woman", "man"};
    
    ImageView ProfilePic;
    TextView UserName, LookingFor, AgeGender, LivesIn;
    
    SparkziApplication appInstance;
    ImageLoader imageLoader;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        
        ProfilePic = (ImageView) findViewById(R.id.iv_profile_pic);
        UserName = (TextView) findViewById(R.id.tv_name);
        LookingFor = (TextView) findViewById(R.id.tv_look_for);
        AgeGender = (TextView) findViewById(R.id.tv_age_gender);
        LivesIn = (TextView) findViewById(R.id.tv_lives_in);
        
        appInstance = (SparkziApplication) getApplication();
        UserCred userCred = appInstance.getUserCred();
        String imageUrl = userCred.getPic();
        
        ImageLoader imageLoader = new ImageLoader(ProfileActivity.this);
        imageLoader.DisplayImage(imageUrl, ProfilePic);
        
        UserName.setText(userCred.getUsername());
        LookingFor.setText("looking for " + looking_for[userCred.getGender()]);
        LivesIn.setText("lives in " + userCred.getHometown() + ", " + Utility.COUNTRY_LIST[userCred.getCountry()]);
        AgeGender.setText(userCred.getAge() + " | " + Utility.Gender[userCred.getGender()].substring(0, 1));
        
        FragmentPagerAdapter adapter = new ProfileAdapter(getSupportFragmentManager());

        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        final TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }
    
    
    class ProfileAdapter extends FragmentPagerAdapter{
        public ProfileAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return ProfileQuestionsFragment.newInstance();
            } else if (position == 1) {
                return ProfileEssentialsfragment.newInstance();
            } else
                return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length];
        }

        @Override
        public int getCount() {
            return CONTENT.length;
        }
    }
    
    

}
