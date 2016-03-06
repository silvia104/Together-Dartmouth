package edu.dartmouth.cs.together;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private List<Event> values;
    private Map<Marker, Long> allMarkersMap = new HashMap<Marker, Long>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_eventmap, container, false);
        datasource = new EventDataSource(getContext());
        values= new ArrayList<Event>();
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
        datasource = new EventDataSource(getContext());
        values = datasource.queryEvents(EventDataSource.ALL_EVENT);
        //datasource.close();
        for(int i=0;i<values.size();i++){
            Event tmp=values.get(i);
            LatLng llg=tmp.getLatLng();
            Marker marker=mMap.addMarker(new MarkerOptions().position(llg).title(tmp.getCategotyName()));
            allMarkersMap.put(marker, tmp.getEventId());
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
                Long eventid = allMarkersMap.get(arg0);
                Intent i = new Intent(getContext(), EventDetailActivity.class);
                i.putExtra("TAG", 2);
                i.putExtra(Event.ID_KEY, eventid);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(i);
                return true;
            }

        });
    }

    class UpdateListAsyncTask extends AsyncTask<Void, Void, String> {
        private Context context;

        public UpdateListAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            values = datasource.queryEvents(EventDataSource.ALL_EVENT);
            return null;
        }

        @Override
        protected void onPostExecute(String msg) {
            setMap();
        }
    }

}
