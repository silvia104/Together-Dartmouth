package edu.dartmouth.cs.together;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import edu.dartmouth.cs.together.utils.Globals;
import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.data.EventDataSource;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyEventsAsJoiner extends Fragment implements
    LoaderManager.LoaderCallbacks<List<Event>> {

        private ListView mListView;
        private initiatedEventsAdapter mApater;
        private List<Event> mInitiatedEventList = new ArrayList<>();
        private EventDataSource mDB;
        private Context mContext;



        public MyEventsAsJoiner() {
            // Required empty public constructor
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            final View view = inflater.inflate(R.layout.fragment_my_events_as_starter, container, false);
            mListView = (ListView)view.findViewById(R.id.initiated_event_list);
            mContext = getActivity();
            mApater = new initiatedEventsAdapter(mContext, mInitiatedEventList);
            mListView.setAdapter(mApater);
            mDB = new EventDataSource(mContext);
//        mListView.setOnItemClickListener(new EventListFragment.ListClickHandler());
            if (savedInstanceState == null) {
                getLoaderManager().initLoader(0, null, this).forceLoad();
            }
            return view;
        }

        @Override
        public Loader<List<Event>> onCreateLoader(int id, Bundle args) {
            //load all data in myownevent table
            mInitiatedEventList = mDB.queryEventByJoinerId(Globals.currentUser.getId());
            return new RecordLoader(mContext);
        }

        @Override
        public void onLoadFinished(Loader<List<Event>> loader, List<Event> data) {
            mInitiatedEventList.addAll(data);
            mApater.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<List<Event>> loader) {

        }

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

    static class RecordLoader extends AsyncTaskLoader<List<Event>> {
        private EventDataSource mDB;
        public RecordLoader(Context context) {
            super(context);
            mDB = new EventDataSource(context);
        }
        // get all records in background as loader
        @Override
        public List<Event> loadInBackground() {
            return mDB.queryEventByJoinerId(Globals.currentUser.getId());
        }

    }
}
