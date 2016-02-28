package edu.dartmouth.cs.together;

import android.os.Bundle;

import butterknife.OnClick;
import edu.dartmouth.cs.together.utils.Globals;

public class EventDetailActivity extends BaseEventActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeButton("Event Details");
        QaRecVewAdapter mQaAdapter = new QaRecVewAdapter(this, generateQuestions(), false);
        setBottonRecView(mQaAdapter);
        mFab.setImageResource(R.drawable.ic_person_add);
        mCategoryTv.setText(Globals.event.getCategotyName());
        mLocationTv.setText(Globals.event.getLocation());
        mLocationTv.setTextSize(20);
    }
    @OnClick(R.id.fab)
    public void onFabClick(){
        finish();
    }
}
