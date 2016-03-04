package edu.dartmouth.cs.together;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import edu.dartmouth.cs.together.utils.Helper;

/**
 * Created by TuanMacAir on 2/27/16.
 */
public class JoinerCardViewAdapter extends RecyclerView.Adapter<JoinerCardViewAdapter.JoinerCardViewHolder> {
        private final LayoutInflater mLayoutInflater;
        private final Context mContext;
        private List<String> mNames = new ArrayList<>();
        private Bitmap bitmap;
        public JoinerCardViewAdapter(Context context) {
            for (int i = 0; i < 20; i++){
                mNames.add("John Doe");
            }
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
            bitmap = Helper.getRoundedShape(
                    BitmapFactory.decodeResource(mContext.getResources(), R.drawable.test));
        }

        @Override
        public JoinerCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = mLayoutInflater.inflate(R.layout.itme_joinercard, parent, false);

            return new JoinerCardViewHolder(v);
        }

        @Override
        public void onBindViewHolder(JoinerCardViewHolder holder, int position) {
            holder.mTextView.setText(mNames.get(position));
            holder.mImageView.setImageBitmap(bitmap);
        }

        @Override
        public int getItemCount() {
            return mNames.size();
        }

        public static class JoinerCardViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.imageView) ImageView mImageView;
            @Bind(R.id.textView) TextView mTextView;
            JoinerCardViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }

}
