package com.sparkzi.utility;

import com.sparkzi.model.UserCred;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SparkziApplication extends Application {

	private static Context context;
	protected SharedPreferences User;

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		User = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	public static Context getAppContext() {
		return context;
	}

	public void setFirstTime(Boolean firstTimeFlag) {
		Editor editor = User.edit();
		editor.putBoolean(Constants.FIRST_TIME, firstTimeFlag);
		editor.commit();
	}

	// public void setCredentials(String username, String password){
	// Editor editor = User.edit();
	// editor.putString(Constants.USER_NAME, username);
	// editor.putString(Constants.PASSWORD, password);
	// editor.commit();
	// }

	public void setRememberMe(Boolean rememberMeFlag) {
		Editor editor = User.edit();
		editor.putBoolean(Constants.REMEMBER_ME, rememberMeFlag);
		editor.commit();
	}

	// public void setAccessToken(String token){
	// Editor editor = User.edit();
	// editor.putString(Constants.ACCESS_TOKEN, token);
	// editor.commit();
	// }

	// public void setProfileImageUrl(String imageUrl){
	// Editor editor = User.edit();
	// editor.putString(Constants.PROFILE_PIC_URL, imageUrl);
	// editor.commit();
	// }

	public void setUserCred(UserCred userCred) {
		Editor editor = User.edit();
		editor.putString(Constants.ACCESS_TOKEN, userCred.getToken());
		editor.putInt(Constants.UID, userCred.getUid());
		editor.putString(Constants.USER_NAME, userCred.getUsername());
		editor.putString(Constants.PASSWORD, userCred.getPassword());
		editor.putInt(Constants.GENDER, userCred.getGender());
		editor.putString(Constants.HOME_TOWN, userCred.getHometown());
		editor.putInt(Constants.COUNTRY, userCred.getCountry());
		editor.putInt(Constants.AGE, userCred.getAge());
		editor.putString(Constants.PROFILE_PIC_URL, userCred.getPicUrl());
		editor.commit();
	}

	public UserCred getUserCred() {
		String token = User.getString(Constants.ACCESS_TOKEN, null);
		int uid = User.getInt(Constants.UID, -1);
		String userName = User.getString(Constants.USER_NAME, null);
		String password = User.getString(Constants.PASSWORD, null);
		int gender = User.getInt(Constants.GENDER, -1);
		String hometown = User.getString(Constants.HOME_TOWN, null);
		int country = User.getInt(Constants.COUNTRY, -1);
		int age = User.getInt(Constants.AGE, -1);
		String pic = User.getString(Constants.PROFILE_PIC_URL, null);
		UserCred userCred = new UserCred(token, uid, userName, password, gender, hometown, country, age, pic);
		return userCred;
	}

	public boolean isFirstTime() {
		Boolean firstTimeFlag = User.getBoolean(Constants.FIRST_TIME, true);
		return firstTimeFlag;
	}

	// public String getUserName(){
	// String userName = User.getString(Constants.USER_NAME, null);
	// return userName;
	// }
	//
	//
	// public String getPassword(){
	// String pass = User.getString(Constants.PASSWORD, null);
	// return pass;
	// }

	public boolean isRememberMe() {
		Boolean rememberMeFlag = User.getBoolean(Constants.REMEMBER_ME, false);
		return rememberMeFlag;
	}

	// public String getAccessToken(){
	// String token = User.getString(Constants.ACCESS_TOKEN, null);
	// return token;
	// }
	//
	// public String getProfileImageUrl(){
	// String imageUrl = User.getString(Constants.PROFILE_PIC_URL, null);
	// return imageUrl;
	// }
}
