package com.sparkzi.fragment;

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
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.widget.ListView;

import com.sparkzi.adapter.FavoriteAdapter;
import com.sparkzi.model.Favorite;
import com.sparkzi.model.Question;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;

public class FavoriteAddedFragment extends ListFragment {

    private static final String TAG = FavoriteAddedFragment.class.getSimpleName();
    private Activity activity;   
    JsonParser jsonParser;

    private FavoriteAdapter favAdapter;

    private String token;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        activity = getActivity();
        jsonParser = new JsonParser();

        UserCred userCred = ((SparkziApplication) activity.getApplication()).getUserCred();
        token = userCred.getToken();
        ListView lv = getListView();
        lv.setDivider(activity.getResources().getDrawable(com.sparkzi.R.color.app_theme));
        lv.setDividerHeight(3);

        favAdapter = new FavoriteAdapter(activity, null);
        setListAdapter(favAdapter);
        setListShown(false);

        new GetFavoriteInfo().execute();
    }


    private class GetFavoriteInfo extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            String url = Constants.URL_ROOT + "favs/all/1";
            ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_GET, url,
                    null, null, token);
            if(response.getStatus() == 200){
                Log.d(">>>><<<<", "success in retrieving favorite info");
                JSONObject responseObj = response.getjObj();
                return responseObj;
            }
            else
                return null;
        }

        @Override
        protected void onPostExecute(JSONObject responseObj) {
            super.onPostExecute(responseObj);
            if(responseObj != null){
                try {
                    String status = responseObj.getString("status");
                    if(status.equals("OK")){
                        JSONObject favObj = responseObj.getJSONObject("favs");
                        JSONArray addedFavArray = favObj.getJSONArray("favs");
                        final List<Favorite> fList = Favorite.parseFavorite(addedFavArray);
                        favAdapter.setData(fList);
                        if (isResumed()) {
                            setListShown(true);
                        } else {
                            setListShownNoAnimation(true);
                        }                               
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
