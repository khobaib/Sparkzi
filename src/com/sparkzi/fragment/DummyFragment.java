package com.sparkzi.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ListView;

public class DummyFragment extends ListFragment {
    
    private Activity activity;
    
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        activity = getActivity();    

        ListView lv = getListView();
        lv.setDivider(activity.getResources().getDrawable(com.sparkzi.R.color.app_theme));
        lv.setDividerHeight(3);

        setEmptyText("To be implemented");
        setListShown(true);

    }

}
