package com.sparkzi.model;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UserCred {
	private String token;
	private int uid;
	private String username;
	private String password;
	private int gender;
	private String hometown;
	private int country;
	private int age;
	private String picUrl;

	public UserCred() {
	}

	public UserCred(String token, int uid, String username, String password,
			int gender, String hometown, int country, int age, String picUrl) {
		this.token = token;
		this.uid = uid;
		this.username = username;
		this.password = password;
		this.gender = gender;
		this.hometown = hometown;
		this.country = country;
		this.age = age;
		this.picUrl = picUrl;
	}

	public static UserCred parseUserCred(JSONObject userObj) {
		UserCred userCred = new UserCred();

		GsonBuilder gsonb = new GsonBuilder();
		Gson gson = gsonb.create();
		if (userObj != null) {
			String jsonString = userObj.toString();
			userCred = gson.fromJson(jsonString, UserCred.class);
		}
		return userCred;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getHometown() {
		return hometown;
	}

	public void setHometown(String hometown) {
		this.hometown = hometown;
	}

	public int getCountry() {
		return country;
	}

	public void setCountry(int country) {
		this.country = country;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
}
