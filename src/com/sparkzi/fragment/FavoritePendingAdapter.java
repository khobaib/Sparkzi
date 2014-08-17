package com.sparkzi.fragment;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
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
import com.sparkzi.model.Favorite;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;
import com.sparkzi.utility.Utility;

public class FavoritePendingAdapter extends ArrayAdapter<Favorite> {

	private Context mContext;
	private LayoutInflater mInflater;
	private ImageLoader imageLoader;

	private JsonParser jsonParser;
	private ProgressDialog pDialog;

	public FavoritePendingAdapter(Context context, List<Favorite> qList) {
		super(context, R.layout.row_favorite_pending);
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
		Button bAccept;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.row_favorite_pending, null);

			holder = new ViewHolder();
			holder.UserImage = (ImageView) convertView.findViewById(R.id.iv_pic);
			holder.userName = (TextView) convertView.findViewById(R.id.tv_name);
			holder.userAgeGender = (TextView) convertView.findViewById(R.id.tv_age_gender);
			holder.bAccept = (Button) convertView.findViewById(R.id.b_accept);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Favorite item = getItem(position);

		String imageUrl = item.getPicUrl();
		imageLoader.DisplayImage(imageUrl, holder.UserImage);

		holder.userName.setText(item.getUsername());
		holder.userAgeGender.setText(item.getAge() + " | " + Utility.Gender[item.getGender() - 1].substring(0, 1));
		holder.bAccept.setFocusable(false);
		holder.bAccept.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new RequestApproveFav().execute(item);
			}
		});

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

	private class RequestApproveFav extends AsyncTask<Favorite, Void, JSONObject> {

		private Favorite favItem;

		public RequestApproveFav() {
			// this.btn = (Button) v;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!pDialog.isShowing()) {
				pDialog.setMessage("A moment...");
				pDialog.setCancelable(false);
				pDialog.setIndeterminate(true);
				pDialog.show();
			}
		}

		@Override
		protected JSONObject doInBackground(Favorite... params) {
			favItem = (Favorite) params[0];
			String url = Constants.URL_ROOT + "favs/" + favItem.getUsername();

			UserCred userCred = ((SparkziApplication) ((Activity) mContext).getApplication()).getUserCred();
			String token = userCred.getToken();

			ServerResponse response = jsonParser
					.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null, null, token);
			if (response.getStatus() == 200) {
				Log.d(">>>><<<<", "success - getting response");
				JSONObject responseObj = response.getjObj();
				return responseObj;
			} else
				return null;
		}

		@Override
		protected void onPostExecute(JSONObject responseObj) {
			super.onPostExecute(responseObj);
			if (responseObj != null) {
				try {
					String status = responseObj.getString("status");
					if (status.equals("OK")) {
						// TODO re-init. list
						remove(favItem);
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

}
