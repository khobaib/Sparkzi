package com.sparkzi;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.sparkzi.model.ServerResponse;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;


public class AsynctaskSendregIdToServer extends AsyncTask<Void, Void, String> {
	
	Activity activity;
	
	
	  JsonParser jsonParser;
	  
	String token;
	String profile_info;
	MainActivity lisenar;
	String regid;
	 private ProgressDialog pDialog;

	public AsynctaskSendregIdToServer(String regid,String token) {
		
	
		this.regid=regid;
		jsonParser = new JsonParser();
		this.token=token;
//		pDialog = new ProgressDialog(activity);
//		  pDialog.setMessage("Registration...");
//          pDialog.setIndeterminate(true);
//          pDialog.setCancelable(true);
//          pDialog.show();
		
	}

	@Override
	protected String doInBackground(Void... params) {
		
		
		//  UserCred userCred = appInstance.getUserCred();
         // String token = userCred.getToken();

        //  ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_GET, url,
            //      null, null, token);
	
			String res="dfgfrg";
			
			Log.d("test", "Start");
			String url = Constants.URL_ROOT + "gcm/"+regid;

        
               JSONObject loginObj = new JSONObject();
            
               String loginData = loginObj.toString();

               ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_PUT, url,
                       null, null, token);
               
            //   int reqType, String url, List<NameValuePair> urlParams, String content, String appToken
               
               if(response.getStatus() == 200){
                   Log.d(">>>><<<<", "success in retrieving response in login");
                   JSONObject responseObj = response.getjObj();
                 Log.d("resut", responseObj.toString());
               }
             
               Log.d("resut", response.toString());
		
		return res;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
//		if((pDialog!=null)&&(pDialog.isShowing())){
//			pDialog.cancel();
//		}
//		

	}
	
	
	
}
