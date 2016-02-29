package edu.dartmouth.cs.together.data;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

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

    public String getShortdesc(){
        return mShortDesc;

    }
}
