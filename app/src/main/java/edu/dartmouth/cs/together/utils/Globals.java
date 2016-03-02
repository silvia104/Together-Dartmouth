package edu.dartmouth.cs.together.utils;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
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
    static{
        for (String s : sports){
            categories.add(s);
        }
        for (String s: life){
            categories.add(s);
        }
    }
    public static Event event = new Event();
    public static LatLng DARTMOUTH_GPS = new LatLng(43.726034, -72.142917);
    public static double RADIUS_50MILES = 80467.2;



    public static String[] locationRanges = new String[]{
            "Any Distance", "< 0.5 Mile", "< 1 Mile", "< 2 Miles"};
    public static String[] timeRanges = new String[]{
            "Any Time", "Today", "In 3 Days", "In 1 Week", "In 2 Weeks"
    };

    //temporary content, provided by user preference
    public static String[] interestList = {
            "Sport", "Movie", "Music", "Food", "Pet", "Party", "Other"
    };

    //name of shared preference file
    public static String KEY_SHARED_PREFERENCE_FILE = "shared preference file";

    //keys for extras in intent and shared preference
    public static String KEY_TIME_RANGE = "time range";
    public static String KEY_DISTANCE_RANGE = "distance range";
    public static String KEY_INTEREST_CATEGORY = "interest category";
    public static int MESSAGE_TYPE_NEW_JOIN = 0;
    public static int MESSAGE_TYPE_NEW_QUESTION = 1;
}
