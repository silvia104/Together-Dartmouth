package edu.dartmouth.cs.together.view;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by foxmac on 16/1/15.
 */
public class ActionTabsViewPagerAdapter extends FragmentPagerAdapter{

    private ArrayList<Fragment> fragments;

    public static final int START = 0;
    public static final int HISTORY = 1;
    public static final String UI_TAB_AS_STARTER = "STARTED";
    public static final String UI_TAB_AS_JOINER = "JOINED";

    public ActionTabsViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments){
        super(fm);
        this.fragments = fragments;
    }

    public Fragment getItem(int pos){
        return fragments.get(pos);
    }

    public int getCount(){
        return fragments.size();
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case START:

                return UI_TAB_AS_STARTER;
            case HISTORY:

                return UI_TAB_AS_JOINER;
            default:
                break;
        }
        return null;
    }
}
