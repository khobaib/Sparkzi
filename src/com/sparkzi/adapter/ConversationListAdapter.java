package com.sparkzi.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sparkzi.R;
import com.sparkzi.lazylist.ImageLoader;
import com.sparkzi.model.Conversation;
import com.sparkzi.utility.Utility;

public class ConversationListAdapter extends ArrayAdapter<Conversation>{
    
    private Context mContext;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader;

    public ConversationListAdapter(Context context, List<Conversation> feeds) {
        super(context, R.layout.row_conversation);
        this.mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader((Activity) mContext);
    }
    
    
    private static class ViewHolder {
        ImageView UserImage;
        TextView userName;
        TextView timestamp;
        TextView convDetails;
       
    }
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_conversation, null);

            holder = new ViewHolder();
            holder.UserImage = (ImageView) convertView.findViewById(R.id.iv_pic);
            holder.userName = (TextView) convertView.findViewById(R.id.tv_name);  
            holder.timestamp = (TextView) convertView.findViewById(R.id.tv_timestamp);  
            holder.convDetails = (TextView) convertView.findViewById(R.id.tv_conv_desc);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        Conversation item = getItem(position);
        
        String imageUrl = item.getPicUrl();
        imageLoader.DisplayImage(imageUrl, holder.UserImage);

        holder.userName.setText(item.getUsername());
        holder.timestamp.setText(Utility.getFormattedTime(item.getTimestamp()));
        
        holder.convDetails.setText(item.getMessage());
        
        
//        imageLoader.DisplayImage(item.getThumbImage(), holder.crustImage);
        
        return convertView;
    }
    
    
    public void setData(List<Conversation> convList) {
        clear();
        if (convList != null) {
            for (int i = 0; i < convList.size(); i++) {
                add(convList.get(i));
            }
        }
    }

}
