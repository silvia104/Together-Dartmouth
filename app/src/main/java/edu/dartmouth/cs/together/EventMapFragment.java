package edu.dartmouth.cs.together;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

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

/**
 * Created by di on 2/28/2016.
 */
public class EventMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EventDataSource datasource;
    private List<Event> values= new ArrayList<Event>();
    private Map<Double, List<Integer>> allMarkersMap = new HashMap<Double, List<Integer>>();
    private PopupWindow mpopup;
    private LayoutInflater inflater;
    private int current;
    private ListView list ;
    private EventArrayAdapter eventAdapter;
    private Context mContext;
    private boolean isupdate;

    private BroadcastReceiver receiver= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // after receiver the id from gcm service, update the history fragment
            isupdate=true;
            new UpdateListAsyncTask(getContext()).execute();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_eventmap, container, false);
        datasource = new EventDataSource(mContext);
        this.inflater=inflater;
        isupdate=false;
        IntentFilter filter = new IntentFilter("update");
        // register receiver
        getActivity().registerReceiver(receiver, filter);
        new UpdateListAsyncTask(getContext()).execute();
//        setMap();
        return v;
    }

    private void setMap(){
        if (mMap == null && getActivity() != null && getActivity().getFragmentManager()!= null) {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            SupportMapFragment smf = SupportMapFragment.newInstance();
            smf.setRetainInstance(false);
            manager.beginTransaction().replace(R.id.eventmap, smf).commit();
            smf.getMapAsync(this);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        allMarkersMap.clear();
        //datasource.close();
        for(int i=0;i<values.size();i++){
            Event tmp=values.get(i);
            LatLng llg=new LatLng(tmp.getLatLng().latitude,tmp.getLatLng().longitude);
            Marker marker=mMap.addMarker(new MarkerOptions().position(llg));
            if(allMarkersMap.get(marker.getPosition().latitude)==null){
                allMarkersMap.put(marker.getPosition().latitude, new ArrayList<Integer>());
                allMarkersMap.get(marker.getPosition().latitude).add(i);
            }
            else{
                allMarkersMap.get(marker.getPosition().latitude).add(i);
            }
//            allMarkersMap.put(marker, llg);
        }
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                Intent i = new Intent(getContext(), EventEditorActivity.class);
                i.putExtra(Globals.MAP_LATITUDE, latLng.latitude);
                i.putExtra(Globals.MAP_LONGITUDE, latLng.longitude);
                startActivity(i);
            }
        });

        LatLng destination1 = new LatLng(43.7068109, -72.2735297);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination1,
                10));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                List<Event> popEvents=new ArrayList<Event>();
                List<Integer> popInt=allMarkersMap.get(arg0.getPosition().latitude);

                for(int i=0;i<popInt.size();i++){
                    popEvents.add(values.get(popInt.get(i)));
                }

                View popUpView = inflater.inflate(R.layout.popup, null); // inflating popup layout
                list = (ListView) popUpView.findViewById(R.id.mapevent_list);
                eventAdapter = new eventarrayAdapter(getActivity(), popEvents);

                list.setAdapter(eventAdapter);
                list.setOnItemClickListener(new ListClickHandler());
                mpopup = new PopupWindow(popUpView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true); //Creation of popup
                mpopup.setAnimationStyle(android.R.style.Animation_Dialog);
                mpopup.showAtLocation(popUpView, Gravity.BOTTOM, 0, 0);    // Displaying popup
                // Closes the popup window when touch outside.
                mpopup.setOutsideTouchable(true);
                mpopup.setFocusable(true);
                mpopup.setBackgroundDrawable(null);

                WindowManager wm = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams p = (WindowManager.LayoutParams) mpopup.getContentView().getLayoutParams();
                p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                p.dimAmount = 0.5f;
                wm.updateViewLayout(mpopup.getContentView(), p);

                return true;
            }

        });
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
            dartmouth.setLatitude(edu.dartmouth.cs.together.utils.Globals.DARTMOUTH_GPS.latitude);
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

    public class ListClickHandler implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
            // TODO Auto-generated method stub
            current = position;
            Intent i=new Intent(getContext(),EventDetailActivity.class);
            Event tmp = values.get(position);
            i.putExtra("TAG",2);
            i.putExtra(Event.ID_KEY,tmp.getEventId());
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(i);
        }
    }

    class UpdateListAsyncTask extends AsyncTask<Void, Void, List<Event>> {
        private Context context;

        public UpdateListAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected List<Event> doInBackground(Void... params) {
            return  datasource.queryEvents(EventDataSource.ALL_EVENT);
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            values.clear();
            values.addAll(events);
            dafaultFilter();
            if(!isupdate){
                setMap();
            }
            else{
                onMapReady(mMap);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
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

}
