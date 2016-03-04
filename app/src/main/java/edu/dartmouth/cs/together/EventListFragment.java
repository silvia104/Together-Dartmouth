package edu.dartmouth.cs.together;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
public class EventListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Object> {

    private ListView list ;
    private EventArrayAdapter eventAdapter;
    private int current;
    private List<Event> values;
    private EventDataSource datasource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_eventlist, container, false);
        list = (ListView) view.findViewById(R.id.event_list);
        datasource = new EventDataSource(getContext());
        values= new ArrayList<Event>();
        UpdateEvent();
        dafaultFilter();
        eventAdapter = new eventarrayAdapter(getActivity().getApplicationContext(), values);
        list.setAdapter(eventAdapter);
        list.setOnItemClickListener(new ListClickHandler());
//        getLoaderManager().initLoader(1, null, this).forceLoad();

        // register receiver
        return view;
    }

    public class eventarrayAdapter extends EventArrayAdapter<Event> {
        public eventarrayAdapter(Context context, List<Event> exercise) {
            super(context, exercise);
        }

        @Override
        public String lineOneText(Event e) {
//            return e.getShortdesc();
            return e.getLocation();
        }

        @Override
        public String lineTwoText(Event e) {
//            return e.getLocation();
            return e.getTime();
        }

        @Override
        public String lineTreText(Event e) {
            return "Duration:  "+e.getDuration();
        }

        @Override
        public String lineFouText(Event e) {
            return e.getShortdesc();
        }
        @Override
        public String lineFivText(Event e) {
            return "Joined number";
        }
        @Override
        public String lineSixText(Event e) {
            return e.getmJoinerCount()+"/"+e.getLimit();
        }
    }

    public void dafaultFilter(){
        Calendar c = Calendar.getInstance();
        SharedPreferences sharedPref = getContext().getSharedPreferences(
                edu.dartmouth.cs.together.utils.Globals.KEY_SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);
        int time=sharedPref.getInt(edu.dartmouth.cs.together.utils.Globals.KEY_TIME_RANGE, 3);
        int dist=sharedPref.getInt(edu.dartmouth.cs.together.utils.Globals.KEY_DISTANCE_RANGE,50);
        String insterests=sharedPref.getString(edu.dartmouth.cs.together.utils.Globals.KEY_INTEREST_CATEGORY, null);
        List<Integer> filtint=null;
        List<Event> filter=null;
        for(int i=0;i<values.size();i++){
            Event tmp=values.get(i);
            //filte event based on time and distance
            Location eventlocat= new Location("");
            eventlocat.setLatitude(tmp.getLatLng().latitude);
            eventlocat.setLongitude(tmp.getLatLng().longitude);
            Location dartmouth= new Location("");
            dartmouth.setLatitude(edu.dartmouth.cs.together.utils.Globals.DARTMOUTH_GPS.latitude);
            dartmouth.setLongitude(Globals.DARTMOUTH_GPS.longitude);
            if(eventlocat.distanceTo(dartmouth)<dist){
                    if(filtint.contains(tmp.getEventId())){
                        filter.add(tmp);
                    }
                }
            }
        }
        updateListfromvalues();
    }


    public void UpdateEvent(){
        Map<String, String> params = new HashMap<String, String>();
        new AsyncTask<Void, Void, String>() {

            @Override
            // Get history and upload it to the server.
            protected String doInBackground(Void... arg0) {
                JSONArray jarray=null;
                String event=null;
                datasource.open();
//            datasource.clearAllEvent();
                try {
                    Map<String, String> params = new HashMap<String, String>();
                    event= ServerUtilities.post(edu.dartmouth.cs.together.utils.Globals.SERVER_ADDR + "/update.do", params);
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
                    }
                }
                datasource.close();
                return null;
            }
            @Override
            protected void onPostExecute(String errString) {
                updateList();
            }
        }.execute();
    }

    public void updateList(){

        datasource.open();
        list.setAdapter(new eventarrayAdapter(getActivity().getApplicationContext(), values) {
        });
        datasource.close();
    }

    public void updateListfromvalues(){
        list.setAdapter(new eventarrayAdapter(getActivity().getApplicationContext(), values) {
        });
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

    public class ListClickHandler implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            // TODO Auto-generated method stub
            current = position;
            Intent intent;
            // display detail information of selected event
            Intent i = new Intent(getActivity(),EventDetailActivity.class);
            startActivity(i);
        }

    }

}
