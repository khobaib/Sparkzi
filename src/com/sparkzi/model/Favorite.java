package com.sparkzi.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Favorite {
    private int uid;
    private String username;
    private int gender;
    private String hometown;
    private int country;
    private String pic;
    private int age;
    
    public Favorite() {
    }

    public Favorite(int uid, String username, int gender, String hometown, int country, String pic, int age) {
        this.uid = uid;
        this.username = username;
        this.gender = gender;
        this.hometown = hometown;
        this.country = country;
        this.pic = pic;
        this.age = age;
    }
    
    
    public static List<Favorite> parseFavorite(JSONArray favArray){
        List<Favorite> favList = new ArrayList<Favorite>(); 
                
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        
        try {
            for(int i=0; i<favArray.length(); i++){

                JSONObject thisFav = favArray.getJSONObject(i);
                if(thisFav != null){
                    String jsonString = thisFav.toString();
                    Favorite fav = gson.fromJson(jsonString, Favorite.class);
                    
                    String imageUrl = fav.getPic();
                    if (!(imageUrl == null) && !imageUrl.equals("null") && !imageUrl.startsWith("http://") &&
                            !imageUrl.startsWith("https://")){
                        imageUrl = "http://sparkzi.com/api/apinew/" + imageUrl;
                    }
                    fav.setPic(imageUrl);
                    
                    favList.add(fav);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
         
        return favList;
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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
