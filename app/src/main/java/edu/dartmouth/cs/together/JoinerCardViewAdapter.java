package edu.dartmouth.cs.together;

import android.content.Context;
import android.content.Intent;
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
import edu.dartmouth.cs.together.data.User;
import edu.dartmouth.cs.together.utils.Globals;
import edu.dartmouth.cs.together.utils.Helper;

/**
 * Created by TuanMacAir on 2/27/16.
 */
public class JoinerCardViewAdapter extends RecyclerView.Adapter<JoinerCardViewAdapter.JoinerCardViewHolder> {
        private final LayoutInflater mLayoutInflater;
        private final Context mContext;
        private final int midx;
        private List<User> mUsers = new ArrayList<>();
        public JoinerCardViewAdapter(Context context, List<User> userList, int idx) {
            mContext = context;
            midx=idx;
            mUsers.addAll(userList);
            mLayoutInflater = LayoutInflater.from(context);
        }

        public void updateAdapter(List<User> userList){
            mUsers.clear();
            mUsers.addAll(userList);
            notifyDataSetChanged();
        }
        @Override
        public JoinerCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = mLayoutInflater.inflate(R.layout.itme_joinercard, parent, false);

            return new JoinerCardViewHolder(v);
        }

        @Override
        public void onBindViewHolder(JoinerCardViewHolder holder, final int position) {
            holder.mRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = mUsers.get(position);
                    Intent i = new Intent(mContext, ProfileActivity.class);
                    i.putExtra(User.ID_KEY, user.getId());
                    i.putExtra(User.ACCOUNT_KEY,user.getAccount());
                    i.putExtra(User.NAME_KEY,user.getName());
                    i.putExtra(User.RATE_KEY,getrate(user, midx));
                    i.putExtra("cate",midx);
                    mContext.startActivity(i);
                }
            });
            holder.mTextView.setText(mUsers.get(position).getName());
            if (position==0) {
                holder.mImageView.setImageResource(R.drawable.initiater);
            } else {
                holder.mImageView.setImageResource(R.drawable.follower);
            }
        }

    public float getrate(User user, int index){
        String rate = user.getRate();
        String s[] = rate.split(",");
        List<Integer>number=new ArrayList<Integer>();
        List<Float>ratenum=new ArrayList<Float>();
        for(int i=0;i<s.length/2;i++){
            number.add(Integer.parseInt(s[i]));
        }
        for(int i=s.length/2;i<s.length;i++){
            ratenum.add(Float.parseFloat(s[i]));
        }
        return ratenum.get(index);
    }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }

        public static class JoinerCardViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.imageView) ImageView mImageView;
            @Bind(R.id.textView) TextView mTextView;
            @Bind(R.id.joinerLayout) View mRoot;
            JoinerCardViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }

}
