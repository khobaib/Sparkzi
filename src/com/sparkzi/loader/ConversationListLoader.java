package com.sparkzi.loader;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.sparkzi.model.Conversation;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;

public class ConversationListLoader extends AsyncTaskLoader<List<Conversation>> {

	private static final String TAG = ConversationListLoader.class.getSimpleName();

	private JsonParser jsonParser;

	private String token;
	private String username;
	private int listType;

	private List<Conversation> mConvs; // holder to keep previous conv while
										// copying new ones

	public ConversationListLoader(Context context, String token, int listType, String userName) {
		super(context);
		jsonParser = new JsonParser();
		this.username = userName;
		this.token = token;
	}

	@SuppressWarnings("unused")
	@Override
	public List<Conversation> loadInBackground() {

		String rootUrl = Constants.URL_ROOT;
		if (listType == Constants.RETRIEVE_ALL_CONVERSATIONS)
			rootUrl = rootUrl + "messages";
		else
			rootUrl = rootUrl + "messages/" + username;
		Log.d(TAG, "token - " + token);

		ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_GET, rootUrl, null, null, token);

		if (response.getStatus() == Constants.RESPONSE_STATUS_CODE_SUCCESS) {
			JSONObject responseObj = response.getjObj();
			try {
				String status = responseObj.getString("status");
				String desc = responseObj.getString("description");
				JSONArray msgArray = responseObj.getJSONArray("messages");

				List<Conversation> convList = Conversation.parseConversationList(msgArray);
				return convList;

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		return null;
	}

	@Override
	public void deliverResult(List<Conversation> convs) {
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
			if (convs != null) {
				releaseResources(convs);
				return;
			}
		}

		// Hold a reference to the old data so it doesn't get garbage collected.
		// We must protect it until the new data has been delivered.
		List<Conversation> oldConvs = mConvs;
		mConvs = convs;

		if (isStarted()) {
			// If the Loader is in a started state, have the superclass deliver
			// the
			// results to the client.
			super.deliverResult(convs);
		}

		// Invalidate the old data as we don't need it any more.
		if (oldConvs != null && oldConvs != convs) {
			releaseResources(oldConvs);
		}
	}

	@Override
	protected void onStartLoading() {

		if (mConvs != null) {
			// Deliver any previously loaded data immediately.
			deliverResult(mConvs);
		}

		if (mConvs == null) {
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

		if (mConvs != null) {
			releaseResources(mConvs);
			mConvs = null;
		}
	}

	@Override
	public void onCanceled(List<Conversation> convs) {
		// Attempt to cancel the current asynchronous load.
		super.onCanceled(convs);

		// The load has been canceled, so we should release the resources
		// associated with 'mApps'.
		releaseResources(convs);
	}

	private void releaseResources(List<Conversation> convs) {
		// For a simple List, there is nothing to do. For something like a
		// Cursor,
		// we would close it in this method. All resources associated with the
		// Loader should be released here.
	}

}
