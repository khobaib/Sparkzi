package com.sparkzi.fragment;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.sparkzi.NewConversationActivity;
import com.sparkzi.R;
import com.sparkzi.ThreadMessageActivity;
import com.sparkzi.adapter.ConversationListAdapter;
import com.sparkzi.loader.ConversationListLoader;
import com.sparkzi.model.Conversation;
import com.sparkzi.model.UserCred;
import com.sparkzi.utility.Constants;
import com.sparkzi.utility.SparkziApplication;

public class ConversationsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Conversation>>{
    
    private static final int LOADER_ID = 1;

    private Activity activity;
    String token;

    private ConversationListAdapter conversationListAdapter;

    //    @Override
    //    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
    //            Bundle savedInstanceState) {
    //    View view = inflater.inflate(R.layout.home_fragment, null);
    //    return view;
    //    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        setHasOptionsMenu(true);

        activity = getActivity();

        UserCred userCred = ((SparkziApplication) activity.getApplication()).getUserCred();
        token = userCred.getToken();

        ListView lv = getListView();
        lv.setDivider(activity.getResources().getDrawable(com.sparkzi.R.color.app_theme));
        lv.setDividerHeight(3);

        conversationListAdapter = new ConversationListAdapter(activity, null);
        setEmptyText("No conversations");
        setListAdapter(conversationListAdapter);
        setListShown(false);


        getLoaderManager().initLoader(LOADER_ID, null, this);
    }
    
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       inflater.inflate(R.menu.menu_conversation, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       // handle item selection
       switch (item.getItemId()) {
          case R.id.action_settings:
             startActivity(new Intent(activity, NewConversationActivity.class));
             return true;
          default:
             return super.onOptionsItemSelected(item);
       }
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Conversation selectedConv = (Conversation) l.getItemAtPosition(position);
        String username = selectedConv.getUsername();
        
        UserCred userCred = ((SparkziApplication) activity.getApplication()).getUserCred();
        int myUId = userCred.getUid();
        
        
        Intent i = new Intent(activity, ThreadMessageActivity.class);
        i.putExtra("user_name", username);
        i.putExtra("user_id", (myUId == selectedConv.getUfrom()) ? selectedConv.getUto() : selectedConv.getUfrom());
        startActivity(i);
    }

    @Override
    public Loader<List<Conversation>> onCreateLoader(int id, Bundle args) {
        return new ConversationListLoader(activity, token, Constants.RETRIEVE_ALL_CONVERSATIONS, "");
    }

    @Override
    public void onLoadFinished(Loader<List<Conversation>> loader, List<Conversation> data) {
        conversationListAdapter.setData(data);

        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Conversation>> loader) {
        conversationListAdapter.setData(null);
    }

}
