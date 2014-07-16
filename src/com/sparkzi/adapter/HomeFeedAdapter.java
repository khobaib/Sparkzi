package com.sparkzi.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sparkzi.R;
import com.sparkzi.lazylist.ImageLoader;
import com.sparkzi.model.HomeFeed;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.Utility;

public class HomeFeedAdapter extends ArrayAdapter<HomeFeed> {

	private Context mContext;
	private LayoutInflater mInflater;
	private ImageLoader imageLoader;

	public HomeFeedAdapter(Context context, List<HomeFeed> feeds) {
		super(context, R.layout.row_homefeed);
		this.mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader((Activity) mContext);
	}

	private static class ViewHolder {
		ImageView UserImage;
		TextView userName;
		TextView timestamp;
		TextView userAgeGender;
		TextView templateVerb;
		TextView feedTitle;
		TextView feedDetails;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.row_homefeed, null);

			holder = new ViewHolder();
			holder.UserImage = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.userName = (TextView) convertView.findViewById(R.id.tv_name);
			holder.timestamp = (TextView) convertView.findViewById(R.id.tv_timestamp);
			holder.userAgeGender = (TextView) convertView.findViewById(R.id.tv_age_gender);
			holder.templateVerb = (TextView) convertView.findViewById(R.id.tv_verb);
			holder.feedTitle = (TextView) convertView.findViewById(R.id.tv_feed_title);
			holder.feedDetails = (TextView) convertView.findViewById(R.id.tv_feed_desc);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		HomeFeed item = getItem(position);

		String imageUrl = item.getPicUrl();
		imageLoader.DisplayImage(imageUrl, holder.UserImage);

		holder.userName.setText(item.getUsername());
		holder.timestamp.setText(Utility.getFormattedTime(item.getTimestamp()));
		holder.userAgeGender.setText(item.getAge() + " | " + Utility.Gender[item.getGender() - 1].substring(0, 1));

		if (item.getTemplate() == Constants.TEMPLATE_ID_SOMETHING_ELSE)
			holder.templateVerb.setText("");
		else
			holder.templateVerb.setText(Utility.LAST_NIGHT_VERB[item.getTemplate()]);

		holder.feedTitle.setText(item.getFeed());
		holder.feedDetails.setText(item.getElaborated());

		// imageLoader.DisplayImage(item.getThumbImage(), holder.crustImage);

		return convertView;
	}

	public void setData(List<HomeFeed> feedList) {
		clear();
		if (feedList != null) {
			for (int i = 0; i < feedList.size(); i++) {
				add(feedList.get(i));
			}
		}
	}

}
