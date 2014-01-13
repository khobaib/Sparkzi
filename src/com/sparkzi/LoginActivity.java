package com.sparkzi;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;

public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    // Progress Dialog
    private ProgressDialog pDialog;

    EditText Username, Password;
    CheckBox RememberMe;
    TextView ForgetPassword;

    SparkziApplication appInstance;
    String userName, password;
    String imageUrl;

    JsonParser jsonParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(this, "3ec7fda7");
        setContentView(R.layout.login);

        pDialog = new ProgressDialog(LoginActivity.this);
        appInstance = (SparkziApplication) getApplication();
        jsonParser = new JsonParser();

        Username = (EditText) findViewById(R.id.et_user_name);
        Password = (EditText) findViewById(R.id.et_password);   

        ForgetPassword = (TextView) findViewById(R.id.tv_click_here);
        ForgetPassword.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onClickForgetPassword();

            }
        });

        RememberMe = (CheckBox) findViewById(R.id.cb_remember_me);
        RememberMe.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked()){
                    Log.d(TAG, "Remember Me checked");
                    //                    appInstance.setRememberMe(true);
                }
                else{
                    Log.d(TAG, "Remember Me unchecked");
                    //                    appInstance.setRememberMe(false);
                }               
            }
        });
    }
    
    
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        BugSenseHandler.startSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BugSenseHandler.closeSession(this);
    }


    public void onClickLogin(View v){
        userName = Username.getText().toString().trim();
        password = Password.getText().toString().trim();

        if(userName == null || userName.equals(""))
            Toast.makeText(LoginActivity.this, "Please enter user name.", Toast.LENGTH_SHORT).show();
        else if(password == null || password.equals(""))
            Toast.makeText(LoginActivity.this, "Please enter password.", Toast.LENGTH_SHORT).show();
        else{
            new Login().execute();
        }
    }

    public void onClickRegister(View v){
        startActivity(new Intent(LoginActivity.this, GetStartedActivity.class));
    }

    public void onClickForgetPassword(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
        startActivity(browserIntent);
    }


    //    public class SendForgetPassRequest extends AsyncTask<String, Void, String> {
    //        
    //        @Override
    //        protected void onPreExecute() {
    //            super.onPreExecute();
    //            pDialog.setMessage("Loading...");
    //            pDialog.setIndeterminate(true);
    //            pDialog.setCancelable(true);
    //            pDialog.show();
    //        }
    //
    //        @Override
    //        protected String doInBackground(String... params) {
    //            String rootUrl = Constants.URL_ROOT;
    //
    //            List<NameValuePair> urlParam = new ArrayList<NameValuePair>();
    //            urlParam.add(new BasicNameValuePair("method", Constants.METHOD_FORGET_PASSWORD));
    //            
    //            try {
    //                JSONObject emailObj = new JSONObject();
    //                emailObj.put("email", params[0]);
    //                String emailData = emailObj.toString();
    //
    //                ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, rootUrl,
    //                        urlParam, emailData, null);
    //                if(response.getStatus() == Constants.RESPONSE_STATUS_CODE_SUCCESS){
    //                    Log.d(">>>><<<<", "RESPONSE_STATUS_CODE_SUCCESS");
    //                    JSONObject responsObj = response.getjObj();
    //                    String login = responsObj.getString("response");
    //                    return login;
    //                }
    //                return "false";
    //            } catch (JSONException e) {                
    //                e.printStackTrace();
    //                return "false";
    //            }
    //        }
    //        
    //        
    //        @Override
    //        protected void onPostExecute(String result) {
    //            if(pDialog.isShowing())
    //                pDialog.dismiss();
    //            if(result.equals("success")){
    //                alert("Your password is sent to your email adderess.");
    //            }
    //            else if(result.equals("email doesn't exist")){
    //                alert("This email address doesn't exist.");
    //            }
    //            else{
    //                alert("Request Failure.");
    //            }
    //
    //        }
    //        
    //    }

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
                        imageUrl = userCred.getPicUrl();
                        Log.d(">>>>", "imageUrl = " + ((imageUrl == null) ? "null" : imageUrl));
                        if (!(imageUrl == null) && !imageUrl.equals("null") && !imageUrl.startsWith("http://") &&
                                !imageUrl.startsWith("https://")){
                            imageUrl = "http://sparkzi.com/api/apinew/" + imageUrl;
                            //                        Log.d("??????????", "image url = " + imageUrl);
                            userCred.setPicUrl(imageUrl);
                        }

                        appInstance.setUserCred(userCred);

                        //                        appInstance.setAccessToken(token);
                        //                        appInstance.setProfileImageUrl(imageUrl);

                        //                        runOnUiThread(new Runnable() {
                        //                            public void run() {

                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(Username.getWindowToken(), 0);
                        imm.hideSoftInputFromWindow(Password.getWindowToken(), 0);

                        if(RememberMe.isChecked()){
                            appInstance.setRememberMe(true);

                            userCred = appInstance.getUserCred();
                            userCred.setUsername(userName);
                            userCred.setPassword(password);
                            appInstance.setUserCred(userCred);
                            //                                    appInstance.setCredentials(userName, password);
                        }

                        Intent i = new Intent();
                        //                        imageUrl = null;            // TEST PURPOSE
                        if(imageUrl == null){
                            //                            i = new Intent(LoginActivity.this, EssentialDetailsActivity.class);

                            i = new Intent(LoginActivity.this, UploadPicActivity.class);

                            Bundle bundle = new Bundle();
                            bundle.putInt(Constants.FROM_ACTIVITY, Constants.PARENT_ACTIVITY_LOGIN);                
                            i.putExtras(bundle);
                        }
                        else{
                            i = new Intent(LoginActivity.this, MainActivity.class);
                        }
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                        //                            }
                        //                        });

                        //                        alert("Registration Successful.", true);
                    }
                    else{
                        alert("Invalid username/password.");
                    }
                } catch (JSONException e) {
                    alert("Registration Exception.");
                    e.printStackTrace();
                }

            }
            else{
                alert("Registration error, please try again.");
            }

        }
    }


    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(LoginActivity.this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //                Intent i = null;
                //                if(success){
                //                    i = new Intent(LoginActivity.this, HomeActivity.class);
                //                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //                    startActivity(i);
                //                    finish();
                //                }
            }
        });
        bld.create().show();
    }


    //    void alert(String message) {
    //        AlertDialog.Builder bld = new AlertDialog.Builder(LoginActivity.this);
    //        bld.setMessage(message);
    //        bld.setNeutralButton("OK", null);
    //        //      Log.d(TAG, "Showing alert dialog: " + message);
    //        bld.create().show();
    //    }

}
