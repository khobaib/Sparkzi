package com.sparkzi.model;

public class City {
    private int countryId;
    private String cityName;
    
    public City() {
        // TODO Auto-generated constructor stub
    }   

    public City(int countryId, String cityName) {
        this.countryId = countryId;
        this.cityName = cityName;
    }


    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    
    

}
