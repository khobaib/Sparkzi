package com.sparkzi;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.sparkzi.adapter.LeftDrawerAdapter;
import com.sparkzi.fragment.ConversationsFragment;
import com.sparkzi.fragment.DummyFragment;
import com.sparkzi.fragment.FavoriteAddedFragment;
import com.sparkzi.fragment.FavoriteFragment;
import com.sparkzi.fragment.HomeFragment;
import com.sparkzi.fragment.LastNightFragment;
import com.sparkzi.fragment.ProfileFragment;
import com.sparkzi.fragment.SearchFragment;
import com.sparkzi.fragment.SettingsFragment;
import com.sparkzi.lazylist.ImageLoader;
import com.sparkzi.model.DrawerItem;
import com.sparkzi.model.UserCred;
import com.sparkzi.parser.JsonParser;
import com.sparkzi.utility.SparkziApplication;
import com.sparkzi.utility.Utility;

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
    
    private List<DrawerItem> drawerItemList;
//    private String[] mDrawerItems;

    private Fragment myFragment;
    int currentFragmentIndex;

    ImageLoader imageLoader;
    RelativeLayout rlProfileMenu;
    ImageView ivProfilePic;
    TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(this, "2c5ced14");
        setContentView(R.layout.home);

        appInstance = (SparkziApplication) getApplication();
        pDialog = new ProgressDialog(MainActivity.this);
        jsonParser = new JsonParser();
        imageLoader = new ImageLoader(MainActivity.this);

        currentFragmentIndex = 0;

        mTitle = getTitle();
        //        mDrawerTitle = getTitle();
//        mDrawerItems = getResources().getStringArray(R.array.drawer_menu_array);
        
        drawerItemList = new ArrayList<DrawerItem>();
        setDrawerMenuItems();
        

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLinear = (LinearLayout) findViewById(R.id.left_drawer);
        //        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_child);

        rlProfileMenu = (RelativeLayout) findViewById(R.id.rl_menu_profile);
        ivProfilePic = (ImageView) findViewById(R.id.iv_profile_pic);
        tvUserName = (TextView) findViewById(R.id.tv_name);

        UserCred userCred = appInstance.getUserCred();
        String userName = userCred.getUsername();
        String imageUrl = userCred.getPicUrl();

        imageLoader.DisplayImage(imageUrl, ivProfilePic);
        tvUserName.setText(userName);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
//        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerItems));
        mDrawerList.setAdapter(new LeftDrawerAdapter(drawerItemList, this));
        
        
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
    
    
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        BugSenseHandler.startSession(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BugSenseHandler.closeSession(this);
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

//        if (item.getItemId() == android.R.id.home){
//            if (mDrawerLayout.isDrawerOpen(mDrawerList)){
//                mDrawerLayout.closeDrawer(mDrawerList);
//            } 
//            else{
//                mDrawerLayout.openDrawer(mDrawerList);
//            }
//        }
//        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onResume() {
        super.onResume();

        UserCred userCred = appInstance.getUserCred();
        String imageUrl = userCred.getPicUrl();

        imageLoader.DisplayImage(imageUrl, ivProfilePic);
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(currentFragmentIndex != position){             
                selectItem(position);
            }
            else
                mDrawerLayout.closeDrawer(mDrawerLinear);
        }
    }

    public void onClickMenuProfile(View v){
        mDrawerLayout.closeDrawer(mDrawerLinear);        
    }

    public void selectItem(int position) {
        currentFragmentIndex = position;
        switch (Utility.SLIDING_MENU_OPTION.values()[position]){
            case HOME:
                myFragment = new HomeFragment();
                break;
            case CONVERSATIONS:
                myFragment = new ConversationsFragment();
                break;
            case LASTNIGHT:
                myFragment = new LastNightFragment();
                break;
            case PROFILE:
                myFragment = new ProfileFragment();
                break;
            case FAVORITES:
                myFragment = new FavoriteFragment();
                break;
            case SEARCH:
                myFragment = new SearchFragment();
                break;
            case SETTINGS:
                myFragment = new SettingsFragment();
                break;
            default:
                Log.e("????????", "DUMMY FRAGMENT");
                myFragment = new DummyFragment();
                break;
        }

        // update selected item and title, then close the drawer

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, myFragment).commit();

        mDrawerList.setItemChecked(position, true);
        setTitle(drawerItemList.get(position).getName());
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
    
    
    private void setDrawerMenuItems() {
        DrawerItem leftDrawerItem = new DrawerItem();
        leftDrawerItem.setName("Home");
        leftDrawerItem.setImage(getResources().getDrawable(R.drawable.icon_home));
        drawerItemList.add(leftDrawerItem);

        leftDrawerItem = new DrawerItem();
        leftDrawerItem.setName("Conversations");
        leftDrawerItem.setImage(getResources().getDrawable(R.drawable.icon_conversation));
        drawerItemList.add(leftDrawerItem);

        leftDrawerItem = new DrawerItem();
        leftDrawerItem.setName("Last Night");
        leftDrawerItem.setImage(getResources().getDrawable(R.drawable.icon_last_night));
        drawerItemList.add(leftDrawerItem);

        leftDrawerItem = new DrawerItem();
        leftDrawerItem.setName("Search");
        leftDrawerItem.setImage(getResources().getDrawable(R.drawable.icon_search));
        drawerItemList.add(leftDrawerItem);

        leftDrawerItem = new DrawerItem();
        leftDrawerItem.setName("Favorites");
        leftDrawerItem.setImage(getResources().getDrawable(R.drawable.icon_favorite));
        drawerItemList.add(leftDrawerItem);
        
        leftDrawerItem = new DrawerItem();
        leftDrawerItem.setName("Profile");
        leftDrawerItem.setImage(getResources().getDrawable(R.drawable.icon_profile));
        drawerItemList.add(leftDrawerItem);
        
        leftDrawerItem = new DrawerItem();
        leftDrawerItem.setName("Settings");
        leftDrawerItem.setImage(getResources().getDrawable(R.drawable.icon_settings));
        drawerItemList.add(leftDrawerItem);
        
        leftDrawerItem = new DrawerItem();
        leftDrawerItem.setName("Log out");
        leftDrawerItem.setImage(getResources().getDrawable(R.drawable.icon_logout));
        drawerItemList.add(leftDrawerItem);
        
    }



}
