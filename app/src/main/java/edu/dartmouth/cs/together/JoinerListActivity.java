package edu.dartmouth.cs.together;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.dartmouth.cs.together.cloud.GetJoinerIntentService;
import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.data.EventDataSource;
import edu.dartmouth.cs.together.data.User;
import edu.dartmouth.cs.together.data.UserDataSource;
import edu.dartmouth.cs.together.utils.Globals;

public class JoinerListActivity extends BasePopoutActivity implements
        LoaderManager.LoaderCallbacks<List<User>> {
    @Bind(R.id.joinerRecVew) RecyclerView mJoinerRecVew;
    @Bind(R.id.joiner_progress) ProgressBar mProgress;
    private BroadcastReceiver mDateReloadReceiver;
    private List<User> mJoiners = new ArrayList<>();
    private long mEventId;
    private JoinerCardViewAdapter mAdapter;
    private boolean mRefreshed = false;
    private long mOwnerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joiner_list);
        ButterKnife.bind(this);
        Intent i = getIntent();
        mEventId = i.getLongExtra(Event.ID_KEY,-1);
        mOwnerId = i.getLongExtra(User.ID_KEY,-1);
        mJoinerRecVew.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
        mJoinerRecVew.setHasFixedSize(true);
        mAdapter = new JoinerCardViewAdapter(this,mJoiners);
        mJoinerRecVew.setLayoutManager(new LinearLayoutManager(this));

        mJoinerRecVew.setAdapter(mAdapter);
        setHomeButton("Participants");
        mDateReloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRefreshed=true;
                getLoaderManager().restartLoader(0,null,JoinerListActivity.this).forceLoad();
            }
        };
        if (mDateReloadReceiver != null){
            getApplicationContext().registerReceiver(mDateReloadReceiver,
                    new IntentFilter(Globals.RELOAD_JOINER_DATA));
        }
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    protected void onDestroy() {
        getApplicationContext().unregisterReceiver(mDateReloadReceiver);
        super.onDestroy();
    }

    @Override
    public Loader<List<User>> onCreateLoader(int id, Bundle args) {
        return new JoinerLoader(this, mEventId, mOwnerId);
    }

    @Override
    public void onLoadFinished(Loader<List<User>> loader, List<User> data) {
        mJoiners = data;
        mAdapter.updateAdapter(mJoiners);
        mProgress.setVisibility(View.GONE);
        mJoinerRecVew.setVisibility(View.VISIBLE);
        if (data.size() == 0 || !mRefreshed){
            downloadJoiners();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh){
            downloadJoiners();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoaderReset(Loader<List<User>> loader) {

    }

    static class JoinerLoader extends AsyncTaskLoader<List<User>>{
        private Context mContext;
        private long mEventId;
        private long mUserId;
        public JoinerLoader(Context context, long eventId, long userId) {
            super(context);
            mContext = context;
            mEventId = eventId;
            mUserId = userId;
        }

        @Override
        public List<User> loadInBackground() {
            List<User> users =  new UserDataSource(mContext).queryJoiners(mEventId);
            users.add (0,new UserDataSource(mContext).queryUserById(mUserId));
            return users;
        }
    }

    private void downloadJoiners(){
        Intent i = new Intent(getApplicationContext(), GetJoinerIntentService.class);
        i.putExtra(Event.ID_KEY,mEventId);
        getApplication().startService(i);
    }
}
