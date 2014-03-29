package com.sparkzi;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.sparkzi.model.Favorite;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;

public class NewConversationActivity extends Activity{

    JsonParser jsonParser;
    ProgressDialog pDialog;
    SparkziApplication appInstance;

   String selectedUserName;
    String msgBody;

    List<Favorite> fList;
    List<String> favoriteUserNameList;

    EditText etMsgBody;
    AutoCompleteTextView actUserNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(this, "2c5ced14");
        setContentView(R.layout.new_conversation);

        appInstance = (SparkziApplication) getApplication();
        jsonParser = new JsonParser();        
        pDialog = new ProgressDialog(NewConversationActivity.this);

        selectedUserName = null;

        etMsgBody = (EditText) findViewById(R.id.et_msg_body);

        actUserNameList = (AutoCompleteTextView) findViewById(R.id.act_to);
        actUserNameList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                selectedUserName = (String)parent.getItemAtPosition(pos);
            }
        });

        new GetFavoriteList().execute();
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
    

    public void onClickSend(View v){
        msgBody = etMsgBody.getText().toString().trim();
        if(msgBody == null || msgBody.equals("")){
            Toast.makeText(NewConversationActivity.this, "Your message is empty.", Toast.LENGTH_SHORT).show();
        }
        else{
            if(selectedUserName == null)
                selectedUserName = actUserNameList.getText().toString();
            if(selectedUserName == null || selectedUserName.equals("")){
                Toast.makeText(NewConversationActivity.this, "Please select a user.", Toast.LENGTH_SHORT).show();
            }
            else{
                new SendMessageToServer().execute();
            }
        }
    }


    private class GetFavoriteList extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Loading...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            String url = Constants.URL_ROOT + "favs/all";

            UserCred userCred = appInstance.getUserCred();
            String token = userCred.getToken();

            ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_GET, url,
                    null, null, token);
            if(response.getStatus() == 200){
                Log.d(">>>><<<<", "success in retrieving fav list");
                JSONObject responseObj = response.getjObj();
                return responseObj;
            }
            else
                return null;
        }

        @Override
        protected void onPostExecute(JSONObject responseObj) {
            // TODO Auto-generated method stub
            super.onPostExecute(responseObj);
            if(pDialog.isShowing())
                pDialog.dismiss();
            if(responseObj != null){
                try {
                    String status = responseObj.getString("status");
                    if(status.equals("OK")){
                        JSONObject favObj = responseObj.getJSONObject("favs");
                        JSONArray addedFavArray = favObj.getJSONArray("favs");
                        fList = Favorite.parseFavorite(addedFavArray);

                        favoriteUserNameList = new ArrayList<String>();
                        for(Favorite fav : fList)
                            favoriteUserNameList.add(fav.getUsername());

                        ArrayAdapter<String> favoriteListAdapter = new ArrayAdapter<String>(NewConversationActivity.this,
                                R.layout.my_simple_spinner_item, favoriteUserNameList);
                        actUserNameList.setAdapter(favoriteListAdapter);
                        favoriteListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    }
                    else{
                        alert("Invalid token.", false);
                    }
                } catch (JSONException e) {
                    alert("Exception.", false);
                    e.printStackTrace();
                }
            }
        }

    }


    public class SendMessageToServer extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Please wait while your message is being sent...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            String url = Constants.URL_ROOT + "messages/" + selectedUserName;

            UserCred userCred = appInstance.getUserCred();
            String token = userCred.getToken();

            //            List<NameValuePair> urlParam = new ArrayList<NameValuePair>();
            //            urlParam.add(new BasicNameValuePair("user", email.substring(0, email.lastIndexOf("@"))));


            try {
                JSONObject msgObj = new JSONObject();
                msgObj.put("message", msgBody);

                String msgData = msgObj.toString();
                ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_PUT, url,
                        null, msgData, token);
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
                        alert("Message Sent.", true);
                    }
                    else{
                        //                        String desc = responseObj.getString("description");
                        //                        if(desc.equals("User already exists"))
                        //                            alert("This user already exists, please choose another username.", false);
                        //                        else
                        //                            alert("Please check all the info & try again.", false);
                    }
                } catch (JSONException e) {
                    alert("Registration Exception.", false);
                    e.printStackTrace();
                }

            }
            else{
                alert("Message sending error, please try again.", false);
            }
        }               

    }

    void alert(String message, final Boolean success) {
        AlertDialog.Builder bld = new AlertDialog.Builder(NewConversationActivity.this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(success){
                    //                    Intent i = new Intent(NewConversationActivity.this, LoginActivity.class);
                    //                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //                    startActivity(i);
                    finish();
                }


            }
        });
        bld.create().show();
    }


}
