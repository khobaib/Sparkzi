package com.sparkzi;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.sparkzi.model.ServerResponse;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.Base64;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;

public class UploadPicActivity extends Activity {


    private Uri imageUri;
    private static final int CAMERA_REQ_CODE = 901;
    private static final int GALLERY_REQ_CODE = 902;

    private ProgressDialog pDialog;
    JsonParser jsonParser;

    //    ImageLoader imageLoader;

    SparkziApplication appInstance;

    ImageView ProfilePic;
    Button Update;

    Bitmap bitmap;
    Bitmap scaledBmp;

    //    private Boolean isPicassaImage;

    private String selectedImagePath;
    //ADDED
    //    private String filemanagerstring;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //        BugSenseHandler.initAndStartSession(UploadPicActivity.this, "e8ecd3f1");
        setContentView(R.layout.upload_pic);

        appInstance = (SparkziApplication) getApplication();
        jsonParser = new JsonParser();

        ProfilePic = (ImageView) findViewById(R.id.iv_profile_pic);
        Update = (Button) findViewById(R.id.b_update);
        Update.setVisibility(View.GONE);

        String imageUrl = appInstance.getProfileImageUrl();
        //        imageLoader = new ImageLoader(UploadPicActivity.this);
        //        imageLoader.DisplayImage(imageUrl, ProfilePic);

    }


    public void onClickTakePic(View v){
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        File directory = Constants.APP_DIRECTORY;
        String mainDir = directory.toString();

        long currentTime = System.currentTimeMillis();
        String photoFileName = "profile_pic" + ".png";
        //        Log.d(TAG, photoFileName);

        File photoFolder = new File(mainDir, photoFileName);
        imageUri = Uri.fromFile(photoFolder);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);    
        startActivityForResult(intent, CAMERA_REQ_CODE);        
    }

    public void onClickGoToGallery(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQ_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQ_CODE:
                    //                    imageUriToString = imageUri.toString();
                    //                    Intent returnIntent = new Intent();
                    //                    returnIntent.putExtra("image_uri", imageUriToString);
                    //                    returnIntent.putExtra("user_photo_type", iSchoolConstant.USER_PHOTO_TYPE_TAKE_PIC);
                    //                    setResult(iSchoolConstant.RESULT_CODE_PHOTO_TYPE_TAKE_PIC, returnIntent);     
                    //                    finish();
                    getContentResolver().notifyChange(imageUri, null);
                    //                    final ImageView Preview = (ImageView) findViewById(R.id.ivPhoto);
                    ContentResolver cr = getContentResolver();

                    try {
                        if(bitmap != null)
                            bitmap.recycle();
                        bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imageUri);
                        Log.d("BITMAP SIZE", "bitmap size = " + bitmap.getByteCount());
                        scaledBmp = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
                        Log.d("scaled BITMAP SIZE", "bitmap size = " + scaledBmp.getByteCount());

                        ProfilePic.setImageBitmap(scaledBmp);
                        Update.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        Toast.makeText(UploadPicActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
                        Log.e("Camera", e.toString());
                    }

                    break;

                case GALLERY_REQ_CODE:

                    Uri selectedImageUri = data.getData();
                    Log.d("URI VAL", "selectedImageUri = " + selectedImageUri.toString());
                    selectedImagePath = getPath(selectedImageUri);

                    if(selectedImagePath!=null){         // if local image
                        System.out.println("selectedImagePath is the right one for you!");
                        FileInputStream in;
                        BufferedInputStream buf;
                        try 
                        {
                            in = new FileInputStream(selectedImagePath);
                            buf = new BufferedInputStream(in,1070);
                            byte[] bMapArray= new byte[buf.available()];
                            buf.read(bMapArray);
                            Bitmap bMap = BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
                            scaledBmp = Bitmap.createScaledBitmap(bMap, 200, 200, true);
                            ProfilePic.setImageBitmap(scaledBmp);
                            Update.setVisibility(View.VISIBLE);
                            if (in != null) 
                                in.close();
                            if (buf != null) 
                                buf.close();
                        } 
                        catch (Exception e) {
                            Log.e("Error reading file", e.toString());
                        }
                    }
                    else{
                        System.out.println("picasa image!");
                        loadPicasaImageFromGallery(selectedImageUri);
                    }

                    break;
            }
        }
    }


    private void loadPicasaImageFromGallery(final Uri uri) {
        String[] projection = {  MediaColumns.DATA, MediaColumns.DISPLAY_NAME };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(MediaColumns.DISPLAY_NAME);
            if (columnIndex != -1) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            scaledBmp = Bitmap.createScaledBitmap(bitmap, 200, 200, true);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProfilePic.setImageBitmap(scaledBmp);
                                    Update.setVisibility(View.VISIBLE);
                                }
                            });

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();
            }
        }
        cursor.close();

    }


    public String getPath(Uri uri) {
        String[] projection = {  MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if(cursor != null) {
            //HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            //THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        else return uri.getPath();
    }


    public void onClickUpdate(View v){
        new UploadProfilePicture().execute();
        //        finish();
        //        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { // Back key pressed
            finish();
            //            BugSenseHandler.closeSession(UploadPicActivity.this);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    protected void onStop() {
        super.onStop();
        //        BugSenseHandler.closeSession(UploadPicActivity.this);
    }



    private class UploadProfilePicture extends AsyncTask<Void, Void, JSONObject> {


        protected void onPreExecute() {
            pDialog = new ProgressDialog(UploadPicActivity.this);
            pDialog.setMessage("Uploading profile pic ...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected JSONObject doInBackground(Void... params) {

            //            Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            scaledBmp.compress(Bitmap.CompressFormat.PNG, 90, bao);
            byte[] ba = bao.toByteArray();
            Log.d("BITMAP SIZE in asynctask", "bitmap size after compress = " + ba.length);

            String base64Str = Base64.encodeBytes(ba);
            //            ArrayList<NameValuePair> contentParams = new ArrayList<NameValuePair>();
            //            contentParams.add(new BasicNameValuePair("image", base64Str));

            //            List<NameValuePair> urlParam = new ArrayList<NameValuePair>();
            //            urlParam.add(new BasicNameValuePair("method", Constants.METHOD_UPLOAD_PROFILE_PICTURE));

            String url = Constants.URL_ROOT + "pic";
            String token = appInstance.getAccessToken();

            try {           
                JSONObject contentObj = new JSONObject();
                contentObj.put("pic", base64Str);
                contentObj.put("picname", "hello");
                String contentData = contentObj.toString();
                Log.d(">>>><<<<<", "content = " + contentData);

                String appToken = appInstance.getAccessToken(); 
                ServerResponse response = jsonParser.retrieveServerData(Constants.REQUEST_TYPE_PUT, url,
                        null, contentData, token);            

                if(response.getStatus() == 200){
                    Log.d(">>>><<<<", "success in retrieving response in login");
                    JSONObject responseObj = response.getjObj();
                    return responseObj;
                }         
                else
                    return null;
            } catch (JSONException e) {                
                e.printStackTrace();
                return null;
            }

            //                if(response.getStatus() == 200){
            //                    Log.d(">>>><<<<", "success in getting response");
            //                    JSONObject responsObj = response.getjObj();
            //
            //
            //                    String responseStr = responsObj.getString("response");
            //                    if(responseStr.equals("success")){
            //                        String imageUrl = responsObj.getString("image_url");
            //                        appInstance.setProfileImageUrl(imageUrl);
            //                        return true;
            //                    }
            //                    else{
            //                        return false;
            //                    }
            //                } 
            //
            //            }catch (JSONException e) {
            //                // TODO Auto-generated catch block
            //                e.printStackTrace();
            //            }
            //            return false;
        }


        protected void onPostExecute(JSONObject responseObj) {
            if(pDialog.isShowing())
                pDialog.dismiss();
            if(responseObj != null){
                try {
                    String status = responseObj.getString("status");
                    if(status.equals("OK")){

                        runOnUiThread(new Runnable() {
                            public void run() {
                                showUpdateEssentialsDialog();
                                // show dialog answer dialog

//                                Intent i = new Intent(LoginActivity.this, UploadPicActivity.class);
//                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(i);
//                                finish();
                            }
                        });
                    }
                    else{
                        alert("Upload failed. Please try again.");
                    }
                } catch (JSONException e) {
                    alert("Registration Exception.");
                    e.printStackTrace();
                }
            }
            else{
                alert("Server error. Please try again.");                
            }

        }
    }
    
    
    
    private void showUpdateEssentialsDialog(){
        LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
        View addDIalogView = inflater.inflate(R.layout.dialog_answer_essentials, null);
        final AlertDialog alert = new AlertDialog.Builder(UploadPicActivity.this).create();
        alert.setView(addDIalogView);
        alert.setCancelable(false);
        
        Button bLater = (Button) addDIalogView.findViewById(R.id.b_later);
        bLater.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                alert.dismiss();    
              Intent i = new Intent(UploadPicActivity.this, MainActivity.class);
              i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
              startActivity(i);
              finish();
            }

        });
        
        Button bTryOut = (Button) addDIalogView.findViewById(R.id.b_try_out);
        bTryOut.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                alert.dismiss(); 
                Intent i = new Intent(UploadPicActivity.this, EssentialDetailsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }

        });
        alert.show();
    }


    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(UploadPicActivity.this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }

}
