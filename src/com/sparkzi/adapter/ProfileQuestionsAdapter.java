package com.sparkzi.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sparkzi.R;
import com.sparkzi.model.Question;

public class ProfileQuestionsAdapter extends ArrayAdapter<Question> {

    private Context mContext;
    private LayoutInflater mInflater;

    public ProfileQuestionsAdapter(Context context, List<Question> qList) {
        super(context, R.layout.row_profile_question);
        this.mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    private static class ViewHolder {
        TextView qText;
        TextView ansText;      
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_profile_question, null);

            holder = new ViewHolder();
            holder.qText = (TextView) convertView.findViewById(R.id.tv_question);  
            holder.ansText = (TextView) convertView.findViewById(R.id.tv_answer); 

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Question item = getItem(position);

        holder.qText.setText(item.getqText());

        if(item.getAnsText() == null || item.getAnsText().equals("")){
            holder.ansText.setText("I won't say but ask me.");
        }
        else{
            holder.ansText.setText(item.getAnsText());
        }



        return convertView;
    }


    public void setData(List<Question> qList) {
        clear();
        if (qList != null) {
            for (int i = 0; i < qList.size(); i++) {
                add(qList.get(i));
            }
        }
    }

}
