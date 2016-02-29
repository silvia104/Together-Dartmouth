package edu.dartmouth.cs.together;

/**
 * Created by foxmac on 16/2/28.
 */
public class Globals {

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
}
