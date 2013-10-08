package com.sparkzi.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.sparkzi.model.City;
import com.sparkzi.model.Question;

public class Utility {
    
    public static final String[] MONTH_NAME = {
        "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
        "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
    };
    
    public static final String[] WHO_I_AM = {"Man looking for a woman", "Woman looking for a man"};
    
    public static final String[] COUNTRY_LIST = {"Australia", "Canada", "India", "UK", "US"};
    
    public static final City[] CITY_LIST = {
        new City(0, "Adelaide"), new City(0, "Brisbane"), new City(0, "Canberra"), new City(0, "Goal Coast"),
        new City(1, "Calgari"), new City(1, "Cambridge"), new City(1, "Edmonton"), new City(1, "Halifax"),
        new City(2, "Ahmedabad"), new City(2, "Bangalore"), new City(2, "Bhopal"), new City(2, "Chandrigarh"),
        new City(3, "Edinburg"), new City(3, "Glassgow"), new City(3, "Greater London Urban"), new City(3, "Belfast"),
        new City(4, "Alabama"),new City(4, "Alaska"), new City(4, "Austin"), new City(4, "Boston")
    };
    
    public static final String[] QUESTION_TEXTS = {
        "Two truths and a lie about me are ...",
        "You come into possesion of a comically oversized bag of money. How do you spend it?",
        "I'd be excited to meet someone who...",
        "On a typical day off, I like to...",
        "If i had to appoint a panel of three people, dead or alive, to make every decision for me, I'd choose",
        "If i could my sixteen-year-old-self any piece of advice, it would be...",
        "Describe your best friend",
        "If a friend were visiting from out of town, three things we'd have to do would be...",
        "The fictional character I'd love to go on date with is... because...",
    };
    
    
    
    public static final String[] ESSENTIAL_TEXTS = {
        "Education", "Ethnicity", "Diet", "Drinks",
        "Smokes", "Religion", "Kids", "Politics",
        "Sign", "Profession", "Hometown", "Languages"
    };
    
//    public static final Question[] questionList = {
//        new Question(1, "Two truths and a lie about me are ...", null),
//        new Question(2, "You come into possesion of a comically oversized bag of money. How do you spend it?", null),
//        new Question(3, "I'd be excited to meet someone who...", null),
//        new Question(4, "On a typical day off, I like to...", null),
//        new Question(5, "If i had to appoint a panel of three people, dead or alive, to make every decision for me, I'd choose", null),
//        new Question(6, "If i could my sixteen-year-old-self any piece of advice, it would be...", null),
//        new Question(7, "Describe your best friend", null),
//        new Question(8, "If a friend were visiting from out of town, three things we'd have to do would be...", null),
//        new Question(9, "The fictional character I'd love to go on date with is... because...", null)
//    };
    
    public static final String[] AGE_RANGE = {
        "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38"
    };
    
    public static final String[] Gender = {"Male", "Female"};
    
    public static final String[] LAST_NIGHT_VERB = {
        "dummy", "saw", "listened", "watched", "ate at", "went to", "attended", "read"
    };
    
    public static final String[] education_spinner_options = {
        "School of life", "High School", "College", "Grad School", "PhD"  
    };
    
    public static final String[] ethnicity_spinner_options = {
        "Indian", "Asian", "White", "Black", "Not Indian, but kinda like an Indian"  
    };
    
    public static final String[] diet_spinner_options = {
        "I'll eat anything", "Carnivorous", "Vegetarian", "Vegan"
    };
    
    public static final String[] drinks_spinner_options = {
        "Never", "Rarely", "Sometimes", "Regularly", "Like a rockstar"
    };
    
    public static final String[] smokes_spinner_options = {
        "Never", "When drinking", "Sometimes", "Daily but with a tinge of guilt", "Proudly"
    };
    
    public static final String[] religion_spinner_options = {
        "Agnostic", "Atheist", "Buddhist", "Christian", "Hindu", "Jain", "Muslim", "Sikh", "Spiritual, not religious"
    };
    
    public static final String[] kids_spinner_options = {
        "Have kids at home", "Have kids elsewhere", "Want kids someday", "Not interested in kids"
    };
    
    public static final String[] politics_spinner_options = {
        "Workers of the world, unite!", "Liberal", "Conservative"
    };
    
    public static final String[] sign_spinner_options = {
       "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo", "Libra", "Scorpio", "Sagittarius",
       "Capricorn", "Aquarius", "Pisces", "Don't know, don't care"
    };
    
    
    public static List<String> getCityList(int countryId){
        List<String> cityList = new ArrayList<String>();
        
        for(City city : CITY_LIST){
            if(city.getCountryId() == countryId)
                cityList.add(city.getCityName());
        }
        return cityList;
    }



    public static boolean createDirectory() {
        if (!SdIsPresent())
            return false;

        File directory = Constants.APP_DIRECTORY;
        if (!directory.exists())
            directory.mkdir();
        return true;
    }

    /** Returns whether an SD card is present and writable **/
    public static boolean SdIsPresent() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
    
    /*
     * dateFromDb -> yyyy-mm-dd
     * we want to show in the format dd (month_name) year
     */
    public static String parseDate(String dateFromDb){
        String year = dateFromDb.substring(0, 4);
        int month = Integer.parseInt(dateFromDb.substring(5, 7));
        String day = dateFromDb.substring(8, 10);
        Log.d(">>>>>>>>", "year = " + year + " month = " + month + " day = " + day);
        String dateToShow = dateFromDb;
        if(month > 0 && month <=12)
            dateToShow = day + " " + MONTH_NAME[month-1] + " " + year;
        return dateToShow;
    }
    

    public static boolean hasInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null){
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null){
                for (int i = 0; i < info.length; i++){
                    if (info[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(Constants.DISPLAY_MESSAGE_ACTION);
        intent.putExtra(Constants.EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

}
