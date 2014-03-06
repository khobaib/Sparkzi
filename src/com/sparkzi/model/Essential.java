package com.sparkzi.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sparkzi.db.SparkziDatabase;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.Utility;

public class Essential {
	private int id;
	private String value;

	public Essential() {
		// TODO Auto-generated constructor stub
	}

	public Essential(int id, String value) {
		this.id = id;
		this.value = value;
	}

	public static List<Essential> parseStaticEssentialList(
			JSONArray essentialArray) {
		List<Essential> essentialList = new ArrayList<Essential>();

		GsonBuilder gsonb = new GsonBuilder();
		Gson gson = gsonb.create();

		try {
			for (int i = 0; i < essentialArray.length(); i++) {

				JSONObject thisEssential = essentialArray.getJSONObject(i);
				if (thisEssential != null) {
					String jsonString = thisEssential.toString();
					Essential essential = gson.fromJson(jsonString,
							Essential.class);
					essentialList.add(essential);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return essentialList;
	}

	public static List<Essential> parseUserEssential(JSONObject userObj,
			Context context) {
		Essential[] eList = new Essential[Utility.ESSENTIAL_TEXTS.length];

		try {
			SparkziDatabase dbInstance = new SparkziDatabase(context);
			dbInstance.open();
			for (int i = 0; i < Constants.ESSENTIAL_STATIC_FIELD_COUNT; i++) {
				int id = userObj.getInt(Utility.ESSENTIAL_TEXTS[i]);
				Essential essential = dbInstance.retrieveEssential(i, id);
				eList[i] = essential;
			}
			dbInstance.close();

			// for last 3 element
			for (int i = Constants.ESSENTIAL_STATIC_FIELD_COUNT; i < Utility.ESSENTIAL_TEXTS.length; i++) {
				String value = userObj.getString(Utility.ESSENTIAL_TEXTS[i]);
				Essential essential = new Essential(0, value);
				eList[i] = essential;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return Arrays.asList(eList);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
