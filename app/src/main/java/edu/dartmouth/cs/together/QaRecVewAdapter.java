package edu.dartmouth.cs.together;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.dartmouth.cs.together.data.Qa;

/**
 * Created by TuanMacAir on 2/27/16.
 */
public class QaRecVewAdapter extends RecyclerView.Adapter<QaRecVewAdapter.QaViewHolder>{
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Qa> mList ;
    private boolean mIsEdit;
    public QaRecVewAdapter(Context context, List<Qa> qaList, boolean isEdit) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mList = qaList;
        mIsEdit = isEdit;
    }

    @Override
    public QaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.item_qa,parent,false);
        return new QaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(QaViewHolder holder, int i) {
        holder.mQuestion.setText(mList.get(i).getQuestion());
        holder.mAnswer.setText(mList.get(i).getAnswer());
        holder.mAnswer.setVisibility(View.GONE);
        holder.mEditAnswer.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class QaViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.questionText)  TextView mQuestion;
        @Bind(R.id.expand_arrow)  ImageButton mControl;
        @Bind(R.id.answerText) TextView mAnswer;
        @Bind(R.id.editAnswer) ImageButton mEditAnswer;
        public QaViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.questionText)
        public void onControlClick(){
            if (mControl.getTag().toString().equals("closed")){
                mControl.setTag("open");
                mControl.setImageResource(R.drawable.ic_expand_less);
                mAnswer.setVisibility(View.VISIBLE);
                if (mIsEdit){
                    mEditAnswer.setVisibility(View.VISIBLE);
                } else {
                    mEditAnswer.setVisibility(View.GONE);
                }
            } else {
                mControl.setTag("closed");
                mControl.setImageResource(R.drawable.ic_expand_more);
                mAnswer.setVisibility(View.GONE);
                mEditAnswer.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.editAnswer)
        public void onEidtClick(){
            //TODO:
        }

    }
}
