package edu.dartmouth.cs.together;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import edu.dartmouth.cs.together.utils.Globals;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private Context mContext;
    private final int REQUEST_DEFAULT_FILTER = 0;
    private final int GET_RESULT_SUCCESS = -1;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager prefManager = getPreferenceManager();
        sharedPreferences = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);


/*
        Preference pref = findPreference("default_filter");
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivityForResult(preference.getIntent(), REQUEST_DEFAULT_FILTER);
                return true;
            }
        });
/*

        SwitchPreferenceCompat newPeople = (SwitchPreferenceCompat) findPreference("new_people");
        newPeople.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                boolean switched = ((SwitchPreferenceCompat) preference)
                        .isChecked();
                mEditor = sharedPreferences.edit();
                mEditor.putBoolean(Globals.KEY_SHARED_PREF_NOTE_NEW_PEOPLE, switched);
                mEditor.commit();
                return true;
            }
        });

        SwitchPreferenceCompat quitPeople = (SwitchPreferenceCompat) findPreference("quit_people");
        quitPeople.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                boolean switched = ((SwitchPreferenceCompat) preference)
                        .isChecked();
                mEditor = sharedPreferences.edit();
                mEditor.putBoolean(Globals.KEY_SHARED_PREF_NOTE_QUIT_PEOPLE, switched);
                mEditor.commit();
                return true;
            }
        });

        SwitchPreferenceCompat eventChange = (SwitchPreferenceCompat) findPreference("event_change");
        eventChange.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                boolean switched = ((SwitchPreferenceCompat) preference)
                        .isChecked();
                mEditor = sharedPreferences.edit();
                mEditor.putBoolean(Globals.KEY_SHARED_PREF_NOTE_EVENT_CHANGE, switched);
                mEditor.commit();
                return true;
            }
        });

        SwitchPreferenceCompat eventCancel = (SwitchPreferenceCompat) findPreference("event_cancel");
        eventCancel.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                boolean switched = ((SwitchPreferenceCompat) preference)
                        .isChecked();
                mEditor = sharedPreferences.edit();
                mEditor.putBoolean(Globals.KEY_SHARED_PREF_NOTE_EVENT_CANCEL, switched);
                mEditor.commit();
                return true;
            }
        });

        SwitchPreferenceCompat newQ = (SwitchPreferenceCompat) findPreference("new_q");
        newQ.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                boolean switched = ((SwitchPreferenceCompat) preference)
                        .isChecked();
                mEditor = sharedPreferences.edit();
                mEditor.putBoolean(Globals.KEY_SHARED_PREF_NOTE_NEW_Q, switched);
                mEditor.commit();
                return true;
            }
        });

        SwitchPreferenceCompat newA = (SwitchPreferenceCompat) findPreference("new_a");
        newA.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,
                                              Object newValue) {
                boolean switched = ((SwitchPreferenceCompat) preference)
                        .isChecked();
                mEditor = sharedPreferences.edit();
                mEditor.putBoolean(Globals.KEY_SHARED_PREF_NOTE_NEW_A, switched);
                mEditor.commit();
                return true;
            }
        });
*/
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
                        ArrayList<Integer> selectedInterest = extras.getIntegerArrayList(Globals.KEY_INTEREST_CATEGORY);

                        writeSharedPreference(timeRange, distanceRange, selectedInterest);
                    }

            }
        }
    }

    private void writeSharedPreference(int time, int distance, ArrayList<Integer> interests){

        SharedPreferences sharedPref = mContext.getSharedPreferences(
                Globals.KEY_SHARED_PREFERENCE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();

        editor.putInt(Globals.KEY_TIME_RANGE, time);
        editor.putInt(Globals.KEY_DISTANCE_RANGE, distance);
        String interest = "";
        for(Integer i:interests){
            interest = interest + i + " ";
        }

        editor.putString(Globals.KEY_INTEREST_CATEGORY, interest);
        editor.commit();

    }


//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_settings, container, false);
//    }

}
