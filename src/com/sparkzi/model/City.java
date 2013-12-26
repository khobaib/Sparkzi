package com.sparkzi.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class City {
    private int id;
    private int country_id;
    private String value;
    
    public City() {
        // TODO Auto-generated constructor stub
    }   

    public City(int id, int countryId, String value) {
        this.id = id;
        this.country_id = countryId;
        this.value = value;
    }
    
    public static List<City> parseCity(JSONArray ctyArray){
        List<City> ctyList = new ArrayList<City>(); 
                
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        
        try {
            for(int i=0; i<ctyArray.length(); i++){

                JSONObject thisCty = ctyArray.getJSONObject(i);
                if(thisCty != null){
                    String jsonString = thisCty.toString();
                    City city = gson.fromJson(jsonString, City.class);
                    ctyList.add(city);
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

    public int getCountryId() {
        return country_id;
    }

    public void setCountryId(int countryId) {
        this.country_id = countryId;
    }

    public String getvalue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    

}
