package edu.dartmouth.cs.together;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;

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
        mPostButton.setVisibility(View.GONE);
        mCancelButton.setVisibility(View.GONE);
        mPinInMap.setVisibility(View.GONE);
    }
    @OnClick(R.id.fab)
    public void onFabClick(){
        finish();
    }
}
