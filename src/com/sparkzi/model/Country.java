package com.sparkzi.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Country {
    private int id;
    private String value;
    
    public Country() {
    }

    public Country(int id, String value) {
        this.id = id;
        this.value = value;
    }
    
    
    public static List<Country> parseCountry(JSONArray ctyArray){
        List<Country> ctyList = new ArrayList<Country>(); 
                
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        
        try {
            for(int i=0; i<ctyArray.length(); i++){

                JSONObject thisCty = ctyArray.getJSONObject(i);
                if(thisCty != null){
                    String jsonString = thisCty.toString();
                    Country country = gson.fromJson(jsonString, Country.class);
                    ctyList.add(country);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
         
        return ctyList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
