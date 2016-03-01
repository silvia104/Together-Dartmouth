package edu.dartmouth.cs.together;

import android.graphics.drawable.Drawable;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import butterknife.OnClick;
import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.utils.Globals;
import edu.dartmouth.cs.together.utils.Helper;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventEditorActivity extends BaseEventActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    private static final int SHORT_DESCRIPTION_DIALOG = 0;
    private static final int LONG_DESCRIPTION_DIALOG = 1;
    private static final int LOCATION_DIALOG = 2;
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
    private Event mEvent = Globals.event;
    private Calendar mNow = Calendar.getInstance();
    private Calendar mTime = Calendar.getInstance();
    private int mEventIdx;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeButton("Event Editor");
        setCategories();
        setQa();
        setLimit();
        Intent i = getIntent();
        mEventIdx = i.getIntExtra(Globals.EVENT_INDEX_KEY, 0);
        if (mEventIdx > -1) {
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
            //TODO: Hook mEvent with mEventIdx
            displayEventValues(mEvent);
        }

        if (savedInstanceState == null) {
            //displayEventValues(mEvent);
        } else {
            restoreValues(savedInstanceState);
        }

    }

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
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //noinspection SimplifiableIfStatement
            case R.id.action_cancelevent:
                showConfirmDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.locationLayout)
    public void onLocationClick(View view) {
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
        getMenuInflater().inflate(R.menu.cancelevent, menu);
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

    }

    private void setLimit() {
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
                    mLocationTv.setText(
                            getString(R.string.wronglocation));
                    mLocationTv.setTextColor(Color.RED);
                    mLatLng = null;
                } else {
                    mLocationTv.setTextColor(Color.GRAY);
                    mLatLng = latlng;
                    String address = place.getAddress().toString();
                    if (address.length() == 0) {
                        ShowInputDialog(LOCATION_DIALOG);
                    } else {
                        mLocationTv.setText(place.getName() + "\n"
                                + place.getAddress());
                    }

                }
            }
        }
    }

    private void setQa() {
        QaRecVewAdapter mQaAdapter = new QaRecVewAdapter(this, generateQuestions(), true);
        setBottonRecView(mQaAdapter);
    }

    private void setCategories() {
        mCategoryRecView.setLayoutManager(new GridLayoutManager(this, 5));
        mCategoryRecView.setHasFixedSize(true);
        List<Integer> categoryIconList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            categoryIconList.add(R.drawable.movie);
        }
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
        return true;
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

    }

    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirmDeleteEvent))
                .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO:
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    private void ShowInputDialog(final int type) {

        final Dialog inputDialog = createInputDialog(type);
        inputDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button cancel = (Button) inputDialog.findViewById(R.id.cancelBtn);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputDialog.cancel();
                    }
                });
                Button save = (Button) inputDialog.findViewById(R.id.saveBtn);
                save.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText editText;
                                switch (type) {
                                    case LONG_DESCRIPTION_DIALOG:
                                        editText = (EditText) inputDialog.findViewById(R.id.editLongDesc);
                                        mLongDesc.setText(editText.getText());
                                        inputDialog.cancel();
                                        break;
                                    case SHORT_DESCRIPTION_DIALOG:
                                        editText = (EditText) inputDialog.findViewById(R.id.editShortDesc);
                                        if (editText.getText().length() == 0) {
                                            editText.setHint("Short description can't be empty!");
                                            editText.setHintTextColor(Color.RED);
                                        } else {
                                            mShortDesc.setText(editText.getText());
                                            mShortDesc.setTag("");
                                            inputDialog.cancel();

                                        }
                                        break;
                                    case LOCATION_DIALOG:
                                        editText = (EditText) inputDialog.findViewById(R.id.editLongDesc);
                                        if (editText.getText().length() == 0) {
                                            editText.setHint("Location description can't be empty!");
                                            editText.setHintTextColor(Color.RED);
                                        } else {
                                            mLocationTv.setText(editText.getText());
                                            mLocationTv.setTextColor(Color.GRAY);
                                            mLocationTv.setTag("");
                                            inputDialog.cancel();
                                        }
                                        break;
                                }
                            }

                        }

                );
            }
        });
        inputDialog.show();
        inputDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        inputDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private Dialog createInputDialog(int type) {
        View v = this.getLayoutInflater().inflate(R.layout.dialog_input, null);
        EditText shortDesc = (EditText) v.findViewById(R.id.editShortDesc);
        EditText longDesc = (EditText) v.findViewById(R.id.editLongDesc);
        ;
        String title = "";
        switch (type) {
            case SHORT_DESCRIPTION_DIALOG:
                longDesc.setVisibility(View.GONE);
                title = "Input Short Description";
                if (mShortDesc.getText().length() > 0) {
                    shortDesc.setText(mShortDesc.getText());
                    shortDesc.setSelection(shortDesc.getText().length());

                }
                break;
            case LONG_DESCRIPTION_DIALOG:
                shortDesc.setVisibility(View.GONE);
                title = "Input Long Description";
                if (mLongDesc.getText().length() > 0) {
                    longDesc.setText(mLongDesc.getText());
                    longDesc.setSelection(longDesc.getText().length());
                }
                break;
            case LOCATION_DIALOG:
                shortDesc.setVisibility(View.GONE);
                title = "Input Location Description";
                break;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v)
                .setTitle(title);
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
