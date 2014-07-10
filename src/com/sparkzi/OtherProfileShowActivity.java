package com.sparkzi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.sparkzi.fragment.OtherProfileEssentialsfragment;
import com.sparkzi.fragment.OtherProfileQuestionsFragment;
import com.sparkzi.lazylist.ImageLoader;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.Utility;
import com.viewpagerindicator.TabPageIndicator;

public class OtherProfileShowActivity extends FragmentActivity {

	@SuppressWarnings("unused")
	private static final String TAG = OtherProfileShowActivity.class
			.getSimpleName();
	private static final String[] CONTENT = new String[] { "Questions",
			"Essentials" };

	// private static final String[] looking_for = new String[] { "woman", "man"
	// };

	private ImageView ivProfilePic;
	private TextView tvUserName, tvAgeGender, tvLivesIn;// tvLookingFor,

	// private static SparkziApplication appInstance;

	// private static ImageLoader imageLoader;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(this, "2c5ced14");
		setContentView(R.layout.profile);

		Intent intent = getIntent();
		String uName = intent.getStringExtra(Constants.USER_NAME);
		getActionBar().setTitle(uName);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// new GetEssentialInfo().execute();

		ivProfilePic = (ImageView) findViewById(R.id.iv_profile_pic);
		tvUserName = (TextView) findViewById(R.id.tv_name);
		// tvLookingFor = (TextView) findViewById(R.id.tv_look_for);
		tvAgeGender = (TextView) findViewById(R.id.tv_age_gender);
		tvLivesIn = (TextView) findViewById(R.id.tv_lives_in);

		// appInstance = (SparkziApplication) getApplication();
		// UserCred userCred = appInstance.getUserCred();
		// String imageUrl = userCred.getPicUrl();

		ImageLoader imageLoader = new ImageLoader(OtherProfileShowActivity.this);
		// imageLoader.DisplayImage(imageUrl, ivProfilePic);
		ivProfilePic.setBackgroundResource(android.R.color.transparent);
		ivProfilePic
				.setImageBitmap(imageLoader.getRoundedPicFromURL(
						intent.getStringExtra(Constants.PROFILE_PIC_URL),
						ivProfilePic));

		tvUserName.setText(uName);
		// tvLookingFor
		// .setText("looking for " + looking_for[intent.getIntExtra("", 0)]);
		tvLivesIn.setText("lives in "
				+ intent.getStringExtra(Constants.HOME_TOWN) + ", " // TODO get
																	// home town
																	// array
				+ Utility.COUNTRY_LIST[intent.getIntExtra("country", 1) - 1]);
		tvAgeGender.setText(intent.getIntExtra("age", 18)
				+ " | "
				+ Utility.Gender[intent.getIntExtra("gender", 1) - 1]
						.substring(0, 1));

		FragmentPagerAdapter adapter = new OtherProfileAdapter( // TODO change
																// for
				// others
				getSupportFragmentManager());

		final ViewPager pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		final TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==android.R.id.home){
			finish();
			return true;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();

		// UserCred userCred = appInstance.getUserCred();
		// String imageUrl = userCred.getPicUrl();
		// ImageLoader imageLoader = new
		// ImageLoader(OtherProfileShowActivity.this);
		// // imageLoader.DisplayImage(imageUrl, ivProfilePic);
		// ivProfilePic.setBackgroundResource(android.R.color.transparent);
		// ivProfilePic.setImageBitmap(imageLoader.getRoundedPicFromURL(imageUrl,
		// ivProfilePic));
	}

	// private class GetEssentialInfo extends AsyncTask<Void, Void, JSONObject>
	// {
	//
	// @Override
	// protected JSONObject doInBackground(Void... params) {
	// String url = Constants.URL_ROOT + "user/"
	// + getIntent().getStringExtra(Constants.USER_NAME); // TODO
	// UserCred userCred = ((SparkziApplication) getApplication())
	// .getUserCred();
	// String token = userCred.getToken();
	// ServerResponse response = new JsonParser().retrieveServerData(
	// Constants.REQUEST_TYPE_GET, url, null, null, token);
	// if (response.getStatus() == 200) {
	// Log.d(">>>><<<<", "success in retrieving user info");
	// JSONObject responseObj = response.getjObj();
	// return responseObj;
	// } else
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(JSONObject responseObj) {
	// super.onPostExecute(responseObj);
	// if (responseObj != null) {
	// try {
	// String status = responseObj.getString("status");
	// if (status.equals("OK")) {
	// JSONObject userObj = responseObj.getJSONObject("user");
	// // final List<Essential> eList =
	// // Essential.parseEssentialList(userObj);
	// Log.d("T_UserObj", userObj.toString());
	// } else {
	// alert("Invalid token.");
	// }
	// } catch (JSONException e) {
	// alert("Exception.");
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// }

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		bld.create().show();
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

	class OtherProfileAdapter extends FragmentPagerAdapter {
		public OtherProfileAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return OtherProfileQuestionsFragment.newInstance(getIntent()
						.getStringExtra(Constants.USER_NAME));
			} else if (position == 1) {
				return OtherProfileEssentialsfragment.newInstance(getIntent()
						.getStringExtra(Constants.USER_NAME));
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
