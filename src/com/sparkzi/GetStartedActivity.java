package com.sparkzi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.sparkzi.fragment.DatePickerFragment;
import com.sparkzi.model.City;
import com.sparkzi.model.Country;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.Utility;

public class GetStartedActivity extends FragmentActivity implements OnDateSetListener{

    Spinner sWhoIAm, sStartAge, sEndAge, sCountry, sCity;
    TextView tvDoB;

    String whoAmI, startAge, endAge, selectedCountryName, selectedCityName;
    Calendar calendar;

    JsonParser jsonParser;
    ProgressDialog pDialog;

    List<Country> countryList;
    List<City> cityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(this, "3ec7fda7");
        setContentView(R.layout.get_started);

        jsonParser = new JsonParser();
        pDialog = new ProgressDialog(GetStartedActivity.this);
        calendar = Calendar.getInstance();

        tvDoB = (TextView) findViewById(R.id.tv_dob);
        tvDoB.setText(String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
        
        sWhoIAm = (Spinner) findViewById(R.id.s_who_i_am);
        generateSpinner(sWhoIAm, Utility.WHO_I_AM);
        sWhoIAm.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                whoAmI = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        sStartAge = (Spinner) findViewById(R.id.s_start_age);
        generateSpinner(sStartAge, Utility.AGE_RANGE);
        sStartAge.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                startAge = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        sEndAge = (Spinner) findViewById(R.id.s_end_age);
        generateSpinner(sEndAge, Utility.AGE_RANGE);
        sEndAge.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                endAge = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        sCity = (Spinner) findViewById(R.id.s_city);
        sCity.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                selectedCityName = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        sCountry = (Spinner) findViewById(R.id.s_country);
        sCountry.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                selectedCountryName = (String)parent.getItemAtPosition(position);
                List<String> cityList = getCityList(countryList.get(position).getId());
                Log.e(">>>", "city list size = " + cityList);
                generateSpinner(sCity, cityList.toArray(new String[cityList.size()]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        new GetCountryList().execute();
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
    

    private List<String> getCityList(int countryId){
        List<String> ctyList = new ArrayList<String>();

        for(City city : cityList){
            if(city.getCountryId() == countryId)
                ctyList.add(city.getvalue());
        }
        return ctyList;
    }

    private int getCityId(String cityName){
        for(City city : cityList){
            if(city.getvalue().equals(cityName))
                return city.getId();
        }
        return -1;
    }


    private void generateSpinner(Spinner spinner, String[] arrayToSpinner) {
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(
                GetStartedActivity.this, R.layout.my_simple_spinner_item, arrayToSpinner);
        spinner.setAdapter(myAdapter);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }




    public void onClickCalendar(View v){
        DialogFragment newFragment = new DatePickerFragment().newInstance(calendar, "get_started");
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        tvDoB.setText(String.format("%04d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth));
        calendar.set(year, monthOfYear, dayOfMonth);
        //        selectedDate = DoB.getText().toString();
    }

    public void onClickNext(View v){
        String dob = tvDoB.getText().toString().trim();
        if(dob == null || dob.isEmpty()){
            Toast.makeText(GetStartedActivity.this, "Please select your birthday.", Toast.LENGTH_SHORT).show();
        }
        else if(Integer.parseInt(startAge) >= Integer.parseInt(endAge)){
            Toast.makeText(GetStartedActivity.this, "Please choose a valid age interval.", Toast.LENGTH_SHORT).show();
        }
        else{
            Intent i = new Intent(GetStartedActivity.this, RegistrationActivity.class);
            i.putExtra("who_am_i", whoAmI);
            i.putExtra("min_age", startAge);
            i.putExtra("max_age", endAge);
            i.putExtra("dob", tvDoB.getText().toString());
            i.putExtra("country", selectedCountryName);
            i.putExtra("city", selectedCityName);
            startActivity(i);
        }
    }






    private class GetCountryList extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!pDialog.isShowing()){
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.setIndeterminate(true);
                pDialog.show();
            }
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            String url = Constants.URL_ROOT + "enum/country";
            ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_GET, url,
                    null, null, null);
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
                new GetCityList().execute();
                try {
                    String status = responseObj.getString("status");
                    if(status.equals("OK")){
                        JSONArray ctyArray = responseObj.getJSONArray("feeds");
                        countryList = Country.parseCountry(ctyArray);
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



    private class GetCityList extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... params) {
            String url = Constants.URL_ROOT + "enum/city";
            ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_GET, url,
                    null, null, null);
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
                        JSONArray ctyArray = responseObj.getJSONArray("feeds");
                        cityList = City.parseCity(ctyArray);
                        
                        final List<String> cntryList = new ArrayList<String>();
                        for(Country country : countryList){
                            cntryList.add(country.getValue());
                        }
                        generateSpinner(sCountry, cntryList.toArray(new String[cntryList.size()]));
                    }
                    else{
                        alert("Invalid token.");
                    }
                } catch (JSONException e) {
                    alert("Exception.");
                    e.printStackTrace();
                }
            }
            if(pDialog.isShowing())
                pDialog.dismiss();
        }
    }





    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(GetStartedActivity.this);
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
