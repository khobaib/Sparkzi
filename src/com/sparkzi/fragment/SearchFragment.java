package com.sparkzi.fragment;

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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.sparkzi.GetStartedActivity;
import com.sparkzi.R;
import com.sparkzi.adapter.SearchAdapter;
import com.sparkzi.model.Country;
import com.sparkzi.model.Favorite;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;
import com.sparkzi.utility.Utility;

public class SearchFragment extends Fragment {

	@SuppressWarnings("unused")
	private static final String TAG = SearchFragment.class.getSimpleName();
	private Activity activity;

	Spinner sStartAge, sEndAge;
	Button bSearch;
	ListView lvSearchResult;
	AutoCompleteTextView sCountry1;
	JsonParser jsonParser;
	ProgressDialog pDialog;

	SearchAdapter searchAdapter;

	List<Country> countryList;

	int oppositeGender, selectedMinAge, selectedMaxAge, selectedCountryId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_search, null);
		sStartAge = (Spinner) view.findViewById(R.id.s_start_age);
		sStartAge.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				selectedMinAge = Integer.parseInt((String) parent
						.getItemAtPosition(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		sEndAge = (Spinner) view.findViewById(R.id.s_end_age);
		sEndAge.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				selectedMaxAge = Integer.parseInt((String) parent
						.getItemAtPosition(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		sCountry1 =  (AutoCompleteTextView) view.findViewById(R.id.s_country);
		
		  sCountry1.setThreshold(1);
	        sCountry1.setOnItemClickListener(new OnItemClickListener() {

	            @Override
	            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            		//selectedCountryId = countryList.get(position).getId();
	            		 String selection = (String) parent.getItemAtPosition(position);
	 			        int pos = -1;

	 			        for (int i = 0; i < countryList.size(); i++) {
	 			            if (countryList.get(i).getValue().equals(selection)) {
	 			                pos = i;
	 			                break;
	 			            }
	 			        }
	 			       selectedCountryId = countryList.get(pos).getId();
	            	}
	        });
		

		bSearch = (Button) view.findViewById(R.id.b_search);
		bSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (selectedMinAge >= selectedMaxAge) {
					Toast.makeText(activity,
							"Please choose a valid age interval.",
							Toast.LENGTH_SHORT).show();
				} else {
					UserCred userCred = ((SparkziApplication) activity
							.getApplication()).getUserCred();
					oppositeGender = (userCred.getGender() == 1) ? 2 : 1;
					new GetSearchResult().execute();
				}

			}
		});

		lvSearchResult = (ListView) view.findViewById(R.id.lv_search_list);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		activity = getActivity();
		if (activity != null) {
			jsonParser = new JsonParser();
			pDialog = new ProgressDialog(activity);

			searchAdapter = new SearchAdapter(activity, null);
			lvSearchResult.setAdapter(searchAdapter);
			// lvSearchResult.setVisibility(View.GONE);

			new GetCountryList().execute();

			// generateSpinner(sShowMe, Utility.SHOW_ME);
			generateSpinner(sStartAge, Utility.AGE_RANGE);
			generateSpinner(sEndAge, Utility.AGE_RANGE);

		}
	}

	private class GetSearchResult extends AsyncTask<Void, Void, JSONObject> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (!pDialog.isShowing()) {
				pDialog.setMessage("Searching...");
				pDialog.setCancelable(false);
				pDialog.setIndeterminate(true);
				pDialog.show();
			}
		}

		@Override
		protected JSONObject doInBackground(Void... params) {
			String url = Constants.URL_ROOT + "user/search" + "/gender/"
					+ oppositeGender + "/country/" + selectedCountryId
					+ "/minage/" + selectedMinAge + "/maxage/" + selectedMaxAge;

			UserCred userCred = ((SparkziApplication) activity.getApplication())
					.getUserCred();
			String token = userCred.getToken();

			ServerResponse response = jsonParser.retrieveServerData(
					Constants.REQUEST_TYPE_GET, url, null, null, token);
			if (response.getStatus() == 200) {
				Log.d(">>>><<<<", "success in retrieving country info");
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
						JSONArray searchArray = responseObj
								.getJSONArray("result");
						List<Favorite> peopleList = Favorite
								.parseFavorite(searchArray);
						Log.e("???????", "search count = " + peopleList.size());

						searchAdapter.setData(peopleList);
						searchAdapter.notifyDataSetChanged();
						// searchAdapter = new SearchAdapter(activity,
						// peopleList);
						// lvSearchResult.setAdapter(searchAdapter);
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
				Log.d(">>>><<<<", "success in retrieving country info");
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
						JSONArray ctyArray = responseObj.getJSONArray("feeds");
						countryList = Country.parseCountry(ctyArray);

						List<String> cntryList = new ArrayList<String>();
						for (Country country : countryList) {
							cntryList.add(country.getValue());
						}
				        generateautocomplete(sCountry1, cntryList.toArray(new String[cntryList.size()]));
		                 
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
	
	private void generateautocomplete(AutoCompleteTextView autextview,String[] arrayToSpinner){
		   ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(
	               getActivity(),  R.layout.my_autocomplete_text_style, arrayToSpinner);
		   autextview.setAdapter(myAdapter);
		   
	   }

	private void generateSpinner(Spinner spinner, String[] arrayToSpinner) {
		ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(activity,
				R.layout.my_simple_spinner_item, arrayToSpinner);
		spinner.setAdapter(myAdapter);
		myAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(activity);
		bld.setMessage(message);
		bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		bld.create().show();
	}

}
