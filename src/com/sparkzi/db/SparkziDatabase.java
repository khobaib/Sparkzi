package com.sparkzi.db;

import java.util.List;

import com.sparkzi.model.Essential;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SparkziDatabase {

    private static final String TAG = SparkziDatabase.class.getSimpleName();

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private Context mContext;


    private static final String DATABASE_NAME = "sparkzi_db";
    private static final int DATABASE_VERSION = 1;


    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            EssentialDbManager.createTable(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            EssentialDbManager.dropTable(db);

            onCreate(db);
        }
    }

    /** Constructor */
    public SparkziDatabase(Context context) {
        mContext = context;
    }

    public SparkziDatabase open() throws SQLException {
        dbHelper = new DatabaseHelper(mContext);
        db = dbHelper.getWritableDatabase();
        return this;
    }


    public void close() {
        dbHelper.close();
    }
    
    
    public void insertOrupdateEssential(Essential essential, int type){
        EssentialDbManager.insertOrupdate(this.db, essential, type);
    }
    
    
    public void deleteAllEssential(){
        EssentialDbManager.deleteAll(this.db);
    }
    
    public List<Essential> retrieveEssential(){
        return EssentialDbManager.retrieve(this.db);
    }
    
    public Essential retrieveEssential(int essentialType, int id){
        return EssentialDbManager.retrieve(this.db, essentialType, id);
    }
    
    public List<Essential> retrieveEssential(int essentialType){
        return EssentialDbManager.retrieve(this.db, essentialType);
    }

}
