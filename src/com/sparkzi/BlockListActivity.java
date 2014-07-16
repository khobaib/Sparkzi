package com.sparkzi;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.ListView;

import com.bugsense.trace.BugSenseHandler;
import com.sparkzi.adapter.BlockedListAdapter;
import com.sparkzi.loader.BlockListLoader;
import com.sparkzi.model.BlockedUser;
import com.sparkzi.model.UserCred;
import com.sparkzi.utility.SparkziApplication;

public class BlockListActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<List<BlockedUser>> {

	private static final int LOADER_ID = 1;
	private BlockedListAdapter blockedListAdapter;

	String token;

	ListView lvBlockedList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(this, "2c5ced14");
		setContentView(R.layout.activity_block_list);

		UserCred userCred = ((SparkziApplication) getApplication()).getUserCred();
		token = userCred.getToken();

		lvBlockedList = (ListView) findViewById(R.id.lv_block_list);

		blockedListAdapter = new BlockedListAdapter(this, null);
		lvBlockedList.setAdapter(blockedListAdapter);

		getSupportLoaderManager().initLoader(LOADER_ID, null, this);
	}

	@Override
	public Loader<List<BlockedUser>> onCreateLoader(int id, Bundle args) {
		return new BlockListLoader(this, token);
	}

	@Override
	public void onLoadFinished(Loader<List<BlockedUser>> loader, List<BlockedUser> data) {
		blockedListAdapter.setData(data);

	}

	@Override
	public void onLoaderReset(Loader<List<BlockedUser>> loader) {
		blockedListAdapter.setData(null);
	}

}
