package edu.dartmouth.cs.together;

import android.app.FragmentManager;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by di on 2/28/2016.
 */
public class EventMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_eventmap,container,false);
        setMap();
        return v;
    }

    private void setMap(){
        if (mMap == null && getActivity() != null && getActivity().getFragmentManager()!= null) {
            FragmentManager manager = getActivity().getFragmentManager();
            MapFragment smf = MapFragment.newInstance();
            smf.setRetainInstance(false);
            manager.beginTransaction().replace(R.id.eventmap, smf).commit();
            smf.getMapAsync(this);

        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng destination1 = new LatLng(42.352311, -71.055304);
        mMap.addMarker(new MarkerOptions().position(destination1).title("South Station, Boston"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(destination1));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(6));


        LatLng destination2 = new LatLng(42.3656, -71.0096);
        mMap.addMarker(new MarkerOptions().position(destination2).title("Logan Airport, Boston"));

        LatLng destination3 = new LatLng(43.7068109, -72.2735297);
        mMap.addMarker(new MarkerOptions().position(destination3).title("Hanover, NH"));


        LatLng destination4 = new LatLng(40.7508, -73.9755);
        mMap.addMarker(new MarkerOptions().position(destination4).title("New York City"));
    }
}
