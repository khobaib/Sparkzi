package com.sparkzi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.sparkzi.fragment.ProfileEssentialsfragment;
import com.sparkzi.fragment.ProfileQuestionsFragment;
import com.sparkzi.lazylist.ImageLoader;
import com.sparkzi.model.UserCred;
import com.sparkzi.utility.SparkziApplication;
import com.sparkzi.utility.Utility;
import com.viewpagerindicator.TabPageIndicator;

public class ProfileActivity extends FragmentActivity {

	@SuppressWarnings("unused")
	private static final String TAG = ProfileActivity.class.getSimpleName();
	private static final String[] CONTENT = new String[] { "Questions",
			"Essentials" };

	private static final String[] looking_for = new String[] { "woman", "man" };

	private ImageView ivProfilePic;
	private TextView tvUserName, tvLookingFor, tvAgeGender, tvLivesIn;

	private static SparkziApplication appInstance;

	// private static ImageLoader imageLoader;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(this, "2c5ced14");
		setContentView(R.layout.profile);

		ivProfilePic = (ImageView) findViewById(R.id.iv_profile_pic);
		tvUserName = (TextView) findViewById(R.id.tv_name);
		tvLookingFor = (TextView) findViewById(R.id.tv_look_for);
		tvAgeGender = (TextView) findViewById(R.id.tv_age_gender);
		tvLivesIn = (TextView) findViewById(R.id.tv_lives_in);

		appInstance = (SparkziApplication) getApplication();
		UserCred userCred = appInstance.getUserCred();
		String imageUrl = userCred.getPicUrl();

		ImageLoader imageLoader = new ImageLoader(ProfileActivity.this);
		// imageLoader.DisplayImage(imageUrl, ivProfilePic);
		ivProfilePic.setBackgroundResource(android.R.color.transparent);
		ivProfilePic.setImageBitmap(imageLoader.getRoundedPicFromURL(imageUrl,
				ivProfilePic));

		tvUserName.setText(userCred.getUsername());
		tvLookingFor
				.setText("looking for " + looking_for[userCred.getGender()]);
		tvLivesIn.setText("lives in " + userCred.getHometown() + ", "
				+ Utility.COUNTRY_LIST[userCred.getCountry()]);
		tvAgeGender.setText(userCred.getAge() + " | "
				+ Utility.Gender[userCred.getGender()].substring(0, 1));

		FragmentPagerAdapter adapter = new ProfileAdapter(
				getSupportFragmentManager());

		final ViewPager pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		final TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
	}

	@Override
	protected void onResume() {
		super.onResume();

		UserCred userCred = appInstance.getUserCred();
		String imageUrl = userCred.getPicUrl();
		ImageLoader imageLoader = new ImageLoader(ProfileActivity.this);
		// imageLoader.DisplayImage(imageUrl, ivProfilePic);
		ivProfilePic.setBackgroundResource(android.R.color.transparent);
		ivProfilePic.setImageBitmap(imageLoader.getRoundedPicFromURL(imageUrl,
				ivProfilePic));
	}

	@Override
	protected void onStart() {
		super.onStart();
		BugSenseHandler.startSession(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		BugSenseHandler.closeSession(this);
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
