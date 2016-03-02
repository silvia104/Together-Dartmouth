package edu.dartmouth.cs.together.backend.data;


import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

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

    public static String Event_PARENT_ENTITY_KEY = "EventParent";
    public static String Event_PARENT_ENTITY_NAME = "EventParent";
    public static String Event_ENTITY_NAME= "Event";

    private long mEventId;
    private int mCategory;
    private String mShortDesc = "";
    private String mLongDesc = "";
    private long mTimeMillis;
    private int mDuration;
    private double mLat;
    private double mLng;
    private String mLocation = "";
    private int mLimit;
    private long mOwnerId;
    private int mJoinerCount;

    public Event (){}
    public Event (JSONObject json){
        try {
            setEventId(json.getLong(Event.ID_KEY));
            setCategory(json.getInt(Event.CATEGORY_KEY));
            setShortDesc(json.getString(Event.SHORT_DESC_KEY));
            setLongDesc(json.getString(Event.LONG_DESC_KEY));
            setLat(json.getDouble(Event.LATITUDE_KEY));
            setLng(json.getDouble(Event.LONGITUDE_KEY));
            setLocation(json.getString(Event.LOCATION_KEU));
            setTimeMillis(json.getLong(Event.TIME_MILLIS_KEY));
            setDuration(json.getInt(Event.DURATION_KEY));
            setOwnerId(json.getLong(Event.OWNER_KEY));
            setLimit(json.getInt(Event.LIMIT_KEY));
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

    public String getShortdesc() {
        return mShortDesc;
    }
    public void setLongDesc(String s) {
        mLongDesc = s;
    }
    public void setShortDesc(String s) {
        mShortDesc = s;
    }

    public String getLongDesc() {
        return mLongDesc;
    }

    public void setTimeMillis(long millis) {
        mTimeMillis = millis;
    }

    public long getTimeMillis() {
        return mTimeMillis;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public int getDuration() {
        return mDuration;
    }
    public void setLat(double lat) {
        mLat = lat;
    }
    public void setLng(double lng) {
        mLng = lng;
    }
    public double getLat(){return mLat;}
    public double getLng(){return mLng;}
    public int getLimit() {
        return mLimit;
    }


    public void setLimit(int n) {
        mLimit = n;
    }

    public int getCategoryIdx() {
        return mCategory;
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

    public int getJoinerCount (){
        return mJoinerCount;
    }

    public void setId(long id) {
        mEventId = id;
    }
}
