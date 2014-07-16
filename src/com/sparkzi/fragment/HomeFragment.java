package com.sparkzi.fragment;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sparkzi.R;
import com.sparkzi.adapter.HomeFeedAdapter;
import com.sparkzi.lazylist.ImageLoader;
import com.sparkzi.loader.HomeFeedLoader;
import com.sparkzi.model.HomeFeed;
import com.sparkzi.model.UserCred;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;
import com.sparkzi.utility.Utility;

public class HomeFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<HomeFeed>> {

	private static final int LOADER_ID = 1;

	private Activity activity;
	String token;

	private HomeFeedAdapter homeFeedAdapter;

	// @Override
	// public View onCreateView(LayoutInflater inflater, ViewGroup container,
	// Bundle savedInstanceState) {
	// View view = inflater.inflate(R.layout.home_fragment, null);
	// return view;
	// }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		activity = getActivity();

		UserCred userCred = ((SparkziApplication) activity.getApplication()).getUserCred();
		token = userCred.getToken();

		ListView lv = getListView();
		lv.setDivider(activity.getResources().getDrawable(com.sparkzi.R.color.app_theme));
		lv.setDividerHeight(3);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HomeFeed feed = (HomeFeed) parent.getItemAtPosition(position);
				showFeedDetailsDialog(feed);
			}
		});

		homeFeedAdapter = new HomeFeedAdapter(activity, null);
		setEmptyText("No feeds");
		setListAdapter(homeFeedAdapter);
		setListShown(false);

		getLoaderManager().initLoader(LOADER_ID, null, this);
	}

	@Override
	public Loader<List<HomeFeed>> onCreateLoader(int id, Bundle args) {
		return new HomeFeedLoader(activity, token);
	}

	@Override
	public void onLoadFinished(Loader<List<HomeFeed>> loader, List<HomeFeed> data) {
		homeFeedAdapter.setData(data);

		if (isResumed()) {
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}

	}

	private void showFeedDetailsDialog(HomeFeed feed) {
		final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		dialog.setContentView(R.layout.feed_details);

		TextView tvName = (TextView) dialog.findViewById(R.id.tv_name);
		tvName.setText(feed.getUsername());
		TextView tvTimestamp = (TextView) dialog.findViewById(R.id.tv_timestamp);
		tvTimestamp.setText(Utility.getFormattedTime(feed.getTimestamp()));
		TextView tvUserAgeGender = (TextView) dialog.findViewById(R.id.tv_age_gender);
		tvUserAgeGender.setText(feed.getAge() + " | " + Utility.Gender[feed.getGender() - 1].substring(0, 1));
		TextView tvTemplateVerb = (TextView) dialog.findViewById(R.id.tv_verb);
		if (feed.getTemplate() == Constants.TEMPLATE_ID_SOMETHING_ELSE)
			tvTemplateVerb.setText("");
		else
			tvTemplateVerb.setText(Utility.LAST_NIGHT_VERB[feed.getTemplate()]);
		TextView tvFeedTitle = (TextView) dialog.findViewById(R.id.tv_feed_title);
		tvFeedTitle.setText(feed.getFeed());
		TextView tvFeedDetails = (TextView) dialog.findViewById(R.id.tv_feed_desc);
		tvFeedDetails.setText(feed.getElaborated());

		ImageView ivUserImage = (ImageView) dialog.findViewById(R.id.iv_pic);
		ImageLoader imageLoader = new ImageLoader(activity);
		ivUserImage.setBackgroundResource(android.R.color.transparent);
		ivUserImage.setImageBitmap(imageLoader.getRoundedPicFromURL(feed.getPicUrl(), ivUserImage));

		// Center-focus the dialog
		Window window = dialog.getWindow();
		window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		window.setGravity(Gravity.CENTER);

		// The below code is EXTRA - to dim the parent view by 70% :D
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = 0.7f;
		lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		dialog.getWindow().setAttributes(lp);
		dialog.show();
	}

	@Override
	public void onLoaderReset(Loader<List<HomeFeed>> loader) {
		homeFeedAdapter.setData(null);

	}

}
