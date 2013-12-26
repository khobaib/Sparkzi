package com.sparkzi;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.sparkzi.adapter.ThreadMessageAdapter;
import com.sparkzi.loader.ConversationListLoader;
import com.sparkzi.model.Conversation;
import com.sparkzi.model.UserCred;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;

public class ThreadMessageActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<List<Conversation>>{

    private static final int LOADER_ID = 1;

    ListView ThreadMessageList;

    ThreadMessageAdapter threadMessageAdapter;
    List<Conversation> messageList;
    
    ProgressDialog pDialog;
    
//    SparkziApplication appInstance;
    int myUId;
    String myImageUrl;
    String token;
    String threadUserName;            // the other user of this conversation

    EditText MessageBody;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.thread_message);
        
        UserCred userCred = ((SparkziApplication) getApplication()).getUserCred();
        myUId = userCred.getUid();
        myImageUrl = userCred.getPicUrl();
        token = userCred.getToken();
        
        threadUserName = getIntent().getExtras().getString("user_name");
        
        pDialog = new ProgressDialog(ThreadMessageActivity.this);
        pDialog.setMessage("Loading...");

        ThreadMessageList = (ListView) findViewById(R.id.lv_thread_messages);

        MessageBody = (EditText) findViewById(R.id.et_msg_body);

        threadMessageAdapter = new ThreadMessageAdapter(ThreadMessageActivity.this, new ArrayList<Conversation>(), myUId, myImageUrl);
        ThreadMessageList.setAdapter(threadMessageAdapter);
        
        getSupportLoaderManager().initLoader(LOADER_ID, null, ThreadMessageActivity.this);
        if(!pDialog.isShowing())
            pDialog.show();
    }
    
    
    public void onClickReply(View v){
        String msgBody = MessageBody.getText().toString().trim();

        if(msgBody == null || msgBody.equals(""))
            Toast.makeText(ThreadMessageActivity.this, "Message is empty.", Toast.LENGTH_SHORT).show();
        else{
            // send message
        }
    }
    
    public void onClickDelete(View v){
        
    }
    
    public void onClickBlock(View v){
        
    }


    @Override
    public Loader<List<Conversation>> onCreateLoader(int id, Bundle args) {
        return new ConversationListLoader(ThreadMessageActivity.this, token, 
                Constants.RETRIEVE_SPECIFIC_USER_CONVERSATIONS, threadUserName);
    }

    @Override
    public void onLoadFinished(Loader<List<Conversation>> loader, List<Conversation> data) {
        threadMessageAdapter.setData(data);
        threadMessageAdapter.notifyDataSetChanged();
        if(pDialog.isShowing())
            pDialog.dismiss();  

    }

    @Override
    public void onLoaderReset(Loader<List<Conversation>> loader) {
        threadMessageAdapter.setData(null);
        threadMessageAdapter.notifyDataSetChanged();
    }




}
