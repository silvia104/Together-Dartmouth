package edu.dartmouth.cs.together;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import butterknife.OnClick;
import edu.dartmouth.cs.together.cloud.PostEventIntentService;
import edu.dartmouth.cs.together.cloud.QaIntentService;
import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.data.EventDataSource;
import edu.dartmouth.cs.together.data.Qa;
import edu.dartmouth.cs.together.utils.Globals;
import edu.dartmouth.cs.together.utils.Helper;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

// activity to to edit event

public class EventEditorActivity extends BaseEventActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private static final String LATLNG_KEY ="CANLENDAR_KEY" ;
    private static final String CATEGORY_KEY = "category";
    private static final String SHORT_DESC_KEY="shortdesc";
    private static final String LOCATION_KEY="location";
    private static final String LONG_DESC_KEY="longdesc";
    private static final String DATE_KEY="date";
    private static final String TIME_KEY="time";
    private static final String LIMIT_KEY="limit";
    private static final String DURATION_KEY="duration";
    private static final String MILLIS_KEY="millis";
    private static final String LIMIT_PREFIX = "Max Participants Number: ";

    private int PLACE_PICKER_REQUEST = 1;
    private Calendar mNow = Calendar.getInstance();
    private Calendar mTime = Calendar.getInstance();
    private long mEventId=-1;

    private boolean mRefreshed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeButton("Event Editor");
        setCategories();
        setLimit();

        mQaAdapter = new QaRecVewAdapter(this, new ArrayList<Qa>(), true);
        setBottonRecView(mQaAdapter);
        mJoinBtn.setVisibility(View.GONE);

        Intent i = getIntent();
        mEventId = i.getLongExtra(Event.ID_KEY, -1);
        if (mEventId != -1){
            new LoadEventAsyncTask(EventDataSource.MY_OWN_EVENT).execute(mEventId);
        }
        double lat = i.getDoubleExtra(Globals.MAP_LATITUDE,0);
        double lng = i.getDoubleExtra(Globals.MAP_LONGITUDE,0);
        if (lat!=0 && lng != 0){
            mLatLng = new LatLng(lat,lng);
            mLocationTv.setText(mLatLng.toString());
            mLocationTv.setTag("");
        }
        if (savedInstanceState != null) {
            restoreValues(savedInstanceState);
        }

        if (mEventId != -1) {
            disableLimitSeekbar();
            findViewById(R.id.categoryLayout).setOnClickListener(null);
            mPostButton.setText(getString(R.string.re_post));
            new LoadEventAsyncTask(EventDataSource.MY_OWN_EVENT).execute(mEventId);
            getLoaderManager().initLoader(0,null,this).forceLoad();
        } else {
            action=Globals.ACTION_ADD;
            mProgress.setVisibility(View.GONE);
            mContentlayout.setVisibility(View.VISIBLE);
            mEvent = new Event();
            mFab.hide();
        }
        setBtmShtBehavior(mEventId);

        mDateReloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRefreshed = true;
                getLoaderManager().restartLoader(0,null,EventEditorActivity.this).forceLoad();
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
                if (id == -1L) {
                    finish();
                    return;
                }
                new LoadEventAsyncTask(EventDataSource.MY_OWN_EVENT).execute(mEventId);
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
    public Loader<List<Qa>> onCreateLoader(int id, Bundle args) {
        return new QaLoader(getApplicationContext(),mEventId);
    }

    @Override
    public void onLoadFinished(Loader<List<Qa>> loader, List<Qa> data) {
        super.onLoadFinished(loader, data);
        if (data.size()==0){
            Intent i = new Intent(getApplicationContext(),
                    QaIntentService.class);
            i.putExtra(Event.ID_KEY,mEventId);
            startService(i);
        }else {
            mQaAdapter.updateData(data);
        }

    }

    // save status of the activity

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (!isTextViewEmpty(mCategoryTv)) {
            outState.putInt(CATEGORY_KEY,mCategoryIdx);
        }
        if(!isTextViewEmpty(mShortDesc)) {
            outState.putString(SHORT_DESC_KEY, mShortDesc.getText().toString());
        }
        outState.putString(LONG_DESC_KEY, mLongDesc.getText().toString());
        if (!isTextViewEmpty(mDateText)){
            outState.putString(DATE_KEY,mDateText.getText().toString());
        }
        if (!isTextViewEmpty(mTimeText)){
            outState.putString(TIME_KEY, mTimeText.getText().toString());
        }
        outState.putLong(MILLIS_KEY, mTime.getTimeInMillis());
        if (mLatLng!=null) {
            outState.putParcelable(LATLNG_KEY, mLatLng);
        }
        if (!isTextViewEmpty(mLocationTv)) {
            outState.putString(LOCATION_KEY, mLocationTv.getText().toString());
        }
        outState.putInt(LIMIT_KEY, mLimitNum);
        outState.putString(DURATION_KEY, mDuration.getText().toString());
        outState.putLong(Event.ID_KEY, mEventId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cancelevent:
                showConfirmDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.editLocationDescBtn)
    public void onEditLocDescClick(View view){
        ShowInputDialog(LOCATION_DIALOG);
    }

    @OnClick(R.id.locationText)
    public void onLocationClick(View view) {
        mLocationTv.setError(null);
        int PLACE_PICKER_REQUEST = 1;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        builder.setLatLngBounds(
                Helper.convertCenterAndRadiusToBounds(Globals.DARTMOUTH_GPS,
                        Globals.RADIUS_50MILES));
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mEvent !=null &&Globals.currentUser.getId()==mEvent.getOwner()) {
            getMenuInflater().inflate(R.menu.cancelevent, menu);
            super.onCreateOptionsMenu(menu);
        }
        return true;
    }

    @OnClick(R.id.categoryLayout)
    public void onCategoryClick(View view) {
        mCategoryRecView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.longdescLayout)
    public void onLongDescClick(View view) {
        ShowInputDialog(LONG_DESCRIPTION_DIALOG);
    }

    @OnClick(R.id.shortDescLayout)
    public void onShortDescClick(View view) {
        mShortDesc.setError(null);
        ShowInputDialog(SHORT_DESCRIPTION_DIALOG);

    }

    @OnClick(R.id.dateText)
    public void onDateTextClick() {
        DatePickerDialog dialog = new DatePickerDialog(this, this, mNow.get(Calendar.YEAR),
                mNow.get(Calendar.MONTH), mNow.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(mNow.getTimeInMillis());
        dialog.getDatePicker().setMaxDate(mNow.getTimeInMillis()+13*24*3600*1000);
        dialog.show();
    }

    @OnClick(R.id.timeText)
    public void onTimeTextClick() {
        new TimePickerDialog(this, this, mNow.get(Calendar.HOUR_OF_DAY),
                mNow.get(Calendar.MINUTE), true).show();
    }

    @OnClick(R.id.addDuration)
    public void onAddDurationClick() {
        int cur = Integer.parseInt(mDuration.getText().toString());
        if (cur < 24) {
            cur++;
            mDuration.setText("" + cur);
        }
    }

    @OnClick(R.id.decreateDuration)
    public void onDecreaseDurationClick() {
        int cur = Integer.parseInt(mDuration.getText().toString());
        if (cur > 1) {
            cur--;
            mDuration.setText("" + cur);
        }
    }

    @OnClick(R.id.postBtn)
    public void onPostClick() {
        //TODO:
        if (saveEvent()) {
            finish();
        }
    }

    @OnClick(R.id.cancelBtn)
    public void onCancleClick() {
        finish();
    }

    private void restoreValues(Bundle state){
        String str = null;
        if ( (mCategoryIdx = state.getInt(CATEGORY_KEY,-1)) != -1){
            mCategoryTv.setTag("");
            mCategoryTv.setText(Globals.categories.get(mCategoryIdx));
        }
        if ((str = state.getString(SHORT_DESC_KEY,null))!=null){
            mShortDesc.setTag("");
            mShortDesc.setText(str);
        }
        if ((str = state.getString(LOCATION_KEY,null) )!= null){
            mLocationTv.setTag("");
            mLocationTv.setText(str);
        }
        if ((str = state.getString(DATE_KEY,null)) != null){
            mDateText.setTag("");
            mDateText.setText(str);
        }
        if ((str = state.getString(TIME_KEY,null)) != null){
            mTimeText.setTag("");
            mTimeText.setText(str);
        }
        mLongDesc.setText(state.getString(LONG_DESC_KEY));
        mLatLng = state.getParcelable(LATLNG_KEY);
        mTime = Calendar.getInstance();
        mTime.setTimeInMillis(state.getLong(MILLIS_KEY));
        mLimitNum = state.getInt(LIMIT_KEY,0);
        mLimitCount.setText(LIMIT_PREFIX+mLimitNum);
        mDuration.setText(state.getString(DURATION_KEY));
        mEventId = state.getLong(Event.ID_KEY,-1);
    }

    private void setLimit() {
        mLimitCount.setText(LIMIT_PREFIX + 0);
        mLimit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mLimitNum = progress;
                mLimitCount.setText(LIMIT_PREFIX + progress);
            }
        });
    }

    // get location info from place finder
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                LatLng latlng = place.getLatLng();
                float[] result = new float[1];
                Location.distanceBetween(Globals.DARTMOUTH_GPS.latitude,
                        Globals.DARTMOUTH_GPS.longitude, latlng.latitude,
                        latlng.longitude, result);
                if (result[0] > Globals.RADIUS_50MILES) {
                    mLocationTv.setError(
                            getString(R.string.wronglocation));
                    mLatLng = null;
                } else {
                    mLatLng = latlng;
                    String address = place.getAddress().toString();
                    if (address.length() == 0) {
                        ShowInputDialog(LOCATION_DIALOG);
                    } else {
                        mLocationTv.setText(place.getName() + "\n"
                                + place.getAddress());
                        mLocationTv.setTag("");
                    }

                }
            }
        }
    }
    private void setCategories() {
        mCategoryRecView.setLayoutManager(new GridLayoutManager(this, 5));
        mCategoryRecView.setHasFixedSize(true);
        List<Integer> categoryIconList = Arrays.asList(Globals.categoriIcons);
        mCategoryRecView.setAdapter(new CategoryAdapter(this, categoryIconList, R.layout.item_imagecard));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mTime.set(year, monthOfYear, dayOfMonth);
        mDateText.setText(Helper.dateToString(mTime));
        mDateText.setText(Helper.dateToString(mTime));
        mDateText.setTag("");
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (mTime.get(Calendar.DAY_OF_YEAR) == mNow.get(Calendar.DAY_OF_YEAR)
                &&hourOfDay < mNow.get(Calendar.HOUR_OF_DAY) + 1) {
            Toast.makeText(getApplicationContext(), "Time must be at least one hour later than now"
                    , Toast.LENGTH_SHORT).show();
            return;
        }
        mTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mTime.set(Calendar.MINUTE, minute);
        mTimeText.setText(Helper.timeToString(mTime));
        mTimeText.setTag("");
    }

    class CategoryAdapter extends ImageRecVewAdapter {
        public CategoryAdapter(Context context, List<Integer> drawableIdListList, int layoutId) {
            super(context, drawableIdListList, layoutId);
        }

        @Override
        public void onBindViewHolder(final ImageCardViewHolder holder, int i) {
            super.onBindViewHolder(holder, i);
            final int index = i;
            holder.mImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCategoryIdx = index;
                    mCategoryTv.setText(Globals.categories.get(index));
                    mCategoryIcon.setImageResource(Globals.categoriIcons[index]);
                    mCategoryRecView.setVisibility(View.GONE);
                    mCategoryTv.setTag("");

                }
            });
        }
    }

    private boolean isTextViewEmpty(TextView v) {
        String tag = (String) v.getTag();
        return tag.equalsIgnoreCase(getString(R.string.empty_tag));
    }

    // check event info and save it
    private boolean saveEvent() {
        if (isTextViewEmpty(mCategoryTv)) {
            showToast("Please select a category");
            return false;
        }
        if (isTextViewEmpty(mShortDesc)) {
            showToast("Please provide short description!");
            return false;
        }
        if (mLatLng == null) {
            showToast("Please provide location!");
            return false;
        }

        if (isTextViewEmpty(mDateText)) {
            showToast("Please provide date!");
            return false;
        }
        if (isTextViewEmpty(mTimeText)) {
            showToast("Please provide time!");
            return false;
        }
        mEvent.setShortDesc(mShortDesc.getText().toString());
        mEvent.setLongDesc(mLongDesc.getText().toString());
        mEvent.setLocation(mLocationTv.getText().toString());
        mEvent.setDateTime(mTime);
        mEvent.setCategory(mCategoryIdx);
        mEvent.setDuration(Integer.parseInt(mDuration.getText().toString()));
        mEvent.setLimit(mLimitNum);
        mEvent.setLatLng(mLatLng);
        mEvent.setOwnerId(Globals.currentUser.getId());
        if(action.equals(Globals.ACTION_ADD)){
            mEvent.setEventId(Helper.intToUnsignedLong(
                    (mEvent.getOwner() + "" + System.currentTimeMillis()).hashCode()));
        }
        new EventOperationAsyncTask().execute(mEvent);
        return true;
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

    }

    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirmDeleteEvent))
                .setPositiveButton(getString(R.string.DELETE), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        action = Globals.ACTION_DELETE;
                        new EventOperationAsyncTask().execute(mEvent);
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }


    // add or delete event aysnc task
    class EventOperationAsyncTask extends AsyncTask<Event,Void,Long>{
        @Override
        protected Long doInBackground(Event... events) {
            EventDataSource db = new EventDataSource(getApplicationContext());
            Event event = events[0];
            long local_id= -1;
            if (action.equals(Globals.ACTION_ADD)) {
                local_id = db.insertEvent(EventDataSource.MY_OWN_EVENT, event);
            } else if (action.equals(Globals.ACTION_UPDATE)){
                local_id = db.updateEvent(EventDataSource.MY_OWN_EVENT,event.getEventId(), event);
            }
            if (local_id != -1 || action.equals(Globals.ACTION_DELETE)) {
                Intent i = new Intent(getApplicationContext(), PostEventIntentService.class);
                i.putExtra(Globals.ACTION_KEY, action);
                i.putExtra(Event.ID_KEY,event.getEventId());
                getApplication().startService(i);
            }
            return local_id;
        }
    }
}
