package com.sparkzi.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sparkzi.R;
import com.sparkzi.model.Essential;
import com.sparkzi.model.Question;

public class ProfileEssentialAdapter extends ArrayAdapter<Essential> {
    
    private Context mContext;
    private LayoutInflater mInflater;

    public ProfileEssentialAdapter(Context context, List<Essential> essntialList) {
        super(context, R.layout.row_profile_essential);
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
            convertView = mInflater.inflate(R.layout.row_profile_essential, null);

            holder = new ViewHolder();
            holder.qText = (TextView) convertView.findViewById(R.id.tv_question);  
            holder.ansText = (TextView) convertView.findViewById(R.id.tv_answer); 

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        Essential item = getItem(position);

        holder.qText.setText(item.getEssentialText());
        holder.ansText.setText(item.getAnswerText());

        return convertView;
    }
    
    
    public void setData(List<Essential> eList) {
        clear();
        if (eList != null) {
            for (int i = 0; i < eList.size(); i++) {
                add(eList.get(i));
            }
        }
    }

}
