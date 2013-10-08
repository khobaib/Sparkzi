package com.sparkzi.model;

import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sparkzi.utility.Utility;

public class Question {
    
    private int qid;
    private String qText;
    private String answer;
    
    public Question() {
        // TODO Auto-generated constructor stub
    }       

    public Question(int qId, String qText, String ansText) {
        this.qid = qId;
        this.qText = qText;
        this.answer = ansText;
    }
    
    public static List<Question> parseQList(JSONArray qArray){
        Question[] qList = new Question[Utility.QUESTION_TEXTS.length];
        
        for(int i = 0; i < Utility.QUESTION_TEXTS.length; i++){
            qList[i] = new Question();
            qList[i].setqId(i + 1);
            qList[i].setqText(Utility.QUESTION_TEXTS[i]);
        }
        
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();
        
        try {
            for(int i=0; i<qArray.length(); i++){

                JSONObject thisQ = qArray.getJSONObject(i);
                if(thisQ != null){
                    String jsonString = thisQ.toString();
                    Question qst = gson.fromJson(jsonString, Question.class);
                    
                    qList[qst.getqId() - 1].setAnsText(qst.getAnsText());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
//        List<Question> questionList = 
        return Arrays.asList(qList);
    }

    public int getqId() {
        return qid;
    }

    public void setqId(int qId) {
        this.qid = qId;
    }

    public String getqText() {
        return qText;
    }

    public void setqText(String qText) {
        this.qText = qText;
    }

    public String getAnsText() {
        return answer;
    }

    public void setAnsText(String ansText) {
        this.answer = ansText;
    }
    
    

}
