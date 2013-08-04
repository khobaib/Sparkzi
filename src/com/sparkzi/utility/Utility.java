package com.sparkzi.utility;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

public class Utility {
    
    public static final String[] MONTH_NAME = {
        "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
        "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
    };
    
    public static final String[] WHO_I_AM = {"Man looking for a woman", "Woman looking for a man"};
    
    public static final String[] COUNTRY_LIST = {"Australia", "Canada", "India", "UK", "US"};
    
    public static final String[] AGE_RANGE = {
        "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38"
    };



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
