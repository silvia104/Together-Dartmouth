package edu.dartmouth.cs.together.view;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by foxmac on 16/1/15.
 */
public class ActionTabsViewPagerAdapter extends FragmentPagerAdapter{

    private ArrayList<Fragment> fragments;

    public static final int START = 0;
    public static final int HISTORY = 1;
    public static final String UI_TAB_AS_STARTER = "STARTED";
    public static final String UI_TAB_AS_JOINER = "JOINED";
    private Map<Integer, String> mFragmentTags = new HashMap<Integer,String>();
    private FragmentManager mFragmentManager;


    public ActionTabsViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments){
        super(fm);
        mFragmentManager = fm;
        this.fragments = fragments;
    }

    @Override
    public int getItemPosition(Object object){
        return POSITION_NONE;
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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof Fragment) {
            // record the fragment tag here.
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            mFragmentTags.put(position, tag);
        }
        return obj;
    }


    public Fragment getFragment(int position) {
        String tag = mFragmentTags.get(position);
        if (tag == null)
            return null;
        return mFragmentManager.findFragmentByTag(tag);
    }


}
