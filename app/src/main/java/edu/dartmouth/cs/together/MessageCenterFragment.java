package edu.dartmouth.cs.together;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.dartmouth.cs.together.data.Message;
import edu.dartmouth.cs.together.data.MessageDataSource;
import edu.dartmouth.cs.together.utils.Globals;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessageCenterFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<List<Message>>{


    private Context mContext;
    private msgRowAdapter mAdapter;
    private List<Message> mMessageList = new ArrayList<>();;
    private NewMessageReceiver mNewMessageReceiver;
    private static MessageDataSource mDB;


    public MessageCenterFragment() {
            // Required empty public constructor
            }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        mAdapter = new msgRowAdapter();
        setListAdapter(mAdapter);
        mNewMessageReceiver = new NewMessageReceiver();
        mDB = new MessageDataSource(mContext);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
            //setRetainInstance(true);
            if (savedInstanceState == null) {
                getLoaderManager().initLoader(0, null, this).forceLoad();
            }
            return  inflater.inflate(R.layout.fragment_message_center, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
            Message message = (Message) mAdapter.getItem(position);
            if(message.getMsgType() == Globals.MESSAGE_TYPE_NEW_QUESTION) {
//                ArrayList<String> stringList = getStringList(message);
//                long[] idList = getIdList(message);
                Bundle extras = new Bundle();
                extras.putString("question", message.getQuestion());
                extras.putString("eventDesc", message.getEventShortDesc());
                extras.putLong("eventId", message.getEventId());
                extras.putLong("qaId", message.getQaId());
                extras.putString("time", message.getDateTimeString("dd/MM hh:mm"));

                DialogFragment replyDialog = QuestionReplyDialogFragment.newInstance(extras);
                replyDialog.show(getChildFragmentManager(), "ReplyQuestion");
            }
            //set isRead to true;
            if(!message.getIsRead()) {
                message.setIsRead(true);
                //update database
                UpdateNewRecordTask updateTask = new UpdateNewRecordTask();
                updateTask.doInBackground(message);
                ImageView blob = (ImageView) v.findViewById(R.id.message_type);
                blob.setVisibility(View.INVISIBLE);
                TextView eventName = (TextView) v.findViewById(R.id.message_event);
                eventName.setTypeface(null, Typeface.NORMAL);
            }

    }


    private class msgRowAdapter extends ArrayAdapter {

        private ImageView msgType;
        private TextView msgTime;
        private TextView eventBrief;
        private TextView eventName;
        msgRowAdapter(){
            super(mContext, R.layout.message_item, R.id.message_event, mMessageList);
        }

        public View getView (int position,  View convertView, ViewGroup parent) {

            Message message = (Message) getItem(position);
            if(convertView == null) {
                convertView = super.getView(position, convertView, parent);
                msgType = (ImageView) convertView.findViewById(R.id.message_type);
                msgTime = (TextView) convertView.findViewById(R.id.message_time);
                eventBrief = (TextView) convertView.findViewById(R.id.message_brief);
                eventName = (TextView) convertView.findViewById(R.id.message_event);
                msgType.setTag(position);
            }
            //set the image view invisible only if it's the same position
            if (!message.getIsRead() && (int)msgType.getTag() == position) {
                eventName.setTypeface(null, Typeface.BOLD);
                if (message.getMsgType() == Globals.MESSAGE_TYPE_NEW_QUESTION
                    || message.getMsgType() == Globals.MESSAGE_TYPE_NEW_ANSWER) {

                    msgType.setImageResource(R.drawable.ic_new_question);

                } else  if (message.getMsgType() == Globals.MESSAGE_TYPE_NEW_JOIN
                        || message.getMsgType() == Globals.MESSAGE_TYPE_EVENT_QUIT){

                    msgType.setImageResource(R.drawable.ic_new_joiner);
                }
                else if (message.getMsgType() == Globals.MESSAGE_TYPE_EVENT_CHANGE
                        ||message.getMsgType() == Globals.MESSAGE_TYPE_EVENT_CANCEL){

                    msgType.setImageResource(R.drawable.ic_new_event_change);
                }

            }

            String time = message.getDateTimeString("dd/MM hh:mm");
            msgTime.setText(time);
            eventBrief.setText(message.getMsgContent());
            eventName.setText(String.valueOf(message.getEventShortDesc()));
            return convertView;
        }
    }




    public class NewMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive (Context context,Intent intent) {

            //write message in local database
            Bundle extras = intent.getExtras();
            String message = extras.getString(Globals.KEY_MESSAGE_BUNDLE_MESSAGE);
            if (message == null) return;
            String[] msgFields = message.split(Globals.SPLITER);
            //is msgFilds.length > 1, there must be a spliter inserted when the
            // message was sent
            if(msgFields.length > 1) {
                Message messageToInsert = setupMessage(msgFields);
                long time = extras.getLong(Globals.KEY_MESSAGE_BUNDLE_TIME);
                int type = extras.getInt(Globals.KEY_MESSAGE_BUNDLE_TYPE);
                messageToInsert.setMsgType(type);
                messageToInsert.setMsgTime(time);
                messageToInsert.setIsRead(false);
                InsertNewMessage insertNewMessage = new InsertNewMessage();
                insertNewMessage.doInBackground(messageToInsert);


            }
        }
    }


    private Message setupMessage( String[] msgFields){
        Message msg = new Message();


        if(msgFields[0].startsWith("Event")){
            int i=1;
            while(i<msgFields.length) {
                switch (i) {
                    case 1:
                        msg.setEventId(Long.valueOf(msgFields[1]));
                        break;
                    case 2:
                        msg.setEventShortDesc(msgFields[2]);
                        break;
                    case 3:
                        msg.setUserId(Long.valueOf(msgFields[3]));
                        break;
                    case 4:
                        msg.setUserName(msgFields[4]);
                        break;
                }
                i++;
            }
        }else if(msgFields[0].startsWith("Question")){
            int i=1;
            while(i<msgFields.length) {
                switch (i) {
                    case 1:
                        msg.setEventId(Long.valueOf(msgFields[1]));
                        break;
                    case 2:
                        msg.setEventShortDesc(msgFields[2]);
                        break;
                    case 3:
                        msg.setQaId(Long.valueOf(msgFields[3]));
                        break;
                    case 4:
                        msg.setQuestion(msgFields[4]);
                        break;
                    case 5:
                        msg.setAnswer(msgFields[5]);
                }
                i++;
            }
        }
        return msg;
    }

    class InsertNewMessage extends AsyncTask<Message, Void, Long> {
        private MessageDataSource mDB;
        // get the new record.
        @Override
        protected Long doInBackground(Message... msg) {
            mDB = new MessageDataSource(getActivity());
            Long id = mDB.insertMessage(msg[0]);
            return id;
        }
        // update ListView
        public void onPostExecute(Message msg) {
            mMessageList.add(msg);
            mAdapter.notifyDataSetChanged();
        }
    }

    class UpdateNewRecordTask extends AsyncTask<Message, Message, Long> {

        // get the new record.
        @Override
        protected Long doInBackground(Message... msg) {
            Long id= mDB.updateIsRead(msg[0].getMsgId(), msg[0]);
            return id;
        }
        // update ListView
        @Override
        protected void onPostExecute(Long id) {
            mAdapter.notifyDataSetChanged();
        }
    }

    static class RecordLoader extends AsyncTaskLoader<List<Message>> {
        private MessageDataSource mDB;
        public RecordLoader(Context context) {
            super(context);
            mDB = new MessageDataSource(context);
        }

        // get all records in background as loader
        @Override
        public List<Message> loadInBackground() {
            //return mDB.getAllRecords();
            return mDB.getCurrentUserRecords();
        }

    }
    @Override
    public Loader<List<Message>> onCreateLoader(int id, Bundle args) {
        return new RecordLoader(getActivity());
    }


    @Override
    public void onLoadFinished(Loader<List<Message>> loader, List<Message> data) {
        mMessageList.addAll(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Message>> loader) {

    }
}
