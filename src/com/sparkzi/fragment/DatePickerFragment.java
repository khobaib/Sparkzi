package com.sparkzi.fragment;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.sparkzi.GetStartedActivity;

public class DatePickerFragment extends DialogFragment {

	static int mYear;
	static int mMonth;
	static int mDay;
	static String parentActivity;

	public static DatePickerFragment newInstance(Calendar calTime, String parent) {
		DatePickerFragment dialog = new DatePickerFragment();

		mYear = calTime.get(Calendar.YEAR);
		mMonth = calTime.get(Calendar.MONTH);
		mDay = calTime.get(Calendar.DAY_OF_MONTH);
		parentActivity = parent;
		/* I dont really see the purpose of the below */
		Bundle args = new Bundle();
		args.putString("title", "Set Date");
		dialog.setArguments(args);// setArguments can only be called before
									// fragment is attached to an activity, so
									// right after the fragment is created

		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		// final Calendar c = Calendar.getInstance();
		// int year = c.get(Calendar.YEAR);
		// int month = c.get(Calendar.MONTH);
		// int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		// if(parentActivity.equals("get_started"))
		return new DatePickerDialog(getActivity(), (GetStartedActivity) getActivity(), mYear, mMonth, mDay);
		// else // "profile"
		// return new DatePickerDialog(getActivity(),
		// (ProfileActivity)getActivity(), mYear, mMonth, mDay);
	}

}
