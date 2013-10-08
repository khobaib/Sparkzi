package com.sparkzi.utility;

import java.io.File;

import android.os.Environment;

public class Constants {
    
    
    public static final int REQUEST_TYPE_GET = 1;
    public static final int REQUEST_TYPE_POST = 2;
    public static final int REQUEST_TYPE_PUT = 3;
    public static final int REQUEST_TYPE_DELETE = 4;
    
    public static final String FIRST_TIME = "first_time_run";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String USER_NAME = "user_name";
    public static final String PASSWORD = "password";
    public static final String REMEMBER_ME = "remember_me";
    public static final String PROFILE_PIC_URL = "profile_pic_url";
    public static final String UID = "uid";
    public static final String GENDER = "gender";
    public static final String HOME_TOWN = "home_town";
    public static final String COUNTRY = "country";
    public static final String AGE = "age";
    
    public static final int ESSENTIAL_EDUCATION_INDEX = 0;
    public static final int ESSENTIAL_ETHNICITY_INDEX = 1;
    public static final int ESSENTIAL_DIET_INDEX = 2;
    public static final int ESSENTIAL_DRINKS_INDEX = 3;
    public static final int ESSENTIAL_SMOKES_INDEX = 4;
    public static final int ESSENTIAL_RELIGION_INDEX = 5;
    public static final int ESSENTIAL_KIDS_INDEX = 6;
    public static final int ESSENTIAL_POLITICS_INDEX = 7;
    public static final int ESSENTIAL_SIGN_INDEX = 8;
    public static final int ESSENTIAL_PROFESSION_INDEX = 9;
    public static final int ESSENTIAL_HOMETOWN_INDEX = 10;
    public static final int ESSENTIAL_LANGUAGES_INDEX = 11;
    
    
    public static final String URL_ROOT = "http://sparkzi.com/api/apinew/";

    
    public static final int RESPONSE_STATUS_CODE_SUCCESS = 200;
    
//    public static final String FROM_ACTIVITY = "from_activity";
//    public static final int PARENT_ACTIVITY_NEW_POLLS = 101;
//    public static final int PARENT_ACTIVITY_ALL_POLLS = 102;
//    public static final int PARENT_ACTIVITY_MY_POLLS = 103;
    
    /**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */
//    public static final String SERVER_URL = "http://apps.priyo.com/jonopriyo/gcm/register.php";

    /**
     * Google API project id registered to use GCM.
     */
//    public static final String SENDER_ID = "528409180961";
 
    /**
     * Intent used to display a message in the screen.
     */
    public static final String DISPLAY_MESSAGE_ACTION ="com.sparkzi.DISPLAY_MESSAGE";
 
    public static final String EXTRA_MESSAGE = "message";

    
    public static final File APP_DIRECTORY =
            new File(Environment.getExternalStorageDirectory(),"sparkzi");
    

}
