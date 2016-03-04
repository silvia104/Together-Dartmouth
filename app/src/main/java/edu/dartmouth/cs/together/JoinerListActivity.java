package edu.dartmouth.cs.together;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class JoinerListActivity extends BasePopoutActivity {
    @Bind(R.id.joinerRecVew) RecyclerView mJoinerRecVew;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joiner_list);
        ButterKnife.bind(this);
        mJoinerRecVew.setHasFixedSize(true);
        mJoinerRecVew.setLayoutManager(new LinearLayoutManager(this));
        mJoinerRecVew.setAdapter(new JoinerCardViewAdapter(this));
        setHomeButton("Participants");
        mDateReloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
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
        return new JoinerLoader(this, mEventId);
    }

    @Override
    public void onLoadFinished(Loader<List<User>> loader, List<User> data) {
        mJoiners = data;
        mAdapter.updateAdapter(mJoiners);
        mProgress.setVisibility(View.GONE);
        mJoinerRecVew.setVisibility(View.VISIBLE);
        if (data.size() == 0){
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
        public JoinerLoader(Context context, long eventId) {
            super(context);
            mContext = context;
            mEventId = eventId;
        }

        @Override
        public List<User> loadInBackground() {
            return new UserDataSource(mContext).queryJoiners(mEventId);
        }
    }

    private void downloadJoiners(){
        Intent i = new Intent(getApplicationContext(), GetJoinerIntentService.class);
        i.putExtra(Event.ID_KEY,mEventId);
        getApplication().startService(i);
    }
}
