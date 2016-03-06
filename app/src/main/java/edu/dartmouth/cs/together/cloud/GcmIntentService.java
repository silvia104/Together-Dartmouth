package edu.dartmouth.cs.together.cloud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.dartmouth.cs.together.data.EventDataSource;


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
                if (message.startsWith("Event Delete")) {
                    String[] parts = message.split(":");
                    long id = Long.parseLong(parts[1].trim());
                    EventDataSource db = new EventDataSource(getApplicationContext());
                    db.deleteEvent(EventDataSource.ALL_EVENT, id);
                    db.deleteEvent(EventDataSource.JOINED_EVENT,id);
                } else if (message.startsWith("Event Joined:")){
                    // Backend:
                    // msg.sendMessage(deviceList, "Event Joined:" + eventId + ":" + userId);
                    // msg.sendMessage(deviceList, "Event Joined:" + EventShortDescription + ":" + UserName);


                } else if (message.startsWith("Event Updated:")) {
                    //msg.sendMessage(deviceList, "Event Updated:" + event.getEventId());
                    //msg.sendMessage(deviceList, "Event Updated:" + Item:XXX + "changed to" + "XXX" ##
                    // Item:XXX + "is changed to" + "XXX" ... );

                }else if(message.startsWith("Event Quited:")){
                    //msg.sendMessage(deviceList, "Event Quited:" + eventId + ":" + userId);
                    //msg.sendMessage(deviceList, "Event Quited:" + EventShortDescription + ":" + UserName);


                } else if (message.startsWith("Question Answered:")){
                    //message = "Question Posted:" + eventId + "," + questionId + "," + q;
                    //message = "Question Posted:" + EventShortDescription + "," + q;


                } else if(message.startsWith("Question Posted:")){
                    //message = "Question Posted:" + EventShortDescription + "," + q;

                }
                showToast(message);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}