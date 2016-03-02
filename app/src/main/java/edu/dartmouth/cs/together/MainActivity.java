package edu.dartmouth.cs.together;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private boolean isListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



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
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main_content_frame, efrag);
        transaction.commit();
        ImageButton addFab = (ImageButton) findViewById(R.id.fab_image_button);
        ImageButton addFabswitch = (ImageButton) findViewById(R.id.fab_image_button2);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EventEditorActivity.class));
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
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                LinearLayout profile = (LinearLayout) drawerView.findViewById(R.id.nav_header);
//                profile.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        drawer.closeDrawers();
//                        Fragment profileFragment = new ProfileFragment();
//                        FragmentTransaction newTransaction = getFragmentManager().beginTransaction();
//                        newTransaction.replace(R.id.main_content_frame, profileFragment);
//                        newTransaction.addToBackStack(null);
//                        newTransaction.commit();
//                    }
//                });
//            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation edu.dartmouth.cs.together.view item clicks here.
        int id = item.getItemId();
        android.support.v4.app.FragmentManager managerV4 = getSupportFragmentManager();
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = null;
        switch (id){
            case R.id.nav_my_events:
                fragment = new MyEventsFragment();
                break;

            case R.id.nav_message:
                fragment = new MessageCenterFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;

            case R.id.nav_recommended_events:
                EventListFragment efrag = new EventListFragment();
                android.support.v4.app.FragmentTransaction transactionV4 = managerV4.beginTransaction();
                transactionV4.replace(R.id.main_content_frame, efrag, "EVENT_LIST_FRAG");
                transactionV4.commit();
                Fragment fragmentApp = fragmentManager.findFragmentByTag("OTHER_FRAGS");
                if (fragmentApp!=null){
                    fragmentManager.beginTransaction().hide(fragmentApp).commit();
                }
                break;
        }
        if (id != R.id.nav_recommended_events){
            android.support.v4.app.FragmentTransaction transactionV4 = managerV4.beginTransaction();

            android.support.v4.app.Fragment fragmentV4 = managerV4.findFragmentByTag("EVENT_LIST_FRAG");
            if (fragmentV4!=null){
                transactionV4.hide(fragmentV4).commit();
            }
            fragmentManager.beginTransaction().replace(
                    R.id.main_content_frame, fragment,"OTHER_FRAGS"
            ).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
