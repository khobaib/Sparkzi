package com.sparkzi.model;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.sparkzi.utility.Constants;
import com.sparkzi.utility.Utility;

public class Essential {

    private String essentialText;
    private int ansIndex;
    private String answerText;

    public Essential() {
        // TODO Auto-generated constructor stub
    }

    public Essential(String essentialText, int ansIndex, String answerText) {
        this.essentialText = essentialText;
        this.ansIndex = ansIndex;
        this.answerText = answerText;
    }

    public static List<Essential> parseEssentialList(JSONObject userObj){
        Essential[] eList = new Essential[Utility.ESSENTIAL_TEXTS.length];

        for(int i = 0; i < Utility.ESSENTIAL_TEXTS.length; i++){
            eList[i] = new Essential();
            eList[i].setEssentialText(Utility.ESSENTIAL_TEXTS[i]);
        }

        try {
            int educationIndex = Integer.parseInt(userObj.getString("education"));
            eList[Constants.ESSENTIAL_EDUCATION_INDEX].setAnsIndex(educationIndex);
            eList[Constants.ESSENTIAL_EDUCATION_INDEX].setAnswerText(Utility.education_spinner_options[educationIndex]);


            int ethnicityIndex = Integer.parseInt(userObj.getString("ethnicity"));
            eList[Constants.ESSENTIAL_ETHNICITY_INDEX].setAnsIndex(ethnicityIndex);
            eList[Constants.ESSENTIAL_ETHNICITY_INDEX].setAnswerText(Utility.ethnicity_spinner_options[ethnicityIndex]);

            int dietIndex = Integer.parseInt(userObj.getString("diet"));
            eList[Constants.ESSENTIAL_DIET_INDEX].setAnsIndex(dietIndex);
            eList[Constants.ESSENTIAL_DIET_INDEX].setAnswerText(Utility.diet_spinner_options[dietIndex]);

            int drinksIndex = Integer.parseInt(userObj.getString("drinks"));
            eList[Constants.ESSENTIAL_DRINKS_INDEX].setAnsIndex(drinksIndex);
            eList[Constants.ESSENTIAL_DRINKS_INDEX].setAnswerText(Utility.drinks_spinner_options[drinksIndex]);

            int smokesIndex = Integer.parseInt(userObj.getString("smokes"));
            eList[Constants.ESSENTIAL_SMOKES_INDEX].setAnsIndex(smokesIndex);
            eList[Constants.ESSENTIAL_SMOKES_INDEX].setAnswerText(Utility.smokes_spinner_options[smokesIndex]);

            String religion = userObj.getString("religion");
            eList[Constants.ESSENTIAL_RELIGION_INDEX].setAnsIndex(0);
            eList[Constants.ESSENTIAL_RELIGION_INDEX].setAnswerText(religion);

            //            String religion = userObj.getString("religion");
            //            eList[Constants.ESSENTIAL_RELIGION_INDEX].setAnsIndex(educationIndex);
            //            eList[Constants.ESSENTIAL_RELIGION_INDEX].setAnswerText(Utility.education_spinner_options[educationIndex]);

            int kidsIndex = Integer.parseInt(userObj.getString("kids"));
            eList[Constants.ESSENTIAL_KIDS_INDEX].setAnsIndex(kidsIndex);
            eList[Constants.ESSENTIAL_KIDS_INDEX].setAnswerText(Utility.kids_spinner_options[kidsIndex]);

            int politicsIndex = Integer.parseInt(userObj.getString("politics"));
            eList[Constants.ESSENTIAL_POLITICS_INDEX].setAnsIndex(politicsIndex);
            eList[Constants.ESSENTIAL_POLITICS_INDEX].setAnswerText(Utility.politics_spinner_options[politicsIndex]);

            int signIndex = Integer.parseInt(userObj.getString("starsign"));
            eList[Constants.ESSENTIAL_SIGN_INDEX].setAnsIndex(signIndex);
            eList[Constants.ESSENTIAL_SIGN_INDEX].setAnswerText(Utility.sign_spinner_options[signIndex]);

            String profession = userObj.getString("profession");
            eList[Constants.ESSENTIAL_PROFESSION_INDEX].setAnsIndex(0);
            eList[Constants.ESSENTIAL_PROFESSION_INDEX].setAnswerText(profession);

            String hometown = userObj.getString("hometown");
            eList[Constants.ESSENTIAL_HOMETOWN_INDEX].setAnsIndex(0);
            eList[Constants.ESSENTIAL_HOMETOWN_INDEX].setAnswerText(hometown);

            String languages = userObj.getString("languages");
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
