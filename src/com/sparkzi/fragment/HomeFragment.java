package com.sparkzi.fragment;

import java.util.List;

import android.R.color;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sparkzi.R;
import com.sparkzi.adapter.HomeFeedAdapter;
import com.sparkzi.loader.HomeFeedLoader;
import com.sparkzi.model.HomeFeed;
import com.sparkzi.utility.SparkziApplication;

public class HomeFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<HomeFeed>> {

    private static final int LOADER_ID = 1;

    private Activity activity;
    String token;

    private HomeFeedAdapter homeFeedAdapter;

    //    @Override
    //    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
    //            Bundle savedInstanceState) {
    //    View view = inflater.inflate(R.layout.home_fragment, null);
    //    return view;
    //    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        activity = getActivity();

        token = ((SparkziApplication) activity.getApplication()).getAccessToken();     

        ListView lv = getListView();
        lv.setDivider(activity.getResources().getDrawable(com.sparkzi.R.color.app_theme));
        lv.setDividerHeight(3);

        homeFeedAdapter = new HomeFeedAdapter(activity, null);
        setEmptyText("No feeds");
        setListAdapter(homeFeedAdapter);
        setListShown(false);


        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<List<HomeFeed>> onCreateLoader(int id, Bundle args) {
        return new HomeFeedLoader(activity, token);
    }

    @Override
    public void onLoadFinished(Loader<List<HomeFeed>> loader, List<HomeFeed> data) {
        homeFeedAdapter.setData(data);

        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<HomeFeed>> loader) {
        homeFeedAdapter.setData(null);

    }

}
