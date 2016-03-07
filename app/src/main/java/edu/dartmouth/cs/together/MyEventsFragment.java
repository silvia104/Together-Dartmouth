package edu.dartmouth.cs.together;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;


import com.google.appengine.labs.repackaged.org.json.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.dartmouth.cs.together.cloud.ServerUtilities;
import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.data.EventDataSource;
import edu.dartmouth.cs.together.utils.Globals;
import edu.dartmouth.cs.together.view.ActionTabsViewPagerAdapter;
import edu.dartmouth.cs.together.view.SlidingTabLayout;


public class MyEventsFragment extends Fragment {

    private Fragment asStarterFragment;
    private Fragment asJoinerFragment;
    private ArrayList<Fragment> fragmentList;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private ActionTabsViewPagerAdapter mViewPageAdapter;

    private List<Event> values = new ArrayList<>();
    private EventDataSource dataSource;
    private Context mContext;


    public static MyEventsFragment newInstance() {

        Bundle args = new Bundle();
        MyEventsFragment fragment = new MyEventsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MyEventsFragment(){

    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        dataSource = new EventDataSource(mContext);

        SharedPreferences sharedPrefs = mContext.getSharedPreferences(
                mContext.getPackageName(), Context.MODE_PRIVATE );
        if(! sharedPrefs.getBoolean(Globals.FIRST_LOAD_ALL_EVENTS_KEY,false)){
            updateData();
            //mofified shared preference, set boolean to not first time of loading data
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean(Globals.FIRST_LOAD_ALL_EVENTS_KEY, true);
            editor.commit();
        }
//        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_my_events, container, false);


        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tab);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);

        //Set up fragments of each tab
        fragmentList = new ArrayList<Fragment>();
        asStarterFragment = new MyEventsAsInitiator();
        asJoinerFragment = new MyEventsAsJoiner();
        fragmentList.add(asStarterFragment);
        fragmentList.add(asJoinerFragment);
        mViewPageAdapter =new ActionTabsViewPagerAdapter(getChildFragmentManager(),
                fragmentList);


        mViewPager.setAdapter(mViewPageAdapter);

//        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//           @Override
//           public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//           }
//
//           @Override
//           public void onPageSelected(int position) {
//               Fragment fragment = ((ActionTabsViewPagerAdapter)mViewPager.getAdapter()).getFragment(position);
//
//               if (position ==1 && fragment != null)
//               {
//                   fragment.onResume();
//               }
//           }
//
//           @Override
//           public void onPageScrollStateChanged(int state) {
//
//           }
//       }
//
//        );
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);
        return view;
    }


    private void updateData(){
        new AsyncTask<Void, Void, List<Event>>() {
            @Override
            // Get history and upload it to the server.
            protected List<Event> doInBackground(Void... arg0) {
                JSONArray jArrayOwned = null;
                JSONArray jArrayJoined = null;
                String event=null;
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("action", "update_user_events");
                    params.put("userId", String.valueOf(Globals.currentUser.getId()));
                    event = ServerUtilities.post(Globals.SERVER_ADDR + "/update.do", params);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(event!=null){
                    try {
                        JSONObject allEvent = new JSONObject(event);
                        jArrayOwned = allEvent.getJSONArray("ownedEvent");
                        jArrayJoined = allEvent.getJSONArray("joinedEvent");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    insertEvent(jArrayOwned, EventDataSource.MY_OWN_EVENT);
                    insertEvent(jArrayJoined, EventDataSource.JOINED_EVENT);
                }
                return values;
            }
        }.execute();
    }

    private void insertEvent(JSONArray jsonArray, int eventType) {
        for(int i=0;i<jsonArray.length();i++) {
            try {
                JSONObject jobj = jsonArray.getJSONObject(i);
                long id=Long.parseLong(jobj.getString("id"));
                String cate = jobj.getString("cate");
                String duration = jobj.getString("duration");
                String lat = jobj.getString("lat");
                String lng = jobj.getString("lng");
                String shotDesc=jobj.getString("shortdesc");
                String location=jobj.getString("location");
                String longDesc=jobj.getString("longdesc");
                long owner = Long.parseLong(jobj.getString("owner"));
                long etime = Long.parseLong(jobj.getString("time"));
                int joinnum = Integer.parseInt(jobj.getString("joincount"));
                int limit = Integer.parseInt(jobj.getString("limit"));
                Event jEvent = new Event();
                jEvent.setEventId(id);
                jEvent.setJoinerCount(joinnum);
                jEvent.setOwnerId(owner);
                jEvent.setLimit(limit);
                jEvent.setLocation(location);
                jEvent.setShortDesc(shotDesc);
                jEvent.setLongDesc(longDesc);
                jEvent.setCategory(Integer.parseInt(cate));
                jEvent.setDuration(Integer.parseInt(duration));
                Calendar cal=Calendar.getInstance();
                cal.setTimeInMillis(etime);
                jEvent.setDateTime(cal);
                LatLng ll = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                jEvent.setLatLng(ll);
                values.add(jEvent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(eventType == EventDataSource.MY_OWN_EVENT) {
                dataSource.insertEvents(EventDataSource.MY_OWN_EVENT, values);
            }else {
                dataSource.insertEvents(EventDataSource.JOINED_EVENT, values);
            }
        }

    }



}
