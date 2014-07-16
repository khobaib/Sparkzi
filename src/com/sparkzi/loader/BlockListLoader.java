package com.sparkzi.loader;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.sparkzi.model.BlockedUser;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;

public class BlockListLoader extends AsyncTaskLoader<List<BlockedUser>> {

	@SuppressWarnings("unused")
	private static final String TAG = BlockListLoader.class.getSimpleName();

	private JsonParser jsonParser;

	private String token;

	private List<BlockedUser> mBlockedUsers; // holder to keep previous
												// blockedUser while copying new
												// ones

	public BlockListLoader(Context context, String token) {
		super(context);
		jsonParser = new JsonParser();

		this.token = token;
	}

	@SuppressWarnings("unused")
	@Override
	public List<BlockedUser> loadInBackground() {

		String url = Constants.URL_ROOT + "block";

		ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_GET, url, null, null, token);

		if (response.getStatus() == Constants.RESPONSE_STATUS_CODE_SUCCESS) {
			JSONObject responseObj = response.getjObj();
			try {
				String status = responseObj.getString("status");
				String desc = responseObj.getString("description");
				JSONArray bUserArray = responseObj.getJSONArray("result");

				List<BlockedUser> bUserList = BlockedUser.parseBlockedUserList(bUserArray);
				return bUserList;

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		return null;
	}

	@Override
	public void deliverResult(List<BlockedUser> blockedUserList) {
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
			if (blockedUserList != null) {
				releaseResources(blockedUserList);
				return;
			}
		}

		// Hold a reference to the old data so it doesn't get garbage collected.
		// We must protect it until the new data has been delivered.
		List<BlockedUser> oldBlockedUserList = mBlockedUsers;
		mBlockedUsers = blockedUserList;

		if (isStarted()) {
			// If the Loader is in a started state, have the superclass deliver
			// the
			// results to the client.
			super.deliverResult(blockedUserList);
		}

		// Invalidate the old data as we don't need it any more.
		if (oldBlockedUserList != null && oldBlockedUserList != blockedUserList) {
			releaseResources(oldBlockedUserList);
		}
	}

	@Override
	protected void onStartLoading() {

		if (mBlockedUsers != null) {
			// Deliver any previously loaded data immediately.
			deliverResult(mBlockedUsers);
		}

		if (mBlockedUsers == null) {
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

		if (mBlockedUsers != null) {
			releaseResources(mBlockedUsers);
			mBlockedUsers = null;
		}
	}

	@Override
	public void onCanceled(List<BlockedUser> bUserList) {
		// Attempt to cancel the current asynchronous load.
		super.onCanceled(bUserList);

		// The load has been canceled, so we should release the resources
		// associated with 'mApps'.
		releaseResources(bUserList);
	}

	private void releaseResources(List<BlockedUser> bUserList) {
		// For a simple List, there is nothing to do. For something like a
		// Cursor,
		// we would close it in this method. All resources associated with the
		// Loader should be released here.
	}

}
