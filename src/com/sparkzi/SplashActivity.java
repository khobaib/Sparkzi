package com.sparkzi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.sparkzi.model.ServerResponse;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;
import com.sparkzi.utility.Utility;

public class SplashActivity extends Activity {

    SparkziApplication appInstance;
    private ProgressDialog pDialog;
    JsonParser jsonParser;

    String userName, password;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_new);

        appInstance = (SparkziApplication) getApplication();
        jsonParser = new JsonParser();
        pDialog = new ProgressDialog(SplashActivity.this);

        Boolean isFirstTime = appInstance.isFirstTime();
        if(isFirstTime){
            Utility.createDirectory();
            appInstance.setFirstTime(false);
        }

        Boolean rememberMeFlag = appInstance.isRememberMe();
        Log.d("login remember me", "" + rememberMeFlag);
        if(rememberMeFlag){
            UserCred userCred = appInstance.getUserCred();
            userName = userCred.getUsername();
            password = userCred.getPassword();
            Log.d(">>>>>>>>>>", "pass = " + password);
            new Login().execute();
        } 

        else{
            Thread timer = new Thread(){
                public void run(){
                    try{
                        sleep(2500);                    
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }finally{

                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            };
            timer.start();
        }
    }


    private class Login extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Signing in, Please wait...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            Log.d("MARKER", "reached this point");
            String url = Constants.URL_ROOT + "session/" + userName;

            //            List<NameValuePair> urlParam = new ArrayList<NameValuePair>();
            //            urlParam.add(new BasicNameValuePair("user", userName));

            try {
                JSONObject loginObj = new JSONObject();
                loginObj.put("password", password);
                String loginData = loginObj.toString();

                ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_PUT, url,
                        null, loginData, null);
                if(response.getStatus() == 200){
                    Log.d(">>>><<<<", "success in retrieving response in login");
                    JSONObject responseObj = response.getjObj();
                    return responseObj;
                    //                    JSONObject responsObj = response.getjObj();
                    //                    String login = responsObj.getString("login");
                    //                    if(login.equals("success")){
                    //                        String token = responsObj.getString("token");
                    //                        String imageUrl = responsObj.getString("image_url");
                    //                        Long userId = responsObj.getLong("user_id");
                    //                        appInstance.setAccessToken(token);
                    //                        appInstance.setProfileImageUrl(imageUrl);
                    //                        return true;
                    //                    }
                    //                    else{
                    //                        return false;
                    //                    }
                }
                else
                    return null;
            } catch (JSONException e) {                
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(JSONObject responseObj) {
            if(pDialog.isShowing())
                pDialog.dismiss();                                
            if(responseObj != null){
                try {
                    String status = responseObj.getString("status");
                    if(status.equals("OK")){
                        UserCred userCred = UserCred.parseUserCred(responseObj);
                        //                        String token = responseObj.getString("token");
                        //                        imageUrl = responseObj.getString("pic");
                        //                        Log.d("??????????", "image url = " + imageUrl);
                        imageUrl = userCred.getPic();
                        if (!(imageUrl == null) && !imageUrl.equals("null") && !imageUrl.startsWith("http://") &&
                                !imageUrl.startsWith("https://")){
                            imageUrl = "http://sparkzi.com/api/apinew/" + imageUrl;
                            //                        Log.d("??????????", "image url = " + imageUrl);
                            userCred.setPic(imageUrl);
                        }
                        userCred.setPassword(password);

                        appInstance.setUserCred(userCred);

                        //                        appInstance.setAccessToken(token);
                        //                        appInstance.setProfileImageUrl(imageUrl);


                        //                        runOnUiThread(new Runnable() {
                        //                            public void run() {                               
                        Intent i = new Intent();
                        if(imageUrl == null){
                            i = new Intent(SplashActivity.this, UploadPicActivity.class);

                            Bundle bundle = new Bundle();
                            bundle.putInt(Constants.FROM_ACTIVITY, Constants.PARENT_ACTIVITY_LOGIN);                
                            i.putExtras(bundle);
                        }
                        else{
                            i = new Intent(SplashActivity.this, MainActivity.class);
                        }
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                        //                            }
                    //                        });
                    }
                    else{
                        alert("Invalid username/password.", false);
                    }
                } catch (JSONException e) {
                    alert("Registration Exception.", false);
                    e.printStackTrace();
                }

            }
            else{
                alert("Registration error, please try again.", false);
            }

        }
    }

    //    void alert(String message) {
    //        AlertDialog.Builder bld = new AlertDialog.Builder(SplashActivity.this);
    //        bld.setMessage(message);
    //        bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {
    //
    //            @Override
    //            public void onClick(DialogInterface dialog, int which) {
    //                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
    //                finish();
    //
    //            }
    //        });
    //        bld.create().show();
    //    }


    void alert(String message, final Boolean success) {
        AlertDialog.Builder bld = new AlertDialog.Builder(SplashActivity.this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = null;
                if(success){
                    i = new Intent(SplashActivity.this, MainActivity.class);
                }
                else{
                    i = new Intent(SplashActivity.this, LoginActivity.class);

                }
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
        bld.create().show();
    }

    //    @Override
    //    public boolean onCreateOptionsMenu(Menu menu) {
    //        // Inflate the menu; this adds items to the action bar if it is present.
    //        getMenuInflater().inflate(R.menu.splash, menu);
    //        return true;
    //    }

}
