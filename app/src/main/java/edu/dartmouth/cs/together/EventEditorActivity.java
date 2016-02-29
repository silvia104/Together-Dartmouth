package edu.dartmouth.cs.together;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.OnClick;
import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.utils.Globals;
import edu.dartmouth.cs.together.utils.Helper;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.List;

public class EventEditorActivity extends BaseEventActivity {
    private int PLACE_PICKER_REQUEST = 1;
    private Event mEvent = Globals.event;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            list.add(R.drawable.movie);
        }
        mCategoryRecView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 5);
        mCategoryRecView.setLayoutManager(layoutManager);
        mCategoryRecView.setAdapter(new CategoryAdapter(this, list, R.layout.item_imagecard));

        setHomeButton("Editor");
        setQa();
    }

    @OnClick(R.id.categoryText)
    public void onCategoryClick(View view){
        mCategoryRecView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.locationText)
    public void onLocationClick(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                mEvent.setLocation(place);
                mLocationTv.setText(Helper.getUnderlinedString(mEvent.getLocation()));
                mLocationTv.setTextSize(20);
            }
        }
    }
    private void setQa(){
        QaRecVewAdapter mQaAdapter = new QaRecVewAdapter(this, generateQuestions(), true);
        setBottonRecView(mQaAdapter);
    }


    class CategoryAdapter extends ImageRecVewAdapter{
        public CategoryAdapter(Context context, List<Integer> drawableIdListList, int layoutId) {
            super(context, drawableIdListList, layoutId);
        }

        @Override
        public void onBindViewHolder(final ImageCardViewHolder holder, int i) {
            super.onBindViewHolder(holder, i);
            final int index = i;
            holder.mImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEvent.setCategory(index);
                    mCategoryTv.setText(Helper.getUnderlinedString(Globals.categories.get(index)));
                    mCategoryRecView.setVisibility(View.GONE);

                }
            });
        }
    }
}
