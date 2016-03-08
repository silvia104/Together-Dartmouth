package edu.dartmouth.cs.together;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.SwitchPreferenceCompat;
<<<<<<< HEAD
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
=======
>>>>>>> Final

import java.util.ArrayList;
import edu.dartmouth.cs.together.utils.Globals;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private Context mContext;
    private final int REQUEST_DEFAULT_FILTER = 0;
    private final int GET_RESULT_SUCCESS = -1;
    private SharedPreferences mSharedPreferences;
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
        mSharedPreferences = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);



        Preference pref = findPreference("default_filter");
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = preference.getIntent();
<<<<<<< HEAD
                startActivityForResult(intent, REQUEST_DEFAULT_FILTER);
                return true;
            }
        });


        SwitchPreferenceCompat newPeople = (SwitchPreferenceCompat) prefManager.findPreference("new_people");
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
=======
                intent.putExtra(Globals.FILTER_FROM_OPTION,false);
                startActivity(intent);
>>>>>>> Final
                return true;
            }
        });

<<<<<<< HEAD
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

=======
>>>>>>> Final
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
<<<<<<< HEAD
                    if (data!=null ) {
                        Bundle extras = data.getExtras();
                        int timeRange = (int)extras.get(Globals.KEY_TIME_RANGE);
                        int distanceRange = (int) extras.get(Globals.KEY_DISTANCE_RANGE);
                        ArrayList<Integer> selectedInterest = extras.getIntegerArrayList(Globals.KEY_INTEREST_CATEGORY);
                        writeSharedPreference(timeRange, distanceRange, selectedInterest);
                    }
=======
                            if (data!=null ) {
                                Bundle extras = data.getExtras();
                                Globals.FILTER_TIME = (int)extras.get(Globals.KEY_TIME_RANGE);
                                Globals.FILTER_DISTANCE = (int) extras.get(Globals.KEY_DISTANCE_RANGE);
                                Globals.FILTER_INTEREST = (ArrayList<Integer>) (extras.get(Globals.KEY_INTEREST_CATEGORY));
                            }
                }
            }


>>>>>>> Final

    }


<<<<<<< HEAD

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
=======
>>>>>>> Final

    private void setGlobalsBySharedPref(){
//        SharedPreferences mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        //set up filter preference
        if(Globals.FILTER_TIME == -1){
            Globals.FILTER_TIME = mSharedPreferences.getInt(Globals.KEY_TIME_RANGE, 14);
        }
        if(Globals.FILTER_DISTANCE == -1){
            Globals.FILTER_DISTANCE = mSharedPreferences.getInt(Globals.KEY_DISTANCE_RANGE, 50);
        }
        if(Globals.FILTER_INTEREST == null){
            Globals.FILTER_INTEREST = new ArrayList<>();
            for(int i = 0 ; i< Globals.categories.size();i++){
                Globals.FILTER_INTEREST.add(i);
            }
        }


    }



//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_settings, container, false);
//    }

}
