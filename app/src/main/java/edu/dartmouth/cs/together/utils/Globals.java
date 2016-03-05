package edu.dartmouth.cs.together.utils;



import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.data.User;

/**
 * Created by TuanMacAir on 2/27/16.
 */
public class Globals {
    public static final String DEVICE_ID_PREF_KEY = "REG_ID_KEY";
    public static final String REGISTRATION_COMPLETE = "edu.dartmouth.cs.together.registration_complete_action";
    public static final String RELOAD_JOINER_DATA = "reload_joiner_data";
    public static final String EVENT_TYPE_KEY = "EVENT_TYPE_KEY";
    public static final String DELETE_EVENT ="delete_event";
    public static final String RELOAD_QUESTION_DATA_IN_DETAIL = "reload_question_in_detail";
    public static final String UPDATE_EVENT_DETAIL = "update_event_detail";
    public static final String MAP_LATITUDE = "latitude";
    public static final String MAP_LONGITUDE = "longitude";
    public static final int SETTING_FILTER = 0;
    public static String[] sports = {"Soccer","Skiing","Cycling","Jogging","Hiking",
            "Tennis","Skating","Dancing","Gym","Basketball","Swimming","Billiard"};
    public static String[] life = {"Movie","Party","Shopping","Dining", "Travel", "Study"};
    public static List<String> categories = new ArrayList<>();
    public static LatLng DARTMOUTH_GPS = new LatLng(43.726034, -72.142917);
    public static double RADIUS_50MILES = 80467.2;
    public static String EVENT_INDEX_KEY="EVENT_INDEX_KEY";

    public static String SERVER_ADDR = "http://10.31.37.24:8080";
    public static String DEVICE_ID;
    public static User currentUser;
    public static final String ACTION_ADD = "add";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_DELETE= "delete";
    public static final String ACTION_JOIN = "join";
    public static final String ACTION_QUIT = "quit";
    public static final String ACTION_POLL = "poll" ;

    public static final String ACTION_NOTHING = "NA";
    public static final String ACTION_KEY = "action_key";

    //temporary content, provided by user preference
    public static String[] interestList = {
            "Sport", "Movie", "Music", "Food", "Pet", "Party", "Other"
    };

    public static String[] timeRanges = new String[]{
            "Any Time", "Today", "In 3 Days", "In 1 Week", "In 2 Weeks"
    };


    //name of shared preference file
    public static String KEY_SHARED_PREFERENCE_FILE = "shared preference file";

    //keys for extras in intent and shared preference
    public static String KEY_TIME_RANGE = "time range";
    public static String KEY_DISTANCE_RANGE = "distance range";
    public static String KEY_INTEREST_CATEGORY = "interest category";

    static{
        for (String s : sports){
            categories.add(s);
        }
        for (String s: life){
            categories.add(s);
        }
        for (String s:interestList){
            categories.add(s);
        }
    }
    public static Event event = new Event();
    static {
        event.setShortDesc("short");
        event.setLongDesc("long");
        event.setLatLng(Globals.DARTMOUTH_GPS);
        event.setLocation("Dartmouth");
        Calendar cal =Calendar.getInstance();
        cal.setTimeInMillis(cal.getTimeInMillis() +2*3600*1000);
        event.setDateTime(cal);
        event.setLimit(15);
    }


}
