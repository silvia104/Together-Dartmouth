package edu.dartmouth.cs.together.utils;



import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.dartmouth.cs.together.R;
import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.data.User;

/**
 * Created by TuanMacAir on 2/27/16.
 */
public class Globals {
    public static final String DEVICE_ID_PREF_KEY = "REG_ID_KEY";
    public static final String RELOAD_JOINER_DATA = "reload_joiner_data";
    public static final String EVENT_TYPE_KEY = "EVENT_TYPE_KEY";
    public static final String DELETE_EVENT ="delete_event";
    public static final String RELOAD_QUESTION_DATA_IN_DETAIL = "reload_question_in_detail";
    public static final String UPDATE_EVENT_DETAIL = "update_event_detail";
    public static final String MAP_LATITUDE = "latitude";
    public static final String MAP_LONGITUDE = "longitude";
    public static final int SETTING_FILTER = 0;
    public static final String ACTION_CODE = "code";
    public static final String LOGIN_STATUS_KEY ="login_status" ;
    public static final String FIRST_LOAD_ALL_EVENTS_KEY = "first_load_all_events";
    public static String[] sports = {"Soccer","Skiing","Cycling","Jogging","Hiking",
            "Tennis","Dancing","Gym","Basketball","Swimming","Billiard"};
    public static String[] life = {"Movie","Party","Shopping","Dining", "Travel", "Study",
            "Music", "Pet", "Other"};
    public static Integer[] categoriIcons={R.drawable.soccer, R.drawable.skiing,R.drawable.bicycle,
            R.drawable.running,R.drawable.hiking,R.drawable.tennis,R.drawable.dancing,R.drawable.gym,
            R.drawable.basketball,R.drawable.swimming,R.drawable.billard,R.drawable.movie,
            R.drawable.party,R.drawable.shopping,R.drawable.dining,R.drawable.travel,R.drawable.study};
    public static List<String> categories = new ArrayList<>();
    public static LatLng DARTMOUTH_GPS = new LatLng(43.726034, -72.142917);
    public static double RADIUS_50MILES = 80467.2;

<<<<<<< HEAD
    public static String SERVER_ADDR = "http://10.0.0.30:8080";
    //public static String SERVER_ADDR = "https://together-1234.appspot.com";
=======
    public static String SERVER_ADDR = "http://10.0.0.70:8080";
>>>>>>> origin/test
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

    public static String[] timeRanges = new String[]{
            "Any Time", "Today", "In 3 Days", "In 1 Week", "In 2 Weeks"
    };


    //name of shared preference file
    public static String KEY_SHARED_PREFERENCE_FILE = "shared preference file";

    //keys for extras in intent and shared preference
    public static String KEY_TIME_RANGE = "time range";
    public static String KEY_DISTANCE_RANGE = "distance range";
    public static String KEY_INTEREST_CATEGORY = "interest category";
    public static String KEY_MESSAGE_BUNDLE_MESSAGE = "new message";
    public static String KEY_MESSAGE_BUNDLE_TIME = "message time";
    public static String KEY_MESSAGE_BUNDLE_TYPE  = "message type";

    public static String ACTION_NEW_MESSAGE_FROM_SERVER = "edu.dartmouth.cs.together.NEWMESSAGE";
    public static final int MESSAGE_TYPE_NEW_QUESTION = 0;
    public static final int MESSAGE_TYPE_NEW_ANSWER = 1;
    public static final int MESSAGE_TYPE_NEW_JOIN = 2;
    public static final int MESSAGE_TYPE_EVENT_QUIT = 3;
    public static final int MESSAGE_TYPE_EVENT_CHANGE = 4;
    public static final int MESSAGE_TYPE_EVENT_CANCEL = 5;
    public static final String SPLITER = String.valueOf((char)30);


    public static final String KEY_SHARED_PREF_NOTE_NEW_PEOPLE = "new_people";
    public static final String KEY_SHARED_PREF_NOTE_QUIT_PEOPLE = "quit_people";
    public static final String KEY_SHARED_PREF_NOTE_EVENT_CHANGE = "event_change";
    public static final String KEY_SHARED_PREF_NOTE_EVENT_CANCEL = "event_cancel";
    public static final String KEY_SHARED_PREF_NOTE_NEW_Q = "new_q";
    public static final String KEY_SHARED_PREF_NOTE_NEW_A = "new_a";


    static {
        for (String s : sports) {
            categories.add(s);
        }
        for (String s : life) {
            categories.add(s);
        }
    }
    public static boolean isRegistered = false;

}
