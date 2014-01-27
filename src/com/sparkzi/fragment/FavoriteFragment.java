package com.sparkzi.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sparkzi.R;
import com.viewpagerindicator.TabPageIndicator;

public class FavoriteFragment extends Fragment {
    
    private static final String   TAG        = FavoriteFragment.class.getSimpleName();
    private static final String[] CONTENT    = new String[] { "Favorites", "Pending", "Request sent" };
    
    private Activity activity;

    private ViewPager mViewPager;
    private TabPageIndicator indicator;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_favorite, container, false);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        indicator = (TabPageIndicator) view.findViewById(R.id.indicator);


        return view;
    }
    
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        if(activity != null){
            final FragmentPagerAdapter adapter = new FavoriteListAdapter(getChildFragmentManager());
            mViewPager.setAdapter(adapter);
            mViewPager.setOffscreenPageLimit(2);
            
            indicator.setViewPager(mViewPager);
        }
    }
    
    
    class FavoriteListAdapter extends FragmentPagerAdapter{
        public FavoriteListAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new FavoriteAddedFragment();
            } else if (position == 1) {
                return new FavoritePendingFragment();
            } else
                return new FavoriteRequestSentFragment();
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
