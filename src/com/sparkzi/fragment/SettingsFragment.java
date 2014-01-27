package com.sparkzi.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.sparkzi.R;
import com.sparkzi.model.Favorite;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;

public class SettingsFragment extends Fragment{

    private Activity activity;
    private ProgressDialog pDialog;
    
    private JsonParser jsonParser;
    private String token;
    
    RelativeLayout rlChangePassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.settings_fragment, null);
        
        rlChangePassword = (RelativeLayout) view.findViewById(R.id.rl_change_pass);
        rlChangePassword.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                onClickChangePassword();
                
            }
        });
        
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();
        if(activity != null){         
            UserCred userCred = ((SparkziApplication) activity.getApplication()).getUserCred();
            token = userCred.getToken();
            
            jsonParser = new JsonParser();
        }
    }
    
    
    public void onClickChangePassword(){

        LayoutInflater inflater = (LayoutInflater) activity.getLayoutInflater();
        View textEntryView = inflater.inflate(R.layout.dialog_change_password, null);
        final AlertDialog alert = new AlertDialog.Builder(activity).create();
        alert.setView(textEntryView, 0, 0, 0, 0);


        final EditText CurrentPass = (EditText) textEntryView.findViewById(R.id.et_current_password);
        final EditText NewPass = (EditText) textEntryView.findViewById(R.id.et_new_password);
        final EditText ConfirmNewPass = (EditText) textEntryView.findViewById(R.id.et_confirm_new_password);

        Button OK = (Button) textEntryView.findViewById(R.id.b_ok);       

        OK.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String curPass = CurrentPass.getText().toString();
                String newPass = NewPass.getText().toString();
                String confirmNewPass = ConfirmNewPass.getText().toString();
                if(curPass.trim().equals("")){
                    Toast.makeText(activity, "Current password field is null.", Toast.LENGTH_SHORT).show();
                }
                else if(newPass.trim().equals("")){
                    Toast.makeText(activity, "New password field is null.", Toast.LENGTH_SHORT).show();
                }
                else if(newPass.equals(confirmNewPass)){
                    alert.dismiss(); 
                    new ChangePasswordReq().execute(curPass, newPass);
                }
                else{
                    Toast.makeText(activity, "password confirmation mismatch.", Toast.LENGTH_SHORT).show();
                }
            }

        });

        alert.show();
    }
    
    
    private class ChangePasswordReq extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(activity);
            pDialog.setMessage("Loading...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.URL_ROOT + "password";

            try {
                JSONObject passwordObj = new JSONObject();
                passwordObj.put("passnow", params[0]);
                passwordObj.put("password", params[1]);
                String content = passwordObj.toString();

                ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url,
                        null, content, token);
                if(response.getStatus() == 200){
                    Log.d(">>>><<<<", "success in updating password");
                    JSONObject responseObj = response.getjObj();
                    return responseObj;
                }            
            } catch (JSONException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(JSONObject responseObj) {
            super.onPostExecute(responseObj);
            
            if(pDialog.isShowing())
                pDialog.dismiss();
            
            if(responseObj != null){
                try {
                    String status = responseObj.getString("status");
                    if(status.equals("OK")){
                        alert("Password updated.");
                    }
                    else{
                        alert("Password update error.");
                    }
                } catch (JSONException e) {
                    alert("Exception.");
                    e.printStackTrace();
                }
            }
            else{
                
            }

        }

    }
    
    
    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(activity);
        bld.setMessage(message);
        bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        bld.create().show();
    } 


}
