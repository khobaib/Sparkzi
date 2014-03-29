package com.sparkzi;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.facebook.Session;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;

public class LoginActivity extends Activity {


	private static final String TAG = LoginActivity.class.getSimpleName();
	private ProgressDialog pDialog;
	EditText Username, Password;
	CheckBox RememberMe;
	TextView ForgetPassword;
	SparkziApplication appInstance;
	String userName, password;
	String imageUrl;
	Session session1;
	JsonParser jsonParser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(this, "2c5ced14");
		setContentView(R.layout.login);

	//	03-27 19:37:58.095: E/MY KEY HASH:(26626):

		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					"com.sparkzi", 
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));

			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}



		pDialog = new ProgressDialog(LoginActivity.this);
		appInstance = (SparkziApplication) getApplication();
		jsonParser = new JsonParser();

		Username = (EditText) findViewById(R.id.et_user_name);
		Password = (EditText) findViewById(R.id.et_password);   

		ForgetPassword = (TextView) findViewById(R.id.tv_click_here);
		ForgetPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickForgetPassword();

			}
		});

		RememberMe = (CheckBox) findViewById(R.id.cb_remember_me);
		RememberMe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(((CheckBox)v).isChecked()){
					Log.d(TAG, "Remember Me checked");
					//                    appInstance.setRememberMe(true);
				}
				else{
					Log.d(TAG, "Remember Me unchecked");
					//                    appInstance.setRememberMe(false);
				}               
			}
		});
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


	public void onClickLogin(View v){
		userName = Username.getText().toString().trim();
		password = Password.getText().toString().trim();

		if(userName == null || userName.equals(""))
			Toast.makeText(LoginActivity.this, "Please enter user name.", Toast.LENGTH_SHORT).show();
		else if(password == null || password.equals(""))
			Toast.makeText(LoginActivity.this, "Please enter password.", Toast.LENGTH_SHORT).show();
		else{
			new Login().execute();
		}
	}

	public void onClickRegister(View v){
		startActivity(new Intent(LoginActivity.this, GetStartedActivity.class));


	}


	public void onClickForgetPassword(){

		LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
		View textEntryView = inflater.inflate(R.layout.dialog_forget_password, null);
		final AlertDialog alert = new AlertDialog.Builder(LoginActivity.this).create();
		alert.setView(textEntryView, 0, 0, 0, 0);

		final EditText etUserName = (EditText) textEntryView.findViewById(R.id.et_username);

		Button OK = (Button) textEntryView.findViewById(R.id.b_ok);
		OK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String userName = etUserName.getText().toString();
				alert.dismiss(); 
				new SendForgetPassRequest().execute(userName);
			}

		});
		alert.show();
	}


	public class SendForgetPassRequest extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setMessage("Loading...");
			pDialog.setIndeterminate(true);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			Log.d("MARKER", "reached this point");
			String url = Constants.URL_ROOT + "user/" + params[0]+ "/reset";


			ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_PUT, url,
					null, null, null);
			if(response.getStatus() == 200){
				Log.d(">>>><<<<", "success in sending pass-reset request");
				JSONObject responseObj = response.getjObj();
				return responseObj;
			}
			else
				return null;
		}


		@Override
		protected void onPostExecute(JSONObject responseObj) {
			if(pDialog.isShowing())
				pDialog.dismiss();                                
			if(responseObj != null){
				try {
					String status = responseObj.getString("status");
					if(status.equals("OK")){  
						alert("Check your email for new password.");
					}
					else{
						alert("Invalid username/password.");
					}
				} catch (JSONException e) {
					alert("Connecting to server Exception.");
					e.printStackTrace();
				}
			}
			else{
				alert("Connection error, please try again.");
			}
		}
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

			try {
				JSONObject loginObj = new JSONObject();
				loginObj.put("password", password);
				String loginData = loginObj.toString();

				ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_PUT, url,
						null, loginData, null);
				if(response.getStatus() == 200){
					Log.d(">>>><<<<", "success in retrieving response in login");
					JSONObject responseObj = response.getjObj();
					return responseObj;
				}
				else
					return null;
			} catch (JSONException e) {                
				e.printStackTrace();
				return null;
			}
		}


		@Override
		protected void onPostExecute(JSONObject responseObj) {
			if(pDialog.isShowing())
				pDialog.dismiss();                                
			if(responseObj != null){
				try {
					String status = responseObj.getString("status");
					if(status.equals("OK")){
						UserCred userCred = UserCred.parseUserCred(responseObj);
						//                        String token = responseObj.getString("token");
						//                        imageUrl = responseObj.getString("pic");
						//                        Log.d("??????????", "image url = " + imageUrl);
						imageUrl = userCred.getPicUrl();
						Log.d(">>>>", "imageUrl = " + ((imageUrl == null) ? "null" : imageUrl));
						if (!(imageUrl == null) && !imageUrl.equals("null") && !imageUrl.startsWith("http://") &&
								!imageUrl.startsWith("https://")){
							imageUrl = "http://sparkzi.com/api/apinew/" + imageUrl;
							//                        Log.d("??????????", "image url = " + imageUrl);
							userCred.setPicUrl(imageUrl);
						}

						appInstance.setUserCred(userCred);

						//                        appInstance.setAccessToken(token);
						//                        appInstance.setProfileImageUrl(imageUrl);

						//                        runOnUiThread(new Runnable() {
						//                            public void run() {

						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(Username.getWindowToken(), 0);
						imm.hideSoftInputFromWindow(Password.getWindowToken(), 0);

						if(RememberMe.isChecked()){
							appInstance.setRememberMe(true);

							userCred = appInstance.getUserCred();
							userCred.setUsername(userName);
							userCred.setPassword(password);
							appInstance.setUserCred(userCred);
							//                                    appInstance.setCredentials(userName, password);
						}

						Intent i = new Intent();
						//                        imageUrl = null;            // TEST PURPOSE
						if(imageUrl == null){
							//                            i = new Intent(LoginActivity.this, EssentialDetailsActivity.class);

							i = new Intent(LoginActivity.this, UploadPicActivity.class);

							Bundle bundle = new Bundle();
							bundle.putInt(Constants.FROM_ACTIVITY, Constants.PARENT_ACTIVITY_LOGIN);                
							i.putExtras(bundle);
						}
						else{
							i = new Intent(LoginActivity.this, MainActivity.class);
						}
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
						finish();
						//                            }
						//                        });

						//                        alert("Registration Successful.", true);
					}
					else{
						alert("Invalid username/password.");
					}
				} catch (JSONException e) {
					alert("Registration Exception.");
					e.printStackTrace();
				}

			}
			else{
				alert("Registration error, please try again.");
			}

		}
	}


	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(LoginActivity.this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//                Intent i = null;
				//                if(success){
				//                    i = new Intent(LoginActivity.this, HomeActivity.class);
				//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				//                    startActivity(i);
				//                    finish();
				//                }
			}
		});
		bld.create().show();
	}

	//    void alert(String message) {
	//        AlertDialog.Builder bld = new AlertDialog.Builder(LoginActivity.this);
	//        bld.setMessage(message);
	//        bld.setNeutralButton("OK", null);
	//        //      Log.d(TAG, "Showing alert dialog: " + message);
	//        bld.create().show();
	//    }



}
