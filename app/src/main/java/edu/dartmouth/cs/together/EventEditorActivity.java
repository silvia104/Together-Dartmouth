package edu.dartmouth.cs.together;

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
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

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
        TimePickerDialog.OnTimeSetListener{
    private int PLACE_PICKER_REQUEST = 1;
    private Event mEvent = Globals.event;
    private Calendar mNow = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeButton("Event Editor");
        setCategories();
        setQa();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            //noinspection SimplifiableIfStatement
            case R.id.action_cancelevent:
                showConfirmDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.locationText)
    public void onLocationClick(View view) {
        int PLACE_PICKER_REQUEST = 1;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cancelevent, menu);
        return true;
    }

    @OnClick(R.id.categoryText)
    public void onCategoryClick(View view){
        mCategoryRecView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.longDesc)
    public void onLongDescClick(View view){
        ShowInputDialog(false);
    }

    @OnClick(R.id.shortDescText)
    public void onShortDescClick(View view){
        ShowInputDialog(true);

    }

    @OnClick(R.id.dateText)
    public void onDateTextClick(){
        new DatePickerDialog(this,this,mNow.get(Calendar.YEAR),
                mNow.get(Calendar.MONTH), mNow.get(Calendar.DAY_OF_MONTH)).show();
    }
    @OnClick(R.id.timeText)
    public void onTimeTextClick(){
        new TimePickerDialog(this,this,mNow.get(Calendar.HOUR_OF_DAY),
                mNow.get(Calendar.MINUTE),true).show();
    }

    @OnClick(R.id.addDuration)
    public void onAddDurationClick(){
        int cur = Integer.parseInt(mDuration.getText().toString());
        if (cur<24){
            cur ++;
            mEvent.setDuration(cur);
            mDuration.setText(""+cur);
        }
    }
    @OnClick(R.id.decreateDuration)
    public void onDecreaseDurationClick(){
        int cur = Integer.parseInt(mDuration.getText().toString());
        if (cur>1){
            cur --;
            mEvent.setDuration(cur);
            mDuration.setText(""+cur);
        }
    }

    @OnClick(R.id.postBtn)
    public void onPostClick(){
        //TODO:
        finish();
    }
    @OnClick(R.id.cancelBtn)
    public void onCancleClick(){
        //TODO:
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                mEvent.setLocation(place);
                LatLng latlng = place.getLatLng();
                float[] result = new float[1];
                Location.distanceBetween(Globals.DARTMOUTH_GPS.latitude,
                        Globals.DARTMOUTH_GPS.longitude, latlng.latitude,
                        latlng.longitude,result);
                if (result[0]> Globals.RADIUS_50MILES){
                    mLocationTv.setText(getString(R.string.wronglocation));
                    mLocationTv.setTextColor(Color.RED);
                }
                mEvent.setLatLng(place.getLatLng());
                mLocationTv.setText(Helper.getUnderlinedString(mEvent.getLocation()));
            }
        }
    }
    private void setQa(){
        QaRecVewAdapter mQaAdapter = new QaRecVewAdapter(this, generateQuestions(), true);
        setBottonRecView(mQaAdapter);
    }

    private void setCategories(){
        mCategoryRecView.setLayoutManager(new GridLayoutManager(this, 5));
        mCategoryRecView.setHasFixedSize(true);
        List<Integer> categoryIconList = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            categoryIconList.add(R.drawable.movie);
        }
        mCategoryRecView.setAdapter(new CategoryAdapter(this,categoryIconList, R.layout.item_imagecard));
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mEvent.setDate(year, monthOfYear, dayOfMonth);
        mDateText.setText(mEvent.getDate());
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mEvent.setTime(hourOfDay, minute);
        mTimeText.setText(mEvent.getTime());
    }

    class CategoryAdapter extends ImageRecVewAdapter{
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
                    mEvent.setCategory(index);
                    mCategoryTv.setText(Helper.getUnderlinedString(Globals.categories.get(index)));
                    mCategoryRecView.setVisibility(View.GONE);

                }
            });
        }
    }


    private void showConfirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirmDeleteEvent))
                .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    private void ShowInputDialog(final boolean isShortDesc){
       AlertDialog.Builder builder = new AlertDialog.Builder(this);
       View v = this.getLayoutInflater().inflate(R.layout.dialog_input, null);
       EditText shortDesc = (EditText)v.findViewById(R.id.editShortDesc);
       EditText longDesc = (EditText)v.findViewById(R.id.editLongDesc);;
       String title ;
       if (isShortDesc){
           longDesc.setVisibility(View.GONE);
           title = "Input Short Description";
           if (mEvent.getShortDesc().length()>0){
               shortDesc.setText(Helper.getUnderlinedString(mEvent.getShortDesc()));
           }
       } else {
           shortDesc.setVisibility(View.GONE);
           title = "Input Long Description";
           if (mEvent.getLongDesc().length()>0){
               longDesc.setText(Helper.getUnderlinedString(mEvent.getLongDesc()));
           }
       }
       builder.setView(v)
            .setTitle(title)
            .setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // FIRE ZE MISSILES!
                }
            })
            .setNegativeButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (!isShortDesc) {
                        EditText editText = (EditText) ((Dialog) dialog).findViewById(R.id.editLongDesc);
                        mEvent.setLongDesc(editText.getText().toString());
                        mLongDesc.setText(Helper.getUnderlinedString(mEvent.getLongDesc()));
                    } else {
                        EditText editText = (EditText) ((Dialog) dialog).findViewById(R.id.editShortDesc);
                        mEvent.setShortDesc(editText.getText().toString());
                        mShortDesc.setText(Helper.getUnderlinedString(mEvent.getShortDesc()));
                    }
                }
            });
       // Create the AlertDialog object and return it
       builder.create().show();
    }
}
