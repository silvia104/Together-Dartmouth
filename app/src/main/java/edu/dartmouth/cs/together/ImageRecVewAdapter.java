package edu.dartmouth.cs.together;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by TuanMacAir on 2/27/16.
 */
public class ImageRecVewAdapter extends RecyclerView.Adapter<ImageRecVewAdapter.ImageCardViewHolder> {
    private final Context mContext;
    private List<Integer> mDrawableIdList = new ArrayList<>();
    private LayoutInflater mInflator;
    private int mLayoutId;
    public ImageRecVewAdapter(Context context, List<Integer> drawableIdListList, int layoutId){
        mContext = context;
        mDrawableIdList = drawableIdListList;
        mInflator = LayoutInflater.from(context);
        mLayoutId = layoutId;

    }
    @Override
    public ImageCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflator.inflate(mLayoutId, parent,false);
        // changed
        return new ImageCardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageCardViewHolder holder, int i) {
        holder.mImgView.setImageBitmap(
                BitmapFactory.decodeResource(mContext.getResources(),
                        mDrawableIdList.get(i)));

    }

    @Override
    public int getItemCount() {
        return mDrawableIdList.size();
    }

    public class ImageCardViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imageView) ImageView mImgView;
        public ImageCardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
