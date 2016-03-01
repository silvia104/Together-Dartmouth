package edu.dartmouth.cs.together.data;

import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.dartmouth.cs.together.utils.Globals;

/**
 * Created by TuanMacAir on 2/27/16.
 */
public class Event {
    private int mCategory;
    private String mShortDesc = "";
    private String mLongDesc = "";
    private Calendar mTime = Calendar.getInstance();
    private int mDuration;
    private List<Integer> mJoinersID = new ArrayList<>();
    private int mOwnerId;
    private List<Qa> mQandA = new ArrayList<>();
    private LatLng mLatLng = null;
    private String mLocation = "";
    private int mLimit;
    public void setCategory(int i) {
        mCategory = i;
    }

    public void setLocation(String str) {
        mLocation = str;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getCategotyName() {
        return Globals.categories.get(mCategory);
    }

    public String getShortdesc() {
        return mShortDesc;
    }
    public void setLongDesc(String s) {
        mLongDesc = s;
    }

    public String getLongDesc() {
        return mLongDesc;
    }

    public void setShortDesc(String s) {
        mShortDesc = s;
    }
    public String getShortDesc(){
        return mShortDesc;
    }

    public String getDate() {
        return  new SimpleDateFormat("MM/dd/yy").format(mTime.getTime());
    }

    public String getTime() {
        return  new SimpleDateFormat("HH:mm").format(mTime.getTime());
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }
    public String getDuration() {
        return mDuration + "";
    }

    public void setLatLng(LatLng latLng) {
        mLatLng = latLng;
    }

    public int getLimit() {
        return mLimit;
    }

    public void setDateTime(Calendar time) {
        mTime = time;
    }

    public void setLimit(int n) {
        mLimit = n;
    }

    public int getCategoryIdx() {
        return mCategory;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }
}
