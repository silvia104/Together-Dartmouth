package edu.dartmouth.cs.together.cloud;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.dartmouth.cs.together.R;
import edu.dartmouth.cs.together.data.EventDataSource;
import edu.dartmouth.cs.together.data.Message;
import edu.dartmouth.cs.together.data.MessageDataSource;
import edu.dartmouth.cs.together.utils.Globals;


/**
 * Created by TuanMacAir on 2/20/16.
 * intent service to handle GCM message
 */
public class GcmIntentService extends BaseIntentSerice {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(this.getClass().getName(), "Service started");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {  // has effect of unparcelling Bundle
            // Since we're not using two way messaging, this is all we really to check for
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());
                String message = extras.getString("message");

                //setup type and event


                String[] msgFields = message.split(Globals.SPLITER);
                Message messageToInsert = setupMessage(msgFields);
                long time = System.currentTimeMillis();
                messageToInsert.setMsgTime(time);
                messageToInsert.setIsRead(false);

                if (msgFields[0].startsWith("Event Delete")) {
//                    String[] parts = message.split(":");
                    long id = Long.parseLong(msgFields[1].trim());
                    EventDataSource db = new EventDataSource(getApplicationContext());
                    db.deleteEvent(EventDataSource.ALL_EVENT, id);
                    db.deleteEvent(EventDataSource.JOINED_EVENT,id);

                    messageToInsert.setMsgType(Globals.MESSAGE_TYPE_EVENT_CANCEL);
                } else if (message.startsWith("Event Joined:")){

                    messageToInsert.setMsgType(Globals.MESSAGE_TYPE_NEW_JOIN);
                } else if (message.startsWith("Event Updated:")) {

                    messageToInsert.setMsgType(Globals.MESSAGE_TYPE_EVENT_CHANGE);
                }else if(message.startsWith("Event Quited:")){
                    messageToInsert.setMsgType(Globals.MESSAGE_TYPE_EVENT_QUIT);
                } else if (message.startsWith("Question Answered:")){
                    messageToInsert.setMsgType(Globals.MESSAGE_TYPE_NEW_ANSWER);
                } else if(message.startsWith("Question Posted:")){
                    messageToInsert.setMsgType(Globals.MESSAGE_TYPE_NEW_QUESTION);
                }

                MessageDataSource mDB = new MessageDataSource(getApplicationContext());
                mDB.insertMessage(messageToInsert);


                showToast(message);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
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
        }else if(msgFields[0].startsWith("Question")) {
            int i = 1;
            while (i < msgFields.length) {
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

}