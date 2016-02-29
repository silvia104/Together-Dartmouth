package edu.dartmouth.cs.together;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.dartmouth.cs.together.view.MessageListAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageCenterFragment extends Fragment {


    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public MessageCenterFragment() {
        // Required empty public constructor
    }

    public static MessageCenterFragment newInstance() {

        Bundle args = new Bundle();
        MessageCenterFragment fragment = new MessageCenterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message_center, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.message_recycler_view);
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);


        //String Array for testing
        String[] testData = new String[]{"The Revenent", "Tennis"};
        mAdapter = new MessageListAdapter(testData);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

}
