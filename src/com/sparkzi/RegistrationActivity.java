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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sparkzi.model.RegistrationInfo;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;

public class RegistrationActivity extends Activity {
    
    EditText etFirstName, etLastName, etEmail, etPassword, etConfirmPass;
    
    String whoAmI, startAge, endAge, countryName, cityName, dob;
    String firstname, lastName, email, password, confirmPass;
    
    Bundle b;
    RegistrationInfo regInfo;
    
    JsonParser jsonParser;
    ProgressDialog pDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        
        jsonParser = new JsonParser();        
        pDialog = new ProgressDialog(RegistrationActivity.this);
        
        b = getIntent().getExtras();
        
        etFirstName = (EditText) findViewById(R.id.et_first_name);
        etLastName = (EditText) findViewById(R.id.et_last_name);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        etConfirmPass = (EditText) findViewById(R.id.et_confirm_password);       
    }
    

    
    public void onClickRegister(View v){
        
        whoAmI = b.getString("who_am_i");
        startAge = b.getString("min_age");
        endAge = b.getString("max_age");
        countryName = b.getString("country");
        cityName = b.getString("city");
        dob = b.getString("dob");
        
        regInfo = new RegistrationInfo();
        
        firstname = etFirstName.getText().toString().trim();
        lastName = etLastName.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        confirmPass = etConfirmPass.getText().toString().trim();

        if(firstname == null || firstname.equals("")){
            Toast.makeText(RegistrationActivity.this, "Please insert your first name.", Toast.LENGTH_SHORT).show();
        }
        else if(lastName == null || lastName.equals("")){
            Toast.makeText(RegistrationActivity.this, "Please insert your last name.", Toast.LENGTH_SHORT).show();
        }
        else if(email == null || email.equals("")){
            Toast.makeText(RegistrationActivity.this, "Please insert your email.", Toast.LENGTH_SHORT).show();
        }
        else if(password == null || password.equals("")){
            Toast.makeText(RegistrationActivity.this, "Please select your password.", Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(confirmPass)){
            Toast.makeText(RegistrationActivity.this, "Password mismatch.", Toast.LENGTH_SHORT).show();
        }
        else{
            regInfo.setGender((whoAmI.startsWith("M"))? "male" : "female");
            regInfo.setLowerAge(startAge);
            regInfo.setUpperAge(endAge);
            regInfo.setDob(dob);
            regInfo.setCountry(countryName);
            regInfo.setCity(cityName);
            
            regInfo.setFirstName(firstname);
            regInfo.setLastName(lastName);
            regInfo.setEmail(email);
            regInfo.setPassword(password);




            new SendRegistrationRequest().execute();
        }
    }
    
    
    public class SendRegistrationRequest extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Please wait while app registering you to the system...");
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String url = Constants.URL_ROOT + "account.php";

            List<NameValuePair> urlParam = new ArrayList<NameValuePair>();
            urlParam.add(new BasicNameValuePair("user", email.substring(0, email.lastIndexOf("@"))));


            try {
                JSONObject regObj = new JSONObject();
                regObj.put("pass", regInfo.getPassword());
                regObj.put("gender", regInfo.getGender());
                regObj.put("lowerage", regInfo.getGender());
                regObj.put("upperage", regInfo.getGender());
                regObj.put("country", regInfo.getCountry());
                regObj.put("city", regInfo.getCity());
                regObj.put("bdate", regInfo.getbDate());
                regObj.put("bmonth", regInfo.getbMonth());
                regObj.put("byear", regInfo.getbYear());                
                regObj.put("email", regInfo.getEmail());
                regObj.put("firstname", regInfo.getFirstName());
                regObj.put("lastname", regInfo.getLastName());

                String regData = regObj.toString();
//                Log.d("<<>>", "req data = " + regData);
                ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_PUT, url,
                        urlParam, regData, null);
                if(response.getStatus() == 200){
                    JSONObject responseObj = response.getjObj();
                    String status = responseObj.getString("status");
                    if(status.equals("OK")){
                        String desc = responseObj.getString("description");
                        String token = responseObj.getString("token");
                        return true;
                    }
                    return false;
                }
                else{
                    return false;
                }
            } catch (JSONException e) {                
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(pDialog != null)
                pDialog.dismiss();
            if(result){
                alert("Registration Successful.", true);
            }
            else{
                alert("Registration error, please try again.", false);
            }
            //                updateUI();
        }        
    }
    
    void alert(String message, final Boolean success) {
        AlertDialog.Builder bld = new AlertDialog.Builder(RegistrationActivity.this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(success){
                    startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                    finish();
                }
                

            }
        });
        bld.create().show();
    }

}
