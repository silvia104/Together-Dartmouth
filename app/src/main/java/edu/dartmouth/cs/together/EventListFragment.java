package edu.dartmouth.cs.together;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import edu.dartmouth.cs.together.data.Event;

/**
 * Created by di on 2/28/2016.
 */
public class EventListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Object> {

    private ListView list ;
    private EventArrayAdapter eventAdapter;
    private int current;
    private List<Event> values;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_eventlist, container, false);
        list = (ListView) view.findViewById(R.id.event_list);
        values= new ArrayList<Event>();
        Event e1=new Event();
        Event e2=new Event();
        Event e3=new Event();
        Event e4=new Event();
        Event e5=new Event();
        values.add(e1);
        values.add(e2);
        values.add(e3);
        values.add(e4);
        values.add(e5);
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
            return "Location";
        }

        @Override
        public String lineTwoText(Event e) {
//            return e.getLocation();
            return "time";
        }

        @Override
        public String lineTreText(Event e) {
            return "Event duration";
        }

        @Override
        public String lineFouText(Event e) {
            return "Short description";
        }
        @Override
        public String lineFivText(Event e) {
            return "Joined number";
        }
        @Override
        public String lineSixText(Event e) {
            return "10/20";
        }
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
        }

    }

}
