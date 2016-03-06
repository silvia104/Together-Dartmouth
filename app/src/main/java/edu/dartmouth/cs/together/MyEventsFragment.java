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


    public static MyEventsFragment newInstance() {

        Bundle args = new Bundle();
        MyEventsFragment fragment = new MyEventsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MyEventsFragment(){

    }

    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
//        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_my_events, container, false);


        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tab);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);


        //Set up fragments of each tab
        fragmentList = new ArrayList<Fragment>();
        asStarterFragment = new MyEventsAsInitiator();
        asJoinerFragment = new MyEventsAsJoiner();
        fragmentList.add(asStarterFragment);
        fragmentList.add(asJoinerFragment);

<<<<<<< HEAD

=======
>>>>>>> origin/test
        mViewPageAdapter =new ActionTabsViewPagerAdapter(getChildFragmentManager(),
                fragmentList);


        mViewPager.setAdapter(mViewPageAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
           @Override
           public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

           }

           @Override
           public void onPageSelected(int position) {
               Fragment fragment = ((ActionTabsViewPagerAdapter)mViewPager.getAdapter()).getFragment(position);

               if (position ==1 && fragment != null)
               {
                   fragment.onResume();
               }
           }

           @Override
           public void onPageScrollStateChanged(int state) {

           }
       }

        );
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);



        return view;
    }





}
