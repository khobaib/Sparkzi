package com.sparkzi.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sparkzi.R;
import com.sparkzi.UploadPicActivity;
import com.sparkzi.lazylist.ImageLoader;
import com.sparkzi.model.UserCred;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;
import com.sparkzi.utility.Utility;
import com.viewpagerindicator.TabPageIndicator;

public class ProfileFragment extends Fragment {

	private static final String TAG = ProfileFragment.class.getSimpleName();
	private static final String[] CONTENT = new String[] { "Questions", "Essentials" };

	private static final String[] looking_for = new String[] { "woman", "man" };
	private Activity activity;

	ImageView ivProfilePic;
	TextView UserName, LookingFor, AgeGender, LivesIn;
	ViewPager pager;
	TabPageIndicator indicator;

	SparkziApplication appInstance;
	ImageLoader imageLoader;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.profile, null);

		ivProfilePic = (ImageView) view.findViewById(R.id.iv_profile_pic);
		ivProfilePic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (activity != null) {
					Intent i = new Intent(activity, UploadPicActivity.class);

					Bundle bundle = new Bundle();
					bundle.putInt(Constants.FROM_ACTIVITY, Constants.PARENT_ACTIVITY_PROFILE);
					i.putExtras(bundle);

					startActivity(i);
				}

			}
		});

		UserName = (TextView) view.findViewById(R.id.tv_name);
		LookingFor = (TextView) view.findViewById(R.id.tv_look_for);
		AgeGender = (TextView) view.findViewById(R.id.tv_age_gender);
		LivesIn = (TextView) view.findViewById(R.id.tv_lives_in);

		pager = (ViewPager) view.findViewById(R.id.pager);
		indicator = (TabPageIndicator) view.findViewById(R.id.indicator);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		activity = getActivity();
		if (activity != null) {
			appInstance = (SparkziApplication) activity.getApplication();
			imageLoader = new ImageLoader(activity);
			UserCred userCred = appInstance.getUserCred();

			UserName.setText(userCred.getUsername());
			LookingFor.setText("looking for " + looking_for[userCred.getGender() - 1]);
			LivesIn.setText("lives in " + userCred.getHometown() + ", "
					+ Utility.COUNTRY_LIST[userCred.getCountry() - 1]);
			AgeGender.setText(userCred.getAge() + " | " + Utility.Gender[userCred.getGender() - 1].substring(0, 1));

			FragmentPagerAdapter adapter = new ProfileAdapter(getChildFragmentManager());

			pager.setAdapter(adapter);
			indicator.setViewPager(pager);

		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "ProfileFragment onResume");
		UserCred userCred = appInstance.getUserCred();
		String imageUrl = userCred.getPicUrl();
		Log.d(TAG, "imageUrl = " + imageUrl);
		// imageLoader.DisplayImage(imageUrl, ivProfilePic);
		ivProfilePic.setBackgroundResource(android.R.color.transparent);
		ivProfilePic.setImageBitmap(imageLoader.getRoundedPicFromURL(imageUrl, ivProfilePic));
	}

	class ProfileAdapter extends FragmentPagerAdapter {
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
