package edu.dartmouth.cs.together;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.data.EventDataSource;
import edu.dartmouth.cs.together.utils.Globals;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyEventsAsJoiner extends ListFragment
    implements  LoaderManager.LoaderCallbacks<List<Event>>{


    private List<Event> mJoinedEventsList = new ArrayList<>();
    private EventDataSource mDB;
    private joinedEventsAdapter mAdapter;
    private Context mContext;

    public MyEventsAsJoiner() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        mAdapter = new joinedEventsAdapter(mContext,mJoinedEventsList);
        setListAdapter(mAdapter);
        mDB = new EventDataSource(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_events_as_joiner, container, false);
    }

    @Override
    public Loader<List<Event>> onCreateLoader(int id, Bundle args) {
        return new joinedEventsLoader(mContext);
    }

    @Override
    public void onLoadFinished(Loader<List<Event>> loader, List<Event> data) {
        if(data == null){
            data = new ArrayList<>();
        }
        mJoinedEventsList.clear();
        mJoinedEventsList.addAll(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Event>> loader) {

    }

    @Override
    public void setUserVisibleHint(boolean isVisible){
        super.setUserVisibleHint(isVisible);
        if(isVisible){
            mJoinedEventsList.clear();
            mAdapter.notifyDataSetChanged();
            getLoaderManager().initLoader(0, null, this).forceLoad();
        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l,v,position, id);
        Intent i = new Intent(getActivity(),EventDetailActivity.class);
        Event event = mAdapter.getItem(position);
        long eventId = event.getEventId();
        i.putExtra(Event.ID_KEY, eventId);
        i.putExtra("TAG", EventDataSource.JOINED_EVENT);
        startActivity(i);
    }



    static class joinedEventsLoader extends AsyncTaskLoader<List<Event>> {
        private EventDataSource mDB;

        public joinedEventsLoader(Context context) {
            super(context);
            mDB = new EventDataSource(context);
        }

        // get all records in background as loader
        @Override
        public List<Event> loadInBackground() {
            return mDB.queryEventByJoinerId(Globals.currentUser.getId());
        }
    }

    class joinedEventsAdapter extends EventArrayAdapter<Event>{

        private int mListItemLayoutResId;

        public joinedEventsAdapter(Context context, List<Event> ts) {
            this(context, R.layout.event_list, ts);
        }

        public joinedEventsAdapter(Context context, int listItemLayoutResourceId, List<Event> ts) {
            super(context, listItemLayoutResourceId, ts, EventDataSource.JOINED_EVENT);
            this.context = context;
            mListItemLayoutResId = listItemLayoutResourceId;
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
