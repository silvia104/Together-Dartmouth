package edu.dartmouth.cs.together;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;

import butterknife.OnClick;
import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.utils.Globals;

public class EventDetailActivity extends BaseEventActivity {
    private Event mEvent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEvent = Globals.event;
        setHomeButton("Event Details");
        QaRecVewAdapter mQaAdapter = new QaRecVewAdapter(this, generateQuestions(), false);
        setBottonRecView(mQaAdapter);
        mFab.setImageResource(R.drawable.ic_person_add);
        mPostButton.setVisibility(View.GONE);
        mCancelButton.setVisibility(View.GONE);
        mAddDuration.setVisibility(View.GONE);
        mDecreaseDuration.setVisibility(View.GONE);
        displayEventValues(mEvent);
        disableLimitSeekbar();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_refresh:
                //TODO:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    public void onFabClick(){
        finish();
    }
}
