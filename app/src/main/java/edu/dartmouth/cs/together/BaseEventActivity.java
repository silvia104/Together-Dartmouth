package edu.dartmouth.cs.together;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;


import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.dartmouth.cs.together.data.Event;
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
    @Bind(R.id.seekBar) SeekBar mLimit;
    @Bind(R.id.limitCount) TextView mLimitCount;
    protected BottomSheetBehavior mBtmShtBehavior;
    protected int mCategoryIdx = -1;
    protected LatLng mLatLng;
    protected int mLimitNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_editor);
        ButterKnife.bind(this);
        setBottomSheet();
        mCategoryRecView.setVisibility(View.GONE);
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

    protected void displayEventValues(Event event){
        if (event == null) return;
        mCategoryTv.setText(event.getCategotyName());
        mCategoryTv.setTag("");
        mCategoryIdx = event.getCategoryIdx();
        mShortDesc.setText(event.getShortdesc());
        mShortDesc.setTag("");
        mLongDesc.setText(event.getLongDesc());
        mLocationTv.setText(event.getLocation());
        mLocationTv.setTag("");
        mDuration.setText(event.getDuration());
        mDateText.setText(event.getDate());
        mDateText.setTag("");
        mTimeText.setText(event.getTime());
        mTimeText.setTag("");
        mLimitNum = event.getLimit();
        mLimitCount.setText("Jointer Number Limit: " + mLimitNum);
        mLimit.setProgress(mLimitNum);
        mLatLng = event.getLatLng();
    }

    protected void disableLimitSeekbar(){
        Drawable thumb = mLimit.getThumb();
        thumb.setTint(0x664E342E);
        thumb.mutate();
        Drawable progress = mLimit.getProgressDrawable();
        progress.setTint(0x664E342E);
        progress.mutate();
        mLimit.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

}
