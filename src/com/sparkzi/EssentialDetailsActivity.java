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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.sparkzi.model.ServerResponse;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;

public class EssentialDetailsActivity extends Activity {
    
    EditText etEducation, etEthnicity, etDiet, etDrinks, etSmokes, etReligion,
                etKids, etPolitics, etSign, etProfession, etHometown, etLanguages;
    
    String education, ethnicity, diet, drinks, smokes, religion, kids, politics, sign, profession, hometown, languages;
        
    JsonParser jsonParser;
    ProgressDialog pDialog;
    SparkziApplication appInstance;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.essential_details);
        
        jsonParser = new JsonParser();        
        pDialog = new ProgressDialog(EssentialDetailsActivity.this);
        appInstance = (SparkziApplication) getApplication();
        
        etEducation = (EditText) findViewById(R.id.et_education);
        etEthnicity = (EditText) findViewById(R.id.et_ethnicity);
        etDiet = (EditText) findViewById(R.id.et_diet);
        etDrinks = (EditText) findViewById(R.id.et_drinks);
        etSmokes = (EditText) findViewById(R.id.et_smokes);
        etReligion = (EditText) findViewById(R.id.et_religion);
        etKids = (EditText) findViewById(R.id.et_kids);
        etPolitics = (EditText) findViewById(R.id.et_politics);
        etSign = (EditText) findViewById(R.id.et_sign);
        etProfession = (EditText) findViewById(R.id.et_profession);
        etHometown = (EditText) findViewById(R.id.et_hometown);
        etLanguages = (EditText) findViewById(R.id.et_languages);
    }
    
    public void onCLickDone(View v){
        education = etEducation.getText().toString().trim();
        ethnicity = etEthnicity.getText().toString().trim();
        diet = etDiet.getText().toString().trim();
        drinks = etDrinks.getText().toString().trim();
        smokes = etSmokes.getText().toString().trim();
        religion = etReligion.getText().toString().trim();
        kids = etKids.getText().toString().trim();
        politics = etPolitics.getText().toString().trim();
        sign = etSign.getText().toString().trim();
        profession = etProfession.getText().toString().trim();
        hometown = etHometown.getText().toString().trim();
        languages = etLanguages.getText().toString().trim();
        
        new SendRegistrationRequest().execute();
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
            urlParam.add(new BasicNameValuePair("user", appInstance.getUserName()));


            try {
                JSONObject essentialObj = new JSONObject();
                essentialObj.put("education", education);
                essentialObj.put("ethnicity", ethnicity);
                essentialObj.put("politics", politics);
                essentialObj.put("diet", diet);
                essentialObj.put("drinks", drinks);
                essentialObj.put("smokes", smokes);
                essentialObj.put("kids", kids);
                essentialObj.put("sign", sign);
                essentialObj.put("hometown", hometown);                
                essentialObj.put("profession", profession);
                essentialObj.put("religion", religion);
                essentialObj.put("languages", languages);

                JSONObject reqObj = new JSONObject();
                reqObj.put("essentials", essentialObj);
                String reqData = reqObj.toString();
//                Log.d("<<>>", "req data = " + regData);
                ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url,
                        urlParam, reqData, appInstance.getAccessToken());
                if(response.getStatus() == 200){
                    JSONObject responseObj = response.getjObj();
//                    if(responseObj.has("success"))
//                        return true;
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
                alert("Update Successful.", true);
            }
            else{
                alert("Update error, please try again.", false);
            }
            //                updateUI();
        }        
    }
    
    void alert(String message, final Boolean success) {
        AlertDialog.Builder bld = new AlertDialog.Builder(EssentialDetailsActivity.this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(success)
                    finish();

            }
        });
        bld.create().show();
    }

}
