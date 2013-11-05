package com.sparkzi.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.sparkzi.GetStartedActivity;
import com.sparkzi.LoginActivity;
import com.sparkzi.MainActivity;
import com.sparkzi.ProfileActivity;
import com.sparkzi.R;
import com.sparkzi.RegistrationActivity;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;
import com.sparkzi.utility.Utility;

public class LastNightFragment extends Fragment {
    
    private static final String TAG = LastNightFragment.class.getSimpleName();
    private Activity activity;

    Spinner sTemplate;
    TextView tvActivity, tvWarning;
    EditText etActivity, etElaborate;
    Button bShare;
    
    int templateIndex;
    
    JsonParser jsonParser;
    ProgressDialog pDialog;
    SparkziApplication appInstance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.last_night_fragment, null);

        sTemplate = (Spinner) view.findViewById(R.id.s_what_did_you_do);
        tvActivity = (TextView) view.findViewById(R.id.tv_activity);
        tvWarning = (TextView) view.findViewById(R.id.tv_warning);
        etActivity = (EditText) view.findViewById(R.id.et_activity);
        etElaborate = (EditText) view.findViewById(R.id.et_elaborate);

        bShare = (Button) view.findViewById(R.id.bShare);
        bShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String activityText = etActivity.getText().toString().trim();
                if(activityText == null || activityText.equals("")){
                    tvWarning.setVisibility(View.VISIBLE);
                    return;
                }
                String elaboratedtext = etElaborate.getText().toString().trim();
                new SendLastNightActivity().execute(activityText, elaboratedtext);

            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();
        if(activity != null){
            
            templateIndex = 0;
            jsonParser = new JsonParser();        
            pDialog = new ProgressDialog(activity);  
            appInstance = (SparkziApplication) activity.getApplication();
            
            generateSpinner(sTemplate, Utility.ACTIVITY_TEMPLATE);
            sTemplate.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                    templateIndex = position;
                    String activityText = Utility.ACTIVITY_QUESTION[templateIndex];
                    tvActivity.setText(activityText);
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });
        }
    }
    
    
    private void generateSpinner(Spinner spinner, String[] arrayToSpinner) {
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(
                activity, R.layout.my_simple_spinner_item, arrayToSpinner);
        spinner.setAdapter(myAdapter);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }
    
    
    
    public class SendLastNightActivity extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Please wait...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = Constants.URL_ROOT + "feeds";
            
            UserCred userCred = appInstance.getUserCred();
            String token = userCred.getToken();

            try {
                JSONObject feedObj = new JSONObject();
                feedObj.put("feed", params[0]);
                feedObj.put("elaborated", params[1]);
                feedObj.put("template", (templateIndex+1));


                String feedData = feedObj.toString();
//                Log.d("<<>>", "req data = " + regData);
                ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_PUT, url,
                        null, feedData, token);
                if(response.getStatus() == 200){
                    JSONObject responseObj = response.getjObj();
                    return responseObj;
                }
                else{
                    return null;
                }
            } catch (JSONException e) {                
                e.printStackTrace();
                return null;
            }
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
                        alert("Successfully posted the feed.", true);
                    }
                    else{
                        String desc = responseObj.getString("description");
                        if(desc.equals("User already exists"))
                            alert("This user already exists, please choose another username.", false);
                        else
                            alert("Please check all the info & try again.", false);
                    }
                } catch (JSONException e) {
                    alert("Registration Exception.", false);
                    e.printStackTrace();
                }
                
            }
            else{
                alert("Failed to post the feed, please try again.", false);
            }
        }               
     
    }
    
    
    void alert(String message, final Boolean success) {
        AlertDialog.Builder bld = new AlertDialog.Builder(activity);
        bld.setMessage(message);
        bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(success){
                    ((MainActivity) activity).selectItem(0);
                }
                

            }
        });
        bld.create().show();
    }

}
