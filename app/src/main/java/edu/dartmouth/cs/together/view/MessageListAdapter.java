package edu.dartmouth.cs.together.view;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import edu.dartmouth.cs.together.utils.Globals;

import edu.dartmouth.cs.together.QuestionReplyDialogFragment;
import edu.dartmouth.cs.together.R;
import edu.dartmouth.cs.together.data.Message;

/**
 * Created by foxmac on 16/2/27.
 */
public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {
    public static ArrayList<Message> messages;
    private static Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mColorBlob;
        public TextView mEventText;
        public TextView mMessageTime;
        public TextView mMessageBrief;



        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View itemView) {
                }
            });

            mColorBlob = (ImageView) itemView.findViewById(R.id.message_type);
            mEventText = (TextView)itemView.findViewById(R.id.message_event);
            mMessageTime = (TextView) itemView.findViewById(R.id.message_time);
            mMessageBrief = (TextView) itemView.findViewById(R.id.message_brief);
        }


    }//ViewHolder




    // Provide a suitable constructor (depends on the kind of dataset)
    public MessageListAdapter(ArrayList<Message> messageArrayList) {
        messages = messageArrayList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MessageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);

        mContext = parent.getContext();
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Message message = messages.get(position);
        if(!message.getIsRead()) {
            if (message.getMsgType() == Globals.MESSAGE_TYPE_NEW_JOIN) {
                holder.mColorBlob.setImageResource(R.drawable.ic_new_joiner);
            } else if (message.getMsgType() == Globals.MESSAGE_TYPE_NEW_QUESTION) {
                holder.mColorBlob.setImageResource(R.drawable.ic_new_question);
            }
        }
        else{
            holder.mColorBlob.setVisibility(View.GONE);
        }

        holder.mEventText.setText(String.valueOf(message.getEventId()));
        String time = message.getDateTimeString("dd/MM/yyyy hh:mm:ss");
        holder.mMessageTime.setText(time);
        holder.mMessageBrief.setText(message.getMsgContent());


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return messages.size();
    }

    public List<Message> getList( ){
        return messages;
    }



    public void getViewHolderByPosition(int position){

        ;
    }
    public void addItem(int position, Message msg){

    }

}