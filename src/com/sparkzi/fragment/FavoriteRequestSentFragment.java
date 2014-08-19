package com.sparkzi.fragment;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.sparkzi.OtherProfileShowActivity;
import com.sparkzi.adapter.FavoriteAdapter;
import com.sparkzi.model.Favorite;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;

public class FavoriteRequestSentFragment extends ListFragment {

	private static final String TAG = FavoriteRequestSentFragment.class.getSimpleName();
	private static final int PROFILE_SHOW_REQ = 101;
	private Activity activity;
	JsonParser jsonParser;

	private FavoriteAdapter favAdapter;

	private String token;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		activity = getActivity();
		jsonParser = new JsonParser();

		UserCred userCred = ((SparkziApplication) activity.getApplication()).getUserCred();
		token = userCred.getToken();
		ListView lv = getListView();
		lv.setDivider(activity.getResources().getDrawable(com.sparkzi.R.color.app_theme));
		lv.setDividerHeight(3);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.i(TAG, "OnItemClick");
				showUserProfile((Favorite) parent.getItemAtPosition(position));
			}
		});

		favAdapter = new FavoriteAdapter(activity, null);
		setListAdapter(favAdapter);
		setListShown(false);

		new GetFavoriteInfo().execute();
	}

	private void showUserProfile(Favorite user) {
		Log.d(TAG, "Showing user profile for: " + user.getUsername());
		Intent intent = new Intent(getActivity(), OtherProfileShowActivity.class);
		intent.putExtra(Constants.USER_NAME, user.getUsername());
		intent.putExtra(Constants.PROFILE_PIC_URL, user.getPicUrl());
		intent.putExtra(Constants.HOME_TOWN, user.getHometown());
		intent.putExtra(Constants.AGE, user.getAge());
		intent.putExtra(Constants.GENDER, user.getGender());
		intent.putExtra(Constants.COUNTRY, user.getCountry());
		intent.putExtra(Constants.EXTRA_MESSAGE, Constants.FAVORITE_STATUS_SENT);
		startActivityForResult(intent, PROFILE_SHOW_REQ);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActiivtyResult");
		if (resultCode == Activity.RESULT_OK && requestCode == PROFILE_SHOW_REQ) {
			boolean favStatusChange = data.getBooleanExtra(Constants.EXTRA_MESSAGE, false);
			Log.d(TAG, "onActiivtyResult : favStatusChange=" + favStatusChange);
			if (favStatusChange) {
				new GetFavoriteInfo().execute();
			}
		}
	}

	private class GetFavoriteInfo extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {
			String url = Constants.URL_ROOT + "favs/all/1";
			ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_GET, url, null, null, token);
			if (response.getStatus() == 200) {
				Log.d(">>>><<<<", "success in retrieving favorite info");
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
						JSONObject favObj = responseObj.getJSONObject("favs");
						JSONArray addedFavArray = favObj.getJSONArray("pendingByMe");

						final List<Favorite> fList = Favorite.parseFavorite(addedFavArray);
						favAdapter.setData(fList);
						if (isResumed()) {
							setListShown(true);
						}
					} else {
						alert("Invalid token.");
					}
				} catch (JSONException e) {
					alert("Exception.");
					e.printStackTrace();
				}
			}
		}

	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(activity);
		bld.setMessage(message);
		bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				activity.finish();
			}
		});
		bld.create().show();
	}

}
