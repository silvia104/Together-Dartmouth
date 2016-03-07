package edu.dartmouth.cs.together;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import edu.dartmouth.cs.together.utils.Globals;
import edu.dartmouth.cs.together.utils.Helper;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener
        {


    private SeekBar mDistanceRangeSeekBar;
    private Spinner mTimeRangeSpinner;
    private ListView mInterestList;
    private ArrayAdapter<String> mListAdapter;
    private TextView mDistanceTextView;
    private Button mApplyButton;
    private Button mCancelButton;

    private int mSelectedTimeRange = 0;
    private int mSelectedDistanceRange = 0;
    private ArrayList<Integer> selectedInterest;
    private final int CLEAR_ALL_FILTER = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        selectedInterest = new ArrayList<>();
        mDistanceTextView = (TextView) findViewById(R.id.distance_value_text);
        mDistanceRangeSeekBar = (SeekBar) findViewById(R.id.distance_seek_bar);
        mApplyButton = (Button) findViewById(R.id.filter_apply_button);
        mCancelButton = (Button) findViewById(R.id.filter_cancel_button);

        setList();
        setSpinner();
        //set distance range
        mDistanceRangeSeekBar.setOnSeekBarChangeListener(this);
        // if the filter is set by the user, don't load shared preference
        // get shared preference for default distance, time and catefory
        if(Globals.FILTER_TIME == -1 && Globals.FILTER_DISTANCE == -1 && Globals.FILTER_INTEREST == null) {
            setDefaultFromSharedPref();
        }
    }

    private void setList() {
        mInterestList = (ListView) findViewById(R.id.interest_list);
        //Array Adapter, for multi choice list
         mListAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_multiple_choice,
                Globals.categories
        );
        mInterestList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mInterestList.setAdapter(mListAdapter);
        mInterestList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectedInterest.add(position);
            }
        });

    }

    private void setSpinner() {
        //set time range
        mTimeRangeSpinner = (Spinner) findViewById(R.id.time_range_spinner);
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, Globals.timeRanges);
        timeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mTimeRangeSpinner.setAdapter(timeAdapter);
        mTimeRangeSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mSelectedTimeRange = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );
    }

    private void setDefaultFromSharedPref() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                Globals.KEY_SHARED_PREFERENCE_FILE, MODE_PRIVATE);
        int time = sharedPreferences.getInt(Globals.KEY_TIME_RANGE, 0);
        int distance = sharedPreferences.getInt(Globals.KEY_DISTANCE_RANGE, 0);
        String[] interest = sharedPreferences.getString(Globals.KEY_INTEREST_CATEGORY, " ")
                .split(" ");

        setWidgets(time, distance, interest);

    }

    private void setWidgets(int time, int distance, String[] interest){
        mTimeRangeSpinner.setSelection(time);
        mDistanceTextView.setText("In " + distance + " Miles");
        mDistanceRangeSeekBar.setProgress(distance);
        if(interest.length>0 && interest[0] != "") {
            for (String str : interest) {
                mInterestList.setItemChecked(Integer.valueOf(str), true);
            }
        }

    }

    public void onApplyClicked(View view) {

        Intent intent = new Intent();
        Bundle extras = new Bundle();


        //send parameters to calling activity
        // int for days, int for distance in meters
        // integer arraylist for interest category
        extras.putInt(Globals.KEY_TIME_RANGE,
                Globals.timeRangesInteger[mSelectedTimeRange]);
        extras.putInt(Globals.KEY_DISTANCE_RANGE,
                Helper.MileToMeters(mSelectedDistanceRange));
        ArrayList<Integer> interest = new ArrayList<>();
        SparseBooleanArray checkedItemPositions = mInterestList.getCheckedItemPositions();
        int itemCount = mInterestList.getCount();
        for(int i=0; i<itemCount; i++){
            if(checkedItemPositions.get(i)){
                interest.add(i);
            }
        }
        extras.putIntegerArrayList(Globals.KEY_INTEREST_CATEGORY, interest);
        intent.putExtras(extras);

        FilterActivity.this.setResult(RESULT_OK, intent);
        FilterActivity.this.finish();
    }

    public void onCancelClicked(View view) {
        finish();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem menuitem;
        menuitem = menu.add(Menu.NONE, CLEAR_ALL_FILTER, CLEAR_ALL_FILTER,
                "Clear All");
        menuitem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CLEAR_ALL_FILTER:
                clearAll();

                return true;
            default:
                finish();
                return false;
        }
    }

    private void clearAll(){
        mTimeRangeSpinner.setSelection(0);
        mDistanceTextView.setText("In " + 0 + " Miles");
        mDistanceRangeSeekBar.setProgress(0);
        SparseBooleanArray checkedItemPositions = mInterestList.getCheckedItemPositions();
        int itemCount = mInterestList.getCount();
        for(int i=0; i<itemCount; i++){
            if(checkedItemPositions.get(i)){
                mInterestList.setItemChecked(i,false);
            }
        }


    }
    //Called when seek bar is changed
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mSelectedDistanceRange = progress;
        String distance = "In " + String.valueOf(progress) + " Miles";
        mDistanceTextView.setText(distance);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}



