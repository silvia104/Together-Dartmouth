package edu.dartmouth.cs.together.utils;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.dartmouth.cs.together.data.Event;

/**
 * Created by TuanMacAir on 2/27/16.
 */
public class Globals {
    public static String[] sports = {"Soccer","Skiing","Cycling","Jogging","Hiking",
            "Tennis","Skating","Dancing","Gym","Basketball","Swimming","Billiard"};
    public static String[] life = {"Movie","Party","Shopping","Dining", "Travel", "Study"};
    public static List<String> categories = new ArrayList<>();
    public static LatLng DARTMOUTH_GPS = new LatLng(43.726034, -72.142917);
    public static double RADIUS_50MILES = 80467.2;
    public static String EVENT_INDEX_KEY="EVENT_INDEX_KEY";
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
