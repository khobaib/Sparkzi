package com.sparkzi.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BlockedUser {
    
    private int uid;
    private String username;
    private int gender;
    private String hometown;
    private int country;
    private int age;
    private String picUrl;
    
    public BlockedUser() {
    }

    
    public BlockedUser(int uid, String username, int gender, String hometown, int country, int age, String picUrl) {
        this.uid = uid;
        this.username = username;
        this.gender = gender;
        this.hometown = hometown;
        this.country = country;
        this.age = age;
        this.picUrl = picUrl;
    }
    
    
    public static List<BlockedUser> parseBlockedUserList(JSONArray blockedUserArray){
        List<BlockedUser> bUserList = new ArrayList<BlockedUser>(); 
                
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        
        try {
            for(int i=0; i<blockedUserArray.length(); i++){

                JSONObject thisUser = blockedUserArray.getJSONObject(i);
                if(thisUser != null){
                    String jsonString = thisUser.toString();
                    BlockedUser bUser = gson.fromJson(jsonString, BlockedUser.class);
                    
                    String imageUrl = bUser.getPicUrl();
                    if (imageUrl != null && !imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")){
                        imageUrl = "http://sparkzi.com/api/apinew/" + imageUrl;
                    }
                    bUser.setPicUrl(imageUrl);
                    
                    bUserList.add(bUser);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
         
        return bUserList;
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
