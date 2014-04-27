package com.sparkzi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.sparkzi.fragment.DatePickerFragment;
import com.sparkzi.model.City;
import com.sparkzi.model.Country;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.Utility;

@SuppressLint("NewApi")
public class GetStartedActivity extends FragmentActivity implements
		OnDateSetListener {

	Spinner sWhoIAm, sStartAge, sEndAge;
	AutoCompleteTextView sCountry1, sCity1;
	TextView tvDoB;

	Button b_facebook;
	String whoAmI, startAge, endAge, selectedCityName;
	int countryId;
	Calendar calendar;
	public static GraphUser graphuser;
	JsonParser jsonParser;
	ProgressDialog pDialog;

	List<Country> countryList;
	List<City> cityList;
	Session session1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		BugSenseHandler.initAndStartSession(this, "2c5ced14");
		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
		}
		setContentView(R.layout.get_started);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		// ImageView iv_app_logo = (ImageView) findViewById(R.id.iv_app_logo1);
		// iv_app_logo.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// finish();
		// }
		// });
		jsonParser = new JsonParser();
		pDialog = new ProgressDialog(GetStartedActivity.this);
		calendar = Calendar.getInstance();

		// retreeive data from facebook

		b_facebook = (Button) findViewById(R.id.b_facebook);
		b_facebook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				registerwithfacebook();
			}
		});

		tvDoB = (TextView) findViewById(R.id.tv_dob);
		tvDoB.setText(String.format("%04d-%02d-%02d",
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
				calendar.get(Calendar.DAY_OF_MONTH)));

		sWhoIAm = (Spinner) findViewById(R.id.s_who_i_am);
		generateSpinner(sWhoIAm, Utility.WHO_I_AM);
		sWhoIAm.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				whoAmI = (String) parent.getItemAtPosition(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		sStartAge = (Spinner) findViewById(R.id.s_start_age);
		generateSpinner(sStartAge, Utility.AGE_RANGE);
		sStartAge.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				startAge = (String) parent.getItemAtPosition(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		sEndAge = (Spinner) findViewById(R.id.s_end_age);
		generateSpinner(sEndAge, Utility.AGE_RANGE);
		sEndAge.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				endAge = (String) parent.getItemAtPosition(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		sCity1 = (AutoCompleteTextView) findViewById(R.id.s_city);
		sCity1.setThreshold(1);

		sCity1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				selectedCityName = (String) parent.getItemAtPosition(position);

			}
		});

		sCountry1 = (AutoCompleteTextView) findViewById(R.id.s_country);
		sCountry1.setThreshold(1);
		sCountry1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				countryId = countryList.get(position).getId();
				List<String> cityList = getCityList(countryList.get(position)
						.getId());
				Log.e(">>>", "city list size = " + cityList);
				generateautocomplete(sCity1,
						cityList.toArray(new String[cityList.size()]));

			}
		});
		
		((TextView) findViewById(R.id.tv_fb_warning)).setFocusableInTouchMode(true);
		((TextView) findViewById(R.id.tv_fb_warning)).requestFocus();


		new GetCountryList().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_next, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_next:
			onClickNext();
			return true;
		case android.R.id.home:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void setAllAutotextview(GraphObject go) {
		try {
			JSONObject jso = go.getInnerJSONObject();
			String name = jso.getString("name");
			Log.i("name", name);
			@SuppressWarnings("unused")
			String[] locationdata = name.split(",");
			String country = "India";// locationdata[1];
			String city = "Delhi";// locationdata[0];
			// countrylist
			List<String> cityList = new ArrayList<String>();
			for (int position = 0; position < countryList.size(); position++) {
				if (countryList.get(position).getValue()
						.equalsIgnoreCase(country)) {
					sCountry1.setText(country);
					countryId = countryList.get(position).getId();
					cityList = getCityList(countryList.get(position).getId());
					Log.e(">>>", "city list size = " + cityList);
					generateautocomplete(sCity1,
							cityList.toArray(new String[cityList.size()]));
				}
			}

			// List<String> getCityList(int countryId)
			for (int position = 0; position < cityList.size(); position++) {
				if (cityList.get(position).equalsIgnoreCase(city)) {
					sCity1.setText(city);
					selectedCityName = cityList.get(position);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		BugSenseHandler.startSession(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		BugSenseHandler.closeSession(this);
	}

	private List<String> getCityList(int countryId) {
		List<String> ctyList = new ArrayList<String>();

		for (City city : cityList) {
			if (city.getCountryId() == countryId)
				ctyList.add(city.getvalue());
		}
		return ctyList;
	}

	@SuppressWarnings("unused")
	private int getCityId(String cityName) {
		for (City city : cityList) {
			if (city.getvalue().equals(cityName))
				return city.getId();
		}
		return -1;
	}

	private void generateSpinner(Spinner spinner, String[] arrayToSpinner) {
		ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(
				GetStartedActivity.this, R.layout.my_simple_spinner_item,
				arrayToSpinner);
		spinner.setAdapter(myAdapter);
		myAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

	}

	private void generateautocomplete(AutoCompleteTextView autextview,
			String[] arrayToSpinner) {
		ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(
				GetStartedActivity.this, R.layout.my_autocomplete_text_style,
				arrayToSpinner);
		autextview.setAdapter(myAdapter);

	}

	@SuppressWarnings("static-access")
	public void onClickCalendar(View v) {
		DialogFragment newFragment = new DatePickerFragment().newInstance(
				calendar, "get_started");
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		tvDoB.setText(String.format("%04d-%02d-%02d", year, (monthOfYear + 1),
				dayOfMonth));
		calendar.set(year, monthOfYear, dayOfMonth);
		// selectedDate = DoB.getText().toString();
	}

	// Not used, instead the below one without any param is used.
	public void onClickNext(View v) {
		String dob = tvDoB.getText().toString().trim();
		if (dob == null || dob.isEmpty()) {
			Toast.makeText(GetStartedActivity.this,
					"Please select your birthday.", Toast.LENGTH_SHORT).show();
		} else if (Integer.parseInt(startAge) >= Integer.parseInt(endAge)) {
			Toast.makeText(GetStartedActivity.this,
					"Please choose a valid age interval.", Toast.LENGTH_SHORT)
					.show();
		} else {
			Intent i = new Intent(GetStartedActivity.this,
					RegistrationActivity.class);
			i.putExtra("who_am_i", whoAmI);
			i.putExtra("min_age", startAge);
			i.putExtra("max_age", endAge);
			i.putExtra("dob", tvDoB.getText().toString());
			i.putExtra("country", countryId);
			i.putExtra("city", selectedCityName);
			startActivity(i);
		}
	}

	private void onClickNext() {
		String dob = tvDoB.getText().toString().trim();
		if (dob == null || dob.isEmpty()) {
			Toast.makeText(GetStartedActivity.this,
					"Please select your birthday.", Toast.LENGTH_SHORT).show();
		} else if (Integer.parseInt(startAge) >= Integer.parseInt(endAge)) {
			Toast.makeText(GetStartedActivity.this,
					"Please choose a valid age interval.", Toast.LENGTH_SHORT)
					.show();
		} else {
			Intent i = new Intent(GetStartedActivity.this,
					RegistrationActivity.class);
			i.putExtra("who_am_i", whoAmI);
			i.putExtra("min_age", startAge);
			i.putExtra("max_age", endAge);
			i.putExtra("dob", tvDoB.getText().toString());
			i.putExtra("country", countryId);
			i.putExtra("city", selectedCityName);
			startActivity(i);
		}
	}

	private class GetCountryList extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!pDialog.isShowing()) {
				pDialog.setMessage("Please wait...");
				pDialog.setCancelable(false);
				pDialog.setIndeterminate(true);
				pDialog.show();
			}
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			String url = Constants.URL_ROOT + "enum/country";
			ServerResponse response = jsonParser.retrieveServerData(
					Constants.REQUEST_TYPE_GET, url, null, null, null);
			if (response.getStatus() == 200) {
				Log.d(">>>><<<<", "success in retrieving user info");
				JSONObject responseObj = response.getjObj();
				return responseObj;
			} else
				return null;
		}

		@Override
		protected void onPostExecute(JSONObject responseObj) {
			super.onPostExecute(responseObj);
			if (responseObj != null) {
				new GetCityList().execute();
				try {
					String status = responseObj.getString("status");
					if (status.equals("OK")) {
						JSONArray countryArray = responseObj
								.getJSONArray("feeds");
						countryList = Country.parseCountry(countryArray);
					} else {
						alert("Invalid token.");
					}
				} catch (JSONException e) {
					alert("Exception.");
					e.printStackTrace();
				}
			}
		}

	}

	private class GetCityList extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(Void... params) {
			String url = Constants.URL_ROOT + "enum/city";
			ServerResponse response = jsonParser.retrieveServerData(
					Constants.REQUEST_TYPE_GET, url, null, null, null);
			if (response.getStatus() == 200) {
				Log.d(">>>><<<<", "success in retrieving user info");
				JSONObject responseObj = response.getjObj();
				return responseObj;
			} else
				return null;
		}

		@Override
		protected void onPostExecute(JSONObject responseObj) {
			super.onPostExecute(responseObj);
			if (responseObj != null) {
				try {
					String status = responseObj.getString("status");
					if (status.equals("OK")) {
						JSONArray ctyArray = responseObj.getJSONArray("feeds");
						cityList = City.parseCity(ctyArray);

						final List<String> cntryList = new ArrayList<String>();

						for (Country country : countryList) {
							cntryList.add(country.getValue());
						}
						// generateSpinner(sCountry, cntryList.toArray(new
						// String[cntryList.size()]));
						generateautocomplete(sCountry1,
								cntryList.toArray(new String[cntryList.size()]));
					} else {
						alert("Invalid token.");
					}
				} catch (JSONException e) {
					alert("Exception.");
					e.printStackTrace();
				}
			}
			if (pDialog.isShowing())
				pDialog.dismiss();
		}
	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(
				GetStartedActivity.this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		bld.create().show();
	}

	public void registerwithfacebook() {

		List<String> permissions = new ArrayList<String>();
		permissions.add("email");
		permissions.add("email");
		permissions.add("user_birthday");
		// openActiveSession(activity, allowLoginUI, callback, permissions)
		session1 = openActiveSession(this, true, new Session.StatusCallback() {
			@SuppressWarnings("deprecation")
			@Override
			public void call(final Session session, SessionState state,
					Exception exception) {

				if (session.isOpened()) {

					Request.executeMeRequestAsync(session,
							new Request.GraphUserCallback() {

								@SuppressWarnings("static-access")
								@Override
								public void onCompleted(GraphUser user,
										Response response) {
									// TODO Auto-generated method stub

									if (user != null) {
										// / graphuser.ge
										graphuser = user;
										String Name = user.getUsername();
										Log.d("result", Name);
										@SuppressWarnings("unused")
										String lastName = user.getLastName();
										String id = user.getId();

										try {
											GraphObject go = graphuser
													.getLocation();
											setAllAutotextview(go);

										} catch (Throwable t) {
											t.printStackTrace();
										}

										// GraphObject{graphObjectClass=GraphLocation,
										// state={"id":"101889586519301","name":"Dhaka, Bangladesh"}}

										/*
										 * if(graphuser.getLocation().getCountry(
										 * )!=null){ String
										 * country=graphuser.getLocation
										 * ().getCountry();
										 * Toast.makeText(GetStartedActivity
										 * .this,country, 1000).show(); for(int
										 * i=0;i<countryList.size();i++){
										 * Log.i("country",
										 * countryList.get(i).getValue());
										 * if(countryList
										 * .get(i).getValue().equalsIgnoreCase
										 * (country)){
										 * 
										 * }
										 * 
										 * }
										 * 
										 * }
										 */

										String email = user
												.getProperty("email")
												.toString();
										Log.d("resonse", Name + "  " + id + " "
												+ email);
										b_facebook.setVisibility(View.GONE);
										String birthdate = user.getBirthday();
										if (birthdate != null) {

											Log.d("birthdate", birthdate);
											String date = birthdate.substring(
													0, birthdate.indexOf('/'));
											String month = birthdate.substring(
													birthdate.indexOf('/') + 1,
													birthdate.lastIndexOf('/'));
											String year = birthdate.substring(birthdate
													.lastIndexOf('/') + 1);
											Log.d("Date", date + "   " + month
													+ "  " + year);
											birthdate = year + "-" + month
													+ "-" + date;
											tvDoB.setText(birthdate);

										}
										String get_gender = (String) user
												.getProperty("gender");
										if (get_gender != null) {
											if (get_gender.equals("male")) {
												sWhoIAm.setSelection(0);
											} else {
												sWhoIAm.setSelection(1);
											}
										}
										session.getActiveSession()
												.closeAndClearTokenInformation();

									} else {
										Log.d("resonse", "not found");

									}
								}
							});

				} else {

					Log.d("resonse", "problem");
				}
			}
		}, permissions);

	}

	private static Session openActiveSession(Activity activity,
			boolean allowLoginUI, Session.StatusCallback callback,
			List<String> permissions) {
		Session.OpenRequest openRequest = new Session.OpenRequest(activity)
				.setPermissions(permissions).setCallback(callback);
		Session session = new Session.Builder(activity).build();
		if (SessionState.CREATED_TOKEN_LOADED.equals(session.getState())
				|| allowLoginUI) {
			Session.setActiveSession(session);
			session.openForRead(openRequest);
			return session;
		}
		return null;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);

	}

}
