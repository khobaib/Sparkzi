package com.sparkzi;

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

import com.bugsense.trace.BugSenseHandler;
import com.facebook.model.GraphUser;
import com.sparkzi.model.RegistrationInfo;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;

public class RegistrationActivity extends Activity {


	EditText etUserName, etFirstName, etLastName, etEmail, etPassword, etConfirmPass;

	String whoAmI, startAge, endAge, cityName, dob;
	String userName, firstname, lastName, email, password, confirmPass;
	int countryId;

	Bundle b;
	RegistrationInfo regInfo;

	JsonParser jsonParser;
	ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(this, "2c5ced14");
		setContentView(R.layout.register);

		jsonParser = new JsonParser();        
		pDialog = new ProgressDialog(RegistrationActivity.this);

		b = getIntent().getExtras();

		etUserName = (EditText) findViewById(R.id.et_user_name);
		etFirstName = (EditText) findViewById(R.id.et_first_name);
		etLastName = (EditText) findViewById(R.id.et_last_name);
		etEmail = (EditText) findViewById(R.id.et_email);
		etPassword = (EditText) findViewById(R.id.et_password);
		etConfirmPass = (EditText) findViewById(R.id.et_confirm_password);     

		if(GetStartedActivity.graphuser!=null){
			GraphUser user=GetStartedActivity.graphuser;
			etFirstName.setText(user.getFirstName());
			etLastName.setText(user.getLastName());
			etEmail.setText(user.getProperty("email").toString());
		}

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



	public void onClickRegister(View v){

		whoAmI = b.getString("who_am_i");
		startAge = b.getString("min_age");
		endAge = b.getString("max_age");
		countryId = b.getInt("country");
		cityName = b.getString("city");
		dob = b.getString("dob");

		regInfo = new RegistrationInfo();

		userName = etUserName.getText().toString().trim();
		firstname = etFirstName.getText().toString().trim();
		lastName = etLastName.getText().toString().trim();
		email = etEmail.getText().toString().trim();
		password = etPassword.getText().toString().trim();
		confirmPass = etConfirmPass.getText().toString().trim();

		if(userName == null || userName.equals("")){
			Toast.makeText(RegistrationActivity.this, "Please choose a username.", Toast.LENGTH_SHORT).show();
		}
		else if(firstname == null || firstname.equals("")){
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
			regInfo.setGender((whoAmI.startsWith("M"))? 1 : 2);
			regInfo.setLowerAge(startAge);
			regInfo.setUpperAge(endAge);
			regInfo.setDob(dob);
			regInfo.setCountry(countryId);
			regInfo.setCity(cityName);

			regInfo.setUserName(userName);
			regInfo.setFirstName(firstname);
			regInfo.setLastName(lastName);
			regInfo.setEmail(email);
			regInfo.setPassword(password);


			new SendRegistrationRequest().execute();
		}
	}


	public class SendRegistrationRequest extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setMessage("Please wait while app registering you to the system...");
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			String url = Constants.URL_ROOT + "user/" + regInfo.getUserName();

			//            List<NameValuePair> urlParam = new ArrayList<NameValuePair>();
			//            urlParam.add(new BasicNameValuePair("user", email.substring(0, email.lastIndexOf("@"))));


			try {
				JSONObject regObj = new JSONObject();
				regObj.put("password", regInfo.getPassword());
				regObj.put("email", regInfo.getEmail());
				regObj.put("gender", regInfo.getGender());
				regObj.put("lowerage", regInfo.getGender());
				regObj.put("upperage", regInfo.getGender());
				regObj.put("country", regInfo.getCountry());
				regObj.put("hometown", regInfo.getCity());
				regObj.put("bdate", regInfo.getbDate());
				regObj.put("bmonth", regInfo.getbMonth());
				regObj.put("byear", regInfo.getbYear());                

				regObj.put("firstname", regInfo.getFirstName());
				regObj.put("lastname", regInfo.getLastName());

				String regData = regObj.toString();
				//                Log.d("<<>>", "req data = " + regData);
				ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_PUT, url,
						null, regData, null);
				if(response.getStatus() == 200){
					JSONObject responseObj = response.getjObj();
					//                    String status = responseObj.getString("status");
					//                    Log.d(">>>>>", "status = " + status);
					//                    if(status.equals("OK")){
					//                        String desc = responseObj.getString("description");
					//                        return true;
					//                    }
					//                    return false;
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
						alert("Registration Successful.", true);
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
				alert("Registration error, please try again.", false);
			}
		}               

	}

	void alert(String message, final Boolean success) {
		AlertDialog.Builder bld = new AlertDialog.Builder(RegistrationActivity.this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(success){
					Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					finish();
				}


			}
		});
		bld.create().show();
	}


}
