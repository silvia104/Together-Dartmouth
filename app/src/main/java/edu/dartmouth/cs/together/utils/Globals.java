package edu.dartmouth.cs.together.utils;


import android.content.IntentFilter;

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
    public static final String UPDATE_JOINER_DATA = "update_joiner_data";
    public static final String RELOAD_JOINER_DATA = "reload_joiner_data";
    public static final String EVENT_TYPE_KEY = "EVENT_TYPE_KEY";
    public static String[] sports = {"Soccer","Skiing","Cycling","Jogging","Hiking",
            "Tennis","Skating","Dancing","Gym","Basketball","Swimming","Billiard"};
    public static String[] life = {"Movie","Party","Shopping","Dining", "Travel", "Study"};
    public static List<String> categories = new ArrayList<>();
    public static LatLng DARTMOUTH_GPS = new LatLng(43.726034, -72.142917);
    public static double RADIUS_50MILES = 80467.2;
    public static String EVENT_INDEX_KEY="EVENT_INDEX_KEY";
    public static String SERVER_ADDR = "http://10.0.0.30:8080";
    public static String DEVICE_ID;
    public static User currentUser;
    public static final String ACTION_ADD = "add";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_DELETE= "delete";
    public static final String ACTION_JOIN = "join";
    public static final String ACTION_QUIT = "quit";
    public static final String ACTION_NOTHING = "NA";
    public static final String ACTION_KEY = "action_key";
    public static long tempEvent = -1;

    static{
        for (String s : sports){
            categories.add(s);
        }
        for (String s: life){
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
