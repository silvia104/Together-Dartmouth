package edu.dartmouth.cs.together;


<<<<<<< HEAD
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
=======
import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import android.support.v4.content.Loader;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
>>>>>>> origin/test
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.data.EventDataSource;
import edu.dartmouth.cs.together.utils.Globals;


/**
 * A simple {@link Fragment} subclass.
 */
<<<<<<< HEAD
public class MyEventsAsInitiator extends Fragment implements
        LoaderManager.LoaderCallbacks<List<Event>> {

    private ListView mListView;
    private initiatedEventsAdapter mApater;
    private List<Event> mInitiatedEventList = new ArrayList<>();
    private EventDataSource mDB;
    private Context mContext;


=======
public class MyEventsAsInitiator extends ListFragment implements
        LoaderManager.LoaderCallbacks<List<Event>>{

    private List<Event> mInitiatedEventsList = new ArrayList<>();
    private EventDataSource mDB;
    private initiatedEventsAdapter mAdapter;
    private Context mContext;



>>>>>>> origin/test
    public MyEventsAsInitiator() {
        // Required empty public constructor
    }

<<<<<<< HEAD
=======
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        mAdapter = new initiatedEventsAdapter(mContext,mInitiatedEventsList);
        setListAdapter(mAdapter);
        if(savedInstanceState == null){
            getLoaderManager().initLoader(0, null, this).forceLoad();
        }
        mDB = new EventDataSource(mContext);
    }
>>>>>>> origin/test

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
<<<<<<< HEAD
        final View view = inflater.inflate(R.layout.fragment_my_events_as_starter, container, false);
        mListView = (ListView)view.findViewById(R.id.initiated_event_list);
        mContext = getActivity();
        mDB = new EventDataSource(getActivity());
//        mListView.setOnItemClickListener(new EventListFragment.ListClickHandler());
        if (savedInstanceState == null) {
            getLoaderManager().initLoader(0, null, this).forceLoad();
        }
        mApater = new initiatedEventsAdapter(mContext, mInitiatedEventList);
        mListView.setAdapter(mApater);
        return view;
    }

    @Override
    public Loader<List<Event>> onCreateLoader(int id, Bundle args) {
        //load all data in myownevent table
        mInitiatedEventList = mDB.queryEvents(EventDataSource.MY_OWN_EVENT);
        return new initaitedEvenLoader(mContext);
=======
        View view =  inflater.inflate(R.layout.fragment_my_events_as_starter, container, false);

        return view;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l,v,position, id);
        Intent i = new Intent(getActivity(),EventDetailActivity.class);
        startActivity(i);
    }

    @Override
    public Loader<List<Event>> onCreateLoader(int id, Bundle args) {
        return new initiatedEventsLoader(mContext);

>>>>>>> origin/test
    }

    @Override
    public void onLoadFinished(Loader<List<Event>> loader, List<Event> data) {
<<<<<<< HEAD
        mInitiatedEventList.addAll(data);
        mApater.notifyDataSetChanged();
    }

=======
        if(data == null){
            data = new ArrayList<>();
        }
        mInitiatedEventsList.addAll(data);
        mAdapter.notifyDataSetChanged();
    }



>>>>>>> origin/test
    @Override
    public void onLoaderReset(Loader<List<Event>> loader) {

    }

<<<<<<< HEAD
=======
    static class initiatedEventsLoader extends AsyncTaskLoader<List<Event>> {
        private EventDataSource mDB;

        public initiatedEventsLoader(Context context) {
            super(context);
            mDB = new EventDataSource(context);
        }

        // get all records in background as loader
        @Override
        public List<Event> loadInBackground() {
            return mDB.queryOwnedEvent(EventDataSource.MY_OWN_EVENT);
        }


    }

>>>>>>> origin/test
    class initiatedEventsAdapter extends EventArrayAdapter<Event>{

        private int mListItemLayoutResId;

        public initiatedEventsAdapter(Context context, List<Event> ts) {
            this(context, R.layout.event_list, ts);
        }

        public initiatedEventsAdapter(Context context, int listItemLayoutResourceId, List<Event> ts) {
            super(context, listItemLayoutResourceId, ts);
            this.context = context;
            mListItemLayoutResId = listItemLayoutResourceId;
        }

        @Override
        public long getid(Event event) {
<<<<<<< HEAD
            return 0;
=======
            return event.getEventId();
>>>>>>> origin/test
        }

        @Override
        public String lineOneText(Event event) {
//            return e.getShortdesc();
            return event.getLocation();
        }
        @Override
        public String lineTwoText(Event event) {
            return event.getTime();
        }
        @Override
        public String lineTreText(Event event) {
            return "Duration" + event.getDuration();
        }
        @Override
        public String lineFouText(Event event) {
            return event.getShortdesc();
        }
        @Override
        public String lineFivText(Event event) {
            return "Joined Number";
        }
        @Override
        public String lineSixText(Event event) {
            return event.getmJoinerCount() + "/" + event.getLimit();
        }

    }
<<<<<<< HEAD
    static class initaitedEvenLoader extends AsyncTaskLoader<List<Event>> {
        private EventDataSource mDB;
        public initaitedEvenLoader(Context context) {
            super(context);
            mDB = new EventDataSource(context);
        }
        // get all records in background as loader
        @Override
        public List<Event> loadInBackground() {
            return mDB.queryEvents(EventDataSource.MY_OWN_EVENT);
        }

    }

}
=======
}
>>>>>>> origin/test
