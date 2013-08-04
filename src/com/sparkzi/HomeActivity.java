package com.sparkzi;

import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.SparkziApplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends Activity {
    
    JsonParser jsonParser;
    ProgressDialog pDialog;
    SparkziApplication appInstance;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        
        appInstance = (SparkziApplication) getApplication();
        pDialog = new ProgressDialog(HomeActivity.this);
        jsonParser = new JsonParser();
        
        if(appInstance.getProfileImageUrl() == null){
            showAddPhotoDialog();
        }
    }
    
    
    private void showAddPhotoDialog(){
        LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
        View addPhotoView = inflater.inflate(R.layout.dialog_add_photo, null);
        final AlertDialog alert = new AlertDialog.Builder(HomeActivity.this).create();
        alert.setView(addPhotoView);
        alert.setCancelable(false);
        
        Button bTakePic = (Button) addPhotoView.findViewById(R.id.b_take_pic);
        bTakePic.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                alert.dismiss();    
            }

        });
        
        Button bFromGallery = (Button) addPhotoView.findViewById(R.id.b_go_gallery);
        bFromGallery.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                alert.dismiss();    
            }

        });
        
        Button bUpload = (Button) addPhotoView.findViewById(R.id.b_upload);
        bUpload.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                alert.dismiss();    
            }

        });
        alert.show();
    }
    
    

}
