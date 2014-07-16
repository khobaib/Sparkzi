package com.sparkzi;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;
import com.sparkzi.utility.Utility;

public class SplashActivity extends Activity {

	SparkziApplication appInstance;
	private ProgressDialog pDialog;
	JsonParser jsonParser;
	Button btn_create_account;
	String userName, password;
	String imageUrl;
	TextView txt_signin, txt_go;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(this, "2c5ced14");
		setContentView(R.layout.splash_new);

		Log.e(">>>>>>>", "I am in splash");

		btn_create_account = (Button) findViewById(R.id.btn_create_account);
		txt_signin = (TextView) findViewById(R.id.txt_signin);
		txt_go = (TextView) findViewById(R.id.txt_go);
		String str = "<html><body><u>Sign In</u></body></html>";
		txt_go.setText(Html.fromHtml(str));
		txt_go.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				gologinn();
			}
		});
		txt_signin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				gologinn();
			}
		});

		appInstance = (SparkziApplication) getApplication();
		jsonParser = new JsonParser();
		pDialog = new ProgressDialog(SplashActivity.this);

		Boolean isFirstTime = appInstance.isFirstTime();
		if (isFirstTime) {
			Utility.createDirectory();
			appInstance.setFirstTime(false);
		}

		Boolean rememberMeFlag = appInstance.isRememberMe();
		Log.d("login remember me", "" + rememberMeFlag);
		if (rememberMeFlag) {
			UserCred userCred = appInstance.getUserCred();
			userName = userCred.getUsername();
			password = userCred.getPassword();
			Log.d(">>>>>>>>>>", "pass = " + password);
			new Login().execute();
		}

		else {
			Thread timer = new Thread() {
				public void run() {
					try {
						sleep(2500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {

						// startActivity(new Intent(SplashActivity.this,
						// LoginActivity.class));
						// finish();
					}
				}
			};
			timer.start();
		}
	}

	public void gogetstartedactivity(View view) {
		Intent intent = new Intent(SplashActivity.this, GetStartedActivity.class);
		startActivity(intent);
	}

	public void gologinn() {
		Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
		startActivity(intent);
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

	private class Login extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setMessage("Signing in, Please wait...");
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			Log.d("MARKER", "reached this point");
			String url = Constants.URL_ROOT + "session/" + userName;

			// List<NameValuePair> urlParam = new ArrayList<NameValuePair>();
			// urlParam.add(new BasicNameValuePair("user", userName));

			try {
				JSONObject loginObj = new JSONObject();
				loginObj.put("password", password);
				String loginData = loginObj.toString();

				ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_PUT, url, null,
						loginData, null);
				if (response.getStatus() == 200) {
					Log.d(">>>><<<<", "success in retrieving response in login");
					JSONObject responseObj = response.getjObj();
					return responseObj;
					// JSONObject responsObj = response.getjObj();
					// String login = responsObj.getString("login");
					// if(login.equals("success")){
					// String token = responsObj.getString("token");
					// String imageUrl = responsObj.getString("image_url");
					// Long userId = responsObj.getLong("user_id");
					// appInstance.setAccessToken(token);
					// appInstance.setProfileImageUrl(imageUrl);
					// return true;
					// }
					// else{
					// return false;
					// }
				} else
					return null;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(JSONObject responseObj) {
			if (pDialog.isShowing())
				pDialog.dismiss();
			if (responseObj != null) {
				try {
					String status = responseObj.getString("status");
					if (status.equals("OK")) {
						UserCred userCred = UserCred.parseUserCred(responseObj);
						// String token = responseObj.getString("token");
						// imageUrl = responseObj.getString("pic");
						// Log.d("??????????", "image url = " + imageUrl);
						imageUrl = userCred.getPicUrl();
						if (!(imageUrl == null) && !imageUrl.equals("null") && !imageUrl.startsWith("http://")
								&& !imageUrl.startsWith("https://")) {
							imageUrl = "http://sparkzi.com/api/apinew/" + imageUrl;
							// Log.d("??????????", "image url = " + imageUrl);
							userCred.setPicUrl(imageUrl);
						}
						userCred.setPassword(password);

						appInstance.setUserCred(userCred);

						// appInstance.setAccessToken(token);
						// appInstance.setProfileImageUrl(imageUrl);

						// runOnUiThread(new Runnable() {
						// public void run() {
						Intent i = new Intent();
						if (imageUrl == null) {
							i = new Intent(SplashActivity.this, UploadPicActivity.class);

							Bundle bundle = new Bundle();
							bundle.putInt(Constants.FROM_ACTIVITY, Constants.PARENT_ACTIVITY_LOGIN);
							i.putExtras(bundle);
						} else {
							i = new Intent(SplashActivity.this, MainActivity.class);
						}
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
						finish();
						// }
						// });
					} else {
						alert("Invalid username/password.");
						appInstance.setRememberMe(false);
					}
				} catch (JSONException e) {
					alert("Registration Exception.");
					appInstance.setRememberMe(false);
				}

			} else {
				alert("Registration error, please try again.");
				appInstance.setRememberMe(false);
			}

		}
	}

	// void alert(String message) {
	// AlertDialog.Builder bld = new AlertDialog.Builder(SplashActivity.this);
	// bld.setMessage(message);
	// bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// startActivity(new Intent(SplashActivity.this, LoginActivity.class));
	// finish();
	//
	// }
	// });
	// bld.create().show();
	// }

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(SplashActivity.this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent i = null;
				i = new Intent(SplashActivity.this, LoginActivity.class);

				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
			}
		});
		bld.create().show();
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.splash, menu);
	// return true;
	// }

}
