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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_eventmap, container, false);
        datasource = new EventDataSource(getContext());
        values= new ArrayList<Event>();
        setMap();
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
            mMap.addMarker(new MarkerOptions().position(llg).title(tmp.getLocation()+" "+tmp.getCategotyName()));
        }
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {
                Intent i = new Intent(getContext(), EventEditorActivity.class);
                i.putExtra(Globals.MAP_LATITUDE,latLng.latitude);
                i.putExtra(Globals.MAP_LONGITUDE,latLng.longitude);
                startActivity(i);
            }
        });

//        datasource.open();
//        List<Event> events=datasource.queryEvents(1);
//        for(int i=0;i<events.size();i++){
//            Event tmp=events.get(i);
//            LatLng llg=tmp.getLatLng();
//            mMap.addMarker(new MarkerOptions().position(llg).title("event"));
//        }
//        datasource.close();



        LatLng destination1 = new LatLng(43.7068109, -72.2735297);
        mMap.addMarker(new MarkerOptions().position(destination1).title("Hanover, NH"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(destination1));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(6));
    }


}
