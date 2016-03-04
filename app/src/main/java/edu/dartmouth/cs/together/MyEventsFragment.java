package edu.dartmouth.cs.together;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.dartmouth.cs.together.view.ActionTabsViewPagerAdapter;
import edu.dartmouth.cs.together.view.SlidingTabLayout;


public class MyEventsFragment extends Fragment {

    private Fragment asStarterFragment;
    private Fragment asJoinerFragment;
    private ArrayList<Fragment> fragmentList;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private ActionTabsViewPagerAdapter mViewPageAdapter;
    private final int SCREEN_PAGE_LIMIT = 1;


    public static MyEventsFragment newInstance() {

        Bundle args = new Bundle();
        MyEventsFragment fragment = new MyEventsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MyEventsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_my_events, container, false);


        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tab);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(SCREEN_PAGE_LIMIT);


        //Set up fragments of each tab
        fragmentList = new ArrayList<Fragment>();
        asStarterFragment = new MyEventsAsInitiator();
        asJoinerFragment = new MyEventsAsJoiner();
        fragmentList.add(asStarterFragment);
        fragmentList.add(asJoinerFragment);


        mViewPageAdapter =new ActionTabsViewPagerAdapter(getChildFragmentManager(),
                fragmentList);
        mViewPager.setAdapter(mViewPageAdapter);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

        return view;


    }




}
