package edu.dartmouth.cs.together.data;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.dartmouth.cs.together.utils.Globals;

/**
 * Created by TuanMacAir on 2/27/16.
 */
public class Event {
    public static final String ID_KEY = "id" ;
    public static final String CATEGORY_KEY = "category" ;
    public static final String SHORT_DESC_KEY = "short";
    public static final String LATITUDE_KEY = "lat";
    public static final String LONGITUDE_KEY = "lng";
    public static final String LOCATION_KEU = "loc";
    public static final String TIME_MILLIS_KEY = "millis";
    public static final String DURATION_KEY ="dur" ;
    public static final String OWNER_KEY = "owner";
    public static final String LIMIT_KEY = "limit";
    public static final String LONG_DESC_KEY = "long";
    public static final String JOINER_COUNT_KEY = "joiner_count";

    private long mEventId;
    private int mCategory;
    private String mShortDesc = "";
    private String mLongDesc = "";
    private Calendar mTime = Calendar.getInstance();
    private int mDuration;
    private List<Integer> mJoinersID = new ArrayList<>();
    private List<Qa> mQandA = new ArrayList<>();
    private LatLng mLatLng = null;
    private String mLocation = "";
    private int mLimit;
    private long mOwnerId;
    private int mJoinerCount;
    private int mStatus;

    public static int STATUS_DRAFT= 0;
    public static int STATUS_POSTED= 1;
    public Event() {}
    public Event (JSONObject json){
        try {
            setEventId(json.getLong(Event.ID_KEY));
            setCategory(json.getInt(Event.CATEGORY_KEY));
            setShortDesc(json.getString(Event.SHORT_DESC_KEY));
            setLongDesc(json.getString(Event.LONG_DESC_KEY));
            setLatLng(new LatLng(json.getDouble(Event.LATITUDE_KEY),
                    json.getDouble(Event.LONGITUDE_KEY)));
            setLocation(json.getString(Event.LOCATION_KEU));
            mTime.setTimeInMillis(json.getLong(Event.TIME_MILLIS_KEY));
            setDuration(json.getInt(Event.DURATION_KEY));
            setOwnerId(json.getLong(Event.OWNER_KEY));
            setLimit(json.getInt(Event.LIMIT_KEY));
            setJoinerCount(json.getInt(Event.JOINER_COUNT_KEY));

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void setEventId(long id){
        mEventId = id;
    }

    public long getEventId(){
        return mEventId;
    }

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

    public String getDate() {
        return  new SimpleDateFormat("MM/dd/yy").format(mTime.getTime());
    }

    public String getTime() {
        return  new SimpleDateFormat("HH:mm").format(mTime.getTime());
    }

    public long getTimeMillis() {
        return mTime.getTimeInMillis();
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

    public Calendar getcalender(){return mTime;}

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

    public long getOwner() {
        return mOwnerId;
    }
    public void setOwnerId(long id){
        mOwnerId = id;
    }
    public void setJoinerCount (int n){
        mJoinerCount = n;
    }

    public int getmJoinerCount (){
        return mJoinerCount;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status){
        mStatus = status;
    }

}
