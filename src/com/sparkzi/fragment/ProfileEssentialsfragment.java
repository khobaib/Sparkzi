package com.sparkzi.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.sparkzi.EssentialDetailsActivity;
import com.sparkzi.adapter.ProfileEssentialAdapter;
import com.sparkzi.model.Essential;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;

public class ProfileEssentialsfragment extends ListFragment {

    private Activity activity;
    JsonParser jsonParser;

    private String token;

    private ProfileEssentialAdapter pEssentialAdapter;

    public static List<Essential> eList;

    public ProfileEssentialsfragment() {
        // TODO Auto-generated constructor stub
    }

    public static ProfileEssentialsfragment newInstance(){
        ProfileEssentialsfragment fragment = new ProfileEssentialsfragment();
        return fragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();
        jsonParser = new JsonParser();

        UserCred userCred = ((SparkziApplication) activity.getApplication()).getUserCred();
        token = userCred.getToken();
        ListView lv = getListView();
        lv.setDivider(activity.getResources().getDrawable(com.sparkzi.R.color.app_theme));
        lv.setDividerHeight(0);


        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if(eList.size() > 0){
                    Intent i = new Intent(activity, EssentialDetailsActivity.class);  

                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.FROM_ACTIVITY, Constants.PARENT_ACTIVITY_PROFILE);                
                    i.putExtras(bundle);

                    startActivity(i);
                }

            }
        });

        pEssentialAdapter = new ProfileEssentialAdapter(activity, null);
        //        setEmptyText("No feeds");
        setListAdapter(pEssentialAdapter);
        setListShown(false);

        eList = new ArrayList<Essential>();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        new GetEssentialInfo().execute();
    }


    private class GetEssentialInfo extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            String url = Constants.URL_ROOT + "user";
            ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_GET, url,
                    null, null, token);
            if(response.getStatus() == 200){
                Log.d(">>>><<<<", "success in retrieving user info");
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
            if(responseObj != null){
                try {
                    String status = responseObj.getString("status");
                    if(status.equals("OK")){
                        JSONObject userObj= responseObj.getJSONObject("user");
                        //                        final List<Essential> eList = Essential.parseEssentialList(userObj);
                        eList = Essential.parseUserEssential(userObj, activity);

                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                pEssentialAdapter.setData(eList);
                                if (isResumed()) {
                                    setListShown(true);
                                } else {
                                    setListShownNoAnimation(true);
                                }                               
                            }

                        });
                    }
                    else{
                        alert("Invalid token.");
                    }
                } catch (JSONException e) {
                    alert("Exception.");
                    e.printStackTrace();
                }
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
                activity.finish();
            }
        });
        bld.create().show();
    }


}
