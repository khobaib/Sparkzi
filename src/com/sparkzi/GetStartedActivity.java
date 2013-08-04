package com.sparkzi;

import java.util.Calendar;

import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.sparkzi.fragment.DatePickerFragment;
import com.sparkzi.utility.Utility;

public class GetStartedActivity extends FragmentActivity implements OnDateSetListener{
    
    Spinner sWhoIAm, sStartAge, sEndAge, sCOuntry, sCity;
    TextView tvDoB;
    
    String whoAmI, startAge, endAge, countryName, cityName;
    Calendar calendar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_started);
        
        calendar = Calendar.getInstance();
        cityName = "Sydney";        // dummy data for now.
        
        tvDoB = (TextView) findViewById(R.id.tv_dob);
        
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
        
        sCOuntry = (Spinner) findViewById(R.id.s_country);
        generateSpinner(sCOuntry, Utility.COUNTRY_LIST);
        sCOuntry.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                countryName = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }
    
    
    private void generateSpinner(Spinner spinner, String[] arrayToSpinner) {
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(
                GetStartedActivity.this, android.R.layout.simple_spinner_item, arrayToSpinner);
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
        Intent i = new Intent(GetStartedActivity.this, RegistrationActivity.class);
        i.putExtra("who_am_i", whoAmI);
        i.putExtra("min_age", startAge);
        i.putExtra("max_age", endAge);
        i.putExtra("dob", tvDoB.getText().toString());
        i.putExtra("country", countryName);
        i.putExtra("city", cityName);
        startActivity(i);
    }

}
