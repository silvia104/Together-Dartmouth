package edu.dartmouth.cs.together;

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

import edu.dartmouth.cs.together.cloud.GcmRegisterIntentService;
import edu.dartmouth.cs.together.data.User;
import edu.dartmouth.cs.together.utils.Globals;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private boolean isListFragment;
    private final int GET_RESULT_SUCCESS = -1;
    public int filterTime;
    public int filterDist;
    private SharedPreferences mSharedPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreference = getSharedPreferences(getPackageName(),MODE_PRIVATE);
        if (!Globals.isRegistered) {
            registerDevice();
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        isListFragment=true;
        EventListFragment efrag = new EventListFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main_content_frame, efrag, "EVENT_LIST_FRAG");
        transaction.commit();
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
                if(isListFragment){
                    EventMapFragment efrag = new EventMapFragment();
                    android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.main_content_frame, efrag);
                    transaction.commit();
                    isListFragment=!isListFragment;
                }
                else{
                    EventListFragment efrag = new EventListFragment();
                    android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.main_content_frame, efrag);
                    transaction.commit();
                    isListFragment=!isListFragment;
                }
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                LinearLayout profile = (LinearLayout) drawerView.findViewById(R.id.nav_header);
                profile.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        drawer.closeDrawers();
                        Fragment profileFragment = new ProfileFragment();
                        FragmentTransaction newTransaction = getSupportFragmentManager().beginTransaction();
                        newTransaction.replace(R.id.main_content_frame, profileFragment);
                        newTransaction.addToBackStack(null);
                        newTransaction.commit();
                    }
                });
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        registerMessageReceiver();
    }



    private void registerMessageReceiver() {
        MessageCenterFragment msgCenterFragment = new MessageCenterFragment();
        MessageCenterFragment.NewMessageReceiver receiver = msgCenterFragment.new NewMessageReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Globals.ACTION_NEW_MESSAGE_FROM_SERVER);
        registerReceiver(receiver, intentFilter);
    }

    //TODO: WHEN TO UNREGISTER THE RECEIVER?

    @Override
    protected void onResume() {
        super.onResume();
        if (!mSharedPreference.getBoolean(Globals.LOGIN_STATUS_KEY,false)) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        } else {
            Globals.currentUser = new User();
            Globals.currentUser.setId(mSharedPreference.getLong(User.ID_KEY,-1));
            Globals.currentUser.setAccount(mSharedPreference.getString(User.ACCOUNT_KEY, "UNKNOWN"));
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
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        switch (id){
            case R.id.nav_my_events:
                fragment = new MyEventsFragment();
                transaction.replace(
                        R.id.main_content_frame, fragment, "OTHER_FRAGS"
                );
                break;

            case R.id.nav_message:
                fragment = new MessageCenterFragment();
                transaction.replace(
                        R.id.main_content_frame, fragment, "OTHER_FRAGS"
                );
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                transaction.replace(
                        R.id.main_content_frame, fragment, "OTHER_FRAGS"
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
}
