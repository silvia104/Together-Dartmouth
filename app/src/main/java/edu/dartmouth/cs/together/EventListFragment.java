package edu.dartmouth.cs.together;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by di on 2/28/2016.
 */
public class EventListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Object> {

    private ListView list ;
    private EventArrayAdapter eventAdapter;
    private int current;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.event_list, container, false);
        list = (ListView) view.findViewById(R.id.event_list);
//        eventAdapter = new EventArrayAdapter(getActivity().getApplicationContext(), new ArrayList<Int>());
        list.setAdapter(eventAdapter);
        list.setOnItemClickListener(new ListClickHandler());
        getLoaderManager().initLoader(1, null, this).forceLoad();

        // register receiver
        return view;
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
