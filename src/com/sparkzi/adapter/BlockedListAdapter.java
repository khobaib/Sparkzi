package com.sparkzi.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sparkzi.R;
import com.sparkzi.lazylist.ImageLoader;
import com.sparkzi.model.BlockedUser;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;
import com.sparkzi.utility.Utility;

public class BlockedListAdapter extends ArrayAdapter<BlockedUser> {

	private Context mContext;
	private LayoutInflater mInflater;
	private ImageLoader imageLoader;
	private JsonParser jsonParser;
	private ProgressDialog pDialog;

	public BlockedListAdapter(Context context, List<BlockedUser> qList) {
		super(context, R.layout.row_blocked_user);
		this.mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader((Activity) mContext);
		jsonParser = new JsonParser();
		pDialog = new ProgressDialog(mContext);
	}

	private static class ViewHolder {
		ImageView UserImage;
		TextView userName;
		TextView userAgeGender;
		Button bUnblock;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.row_blocked_user, null);

			holder = new ViewHolder();
			holder.UserImage = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.userName = (TextView) convertView.findViewById(R.id.tv_name);
			holder.userAgeGender = (TextView) convertView.findViewById(R.id.tv_age_gender);
			holder.bUnblock = (Button) convertView.findViewById(R.id.b_unblock);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.bUnblock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BlockedUser item = getItem(position);
				int uid = item.getUid();
				new RequestUnblockUser(position).execute(uid);
			}
		});

		BlockedUser item = getItem(position);

		String imageUrl = item.getPicUrl();
		imageLoader.DisplayImage(imageUrl, holder.UserImage);

		holder.userName.setText(item.getUsername());
		holder.userAgeGender.setText(item.getAge() + " | " + Utility.Gender[item.getGender() - 1].substring(0, 1));

		return convertView;
	}

	public void setData(List<BlockedUser> bUserList) {
		clear();
		if (bUserList != null) {
			for (int i = 0; i < bUserList.size(); i++) {
				add(bUserList.get(i));
			}
		}
	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(mContext);
		bld.setMessage(message);
		bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		bld.create().show();
	}

	private class RequestUnblockUser extends AsyncTask<Integer, Void, JSONObject> {

		int position;

		public RequestUnblockUser(int position) {
			this.position = position;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!pDialog.isShowing()) {
				pDialog.setMessage("Please wait...");
				pDialog.setCancelable(false);
				pDialog.setIndeterminate(true);
				pDialog.show();
			}
		}

		@Override
		protected JSONObject doInBackground(Integer... params) {
			String url = Constants.URL_ROOT + "block/" + params[0];

			UserCred userCred = ((SparkziApplication) ((Activity) mContext).getApplication()).getUserCred();
			String token = userCred.getToken();

			ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_DELETE, url, null, null,
					token);
			if (response.getStatus() == 200) {
				Log.d(">>>><<<<", "success - getting response");
				JSONObject responseObj = response.getjObj();
				return responseObj;
			} else
				return null;
		}

		@Override
		protected void onPostExecute(JSONObject responseObj) {
			// TODO Auto-generated method stub
			super.onPostExecute(responseObj);
			if (responseObj != null) {
				try {
					String status = responseObj.getString("status");
					if (status.equals("OK")) {
						String desc = responseObj.getString("description");
						if (desc.equals("User unblocked")) {
							remove(getItem(position));
							notifyDataSetChanged();
						}
					} else {
						alert("Invalid token.");
					}
				} catch (JSONException e) {
					alert("Exception.");
					e.printStackTrace();
				}
			}

			if (pDialog.isShowing())
				pDialog.dismiss();
		}

	}

}
