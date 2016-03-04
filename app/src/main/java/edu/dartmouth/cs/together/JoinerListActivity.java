package edu.dartmouth.cs.together;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import butterknife.Bind;
import butterknife.ButterKnife;

public class JoinerListActivity extends BasePopoutActivity {
    @Bind(R.id.joinerRecVew) RecyclerView mJoinerRecVew;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joiner_list);
        ButterKnife.bind(this);
        mJoinerRecVew.setHasFixedSize(true);
        mJoinerRecVew.setLayoutManager(new LinearLayoutManager(this));
        mJoinerRecVew.setAdapter(new JoinerCardViewAdapter(this));
        setHomeButton("Participants");
    }
}
