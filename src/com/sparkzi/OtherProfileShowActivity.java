package com.sparkzi;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.sparkzi.fragment.OtherProfileEssentialsfragment;
import com.sparkzi.fragment.OtherProfileQuestionsFragment;
import com.sparkzi.lazylist.ImageLoader;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;
import com.sparkzi.utility.Utility;
import com.viewpagerindicator.TabPageIndicator;

public class OtherProfileShowActivity extends FragmentActivity {

	private static final String TAG = OtherProfileShowActivity.class.getSimpleName();
	private static final String[] CONTENT = new String[] { "Questions", "Essentials" };

	private JsonParser jsonParser;
	private ProgressDialog pDialog;
	private ImageView ivProfilePic;
	private TextView tvUserName, tvAgeGender, tvLivesIn;// tvLookingFor,
	private int favStatus;
	private String uName;
	private boolean favStatusChange = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(this, "2c5ced14");
		setContentView(R.layout.profile);

		jsonParser = new JsonParser();
		pDialog = new ProgressDialog(OtherProfileShowActivity.this);
		initViews();

		FragmentPagerAdapter adapter = new OtherProfileAdapter(getSupportFragmentManager());
		final ViewPager pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);
		final TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
	}

	@SuppressLint("NewApi")
	private void initViews() {
		Intent intent = getIntent();
		favStatus = intent.getIntExtra(Constants.EXTRA_MESSAGE, Constants.FAVORITE_STATUS_STRANGER);
		uName = intent.getStringExtra(Constants.USER_NAME);
		getActionBar().setTitle(uName);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		ivProfilePic = (ImageView) findViewById(R.id.iv_profile_pic);
		tvUserName = (TextView) findViewById(R.id.tv_name);
		tvAgeGender = (TextView) findViewById(R.id.tv_age_gender);
		tvLivesIn = (TextView) findViewById(R.id.tv_lives_in);

		ImageLoader imageLoader = new ImageLoader(OtherProfileShowActivity.this);
		ivProfilePic.setBackgroundResource(android.R.color.transparent);
		ivProfilePic.setImageBitmap(imageLoader.getRoundedPicFromURL(intent.getStringExtra(Constants.PROFILE_PIC_URL),
				ivProfilePic));

		tvUserName.setText(uName);
		// TODO get home town array
		tvLivesIn.setText("lives in " + intent.getStringExtra(Constants.HOME_TOWN) + ", "
				+ Utility.COUNTRY_LIST[intent.getIntExtra("country", 1) - 1]);
		tvAgeGender.setText(intent.getIntExtra("age", 18) + " | "
				+ Utility.Gender[intent.getIntExtra("gender", 1) - 1].substring(0, 1));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (favStatus == Constants.FAVORITE_STATUS_WAITING)
			getMenuInflater().inflate(R.menu.fav_accept, menu);
		else
			getMenuInflater().inflate(R.menu.fav_add, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			return true;
		} else if (id == R.id.action_add_fav || id == R.id.action_accept_fav) {
			Log.d(TAG, "Add to favorite: favStatus=" + favStatus);
			handleAddFavReq();
			return true;
		}
		return false;
	}

	private void handleAddFavReq() {
		// favStatusChange = false;
		switch (favStatus) {
		case Constants.FAVORITE_STATUS_STRANGER:
			new RequestAddFav().execute(uName);
		case Constants.FAVORITE_STATUS_WAITING:
			new RequestApproveFav().execute(uName);
			break;
		case Constants.FAVORITE_STATUS_SENT:
			alert("You've already sent a request!");
			break;
		case Constants.FAVORITE_STATUS_FRIEND:
			alert("You two are already friends!");
			break;

		default:
			break;
		}
	}

	@Override
	public void finish() {
		Log.d(TAG, "finish()");
		Intent data = new Intent();
		data.putExtra(Constants.EXTRA_MESSAGE, favStatusChange);
		setResult(RESULT_OK, data);
		super.finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// finish();
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
				return OtherProfileQuestionsFragment.newInstance(getIntent().getStringExtra(Constants.USER_NAME));
			} else if (position == 1) {
				return OtherProfileEssentialsfragment.newInstance(getIntent().getStringExtra(Constants.USER_NAME));
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

	private class RequestAddFav extends AsyncTask<String, Void, JSONObject> {

		public RequestAddFav() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!pDialog.isShowing()) {
				pDialog.setMessage("A moment...");
				pDialog.setCancelable(false);
				pDialog.setIndeterminate(true);
				pDialog.show();
			}
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			String url = Constants.URL_ROOT + "favs/" + params[0];

			UserCred userCred = ((SparkziApplication) getApplication()).getUserCred();
			String token = userCred.getToken();

			ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_PUT, url, null, null, token);
			if (response.getStatus() == 200) {
				Log.d(">>>><<<<", "success - getting response");
				JSONObject responseObj = response.getjObj();
				return responseObj;
			} else
				return null;
		}

		@Override
		protected void onPostExecute(JSONObject responseObj) {
			super.onPostExecute(responseObj);
			if (responseObj != null) {
				try {
					String status = responseObj.getString("status");
					if (status.equals("OK")) {
						// ((Button) v).setText("Request sent");
						favStatus = Constants.FAVORITE_STATUS_SENT;
						favStatusChange = true;
						alert("Your request is sent successfully.");
					} else {
						alert("Invalid token.");
					}
				} catch (JSONException e) {
					alert("Exception.");
					e.printStackTrace();
				}
			}

			if (pDialog.isShowing())
				pDialog.dismiss();
		}

	}

	private class RequestApproveFav extends AsyncTask<String, Void, JSONObject> {

		// private Favorite favItem;

		public RequestApproveFav() {
			// this.btn = (Button) v;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!pDialog.isShowing()) {
				pDialog.setMessage("A moment...");
				pDialog.setCancelable(false);
				pDialog.setIndeterminate(true);
				pDialog.show();
			}
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			// favItem = (Favorite) params[0];
			String url = Constants.URL_ROOT + "favs/" + params[0];

			UserCred userCred = ((SparkziApplication) (OtherProfileShowActivity.this).getApplication()).getUserCred();
			String token = userCred.getToken();

			ServerResponse response = jsonParser
					.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null, null, token);
			if (response.getStatus() == 200) {
				Log.d(">>>><<<<", "success - getting response");
				JSONObject responseObj = response.getjObj();
				return responseObj;
			} else
				return null;
		}

		@Override
		protected void onPostExecute(JSONObject responseObj) {
			super.onPostExecute(responseObj);
			if (responseObj != null) {
				try {
					String status = responseObj.getString("status");
					if (status.equals("OK")) {
						favStatus = Constants.FAVORITE_STATUS_FRIEND;
						favStatusChange = true;
						alert("Your two are now friends.");
					} else {
						alert("Invalid token.");
					}
				} catch (JSONException e) {
					alert("Exception.");
					e.printStackTrace();
				}
			}

			if (pDialog.isShowing())
				pDialog.dismiss();
		}

	}
}
