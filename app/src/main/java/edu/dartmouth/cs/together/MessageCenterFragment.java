package edu.dartmouth.cs.together;


import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import edu.dartmouth.cs.together.data.Message;
import edu.dartmouth.cs.together.utils.Globals;
import edu.dartmouth.cs.together.view.DividerItemDecoration;
import edu.dartmouth.cs.together.view.MessageListAdapter;
import edu.dartmouth.cs.together.view.RecyclerItemClickListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageCenterFragment extends ListFragment {


    private Context mContext;
    private msgRowAdapter mAdapter;
    private List<Message> mMessageList;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    public MessageCenterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        mContext = getActivity();
        Message message1 = new Message(20L, Globals.MESSAGE_TYPE_NEW_JOIN, 82233213123L, "User ", false,-1 );
        Message message2 = new Message(21L, Globals.MESSAGE_TYPE_NEW_QUESTION, 82233213553L,"User xx adds a new question about xx", false, 5 );
        mMessageList = new ArrayList<>();
        mMessageList.add(message1);
        mMessageList.add(message2);
        mAdapter = new msgRowAdapter();
        setListAdapter(mAdapter);
        return  inflater.inflate(R.layout.fragment_message_center, container, false);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Message message = (Message) mAdapter.getItem(position);
        if(message.getMsgType() == Globals.MESSAGE_TYPE_NEW_QUESTION) {
            ArrayList<String> stringList = getStringList(message);
            DialogFragment replyDialog = QuestionReplyDialogFragment.newInstance(stringList);
            replyDialog.show(getChildFragmentManager(), "ReplyQuestion");
            // TODO Handle item click
            Toast.makeText(mContext,
                    "Item " + position + " Clicked!", Toast.LENGTH_SHORT)
                    .show();
        }else{
            //hide the image view in child view

        }
        //set isRead to true;
        if(!message.getIsRead()) {
            message.setIsRead(true);
            ImageView blob = (ImageView) v.findViewById(R.id.message_type);
            blob.setVisibility(View.INVISIBLE);
            TextView eventName = (TextView) v.findViewById(R.id.message_event);
            eventName.setTypeface(null, Typeface.NORMAL);
        }

    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        mContext = getActivity();
//
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_message_center, container, false);
//        mRecyclerView = (RecyclerView) view.findViewById(R.id.message_recycler_view);
//        mLayoutManager = new LinearLayoutManager(mContext);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//        //String Array for testing
//        //TODO: Get string array from message
//
//        final ArrayList<Message> messages = new ArrayList<Message>();
//        final
//        messages.add(message1);
//        messages.add(message2);
//        mAdapter = new MessageListAdapter(messages);
//
//        mRecyclerView.setAdapter(mAdapter);
//
//        mRecyclerView.addOnItemTouchListener(
//                new RecyclerItemClickListener(mContext,
//                        new RecyclerItemClickListener.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(View view, int position) {
//                                Message messageClicked = messages.get(position);
//                                if(messageClicked.getMsgType() == Globals.MESSAGE_TYPE_NEW_QUESTION) {
//                                    ArrayList<String> stringList = getStringList(messageClicked);
//                                    DialogFragment replyDialog = QuestionReplyDialogFragment.newInstance(stringList);
//                                    replyDialog.show(getChildFragmentManager(), "ReplyQuestion");
//                                    // TODO Handle item click
//                                    Toast.makeText(mContext,
//                                            "Item " + position + " Clicked!", Toast.LENGTH_SHORT)
//                                            .show();
//                                }else{
//                                    //hide the image view in child view
//
//                                }
//                                //set isRead to true;
//                                messageClicked.setIsRead(true);
//                                mAdapter.notifyDataSetChanged();
//                            }
//                        })
//        );
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext));
//
//        return view;
//    }

    private ArrayList<String> getStringList(Message message){
        ArrayList<String> stringList = new ArrayList<>();
        stringList.add("Event: "+String.valueOf(message.getEventId()));
        stringList.add(message.getDateTimeString("dd/MM hh:mm"));
        stringList.add("Question: " +String.valueOf(message.getQaId()));
        return stringList;
    }

    private class msgRowAdapter extends ArrayAdapter{
        msgRowAdapter(){
            super(mContext, R.layout.message_item, R.id.message_event, mMessageList);
        }

        public View getView (int position,  View convertView, ViewGroup parent) {
            Message message = (Message) getItem(position);

            convertView = super.getView(position, convertView, parent);
            ImageView msgType = (ImageView)convertView.findViewById(R.id.message_type);
            TextView msgTime = (TextView) convertView.findViewById(R.id.message_time);
            TextView eventBrief = (TextView) convertView.findViewById(R.id.message_brief);
            TextView eventName =  (TextView) convertView.findViewById(R.id.message_event);
            if(!message.getIsRead()){
                eventName.setTypeface(null, Typeface.BOLD);
                if(message.getMsgType() == Globals.MESSAGE_TYPE_NEW_QUESTION) {
                    msgType.setImageResource(R.drawable.ic_new_question);
                }else{
                    msgType.setImageResource(R.drawable.ic_new_joiner);
                }
            }

            String time = message.getDateTimeString("dd/MM/yyyy hh:mm:ss");
            msgTime.setText(time);
            eventBrief.setText(message.getMsgContent());

            //TODO: get event name from event id
            eventName.setText(String.valueOf(message.getEventId()));

            return convertView;
        }
    }



}
