package com.sparkzi.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sparkzi.R;
import com.sparkzi.model.DrawerItem;

public class LeftDrawerAdapter extends BaseAdapter {

	private Context context;
	private List<DrawerItem> drawerItems;

	public LeftDrawerAdapter(List<DrawerItem> leftDrawerItems, Context context) {
		this.drawerItems = leftDrawerItems;
		this.context = context;
	}

	// check if list is empty
	public boolean checkListNull() {
		return ((drawerItems == null) ? true : false);
	}

	@Override
	public int getCount() {
		return drawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return drawerItems.get(position).name;
	}

	@Override
	public long getItemId(int position) {
		return drawerItems.get(position).name.hashCode();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;

		LeftDrawerHolder holder;

		if (row == null) {

			LayoutInflater inflater = LayoutInflater.from(context);
			row = inflater.inflate(R.layout.left_drawer_list_item, null);
			holder = new LeftDrawerHolder(row);
			row.setTag(holder);
		} else {

			holder = (LeftDrawerHolder) row.getTag();
		}

		holder.populateFrom(drawerItems, position);

		return row;
	}

	private class LeftDrawerHolder {

		public ImageView selectionIcon;
		private TextView selectionInfo;

		public LeftDrawerHolder(View row) {
			selectionIcon = (ImageView) row.findViewById(R.id.left_drawer_icon);
			selectionInfo = (TextView) row.findViewById(R.id.tv_option_name);
		}

		public void populateFrom(final List<DrawerItem> leftDrawerItemsArray, final int position) {

			selectionInfo.setText(leftDrawerItemsArray.get(position).name);
			selectionIcon.setImageDrawable(leftDrawerItemsArray.get(position).getImage());

		}
	}

}
