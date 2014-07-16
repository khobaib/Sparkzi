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
import com.sparkzi.model.Favorite;
import com.sparkzi.utility.Utility;

public class FavoriteAdapter extends ArrayAdapter<Favorite> {

	private Context mContext;
	private LayoutInflater mInflater;
	private ImageLoader imageLoader;

	public FavoriteAdapter(Context context, List<Favorite> qList) {
		super(context, R.layout.row_favorite);
		this.mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader((Activity) mContext);
	}

	private static class ViewHolder {
		ImageView UserImage;
		TextView userName;
		TextView userAgeGender;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.row_favorite, null);

			holder = new ViewHolder();
			holder.UserImage = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.userName = (TextView) convertView.findViewById(R.id.tv_name);
			holder.userAgeGender = (TextView) convertView.findViewById(R.id.tv_age_gender);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Favorite item = getItem(position);

		String imageUrl = item.getPicUrl();
		imageLoader.DisplayImage(imageUrl, holder.UserImage);

		holder.userName.setText(item.getUsername());
		holder.userAgeGender.setText(item.getAge() + " | " + Utility.Gender[item.getGender() - 1].substring(0, 1));

		return convertView;
	}

	public void setData(List<Favorite> favList) {
		clear();
		if (favList != null) {
			for (int i = 0; i < favList.size(); i++) {
				add(favList.get(i));
			}
		}
	}

}
