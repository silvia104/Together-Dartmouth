package edu.dartmouth.cs.together;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.dartmouth.cs.together.utils.Globals;


import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private Context mContext;
    private final int REQUEST_DEFAULT_FILTER = 0;
    private final int GET_RESULT_SUCCESS = -1;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager prefManager = getPreferenceManager();

        Preference pref = findPreference("default_filter");
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivityForResult(preference.getIntent(), REQUEST_DEFAULT_FILTER);
                return true;
            }
        });
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == REQUEST_DEFAULT_FILTER){
                if(resultCode == GET_RESULT_SUCCESS) {
                    if (data!=null ) {
                        Bundle extras = data.getExtras();
                        int timeRange = (int)extras.get(Globals.KEY_TIME_RANGE);
                        int distanceRange = (int) extras.get(Globals.KEY_DISTANCE_RANGE);
                        String selectedInterest = extras.getString(Globals.KEY_INTEREST_CATEGORY);
                        writeSharedPreference(timeRange, distanceRange, selectedInterest);
                    }

            }
        }
    }

    private void writeSharedPreference(int time, int distance, String interests){

        SharedPreferences sharedPref = mContext.getSharedPreferences(
                Globals.KEY_SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();

        editor.putInt(Globals.KEY_TIME_RANGE, time);
        editor.putInt(Globals.KEY_DISTANCE_RANGE, distance);
        editor.putString(Globals.KEY_INTEREST_CATEGORY, interests);
        editor.commit();

    }


//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_settings, container, false);
//    }

}
