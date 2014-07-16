package com.sparkzi.model;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.sparkzi.utility.Constants;
import com.sparkzi.utility.Utility;

public class Essential_deprecated {

	private String essentialText;
	private int ansIndex;
	private String answerText;

	public Essential_deprecated() {
		// TODO Auto-generated constructor stub
	}

	public Essential_deprecated(String essentialText, int ansIndex, String answerText) {
		this.essentialText = essentialText;
		this.ansIndex = ansIndex;
		this.answerText = answerText;
	}

	public static List<Essential_deprecated> parseEssentialList(JSONObject userObj) {
		Essential_deprecated[] eList = new Essential_deprecated[Utility.ESSENTIAL_TEXTS.length];

		for (int i = 0; i < Utility.ESSENTIAL_TEXTS.length; i++) {
			eList[i] = new Essential_deprecated();
			eList[i].setEssentialText(Utility.ESSENTIAL_TEXTS[i]);
		}

		try {
			int educationIndex = userObj.getInt(Utility.ESSENTIAL_TEXTS[Constants.ESSENTIAL_EDUCATION_INDEX]);
			eList[Constants.ESSENTIAL_EDUCATION_INDEX].setAnsIndex(educationIndex);
			eList[Constants.ESSENTIAL_EDUCATION_INDEX].setAnswerText(Utility.education_spinner_options[educationIndex]);

			int ethnicityIndex = userObj.getInt(Utility.ESSENTIAL_TEXTS[Constants.ESSENTIAL_ETHNICITY_INDEX]);
			eList[Constants.ESSENTIAL_ETHNICITY_INDEX].setAnsIndex(ethnicityIndex);
			eList[Constants.ESSENTIAL_ETHNICITY_INDEX].setAnswerText(Utility.ethnicity_spinner_options[ethnicityIndex]);

			int dietIndex = userObj.getInt(Utility.ESSENTIAL_TEXTS[Constants.ESSENTIAL_DIET_INDEX]);
			eList[Constants.ESSENTIAL_DIET_INDEX].setAnsIndex(dietIndex);
			eList[Constants.ESSENTIAL_DIET_INDEX].setAnswerText(Utility.diet_spinner_options[dietIndex]);

			int drinksIndex = userObj.getInt(Utility.ESSENTIAL_TEXTS[Constants.ESSENTIAL_DRINKS_INDEX]);
			eList[Constants.ESSENTIAL_DRINKS_INDEX].setAnsIndex(drinksIndex);
			eList[Constants.ESSENTIAL_DRINKS_INDEX].setAnswerText(Utility.drinks_spinner_options[drinksIndex]);

			int smokesIndex = userObj.getInt(Utility.ESSENTIAL_TEXTS[Constants.ESSENTIAL_SMOKES_INDEX]);
			eList[Constants.ESSENTIAL_SMOKES_INDEX].setAnsIndex(smokesIndex);
			eList[Constants.ESSENTIAL_SMOKES_INDEX].setAnswerText(Utility.smokes_spinner_options[smokesIndex]);

			// String religion =
			// userObj.getString(Utility.ESSENTIAL_TEXTS[Constants.ESSENTIAL_RELIGION_INDEX]);
			// eList[Constants.ESSENTIAL_RELIGION_INDEX].setAnsIndex(0);
			// eList[Constants.ESSENTIAL_RELIGION_INDEX].setAnswerText(religion);

			int religionIndex = userObj.getInt(Utility.ESSENTIAL_TEXTS[Constants.ESSENTIAL_RELIGION_INDEX]);
			eList[Constants.ESSENTIAL_RELIGION_INDEX].setAnsIndex(religionIndex);
			eList[Constants.ESSENTIAL_RELIGION_INDEX].setAnswerText(Utility.religion_spinner_options[religionIndex]);

			int kidsIndex = userObj.getInt(Utility.ESSENTIAL_TEXTS[Constants.ESSENTIAL_KIDS_INDEX]);
			eList[Constants.ESSENTIAL_KIDS_INDEX].setAnsIndex(kidsIndex);
			eList[Constants.ESSENTIAL_KIDS_INDEX].setAnswerText(Utility.kids_spinner_options[kidsIndex]);

			int politicsIndex = userObj.getInt(Utility.ESSENTIAL_TEXTS[Constants.ESSENTIAL_POLITICS_INDEX]);
			eList[Constants.ESSENTIAL_POLITICS_INDEX].setAnsIndex(politicsIndex);
			eList[Constants.ESSENTIAL_POLITICS_INDEX].setAnswerText(Utility.politics_spinner_options[politicsIndex]);

			int signIndex = userObj.getInt(Utility.ESSENTIAL_TEXTS[Constants.ESSENTIAL_SIGN_INDEX]);
			eList[Constants.ESSENTIAL_SIGN_INDEX].setAnsIndex(signIndex);
			eList[Constants.ESSENTIAL_SIGN_INDEX].setAnswerText(Utility.sign_spinner_options[signIndex]);

			String profession = userObj.getString(Utility.ESSENTIAL_TEXTS[Constants.ESSENTIAL_PROFESSION_INDEX]);
			eList[Constants.ESSENTIAL_PROFESSION_INDEX].setAnsIndex(0);
			eList[Constants.ESSENTIAL_PROFESSION_INDEX].setAnswerText(profession);

			String hometown = userObj.getString(Utility.ESSENTIAL_TEXTS[Constants.ESSENTIAL_HOMETOWN_INDEX]);
			eList[Constants.ESSENTIAL_HOMETOWN_INDEX].setAnsIndex(0);
			eList[Constants.ESSENTIAL_HOMETOWN_INDEX].setAnswerText(hometown);

			String languages = userObj.getString(Utility.ESSENTIAL_TEXTS[Constants.ESSENTIAL_LANGUAGES_INDEX]);
			eList[Constants.ESSENTIAL_LANGUAGES_INDEX].setAnsIndex(0);
			eList[Constants.ESSENTIAL_LANGUAGES_INDEX].setAnswerText(languages);

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return Arrays.asList(eList);
	}

	public String getEssentialText() {
		return essentialText;
	}

	public void setEssentialText(String essentialText) {
		this.essentialText = essentialText;
	}

	public int getAnsIndex() {
		return ansIndex;
	}

	public void setAnsIndex(int ansIndex) {
		this.ansIndex = ansIndex;
	}

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}
}
