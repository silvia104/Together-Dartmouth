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
    public void setCategory(int i) {
        mCategory = i;
    }

    public void setLocation(Place place) {
        mLatLng = place.getLatLng();
        mLocation = place.getName() + "\n" + place.getAddress();
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

    public void setDate(int year, int mOfY, int dOfM){
        mTime.set(year, mOfY, dOfM);
    }
    public void setTime(int hour, int min){
        mTime.set(Calendar.HOUR_OF_DAY,hour);
        mTime.set(Calendar.MINUTE, min);
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
    public int getDuration() {
        return mDuration;
    }

    public void setLatLng(LatLng latLng) {
        mLatLng = latLng;
    }
}
