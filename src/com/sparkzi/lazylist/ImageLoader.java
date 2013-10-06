package com.sparkzi.lazylist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.sparkzi.R;

public class ImageLoader {

    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private Context mContext;
    //    private ProgressBar mProgressBar;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Handler handler=new Handler();//handler to display images in UI thread
    private Bitmap defaultFrame;
    private int imageWidth;
    //    static Bitmap resizedBitmap;

    //    int imageWidth;

    //    final int stub_id=R.drawable.details_back;

    public ImageLoader(Context context){
        this.mContext = context;
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
        //        this.defaultFrame = defaultFrame;
        this.defaultFrame = BitmapFactory.decodeResource(((Activity)context).getResources(), R.drawable.nopic);
        this.imageWidth = 0;
    }

    public ImageLoader(Context context, int imageWidth){
        this.mContext = context;
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
        //        this.defaultFrame = defaultFrame;
        this.defaultFrame = BitmapFactory.decodeResource(((Activity)context).getResources(), R.drawable.nopic);

        this.imageWidth = imageWidth;
    }

    //    public ImageLoader(Activity context, ProgressBar mProgressBar){
    //        this.mContext = context;
    //        fileCache=new FileCache(context);
    //        executorService=Executors.newFixedThreadPool(5);
    //        this.mProgressBar = mProgressBar;
    //        mProgressBar.setVisibility(View.VISIBLE);
    //    }


    //    public void DisplayImage(String url, ImageView imageView){
    //        imageViews.put(imageView, url);
    //        Bitmap bitmap = memoryCache.get(url);
    //        if(bitmap!=null){
    //            imageView.setImageBitmap(resizeBitmap(bitmap));
    //        }
    //        else
    //        {
    //            queuePhoto(url, imageView, 140);
    ////            imageView.setImageResource(stub_id);
    //        }
    //    }

    public void DisplayImage(String url, ImageView imageView){


        imageViews.put(imageView, url);

        //        String downloadedMd5checksum = new Md5Hash(url + "_reflected").toHex();
        Bitmap bitmap = memoryCache.get(url);
        if(bitmap!=null){
            //            Logger.e("ImageLoader", "bitmap is not null");
            if(imageWidth == 0)
                imageView.setImageBitmap(bitmap);
            else{
                Bitmap resizedBitmap = getResizedBannerBitmapWidth(bitmap, imageWidth);
                imageView.setImageBitmap(resizedBitmap);
                //                resizedBitmap = null;
                //                if(resizedBitmap != null)
                //                    resizedBitmap.recycle();
            }

        }
        else{
            //            Logger.e("ImageLoader", "bitmap is null, starting queryphoto()");
            queuePhoto(url, imageView, 200);
            if(imageWidth == 0)
                imageView.setImageBitmap(defaultFrame);
            else{
                Bitmap resizedBitmap = getResizedBannerBitmapWidth(defaultFrame, imageWidth);
                imageView.setImageBitmap(resizedBitmap);
                //                resizedBitmap = null;
                //                if(resizedBitmap != null)
                //                    resizedBitmap.recycle();
            }
        }
    }



    public static Bitmap getResizedBannerBitmapWidth(Bitmap bm, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;

        float dstWidth = ((float)width)*scaleWidth;
        float dstHeight = ((float)height)*scaleWidth;

        //        Matrix matrix = new Matrix();
        //
        //        matrix.postScale(scaleWidth, scaleWidth);

        //        Logger.e("ImageLoader -> getResizedBitmapWidth", "width, height ---> " + width + ", " + height);
        //        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        //                if(resizedBitmap != null)
        //                    resizedBitmap.recycle();
        //        Bitmap resizedBitmap = bm;
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, (int)dstWidth, (int)dstHeight, false);
        //        Logger.e("ImageLoader -> getResizedBitmapWidth", "resizedBitmap width, height " + resizedBitmap.getWidth() + ", " + resizedBitmap.getHeight());
        //        return resizedBitmap;
        return resizedBitmap;
    }


    //    private Bitmap resizeBitmap(Bitmap bitmap) {
    //        // TODO Auto-generated method stub
    //        Bitmap temp = null;
    //        try{
    //            Logger.d("ImageLoader", "bitmap size in resizeBitmap"+bitmap.getRowBytes());
    //            if (bitmap != null) {
    //                WindowManager w = mContext.getWindowManager();
    ////                Point size = new Point();
    //                int Measuredwidth = 0;
    //                int Measuredheight = 0;
    //                float witdh,height;
    //                if(bitmap.getWidth()>mContext.getWindowManager().getDefaultDisplay().getWidth())
    //                {				
    //                    Display d = w.getDefaultDisplay();
    //                    Measuredwidth = d.getWidth();
    //                    Measuredheight = d.getHeight();					
    //                    Logger.e("Measuredwidth",""+Measuredwidth);
    //                    Logger.e("Measuredheight",""+Measuredheight);
    //                    if(Measuredheight<=800){
    //                        witdh = 300;
    //                        height = 500;
    //                    }
    //                    else{					
    //                        witdh = Measuredheight;
    //                        height = Measuredwidth;			
    //                    }
    //                    float retion=((float)witdh)/((float)bitmap.getWidth());
    //                    witdh=((float)bitmap.getWidth())*retion;
    //                    height=((float)bitmap.getHeight())*retion;
    //
    //                    temp=bitmap.createScaledBitmap(bitmap, (int)(witdh), (int)(height), true);
    //                }else
    //                    temp=bitmap;
    //            }
    //
    //        } catch(Exception e){
    //            temp=bitmap; 
    //            e.printStackTrace();
    //        }
    //        return temp;
    //    }
    //
    //    private Bitmap resizeBitmap_splash(Bitmap bitmap) {
    //        // TODO Auto-generated method stub
    //        Bitmap temp = null;
    //        try{
    //            //			Loggerger.logger("bitmap size"+bitmap.getRowBytes());
    //            if (bitmap != null) {
    //                WindowManager w = mContext.getWindowManager();
    //
    //                int Measuredwidth = 0;
    //                int Measuredheight = 0;
    //                float width,height;
    //
    //                Display d = w.getDefaultDisplay();
    //                Measuredwidth = d.getWidth();
    //                Measuredheight = d.getHeight();	
    ////                Loggerger.logger("========", "Measuredwidth = "  + Measuredwidth + " & Measuredheight = " + Measuredheight);
    //                //					Logger.e("Measuredwidth",""+Measuredwidth);
    //                //					Logger.e("Measuredheight",""+Measuredheight);
    //
    ////                Loggerger.logger("========", "bitmap width = "  + bitmap.getWidth() + " & bitmap height = " + bitmap.getHeight());
    //                float retion=((float)Measuredwidth)/((float)bitmap.getWidth());
    //                width=((float)bitmap.getWidth())*retion;
    //                height=((float)bitmap.getHeight())*retion;
    ////                Loggerger.logger("on screen", "bitmap width = "  + width + " & bitmap height = " + height);
    //                //				 Logger.e("witdh",""+witdh);
    //                //					Logger.e("height",""+height);
    //                temp = bitmap.createScaledBitmap(bitmap, (int)(width), (int)(height), true);
    //
    //            }
    //
    //        } catch(Exception e){
    //            temp = bitmap; 
    //            e.printStackTrace();
    //        }
    //        return temp;
    //    }

    private void queuePhoto(String url, ImageView imageView, int imageQuality) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p, imageQuality));
    }

    public Bitmap getBitmap(String url, int imageQuality) {
        File f = fileCache.getFile(url);

        //from SD cache
        Bitmap b = decodeFile(f, imageQuality);
        if(b!=null){
            //            Loggerger.logger("retrieving splash from file cache");
            return b;
        }

        //from web
        try {
            Bitmap bitmap=null;
            Log.d("-------", url);
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            conn.disconnect();
            bitmap = decodeFile(f, imageQuality);
            return bitmap;
        } catch (Throwable ex){
            ex.printStackTrace();
            if(ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }


    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f, int imageQuality){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1=new FileInputStream(f);
            BitmapFactory.decodeStream(stream1,null,o);
            stream1.close();

            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = imageQuality;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale=1;

            while(true){
                if(width_tmp/2 < REQUIRED_SIZE || height_tmp/2 < REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
            Log.i("SCALE", "scale = " + scale);

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public PhotoToLoad(String u, ImageView i){
            url = u; 
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        int imageQuality;
        PhotosLoader(PhotoToLoad photoToLoad, int imageQuality){
            this.photoToLoad = photoToLoad;
            this.imageQuality = imageQuality;
        }

        @Override
        public void run() {
            try{
                if(imageViewReused(photoToLoad))
                    return;
                Bitmap bmp=getBitmap(photoToLoad.url, imageQuality);

                //                String downloadedMd5checksum = new Md5Hash(photoToLoad.url + "_reflected").toHex();
                memoryCache.put(photoToLoad.url, bmp);

                if(imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){
            bitmap = b;
            photoToLoad = p;
        }

        public void run(){
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap != null){
                if(imageWidth == 0)
                    photoToLoad.imageView.setImageBitmap(bitmap);
                else{
                    Bitmap resizedBitmap = getResizedBannerBitmapWidth(bitmap, imageWidth);
                    photoToLoad.imageView.setImageBitmap(resizedBitmap);
                    //                    resizedBitmap = null;
                }
            }
            else{
                if(imageWidth == 0){
                    photoToLoad.imageView.setImageBitmap(defaultFrame);
                }
                else{
                    Bitmap resizedBitmap = getResizedBannerBitmapWidth(defaultFrame, imageWidth);
                    photoToLoad.imageView.setImageBitmap(resizedBitmap);
                    //                    resizedBitmap = null;
                }
            }
            //            if(bitmap != null)
            //                bitmap.recycle();
        }
    }


    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }
}
