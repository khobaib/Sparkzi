package com.sparkzi.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.SyncStateContract.Constants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sparkzi.R;
import com.sparkzi.lazylist.ImageLoader;
import com.sparkzi.model.Conversation;
import com.sparkzi.model.HomeFeed;
import com.sparkzi.utility.SparkziApplication;
import com.sparkzi.utility.Utility;

public class ThreadMessageAdapter extends BaseAdapter {
    
    private static final int MSG_SENT_BY_ME = 0;
    private static final int MSG_RECEIVED_BY_ME = 1;
    
    private Context mContext;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader;
    private List<Conversation> messageList;
    private int myUId;
    private String myImageUrl;
    
    public ThreadMessageAdapter(Context context, List<Conversation> messageList, int myUId, String myImageUrl) {
        this.mContext = context;
        this.messageList = messageList;
        this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(mContext);
        this.myUId = myUId;
        this.myImageUrl = myImageUrl;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
//    private interface MessageViewHolder {
//        public void populate(final Object item, int position, Bitmap bmp);
//    }
    
    private static class MessageHolder{
        
        private ImageView UserPic;        
        private TextView MessageBody;
        private TextView MessageTime;
        
//        public MessageHolder(View row) {
//            UserPic = (ImageView) row.findViewById(R.id.iv_user_pp);
//            MessageBody = (TextView) row.findViewById(R.id.tv_message_body);
//        }
//        
//        public void populateFrom(TextMessage item, int position, Bitmap bmp) {
//            UserPic.setImageBitmap(bmp);
//            MessageBody.setText(item.getMessageBody());
//        }
//
//        public void populate(Object item, int position, Bitmap bmp) {
//            if(item instanceof TextMessage){
//                this.populateFrom((TextMessage)item, position, bmp);
//            }           
//        }
        
    }
       
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Conversation item = (Conversation)getItem(position);

        int type = MSG_SENT_BY_ME;
        if (item.getUto() == myUId) {
            type = MSG_RECEIVED_BY_ME;
        }
        return type;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        
        MessageHolder holder;
        if (convertView == null) {  
            int itemViewType = getItemViewType(position);
            if(itemViewType == MSG_SENT_BY_ME)
                row = mInflater.inflate(R.layout.row_sent_message_item, null);
            else
                row = mInflater.inflate(R.layout.row_received_message_item, null);
            
            holder = new MessageHolder();
            
            holder.UserPic = (ImageView) row.findViewById(R.id.iv_user_pp);
            holder.MessageBody = (TextView) row.findViewById(R.id.tv_message_body);
            holder.MessageTime = (TextView) row.findViewById(R.id.tv_message_time);
            row.setTag(holder);
        } 
        else {
            holder = (MessageHolder) row.getTag();
        }
        
        Conversation message = (Conversation) getItem(position);
        
        holder.MessageBody.setText(message.getMessage());
        holder.MessageTime.setText(Utility.getFormattedTime(message.getTimestamp()));
//        holder.MessageTime.setText(message.getTimestamp());
        
        if(message.getUfrom() == myUId)
            imageLoader.DisplayImage(myImageUrl, holder.UserPic);
        else
            imageLoader.DisplayImage(message.getPicUrl(), holder.UserPic);
            
        return row;
    }
    
    public void setData(List<Conversation> convList) {
        messageList.clear();
        if (convList != null) {
            for (int i = 0; i < convList.size(); i++) {
                messageList.add(convList.get(i));
            }
        }
    }

}
