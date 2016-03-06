package edu.dartmouth.cs.together;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import java.util.List;

import butterknife.OnClick;
import edu.dartmouth.cs.together.cloud.JoinQuitEventIntentService;
import edu.dartmouth.cs.together.cloud.QaIntentService;
import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.data.EventDataSource;
import edu.dartmouth.cs.together.data.Qa;
import edu.dartmouth.cs.together.data.User;
import edu.dartmouth.cs.together.utils.Globals;

public class EventDetailActivity extends BaseEventActivity {
    private long mEventId;
    private int mEventType;
    private boolean mRefreshed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeButton("Event Details");

        mPostButton.setVisibility(View.GONE);
        mCancelButton.setVisibility(View.GONE);
        mAddDuration.setVisibility(View.GONE);
        mDecreaseDuration.setVisibility(View.GONE);
        mAddQuestion.setVisibility(View.VISIBLE);
        disableLimitSeekbar();
        mEditLocBtn.setVisibility(View.GONE);

        Intent i = getIntent();
        mEventId = i.getLongExtra(Event.ID_KEY, -1);
        mEventType = i.getIntExtra("TAG", EventDataSource.ALL_EVENT);
        if (mEventId != -1){
            new LoadEventAsyncTask(mEventType).execute(mEventId);
        } else {
            finish();
        }
        if (mEventType == EventDataSource.JOINED_EVENT){
           mJoinBtn.setVisibility(View.GONE);
        }

        mDateReloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRefreshed = true;
                getLoaderManager().restartLoader(1,null,EventDetailActivity.this).forceLoad();
            }
        };
        if (mDateReloadReceiver != null){
            this.registerReceiver(mDateReloadReceiver,
                    new IntentFilter(Globals.RELOAD_QUESTION_DATA_IN_DETAIL));
        }
        mEventUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(Event.ID_KEY, mEventId);
                if (id == 0L) {
                    finish();
                    return;
                }
                if (intent.getBooleanExtra(Globals.ACTION_JOIN, false)){
                    mJoinBtn.setVisibility(View.GONE);
                }
                new LoadEventAsyncTask(mEventType).execute(mEventId);
            }
        };
        if (mEventUpdateReceiver != null){
            this.registerReceiver(mEventUpdateReceiver,
                    new IntentFilter(Globals.UPDATE_EVENT_DETAIL));
        }

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mEventUpdateReceiver);
        unregisterReceiver(mDateReloadReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mEventType==EventDataSource.JOINED_EVENT) {
            getMenuInflater().inflate(R.menu.quit, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_quit:
                Intent i = new Intent(getApplicationContext(), JoinQuitEventIntentService.class);
                i.putExtra(Event.ID_KEY,mEvent.getEventId());
                i.putExtra(User.ID_KEY, Globals.currentUser.getId());
                i.putExtra(Globals.ACTION_KEY,Globals.ACTION_QUIT);
                getApplication().startService(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.addQuestion)
    public void onAddQuestionClick(){
        ShowInputDialog(QUESTION_DIALOG);
    }

    @OnClick(R.id.joinBtn)
    public void onJoinClick(){
        Intent i = new Intent(getApplicationContext(), JoinQuitEventIntentService.class);
        i.putExtra(Event.ID_KEY,mEvent.getEventId());
        i.putExtra(User.ID_KEY, Globals.currentUser.getId());
        i.putExtra(Globals.ACTION_KEY,Globals.ACTION_JOIN);
        getApplication().startService(i);
    }

    @Override
    public Loader<List<Qa>> onCreateLoader(int id, Bundle args) {
        return new QaLoader(getApplicationContext(),mEventId);
    }

    @Override
    public void onLoadFinished(Loader<List<Qa>> loader, List<Qa> data) {
        super.onLoadFinished(loader, data);
        mQaAdapter.updateData(data);
        if (data.size()==0 || !mRefreshed){
            Intent i = new Intent(getApplicationContext(),
                    QaIntentService.class);
            i.putExtra(Event.ID_KEY,mEventId);
            startService(i);
        }
    }

}
