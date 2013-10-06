package com.sparkzi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sparkzi.fragment.DummyFragment;
import com.sparkzi.fragment.HomeFragment;
import com.sparkzi.lazylist.ImageLoader;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.SparkziApplication;

public class MainActivity extends FragmentActivity {

    JsonParser jsonParser;
    ProgressDialog pDialog;
    SparkziApplication appInstance;

    private DrawerLayout mDrawerLayout;
    LinearLayout mDrawerLinear;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    //    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mDrawerItems;

    private Fragment myFragment;
    int currentFragmentIndex;
    
    ImageLoader imageLoader;
    RelativeLayout rlProfileMenu;
    ImageView ivProfilePic;
    TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        appInstance = (SparkziApplication) getApplication();
        pDialog = new ProgressDialog(MainActivity.this);
        jsonParser = new JsonParser();
        imageLoader = new ImageLoader(MainActivity.this);

        currentFragmentIndex = 0;

        mTitle = getTitle();
        //        mDrawerTitle = getTitle();
        mDrawerItems = getResources().getStringArray(R.array.drawer_menu_array);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLinear = (LinearLayout) findViewById(R.id.left_drawer);
        //        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_child);
        
        rlProfileMenu = (RelativeLayout) findViewById(R.id.rl_menu_profile);
        ivProfilePic = (ImageView) findViewById(R.id.iv_profile_pic);
        tvUserName = (TextView) findViewById(R.id.tv_name);
        
        String userName = appInstance.getUserName();
        String imageUrl = appInstance.getProfileImageUrl();
        
        imageLoader.DisplayImage(imageUrl, ivProfilePic);
        tvUserName.setText(userName);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);


        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }


    }



    //    @Override
    //    public boolean onCreateOptionsMenu(Menu menu) {
    //        MenuInflater inflater = getMenuInflater();
    //        inflater.inflate(R.menu.main, menu);
    //        return super.onCreateOptionsMenu(menu);
    //    }
    //
    //    /* Called whenever we call invalidateOptionsMenu() */
    //    @Override
    //    public boolean onPrepareOptionsMenu(Menu menu) {
    //        // If the nav drawer is open, hide action items related to the content view
    //        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
    //        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
    //        return super.onPrepareOptionsMenu(menu);
    //    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
        // Handle action buttons
        //        switch(item.getItemId()) {
        //        case R.id.action_websearch:
        //            // create intent to perform web search for this planet
        //            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        //            intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
        //            // catch event that there's no activity to handle intent
        //            if (intent.resolveActivity(getPackageManager()) != null) {
        //                startActivity(intent);
        //            } else {
        //                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
        //            }
        //            return true;
        //        default:
        //            return super.onOptionsItemSelected(item);
        //        }
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(currentFragmentIndex != position){
                currentFragmentIndex = position;
                selectItem(position);
            }
            else
                mDrawerLayout.closeDrawer(mDrawerLinear);
        }
    }
    
    public void onClickMenuProfile(View v){
        mDrawerLayout.closeDrawer(mDrawerLinear);
        
        Intent i = new Intent(MainActivity.this, EssentialDetailsActivity.class);
        startActivity(i);
    }

    private void selectItem(int position) {
        switch (position){
            case 0:
                myFragment = new HomeFragment();
                break;
            default:
                Log.e("????????", "DUMMY FRAGMENT");
                myFragment = new DummyFragment();
                break;
        }
        // update the main content by replacing fragments
        //        Fragment fragment = new PlanetFragment();
        //        Bundle args = new Bundle();
        //        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        //        fragment.setArguments(args);
        //
        //        FragmentManager fragmentManager = getFragmentManager();
        //        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, myFragment).commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(mDrawerItems[position]);
        mDrawerLayout.closeDrawer(mDrawerLinear);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }



}
