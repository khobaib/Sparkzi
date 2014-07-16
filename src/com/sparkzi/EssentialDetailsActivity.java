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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.bugsense.trace.BugSenseHandler;
import com.sparkzi.db.SparkziDatabase;
import com.sparkzi.fragment.ProfileEssentialsfragment;
import com.sparkzi.model.Essential;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;
import com.sparkzi.utility.Utility;

public class EssentialDetailsActivity extends Activity implements OnItemSelectedListener {

	private static final String TAG = EssentialDetailsActivity.class.getSimpleName();

	EditText etProfession, etHometown, etLanguages;

	String education, ethnicity, diet, drinks, smokes, religion, kids, politics, sign, profession, hometown, languages;

	Spinner sEducation, sEthnicity, sDiet, sDrinks, sSmokes, sReligion, sKids, sPolitics, sSign;
	int[] spinnerIndex = new int[Constants.ESSENTIAL_STATIC_FIELD_COUNT];
	int[] selectedIds = new int[Constants.ESSENTIAL_STATIC_FIELD_COUNT];
	String[] selectedValues = new String[Constants.ESSENTIAL_STATIC_FIELD_COUNT];

	List<Essential>[] essentialList;
	List<String>[] essentialValueList;

	JsonParser jsonParser;
	ProgressDialog pDialog;
	SparkziApplication appInstance;

	String token;

	int fromActivity;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(this, "2c5ced14");
		setContentView(R.layout.essential_details);

		// Bundle b = getIntent().getExtras();
		fromActivity = getIntent().getExtras().getInt(Constants.FROM_ACTIVITY, 0);
		Log.e(TAG, "fromActivity = " + fromActivity);

		jsonParser = new JsonParser();
		pDialog = new ProgressDialog(EssentialDetailsActivity.this);
		appInstance = (SparkziApplication) getApplication();

		UserCred userCred = appInstance.getUserCred();
		token = userCred.getToken();

		sEducation = (Spinner) findViewById(R.id.s_education);
		sEthnicity = (Spinner) findViewById(R.id.s_ethnicity);
		sDiet = (Spinner) findViewById(R.id.s_diet);
		sDrinks = (Spinner) findViewById(R.id.s_drinks);
		sSmokes = (Spinner) findViewById(R.id.s_smokes);
		sReligion = (Spinner) findViewById(R.id.s_religion);
		sKids = (Spinner) findViewById(R.id.s_kids);
		sPolitics = (Spinner) findViewById(R.id.s_politics);
		sSign = (Spinner) findViewById(R.id.s_sign);

		sEducation.setOnItemSelectedListener(EssentialDetailsActivity.this);
		sEthnicity.setOnItemSelectedListener(EssentialDetailsActivity.this);
		sDiet.setOnItemSelectedListener(EssentialDetailsActivity.this);
		sDrinks.setOnItemSelectedListener(EssentialDetailsActivity.this);
		sSmokes.setOnItemSelectedListener(EssentialDetailsActivity.this);
		sReligion.setOnItemSelectedListener(EssentialDetailsActivity.this);
		sKids.setOnItemSelectedListener(EssentialDetailsActivity.this);
		sPolitics.setOnItemSelectedListener(EssentialDetailsActivity.this);
		sSign.setOnItemSelectedListener(EssentialDetailsActivity.this);

		etProfession = (EditText) findViewById(R.id.et_profession);
		etHometown = (EditText) findViewById(R.id.et_hometown);
		etLanguages = (EditText) findViewById(R.id.et_languages);
		// (ArrayList<Individual>[])new ArrayList[4];

		essentialList = (ArrayList<Essential>[]) new ArrayList[Constants.ESSENTIAL_STATIC_FIELD_COUNT];
		essentialValueList = (ArrayList<String>[]) new ArrayList[Constants.ESSENTIAL_STATIC_FIELD_COUNT];
		for (int i = 0; i < Constants.ESSENTIAL_STATIC_FIELD_COUNT; i++) {
			essentialList[i] = new ArrayList<Essential>();
			spinnerIndex[i] = 0;
			selectedIds[i] = 0;
			selectedValues[i] = null;
		}

		new GetEssentialList().execute();
	}

	public void onClickDone(View v) {

		for (int i = 0; i < Constants.ESSENTIAL_STATIC_FIELD_COUNT; i++) {
			selectedIds[i] = getEssentialIdFromValue(essentialList[i], selectedValues[i]);
		}

		profession = etProfession.getText().toString().trim();
		hometown = etHometown.getText().toString().trim();
		languages = etLanguages.getText().toString().trim();

		new SendEssentialUpdateRequest().execute();
	}

	private class GetEssentialList extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setMessage("Please wait...");
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			String url = Constants.URL_ROOT + "enum/essentials";
			ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_GET, url, null, null, null);
			if (response.getStatus() == 200) {
				Log.d(">>>><<<<", "success in retrieving user info");
				JSONObject responseObj = response.getjObj();
				return responseObj;
			} else
				return null;
		}

		@Override
		protected void onPostExecute(JSONObject responseObj) {
			// TODO Auto-generated method stub
			super.onPostExecute(responseObj);
			if (responseObj != null) {
				try {
					String status = responseObj.getString("status");
					if (status.equals("OK")) {
						JSONObject feedObject = responseObj.getJSONObject("feeds");

						SparkziDatabase dbInstance = new SparkziDatabase(EssentialDetailsActivity.this);
						dbInstance.open();
						for (int i = 0; i < Constants.ESSENTIAL_STATIC_FIELD_COUNT; i++) {
							String essentialField = Utility.ESSENTIAL_TEXTS[i];
							JSONArray essentialArray = feedObject.getJSONArray(essentialField);
							List<Essential> thisEssentialList = Essential.parseStaticEssentialList(essentialArray);

							for (Essential essential : thisEssentialList)
								dbInstance.insertOrupdateEssential(essential, i);

							essentialList[i] = thisEssentialList;
						}
						dbInstance.close();

						for (int i = 0; i < Constants.ESSENTIAL_STATIC_FIELD_COUNT; i++) {
							List<String> valueList = getEssentialValues(essentialList[i]);
							essentialValueList[i] = valueList;
						}
						generateSpinner(sEducation,
								essentialValueList[Constants.ESSENTIAL_EDUCATION_INDEX]
										.toArray(new String[essentialValueList[Constants.ESSENTIAL_EDUCATION_INDEX]
												.size()]));
						generateSpinner(sEthnicity,
								essentialValueList[Constants.ESSENTIAL_ETHNICITY_INDEX]
										.toArray(new String[essentialValueList[Constants.ESSENTIAL_ETHNICITY_INDEX]
												.size()]));
						generateSpinner(sDiet,
								essentialValueList[Constants.ESSENTIAL_DIET_INDEX]
										.toArray(new String[essentialValueList[Constants.ESSENTIAL_DIET_INDEX].size()]));
						generateSpinner(
								sDrinks,
								essentialValueList[Constants.ESSENTIAL_DRINKS_INDEX]
										.toArray(new String[essentialValueList[Constants.ESSENTIAL_DRINKS_INDEX].size()]));
						generateSpinner(
								sSmokes,
								essentialValueList[Constants.ESSENTIAL_SMOKES_INDEX]
										.toArray(new String[essentialValueList[Constants.ESSENTIAL_SMOKES_INDEX].size()]));
						generateSpinner(sReligion,
								essentialValueList[Constants.ESSENTIAL_RELIGION_INDEX]
										.toArray(new String[essentialValueList[Constants.ESSENTIAL_RELIGION_INDEX]
												.size()]));
						generateSpinner(sKids,
								essentialValueList[Constants.ESSENTIAL_KIDS_INDEX]
										.toArray(new String[essentialValueList[Constants.ESSENTIAL_KIDS_INDEX].size()]));
						generateSpinner(sPolitics,
								essentialValueList[Constants.ESSENTIAL_POLITICS_INDEX]
										.toArray(new String[essentialValueList[Constants.ESSENTIAL_POLITICS_INDEX]
												.size()]));
						generateSpinner(sSign,
								essentialValueList[Constants.ESSENTIAL_SIGN_INDEX]
										.toArray(new String[essentialValueList[Constants.ESSENTIAL_SIGN_INDEX].size()]));

						setInitialIndex();

					} else {
						alert("Invalid token.", false);
					}
				} catch (JSONException e) {
					alert("Exception.", false);
					e.printStackTrace();
				}
			}
			if (pDialog.isShowing())
				pDialog.dismiss();
		}
	}

	private void setInitialIndex() {

		if (fromActivity == Constants.PARENT_ACTIVITY_PROFILE) {
			for (int i = 0; i < Constants.ESSENTIAL_STATIC_FIELD_COUNT; i++) {
				selectedIds[i] = ProfileEssentialsfragment.eList.get(i).getId();
				selectedValues[i] = ProfileEssentialsfragment.eList.get(i).getValue();
				spinnerIndex[i] = getEssentialIndexFromValue(essentialList[i], selectedValues[i]);
			}

			etProfession.setText(ProfileEssentialsfragment.eList.get(Constants.ESSENTIAL_PROFESSION_INDEX).getValue());
			etHometown.setText(ProfileEssentialsfragment.eList.get(Constants.ESSENTIAL_HOMETOWN_INDEX).getValue());
			etLanguages.setText(ProfileEssentialsfragment.eList.get(Constants.ESSENTIAL_LANGUAGES_INDEX).getValue());

		}

		sEducation.setSelection(spinnerIndex[Constants.ESSENTIAL_EDUCATION_INDEX]);
		sEthnicity.setSelection(spinnerIndex[Constants.ESSENTIAL_ETHNICITY_INDEX]);
		sDiet.setSelection(spinnerIndex[Constants.ESSENTIAL_DIET_INDEX]);
		sDrinks.setSelection(spinnerIndex[Constants.ESSENTIAL_DRINKS_INDEX]);
		sSmokes.setSelection(spinnerIndex[Constants.ESSENTIAL_SMOKES_INDEX]);
		sReligion.setSelection(spinnerIndex[Constants.ESSENTIAL_RELIGION_INDEX]);
		sKids.setSelection(spinnerIndex[Constants.ESSENTIAL_KIDS_INDEX]);
		sPolitics.setSelection(spinnerIndex[Constants.ESSENTIAL_POLITICS_INDEX]);
		sSign.setSelection(spinnerIndex[Constants.ESSENTIAL_SIGN_INDEX]);

	}

	private List<String> getEssentialValues(List<Essential> thisEssentialList) {
		List<String> esenList = new ArrayList<String>();

		for (Essential essential : thisEssentialList) {
			esenList.add(essential.getValue());
		}
		return esenList;
	}

	private int getEssentialIdFromValue(List<Essential> thisEssentialList, String value) {
		for (Essential essential : thisEssentialList) {
			if (essential.getValue().equals(value))
				return essential.getId();
		}
		return -1;
	}

	private int getEssentialIndexFromValue(List<Essential> thisEssentialList, String value) {
		int size = thisEssentialList.size();
		for (int i = 0; i < size; i++) {
			if (thisEssentialList.get(i).getValue().equals(value))
				return i;
		}
		return 0; // by-default the first position of the spinner will be
					// selected
	}

	private void generateSpinner(Spinner spinner, String[] arrayToSpinner) {
		ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(EssentialDetailsActivity.this,
				R.layout.my_simple_spinner_item, arrayToSpinner);
		spinner.setAdapter(myAdapter);
		myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

	}

	public class SendEssentialUpdateRequest extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setMessage("Please wait while app is updating data...");
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			String url = Constants.URL_ROOT + "user";

			try {
				JSONObject essentialObj = new JSONObject();
				essentialObj.put("education", selectedIds[Constants.ESSENTIAL_EDUCATION_INDEX]);
				essentialObj.put("ethnicity", selectedIds[Constants.ESSENTIAL_ETHNICITY_INDEX]);
				essentialObj.put("politics", selectedIds[Constants.ESSENTIAL_POLITICS_INDEX]);
				essentialObj.put("diet", selectedIds[Constants.ESSENTIAL_DIET_INDEX]);
				essentialObj.put("drinks", selectedIds[Constants.ESSENTIAL_DRINKS_INDEX]);
				essentialObj.put("smokes", selectedIds[Constants.ESSENTIAL_SMOKES_INDEX]);
				essentialObj.put("religion", selectedIds[Constants.ESSENTIAL_RELIGION_INDEX]);
				essentialObj.put("kids", selectedIds[Constants.ESSENTIAL_KIDS_INDEX]);
				essentialObj.put("starsign", selectedIds[Constants.ESSENTIAL_SIGN_INDEX]);
				essentialObj.put("hometown", hometown);
				essentialObj.put("profession", profession);
				essentialObj.put("languages", languages);

				String reqData = essentialObj.toString();
				ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url, null,
						reqData, token);
				if (response.getStatus() == 200) {
					JSONObject responseObj = response.getjObj();
					return responseObj;
				} else {
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
			if (pDialog.isShowing())
				pDialog.dismiss();
			if (responseObj != null) {
				try {
					String status = responseObj.getString("status");
					if (status.equals("OK")) {

						if (fromActivity == Constants.PARENT_ACTIVITY_PROFILE) {
							finish();
						} else {
							Intent i = new Intent(EssentialDetailsActivity.this, MainActivity.class);
							i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(i);
							finish();
						}

					} else {
						alert("Update failed.", false);
					}
				} catch (JSONException e) {
					alert("Update failed.", false);
					e.printStackTrace();
				}

			} else {
				alert("Update failed.", false);
			}
		}
	}

	void alert(String message, final Boolean success) {
		AlertDialog.Builder bld = new AlertDialog.Builder(EssentialDetailsActivity.this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (success)
					finish();

			}
		});
		bld.create().show();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
		// Log.d(TAG, "id = " + parent.getId());
		String selectedValue = (String) parent.getItemAtPosition(position);
		switch (parent.getId()) {
		case R.id.s_education:
			spinnerIndex[Constants.ESSENTIAL_EDUCATION_INDEX] = position;
			selectedValues[Constants.ESSENTIAL_EDUCATION_INDEX] = selectedValue;
			Log.d(TAG, "education");
			break;
		case R.id.s_ethnicity:
			spinnerIndex[Constants.ESSENTIAL_ETHNICITY_INDEX] = position;
			selectedValues[Constants.ESSENTIAL_ETHNICITY_INDEX] = selectedValue;
			Log.d(TAG, "ethnicity");
			break;
		case R.id.s_diet:
			spinnerIndex[Constants.ESSENTIAL_DIET_INDEX] = position;
			selectedValues[Constants.ESSENTIAL_DIET_INDEX] = selectedValue;
			Log.d(TAG, "diet");
			break;
		case R.id.s_drinks:
			spinnerIndex[Constants.ESSENTIAL_DRINKS_INDEX] = position;
			selectedValues[Constants.ESSENTIAL_DRINKS_INDEX] = selectedValue;
			Log.d(TAG, "drinks");
			break;
		case R.id.s_smokes:
			spinnerIndex[Constants.ESSENTIAL_SMOKES_INDEX] = position;
			selectedValues[Constants.ESSENTIAL_SMOKES_INDEX] = selectedValue;
			Log.d(TAG, "smokes");
			break;
		case R.id.s_religion:
			spinnerIndex[Constants.ESSENTIAL_RELIGION_INDEX] = position;
			selectedValues[Constants.ESSENTIAL_RELIGION_INDEX] = selectedValue;
			Log.d(TAG, "religion");
			break;
		case R.id.s_kids:
			spinnerIndex[Constants.ESSENTIAL_KIDS_INDEX] = position;
			selectedValues[Constants.ESSENTIAL_KIDS_INDEX] = selectedValue;
			Log.d(TAG, "kids");
			break;
		case R.id.s_politics:
			spinnerIndex[Constants.ESSENTIAL_POLITICS_INDEX] = position;
			selectedValues[Constants.ESSENTIAL_POLITICS_INDEX] = selectedValue;
			Log.d(TAG, "politics");
			break;
		case R.id.s_sign:
			spinnerIndex[Constants.ESSENTIAL_SIGN_INDEX] = position;
			selectedValues[Constants.ESSENTIAL_SIGN_INDEX] = selectedValue;
			Log.d(TAG, "sign");
			break;
		default:
			break;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

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

}
