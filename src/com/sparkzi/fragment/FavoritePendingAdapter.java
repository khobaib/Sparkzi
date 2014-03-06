package com.sparkzi.fragment;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sparkzi.R;
import com.sparkzi.lazylist.ImageLoader;
import com.sparkzi.model.Favorite;
import com.sparkzi.utility.Utility;

public class FavoritePendingAdapter extends ArrayAdapter<Favorite>{
    
    private Context mContext;
    private LayoutInflater mInflater;
    private ImageLoader imageLoader;

    public FavoritePendingAdapter(Context context, List<Favorite> qList) {
        super(context, R.layout.row_favorite_pending);
        this.mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader((Activity) mContext);
    }


    private static class ViewHolder {
        ImageView UserImage;
        TextView userName;
        TextView userAgeGender;
        @SuppressWarnings("unused")
		Button bAccept;
    }
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_favorite_pending, null);

            holder = new ViewHolder();
            holder.UserImage = (ImageView) convertView.findViewById(R.id.iv_pic);
            holder.userName = (TextView) convertView.findViewById(R.id.tv_name);  
            holder.userAgeGender = (TextView) convertView.findViewById(R.id.tv_age_gender); 
            holder.bAccept = (Button) convertView.findViewById(R.id.b_accept);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        Favorite item = getItem(position);
        
        String imageUrl = item.getPicUrl();
        imageLoader.DisplayImage(imageUrl, holder.UserImage);

        holder.userName.setText(item.getUsername());
        holder.userAgeGender.setText(item.getAge() + " | " + Utility.Gender[item.getGender() - 1].substring(0, 1));
        
        return convertView;
    }
    
    
    public void setData(List<Favorite> favList) {
        clear();
        if (favList != null) {
            for (int i = 0; i < favList.size(); i++) {
                add(favList.get(i));
            }
        }
    }

}
