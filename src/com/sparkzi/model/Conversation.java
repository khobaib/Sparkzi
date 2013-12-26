package com.sparkzi.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Conversation {
    
    private int mid;
    private int cid;
    private int ufrom;
    private int uto;
    private String message;
    private String isread;
    private String timestamp;
    private String username;
    private int gender;
    private int age;
    private String picUrl;
    
    public Conversation() {
        // TODO Auto-generated constructor stub
    }

    public Conversation(int mid, int cid, int ufrom, int uto, String message, String isread, String timestamp,
            String username, int gender, int age, String picUrl) {
        this.mid = mid;
        this.cid = cid;
        this.ufrom = ufrom;
        this.uto = uto;
        this.message = message;
        this.isread = isread;
        this.timestamp = timestamp;
        this.username = username;
        this.gender = gender;
        this.age = age;
        this.picUrl = picUrl;
    }
    
    public static List<Conversation> parseConversationList(JSONArray convArray){
        List<Conversation> convList = new ArrayList<Conversation>();
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        
        try {
            for(int i=0; i<convArray.length(); i++){

                JSONObject thisConv = convArray.getJSONObject(i);
                if(thisConv != null){
                    String jsonString = thisConv.toString();
                    Conversation conv = gson.fromJson(jsonString, Conversation.class);
                    
                    String imageUrl = conv.getPicUrl();
                    if (imageUrl != null && !imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")){
                        imageUrl = "http://sparkzi.com/api/apinew/" + imageUrl;
                    }
                    conv.setPicUrl(imageUrl);
                    
                    convList.add(conv);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return convList;       
    }
    

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public int getUfrom() {
        return ufrom;
    }

    public void setUfrom(int ufrom) {
        this.ufrom = ufrom;
    }

    public int getUto() {
        return uto;
    }

    public void setUto(int uto) {
        this.uto = uto;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIsread() {
        return isread;
    }

    public void setIsread(String isread) {
        this.isread = isread;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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
