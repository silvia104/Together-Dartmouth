package edu.dartmouth.cs.together;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import butterknife.OnClick;
import edu.dartmouth.cs.together.cloud.JoinQuitEventIntentService;
import edu.dartmouth.cs.together.cloud.PostEventIntentService;
import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.data.EventDataSource;
import edu.dartmouth.cs.together.data.User;
import edu.dartmouth.cs.together.utils.Globals;

public class EventDetailActivity extends BaseEventActivity {
    private long mEventId;
    private int mEventType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeButton("Event Details");
        QaRecVewAdapter mQaAdapter = new QaRecVewAdapter(this, generateQuestions(), false);
        setBottonRecView(mQaAdapter);
        mFab.setImageResource(R.drawable.ic_person_add);
        mPostButton.setVisibility(View.GONE);
        mCancelButton.setVisibility(View.GONE);
        mAddDuration.setVisibility(View.GONE);
        mDecreaseDuration.setVisibility(View.GONE);
        disableLimitSeekbar();

        Intent i = getIntent();
        mEventId = i.getLongExtra(Globals.EVENT_INDEX_KEY, -1);
        mEventType = i.getIntExtra(Globals.EVENT_TYPE_KEY, EventDataSource.ALL_EVENT);


        //TODO: to remove
        mEventId = getSharedPreferences(getPackageName(), MODE_PRIVATE).getLong(
                Event.ID_KEY, -1);
        mEventType = EventDataSource.MY_OWN_EVENT;



        if (mEventId!=-1) {
            new LoadEventAsyncTask(mEventType).execute(mEventId);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.quit, menu);
        return true;
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

    @OnClick(R.id.fab)
    public void onFabClick() {
        new JoinEventAsyncTask().execute(mEvent);
        finish();
    }

    class JoinEventAsyncTask extends AsyncTask<Event,Void,Long> {
        @Override
        protected Long doInBackground(Event... events) {
            EventDataSource db = new EventDataSource(getApplicationContext());
            Event event = events[0];
            long local_id= -1;
            local_id = db.insertEvent(EventDataSource.JOINED_EVENT, event);
            Intent i = new Intent(getApplicationContext(), JoinQuitEventIntentService.class);
            i.putExtra(Event.ID_KEY, mEvent.getEventId());
            //TODO: change userID
            long userId = Globals.currentUser.getId();

            i.putExtra(User.ID_KEY, userId);
            i.putExtra(Globals.ACTION_KEY,Globals.ACTION_JOIN);
            getApplication().startService(i);
            return local_id;
        }
    }
}
