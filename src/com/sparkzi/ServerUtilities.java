/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sparkzi;

import java.util.Random;

import android.content.Context;
import android.util.Log;

import com.sparkzi.utility.Utility;

/**
 * Helper class used to communicate with the demo server.
 */
public final class ServerUtilities {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();

    /**
     * Register this account/device pair within the server.
     *
     * @return whether the registration succeeded or not.
     */
    static boolean register(final Context context, final String regId) {
       
    	 long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register it in the
        // demo server. As the server might be down, we will retry it a couple
        // times.

        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
          
        
        	sendRegistrationIdToBackend(regId);
        	 return true;
        }

        return false;
    }
    
    
    
    
    
    
    
    
    

    /**
     * Unregister this account/device pair within the server.
     */
   static void unregister(final Context context, final String regId) {
     /*   Log.i(Utility.TAG, "unregistering device (regId = " + regId + ")");
        String serverUrl = Utility.SERVER_URL + "/unregister";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        JsonParser jsonParser = new JsonParser();

        String unregData = null;

        JSONObject unregObj = new JSONObject();
        try {
            unregObj.put("GCM", regId);

            unregData = unregObj.toString();
            ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, serverUrl,
                    null, unregData, null);
            if(response.getStatus() == Constants.RESPONSE_STATUS_CODE_SUCCESS){
                GCMRegistrar.setRegisteredOnServer(context, false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } */

    }
    
    
    
    private static  void sendRegistrationIdToBackend(String regid) {
  	  Log.d("Start", "Start");
  	
  	  AsynctaskSendregIdToServer senddata=new AsynctaskSendregIdToServer(regid, Utility.token); 
  	  senddata.execute();

     }
}
