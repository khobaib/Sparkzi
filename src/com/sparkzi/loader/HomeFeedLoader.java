package com.sparkzi.loader;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.sparkzi.MainActivity;
import com.sparkzi.model.HomeFeed;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;

public class HomeFeedLoader extends AsyncTaskLoader<List<HomeFeed>> {

	private static final String TAG = HomeFeedLoader.class.getSimpleName();

	private JsonParser jsonParser;

	// private List<HomeFeed> homeFeeds;
	private String token;
	@SuppressWarnings("unused")
	private String username;

	private List<HomeFeed> mFeeds; // holder to keep previous feeds while
									// copying new ones

	public HomeFeedLoader(Context context, String token) {
		super(context);
		jsonParser = new JsonParser();
		// this.token = token;

		SparkziApplication appInstance = (SparkziApplication) ((MainActivity) context).getApplication();
		UserCred userCred = appInstance.getUserCred();
		this.username = userCred.getUsername();
		this.token = userCred.getToken();
	}

	@SuppressWarnings("unused")
	@Override
	public List<HomeFeed> loadInBackground() {

		String rootUrl = Constants.URL_ROOT + "feeds";
		Log.d(TAG, "token - " + token);

		ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_GET, rootUrl, null, null, token);

		if (response.getStatus() == Constants.RESPONSE_STATUS_CODE_SUCCESS) {
			JSONObject responseObj = response.getjObj();
			try {
				String status = responseObj.getString("status");
				String desc = responseObj.getString("description");
				JSONArray feedArray = responseObj.getJSONArray("feeds");

				List<HomeFeed> homeFeeds = HomeFeed.parseFeeds(feedArray);
				return homeFeeds;
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		return null;
	}

	@Override
	public void deliverResult(List<HomeFeed> feeds) {
		if (isReset()) {
			// The Loader has been reset; ignore the result and invalidate the
			// data.
			// This can happen when the Loader is reset while an asynchronous
			// query
			// is working in the background. That is, when the background thread
			// finishes its work and attempts to deliver the results to the
			// client,
			// it will see here that the Loader has been reset and discard any
			// resources associated with the new data as necessary.
			if (feeds != null) {
				releaseResources(feeds);
				return;
			}
		}

		// Hold a reference to the old data so it doesn't get garbage collected.
		// We must protect it until the new data has been delivered.
		List<HomeFeed> oldHomeFeeds = mFeeds;
		mFeeds = feeds;

		if (isStarted()) {
			// If the Loader is in a started state, have the superclass deliver
			// the
			// results to the client.
			super.deliverResult(feeds);
		}

		// Invalidate the old data as we don't need it any more.
		if (oldHomeFeeds != null && oldHomeFeeds != feeds) {
			releaseResources(oldHomeFeeds);
		}
	}

	@Override
	protected void onStartLoading() {

		if (mFeeds != null) {
			// Deliver any previously loaded data immediately.
			deliverResult(mFeeds);
		}

		if (mFeeds == null) {
			// If the current data is null... then we should make it non-null!
			// :)
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading() {
		// The Loader has been put in a stopped state, so we should attempt to
		// cancel the current load (if there is one).
		cancelLoad();
	}

	@Override
	protected void onReset() {
		// Ensure the loader is stopped.
		onStopLoading();

		if (mFeeds != null) {
			releaseResources(mFeeds);
			mFeeds = null;
		}
	}

	@Override
	public void onCanceled(List<HomeFeed> feeds) {
		// Attempt to cancel the current asynchronous load.
		super.onCanceled(feeds);

		// The load has been canceled, so we should release the resources
		// associated with 'mApps'.
		releaseResources(feeds);
	}

	private void releaseResources(List<HomeFeed> feeds) {
		// For a simple List, there is nothing to do. For something like a
		// Cursor,
		// we would close it in this method. All resources associated with the
		// Loader should be released here.
	}

}
