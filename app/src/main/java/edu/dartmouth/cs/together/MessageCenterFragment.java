package edu.dartmouth.cs.together;


import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        //setRetainInstance(true);
        mContext = getActivity();
        mAdapter = new msgRowAdapter();
        setListAdapter(mAdapter);
        if (savedInstanceState == null) {
            getLoaderManager().initLoader(0, null, this).forceLoad();
        }
        mDB = new MessageDataSource(mContext);
        mNewMessageReceiver = new NewMessageReceiver();
        //Message(Long eventId, int type, long time, String description ,boolean isRead, int qaId) {
        //Message message1 = new Message(20L,1,89898998989898L, "is there?", false, 10L);
        //mDB.insertMessage(message1);
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
            }
        //set isRead to true;
        if(!message.getIsRead()) {
            message.setIsRead(true);
            //update database
            UpdateNewRecordTask updateTask = new UpdateNewRecordTask();
            updateTask.doInBackground(message);
//            mDB.updateIsRead(message.getMsgId(), message);
            ImageView blob = (ImageView) v.findViewById(R.id.message_type);
            blob.setVisibility(View.INVISIBLE);
            TextView eventName = (TextView) v.findViewById(R.id.message_event);
            eventName.setTypeface(null, Typeface.NORMAL);
        }

    }


    private ArrayList<String> getStringList(Message message){
        ArrayList<String> stringList = new ArrayList<>();

        //TODO: Async query event by eventID
        stringList.add("Event: "+String.valueOf(message.getEventId()));
        stringList.add(message.getDateTimeString("dd/MM hh:mm"));
        //TODO: Async query qa by qaID
        stringList.add("Question: " +String.valueOf(message.getQaId()));
        return stringList;
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

    private class msgRowAdapter extends ArrayAdapter{

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
                if (message.getMsgType() == Globals.MESSAGE_TYPE_NEW_QUESTION) {
                    msgType.setImageResource(R.drawable.ic_new_question);
                } else {
                    msgType.setImageResource(R.drawable.ic_new_joiner);
                }
            }

            String time = message.getDateTimeString("dd/MM hh:mm");
            msgTime.setText(time);
            eventBrief.setText(message.getMsgContent());

            //TODO: get event name from event id
            eventName.setText(String.valueOf(message.getEventId()));
            return convertView;
        }
    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN: {
//                int x = (int) event.getX();
//                int y = (int) event.getY();
//                //我们想知道当前点击了哪一行
//                int position = pointToPosition(x, y);
//                Log.e(TAG, "postion=" + position);
//                if (position != INVALID_POSITION) {
//                    //得到当前点击行的数据从而取出当前行的item。
//                    //可能有人怀疑，为什么要这么干？为什么不用getChildAt(position)？
//                    //因为ListView会进行缓存，如果你不这么干，有些行的view你是得不到的。
//                    //Message msg = (Message) getItemAtPosition(position);
//                    Message msg = (Message) mAdapter.getItem(position);
//                    mFocusedItemView = data.slideView;
//                    Log.e(TAG, "FocusedItemView=" + mFocusedItemView);
//                }
//            }
//            default:
//                break;
//        }
//
//        //向当前点击的view发送滑动事件请求，其实就是向SlideView发请求
//        if (mFocusedItemView != null) {
//            mFocusedItemView.onRequireTouchEvent(event);
//        }
//
//        return super.onTouchEvent(event);
//    }




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
            return mDB.getAllRecords();
        }

    }

    public class NewMessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive (Context context,Intent intent) {

            //write message in local database
            Bundle extras = intent.getExtras();
            String message = extras.getString(Globals.KEY_MESSAGE_BUNDLE_MESSAGE);
            if (message == null) return;
            String[] msgFields = message.split(",");
            if(msgFields.length == 4) {
                long time = extras.getLong(Globals.KEY_MESSAGE_BUNDLE_TIME);
                Message messageToInsert = new Message();
                messageToInsert = setupMessage(messageToInsert, time, msgFields);
                InsertNewMessage insertNewMessage = new InsertNewMessage();
                insertNewMessage.doInBackground(messageToInsert);

            }
        }
    }
    private Message setupMessage(Message msg, long time, String[] msgFields){
        msg.setMsgTime(time);
        msg.setIsRead(false);
        msg.setMsgType(Integer.valueOf(msgFields[0]));
        msg.setMsgDescription(msgFields[1]);
        msg.setEventId((long)Long.parseLong(msgFields[2], 10));
        msg.setQaId((long) Long.parseLong(msgFields[3], 10));
        return msg;
    }

    class InsertNewMessage extends AsyncTask<Message, Void, Long>{

        // get the new record.
        @Override
        protected Long doInBackground(Message... msg) {
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
}
