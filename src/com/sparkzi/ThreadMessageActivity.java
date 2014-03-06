package com.sparkzi;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.sparkzi.adapter.ThreadMessageAdapter;
import com.sparkzi.loader.ConversationListLoader;
import com.sparkzi.model.Conversation;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;

public class ThreadMessageActivity extends FragmentActivity implements
		LoaderManager.LoaderCallbacks<List<Conversation>> {

	private static final int LOADER_ID = 1;

	ListView ThreadMessageList;

	ThreadMessageAdapter threadMessageAdapter;
	List<Conversation> messageList;

	JsonParser jsonParser;
	ProgressDialog pDialog;

	// SparkziApplication appInstance;
	int myUId;
	String myImageUrl;
	String token;
	String threadUserName; // the other user name of this conversation
	int threadUserId; // the other user id

	EditText MessageBody;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(this, "2c5ced14");
		setContentView(R.layout.thread_message);

		UserCred userCred = ((SparkziApplication) getApplication())
				.getUserCred();
		myUId = userCred.getUid();
		myImageUrl = userCred.getPicUrl();
		token = userCred.getToken();

		threadUserName = getIntent().getExtras().getString("user_name");
		threadUserId = getIntent().getExtras().getInt("user_id");

		jsonParser = new JsonParser();

		pDialog = new ProgressDialog(ThreadMessageActivity.this);
		pDialog.setMessage("Loading...");

		ThreadMessageList = (ListView) findViewById(R.id.lv_thread_messages);

		MessageBody = (EditText) findViewById(R.id.et_msg_body);

		threadMessageAdapter = new ThreadMessageAdapter(
				ThreadMessageActivity.this, new ArrayList<Conversation>(),
				myUId, myImageUrl);
		ThreadMessageList.setAdapter(threadMessageAdapter);

		getSupportLoaderManager().initLoader(LOADER_ID, null,
				ThreadMessageActivity.this);
		if (!pDialog.isShowing())
			pDialog.show();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		BugSenseHandler.startSession(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		BugSenseHandler.closeSession(this);
	}

	public void onClickReply(View v) {
		String msgBody = MessageBody.getText().toString().trim();

		if (msgBody == null || msgBody.equals(""))
			Toast.makeText(ThreadMessageActivity.this, "Message is empty.",
					Toast.LENGTH_SHORT).show();
		else {
			// send message
		}
	}

	public void onClickDelete(View v) {

	}

	public void onClickBlock(View v) {
		new BlockUser().execute();
	}

	@Override
	public Loader<List<Conversation>> onCreateLoader(int id, Bundle args) {
		return new ConversationListLoader(ThreadMessageActivity.this, token,
				Constants.RETRIEVE_SPECIFIC_USER_CONVERSATIONS, threadUserName);
	}

	@Override
	public void onLoadFinished(Loader<List<Conversation>> loader,
			List<Conversation> data) {
		threadMessageAdapter.setData(data);
		threadMessageAdapter.notifyDataSetChanged();
		if (pDialog.isShowing())
			pDialog.dismiss();

	}

	@Override
	public void onLoaderReset(Loader<List<Conversation>> loader) {
		threadMessageAdapter.setData(null);
		threadMessageAdapter.notifyDataSetChanged();
	}

	private class BlockUser extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!pDialog.isShowing())
				pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			String url = Constants.URL_ROOT + "block/" + threadUserId;
			ServerResponse response = jsonParser.retrieveServerData(
					Constants.REQUEST_TYPE_PUT, url, null, null, token);
			if (response.getStatus() == 200) {
				Log.d(">>>><<<<", "success in blocking user = "
						+ threadUserName);
				JSONObject responseObj = response.getjObj();
				return responseObj;
			} else
				return null;
		}

		@Override
		protected void onPostExecute(JSONObject responseObj) {
			super.onPostExecute(responseObj);
			if (pDialog.isShowing())
				pDialog.dismiss();
			if (responseObj != null) {
				try {
					String status = responseObj.getString("status");
					if (status.equals("OK")) {
						alert(threadUserName
								+ " is blocked. You can unblock from settings.");
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

}
