package edu.dartmouth.cs.together;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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

import com.google.appengine.labs.repackaged.com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import edu.dartmouth.cs.together.data.Message;
import edu.dartmouth.cs.together.data.MessageDataSource;
import edu.dartmouth.cs.together.utils.Globals;


/**
 * Message Center Fragment
 * The user receives message here when:
 * joined event are modified by the initiator
 * other users joined or quit initiated events
 * A new question about an initiated event is posted
 * Or a question of the user is answered
 */

public class MessageCenterFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<List<Message>>{


    private Context mContext;
    private msgRowAdapter mAdapter;
    private List<Message> mMessageList = new ArrayList<>();;
    private static MessageDataSource mDB;


    public MessageCenterFragment() {
            // Required empty public constructor
            }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mAdapter = new msgRowAdapter();
        setListAdapter(mAdapter);
        mDB = new MessageDataSource(mContext);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState){
            if (savedInstanceState == null) {
                getLoaderManager().initLoader(0, null, this).forceLoad();
            }
            return  inflater.inflate(R.layout.fragment_message_center, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
            Message message = (Message) mAdapter.getItem(position);
            if(message.getMsgType() == Globals.MESSAGE_TYPE_NEW_QUESTION) {
                Bundle extras = new Bundle();
                extras.putString("question", message.getQuestion());
                extras.putString("eventDesc", message.getEventShortDesc());
                extras.putLong("eventId", message.getEventId());
                extras.putLong("qaId", message.getQaId());
                extras.putString("time", message.getDateTimeString("dd/MM hh:mm"));

                DialogFragment replyDialog = QuestionReplyDialogFragment.newInstance(extras);
                replyDialog.show(getChildFragmentManager(), "ReplyQuestion");
            }

            //If the message is not read:
            // set isRead to true, update the database, and set the icon to invisible
            if(!message.getIsRead()) {
                message.setIsRead(true);

                //update database in background
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

            //If the message is not read, set different icons according to message type
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

            //reverse the list the latest message is showed on the top
            return Lists.reverse(mDB.getCurrentUserRecords());
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
