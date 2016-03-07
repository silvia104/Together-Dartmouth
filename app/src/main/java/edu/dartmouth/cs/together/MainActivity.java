package edu.dartmouth.cs.together;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.dartmouth.cs.together.cloud.GcmRegisterIntentService;
import edu.dartmouth.cs.together.cloud.UploadPicIntentService;
import edu.dartmouth.cs.together.data.User;
import edu.dartmouth.cs.together.utils.Globals;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private int currentFragment;
    private boolean isListFragment;
    private final int GET_RESULT_SUCCESS = -1;
    public static int filterTime;
    public static int filterDist;
    public static List<Integer> CateFilter;
    private static final String List_FRAGMENT_STATE_KEY = "saved_List";
    private static final String CUR_FRAG_KEY = "cur_frag";

    private static final  String FILTER_TIME_KEY="filter_time";
    private static final  String FILTER_DISTANCE_KEY="filter_dist";

    private SharedPreferences mSharedPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreference = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        if (!Globals.isRegistered) {
            registerDevice();
        }
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setGlobalsBySharedPref();

        isListFragment=true;

        filterTime=Globals.FILTER_TIME;
        filterDist=Globals.FILTER_DISTANCE;
        CateFilter=Globals.FILTER_INTEREST;

        if (savedInstanceState != null) {
            isListFragment = savedInstanceState
                    .getBoolean(List_FRAGMENT_STATE_KEY);
            filterTime=savedInstanceState.getInt(FILTER_TIME_KEY);
            filterDist=savedInstanceState.getInt(FILTER_DISTANCE_KEY);
            currentFragment = savedInstanceState.getInt(CUR_FRAG_KEY);
        }

        startFragment();

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View edu.dartmouth.cs.together.view) {
                Snackbar.make(edu.dartmouth.cs.together.view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        ImageButton addFab = (ImageButton) findViewById(R.id.fab_image_button);
        ImageButton addFabswitch = (ImageButton) findViewById(R.id.fab_image_button2);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EventEditorActivity.class));
            }
        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        addFabswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isListFragment=!isListFragment;
                startFragment();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                TextView userName = (TextView) findViewById(R.id.user_name_nav_header);
                if (userName != null){
                    userName.setText("Name: "+Globals.currentUser.getName());
                }
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the image capture uri before the activity goes into background
        outState.putBoolean(List_FRAGMENT_STATE_KEY, isListFragment);
        outState.putInt(FILTER_TIME_KEY, filterTime);
        outState.putInt(FILTER_DISTANCE_KEY, filterDist);
        outState.putInt(CUR_FRAG_KEY,currentFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mSharedPreference.getBoolean(Globals.LOGIN_STATUS_KEY,false)) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        } else {
            Globals.currentUser = new User();
            Globals.currentUser.setId(mSharedPreference.getLong(User.ID_KEY, -1));
            Globals.currentUser.setAccount(mSharedPreference.getString(User.ACCOUNT_KEY, "UNKNOWN"));
        }
    }


    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences sharedPrefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        //the first time load value is false util the app is launched a second time
        if(sharedPrefs.getBoolean(Globals.FIRST_LOAD_ALL_EVENTS_KEY,false)) {
            editor.putBoolean(Globals.FIRST_LOAD_ALL_EVENTS_KEY, false);
            editor.commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.logout, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, FilterActivity.class);
            startActivityForResult(intent,Globals.SETTING_FILTER);
            return true;
        }
        if (id == R.id.action_logout) {
            mSharedPreference.edit()
                    .putBoolean(Globals.LOGIN_STATUS_KEY, false).commit();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setGlobalsBySharedPref(){
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        //set up filter preference
        if(Globals.FILTER_TIME == -1){
            Globals.FILTER_TIME = sharedPreferences.getInt(Globals.KEY_TIME_RANGE, 14);
        }
        if(Globals.FILTER_DISTANCE == -1){
            Globals.FILTER_DISTANCE = sharedPreferences.getInt(Globals.KEY_DISTANCE_RANGE, 50);
        }
        if(Globals.FILTER_INTEREST == null){
            Globals.FILTER_INTEREST = new ArrayList<>();
            for(int i = 0 ; i< Globals.categories.size();i++){
                Globals.FILTER_INTEREST.add(i);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Globals.SETTING_FILTER){
            if(resultCode == GET_RESULT_SUCCESS) {
                if (data!=null ) {
                    Bundle extras = data.getExtras();
                    filterTime = (int)extras.get(Globals.KEY_TIME_RANGE);
                    filterDist = (int) extras.get(Globals.KEY_DISTANCE_RANGE);
                    String selectedInterest = extras.getString(Globals.KEY_INTEREST_CATEGORY);
                }
                Intent itt = new Intent("update");
                sendBroadcast(itt);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation edu.dartmouth.cs.together.view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = null;
        currentFragment = id;
        switch (id){
            case R.id.nav_my_events:
                fragment = new MyEventsFragment();
                transaction.replace(
                        R.id.main_content_frame, fragment, "MY_EVENTS"
                );
                break;

            case R.id.nav_message:
                fragment = new MessageCenterFragment();
                transaction.replace(
                        R.id.main_content_frame, fragment, "MESSAGES"
                );
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                transaction.replace(
                        R.id.main_content_frame, fragment, "SETTINGS"
                );
                break;

            case R.id.nav_recommended_events:
                fragment  = new EventListFragment();

                transaction.replace(
                        R.id.main_content_frame, fragment, "EVENT_LIST_FRAG"
                );

                break;
        }
        Fragment oldFragment = fragmentManager.findFragmentById(R.id.main_content_frame);
        if (oldFragment != null){
            transaction.hide(oldFragment);
        }
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void registerDevice(){
        Intent i = new Intent(getApplicationContext(), GcmRegisterIntentService.class);
        startService(i);
    }

    private void startFragment(){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (currentFragment == R.id.nav_recommended_events) {

            if (!isListFragment) {
                EventMapFragment efrag = new EventMapFragment();
                transaction.replace(R.id.main_content_frame, efrag);
            } else {
                EventListFragment efrag = new EventListFragment();
                transaction.replace(R.id.main_content_frame, efrag);
            }
        } else {
            switch (currentFragment){
                case R.id.nav_my_events:
                    transaction.replace(
                            R.id.main_content_frame, new MyEventsFragment(), "MY_EVENTS"
                    );
                    break;

                case R.id.nav_message:
                    transaction.replace(
                            R.id.main_content_frame, new MessageCenterFragment(), "MESSAGES"
                    );
                    break;
                case R.id.nav_settings:
                    transaction.replace(
                            R.id.main_content_frame, new SettingsFragment(), "SETTINGS"
                    );
                    break;
            }

        }
        transaction.commit();
    }


}
