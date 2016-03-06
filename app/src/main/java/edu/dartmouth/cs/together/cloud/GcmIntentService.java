package edu.dartmouth.cs.together.cloud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.dartmouth.cs.together.data.EventDataSource;
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

                //add message content to a new bundle
                Bundle messageBundle = new Bundle();
                long time = System.currentTimeMillis();
                messageBundle.putLong(Globals.KEY_MESSAGE_BUNDLE_TIME, time);

                if (message.startsWith("Event Delete")) {
                    String[] parts = message.split(":");
                    long id = Long.parseLong(parts[1].trim());
                    EventDataSource db = new EventDataSource(getApplicationContext());
                    db.deleteEvent(EventDataSource.ALL_EVENT, id);
                    db.deleteEvent(EventDataSource.JOINED_EVENT,id);

                    messageBundle.putInt(Globals.KEY_MESSAGE_BUNDLE_TYPE, Globals.MESSAGE_TYPE_EVENT_CANCEL);
                } else if (message.startsWith("Event Joined:")){
                    // msg.sendMessage(deviceList, "Event Joined:"
//                    + eventId + Globals.SPLITER
//                            + eventShortDesc + Globals.SPLITER
//                            +  Globals.JOIN + Globals.SPLITER
//                            + account);
                    messageBundle.putInt(Globals.KEY_MESSAGE_BUNDLE_TYPE, Globals.MESSAGE_TYPE_NEW_JOIN);
                } else if (message.startsWith("Event Updated:")) {
                    messageBundle.putInt(Globals.KEY_MESSAGE_BUNDLE_TYPE, Globals.MESSAGE_TYPE_EVENT_CHANGE);
                }else if(message.startsWith("Event Quited:")){
                    messageBundle.putInt(Globals.KEY_MESSAGE_BUNDLE_TYPE, Globals.MESSAGE_TYPE_EVENT_QUIT);
                } else if (message.startsWith("Question Answered:")){
                    messageBundle.putInt(Globals.KEY_MESSAGE_BUNDLE_TYPE, Globals.MESSAGE_TYPE_NEW_ANSWER);
                } else if(message.startsWith("Question Posted:")){
                    messageBundle.putInt(Globals.KEY_MESSAGE_BUNDLE_TYPE, Globals.MESSAGE_TYPE_NEW_QUESTION);
                }
                Intent sendMessageIntent = new Intent();

                messageBundle.putString(Globals.KEY_MESSAGE_BUNDLE_MESSAGE, message);
                sendMessageIntent.putExtras(messageBundle);
                sendMessageIntent.setAction(Globals.ACTION_NEW_MESSAGE_FROM_SERVER);
                sendBroadcast(sendMessageIntent);
                showToast(message);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}