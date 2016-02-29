package edu.dartmouth.cs.together;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.dartmouth.cs.together.data.Qa;

/**
 * Created by TuanMacAir on 2/27/16.
 */
public class BaseEventActivity extends BasePopoutActivity {
    @Bind(R.id.bottomRecView)  RecyclerView mBottomRecView;
    @Bind(R.id.locationText)  TextView mLocationTv;
    @Bind(R.id.categoryText)  TextView mCategoryTv;
    @Bind(R.id.coordinatorLayout)  CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.categoryRecView)  RecyclerView mCategoryRecView;
    @Bind(R.id.fab) FloatingActionButton mFab;
    @Bind(R.id.dateText) TextView mDateText;
    @Bind(R.id.timeText) TextView mTimeText;
    @Bind(R.id.durationCount) TextView mDuration;
    @Bind(R.id.addDuration) ImageButton mAddDuration;
    @Bind(R.id.decreateDuration) ImageButton mDecreaseDuration;
    @Bind(R.id.shortDescText) TextView mShortDesc;
    @Bind(R.id.longDesc) TextView mLongDesc;
    @Bind(R.id.cancelBtn) Button mCancelButton;
    @Bind(R.id.postBtn) Button mPostButton;
    @Bind(R.id.pinInMap) ImageButton mPinInMap;
    protected BottomSheetBehavior mBtmShtBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_editor);
        ButterKnife.bind(this);
        setBottomSheet();
        mCategoryRecView.setVisibility(View.GONE);
        mFab.setRippleColor(Color.parseColor("#224DB6AC"));
    }

    protected void setBottonRecView(RecyclerView.Adapter adapter){
        mBottomRecView.setHasFixedSize(false);
        mBottomRecView.setLayoutManager(new LinearLayoutManager(this));
        mBottomRecView.setAdapter(adapter);
    }

    protected void setBottomSheet(){
        View bottomSheet = mCoordinatorLayout.findViewById(R.id.bottom_sheet);
        mBtmShtBehavior = BottomSheetBehavior.from(bottomSheet);
        setBtmShtBehavior();
    }

    protected void setBtmShtBehavior(){
        mBtmShtBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_SETTLING:
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        mFab.show();
                        break;
                    default:
                        mFab.hide();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });
        mBtmShtBehavior.setPeekHeight(80);
    }

    @OnClick(R.id.fab)
    public void onFabClick(){
        startActivity(new Intent(getApplicationContext(), JoinerListActivity.class));
    }
    protected List<Qa> generateQuestions() {
        List<Qa> qandAs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            qandAs.add(new Qa("Question" + (i + 1), "Answer!"));
        }

        return qandAs;
    }


}
