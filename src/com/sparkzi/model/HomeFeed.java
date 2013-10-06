package com.sparkzi.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HomeFeed {
    
    private String username;
    private String timestamp;
    private int template;
    private String feed;
    private String elaborated;
    private int age;
    private int gender;
    private String picUrl;
    
    public HomeFeed() {
        // TODO Auto-generated constructor stub
    }

    public HomeFeed(String username, String timestamp, int template, String feed, String elaborated, int age,
            int gender, String picUrl) {
        this.username = username;
        this.timestamp = timestamp;
        this.template = template;
        this.feed = feed;
        this.elaborated = elaborated;
        this.age = age;
        this.gender = gender;
        this.picUrl = picUrl;
    }
    
    
    public static List<HomeFeed> parseFeeds(JSONArray feedArray){
        List<HomeFeed> feedList = new ArrayList<HomeFeed>();
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();

        try {
            for(int i=0; i<feedArray.length(); i++){

                JSONObject thisFeed = feedArray.getJSONObject(i);
                if(thisFeed != null){
                    String jsonString = thisFeed.toString();
                    HomeFeed feed = gson.fromJson(jsonString, HomeFeed.class);
                    feedList.add(feed);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return feedList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getTemplate() {
        return template;
    }

    public void setTemplate(int template) {
        this.template = template;
    }

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    public String getElaborated() {
        return elaborated;
    }

    public void setElaborated(String elaborated) {
        this.elaborated = elaborated;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }   

}
