package edu.dartmouth.cs.together;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.dartmouth.cs.together.cloud.ServerUtilities;
import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.data.EventDataSource;
import edu.dartmouth.cs.together.utils.*;
import edu.dartmouth.cs.together.utils.Globals;

/**
 * Created by di on 2/28/2016.
 */
public class EventListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Object> {

    private ListView list ;
    private EventArrayAdapter eventAdapter;
    private int current;
    private List<Event> values = new ArrayList<>();
    private EventDataSource datasource;
    private Context mContext;
    private SwipeRefreshLayout swipelayout;

    private BroadcastReceiver receiver= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // after receiver the id from gcm service, update the history fragment
            new UpdateListAsyncTask(getContext()).execute();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_eventlist, container, false);
        mContext = getActivity().getApplicationContext();
        list = (ListView) view.findViewById(R.id.list);

        //list = (ListView) view.findViewById(R.id.event_list);
        datasource = new EventDataSource(mContext);
        swipelayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe);
        swipelayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e(getClass().getSimpleName(), "refresh");
                UpdateEvent();
            }
        });

        IntentFilter filter = new IntentFilter("update");
        // register receiver
        getActivity().registerReceiver(receiver, filter);

        new UpdateListAsyncTask(getContext()).execute();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        eventAdapter = new eventarrayAdapter(getActivity(), values);
        setListAdapter(eventAdapter);
        //list.setOnItemClickListener(new ListClickHandler());
        new UpdateListAsyncTask(getContext()).execute();

    }

    public class eventarrayAdapter extends EventArrayAdapter<Event> {
        public eventarrayAdapter(Context context, List<Event> exercise) {
            super(context, exercise, EventDataSource.ALL_EVENT);
        }

        @Override
        public void dismiss(){
        }

        @Override
        public String lineOneText(Event e) {
            return e.getShortdesc();
        }

        @Override
        public String lineTwoText(Event e) {
            String location = e.getLocation();
            int linebreak = location.indexOf('\n');
            if (linebreak>0) {
                return location.substring(0, linebreak);
            }else {
                return location;
            }
        }

        @Override
        public long getid(Event e){return e.getEventId();}

        @Override
        public String lineTreText(Event e) {
            return e.getDate()+"  "+e.getTime();
        }

        @Override
        public String lineFouText(Event e){return "Duration:  "+e.getDuration() + " Hours";
        }
        @Override
        public String lineFivText(Event e) {
            return "Joiner Count";
        }
        @Override
        public String lineSixText(Event e) {
            return e.getmJoinerCount()+"/"+e.getLimit();
        }

        @Override
        public int setImage(Event e){
            return Globals.categoriIcons[e.getCategoryIdx()];
        }
    }

    public void dafaultFilter(){
        Calendar c = Calendar.getInstance();
        int time=MainActivity.filterTime;
        int dist=MainActivity.filterDist;
        List<Integer> filtint=null;
        List<Event> filter=new ArrayList<Event>();
        for(int i=0;i<values.size();i++){
            Event tmp=values.get(i);
            //filte event based on time and distance
            Location eventlocat= new Location("");
            eventlocat.setLatitude(tmp.getLatLng().latitude);
            eventlocat.setLongitude(tmp.getLatLng().longitude);
            Location dartmouth= new Location("");
            dartmouth.setLatitude(Globals.DARTMOUTH_GPS.latitude);
            dartmouth.setLongitude(Globals.DARTMOUTH_GPS.longitude);
            float distdif=eventlocat.distanceTo(dartmouth);
            if(distdif<dist*1609){
                Calendar curtime=Calendar.getInstance();
                int timedif=tmp.getcalender().get(Calendar.DAY_OF_YEAR) -curtime.get(Calendar.DAY_OF_YEAR);
                if(timedif<time){
//                    if(filtint.contains(tmp.getEventId())){
                    if(true){
                        filter.add(tmp);
                    }
                }
            }
        }
        values.clear();
        values.addAll(filter);
    }


    public void UpdateEvent(){
        Map<String, String> params = new HashMap<String, String>();
        new AsyncTask<Void, Void, List<Event>>() {

            @Override
            // Get history and upload it to the server.
            protected List<Event> doInBackground(Void... arg0) {
                JSONArray jarray=null;
                String event=null;
                datasource.clearAllEvent();
                values.clear();
//            datasource.clearAllEvent();
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("action", "update_all");
                    event= ServerUtilities.post(Globals.SERVER_ADDR + "/update.do", params);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(event!=null){
                    try {
                        jarray=new JSONArray(event);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for(int i=0;i<jarray.length();i++) {
                        try {
                            JSONObject jobj = jarray.getJSONObject(i);
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
                            Event eobj = new Event();
                            eobj.setEventId(id);
                            eobj.setJoinerCount(joinnum);
                            eobj.setOwnerId(owner);
                            eobj.setLimit(limit);
                            eobj.setLocation(location);
                            eobj.setShortDesc(shotDesc);
                            eobj.setLongDesc(longDesc);
                            eobj.setCategory(Integer.parseInt(cate));
                            eobj.setDuration(Integer.parseInt(duration));
                            Calendar cal=Calendar.getInstance();
                            cal.setTimeInMillis(etime);
                            eobj.setDateTime(cal);
                            LatLng ll = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                            eobj.setLatLng(ll);
                            values.add(eobj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        datasource.insertEvents(EventDataSource.ALL_EVENT, values);

                    }
                }
                //datasource.close();
                return values;
            }
            @Override
            protected void onPostExecute(List<Event> events) {
                dafaultFilter();
                updateListfromvalues();
                swipelayout.setRefreshing(false);
            }
        }.execute();
    }

    public void updateListfromvalues(){
//        list.setAdapter(new eventarrayAdapter(mContext, values) {});
       eventAdapter.notifyDataSetChanged();
    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }

    class UpdateListAsyncTask extends AsyncTask<Void, Void, List<Event>> {
        private Context context;

        public UpdateListAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<Event> doInBackground(Void... params) {
           return datasource.queryEvents(EventDataSource.ALL_EVENT);
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            super.onPostExecute(events);
            values.clear();
            values.addAll(events);
            dafaultFilter();
            updateListfromvalues();
        }
    }
}
