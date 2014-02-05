package com.sparkzi.adapter;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sparkzi.EssentialDetailsActivity;
import com.sparkzi.MainActivity;
import com.sparkzi.R;
import com.sparkzi.Interface.QuestionListInterface;
import com.sparkzi.fragment.ProfileQuestionsFragment;
import com.sparkzi.model.Question;
import com.sparkzi.model.ServerResponse;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;

public class ProfileQuestionsAdapter extends ArrayAdapter<Question> {

    private Context mContext;
    private LayoutInflater mInflater;
    private String token;

    JsonParser jsonParser;
    ProgressDialog pDialog;

    private QuestionListInterface listener;

    public ProfileQuestionsAdapter(Context context, QuestionListInterface listener, List<Question> qList, String token) {
        super(context, R.layout.row_profile_question);
        this.mContext = context;
        this.listener = listener;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        jsonParser = new JsonParser();        
        pDialog = new ProgressDialog(mContext);
        this.token = token;
    }


    private static class ViewHolder {
        TextView qText;
        TextView ansText;  
        ImageButton ImageButton;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_profile_question, null);

            holder = new ViewHolder();
            holder.qText = (TextView) convertView.findViewById(R.id.tv_question);  
            holder.ansText = (TextView) convertView.findViewById(R.id.tv_answer); 
            holder.ImageButton = (ImageButton) convertView.findViewById(R.id.iv_edit); 

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Question item = getItem(position);

        holder.qText.setText(item.getqText());

        if(item.getAnsText() == null || item.getAnsText().equals("")){
            holder.ansText.setText("I won't say but ask me.");
        }
        else{
            holder.ansText.setText(item.getAnsText());
        }

        holder.ImageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onClickEditQuestion(item);

            }
        });



        return convertView;
    }


    public void onClickEditQuestion(final Question item){

        View textEntryView = mInflater.inflate(R.layout.dialog_edit_question_answer, null);
        final AlertDialog alert = new AlertDialog.Builder(mContext).create();
        alert.setView(textEntryView, 0, 0, 0, 0);

        final EditText etAnswer = (EditText) textEntryView.findViewById(R.id.et_answer);

        Button OK = (Button) textEntryView.findViewById(R.id.b_ok);
        OK.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String answerText = etAnswer.getText().toString();
                alert.dismiss(); 
                new SendQuestionUpdateRequest(answerText).execute(item);
            }

        });
        alert.show();
    }


    public void setData(List<Question> qList) {
        clear();
        if (qList != null) {
            for (int i = 0; i < qList.size(); i++) {
                add(qList.get(i));
            }
        }
    }


    public class SendQuestionUpdateRequest extends AsyncTask<Question, Void, JSONObject> {

        private String ansText;

        public SendQuestionUpdateRequest(String answer) {
            this.ansText = answer;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Please wait while app is updating data...");
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Question... params) {
            String url = Constants.URL_ROOT + "user";

            try {
                JSONArray qArray = new JSONArray();

                JSONObject qObj = new JSONObject();
                qObj.put("qid", params[0].getqId());
                qObj.put("answer", ansText);      

                qArray.put(qObj);
                //                String ansData = qArray.toString();
                //                Log.e(">>>>>", "ansData = " + ansData);

                JSONObject contentObj = new JSONObject();
                contentObj.put("questions", qArray);
                String ansData = contentObj.toString();
                Log.e(">>>>>", "ansData = " + ansData);

                ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_POST, url,
                        null, ansData, token);
                if(response.getStatus() == 200){
                    JSONObject responseObj = response.getjObj();
                    return responseObj;
                }
                else{
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
            if(pDialog.isShowing())
                pDialog.dismiss();
            if(responseObj != null){
                try {
                    String status = responseObj.getString("status");
                    if(status.equals("OK")){
                        listener.updateQuestionList();
                    }
                    else{
                        alert("Update failed.");
                    }
                } catch (JSONException e) {
                    alert("Update failed.");
                    e.printStackTrace();
                }

            }
            else{
                alert("Update failed.");
            }
        }        
    }


    public void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(mContext);
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
