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


import java.util.ArrayList;
import edu.dartmouth.cs.together.utils.Globals;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private Context mContext;
    private SharedPreferences mSharedPreferences;


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

        //start filter activity with parameter Globals.FILTER_FROM_OPTION
        //set default filter so when users first launch the app only wanted events are showed
        Preference pref = findPreference("default_filter");
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = preference.getIntent();
                intent.putExtra(Globals.FILTER_FROM_OPTION,false);
                startActivity(intent);
                return true;
            }
        });

    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }

}
