package edu.dartmouth.cs.together.cloud;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Level;
import java.util.logging.Logger;


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
                if (message == null) return;



                //send broad cast to message service
                Intent newMessageIntent = new Intent();
                Bundle messageBundle = new Bundle();
                messageBundle.putString(Globals.KEY_MESSAGE_BUNDLE_MESSAGE, message);
                long time = System.currentTimeMillis();
                messageBundle.putLong(Globals.KEY_MESSAGE_BUNDLE_TIME, time);
                newMessageIntent.putExtras(messageBundle);
                newMessageIntent.setAction(Globals.ACTION_NEW_MESSAGE_FROM_SERVER);
                sendBroadcast(newMessageIntent);

                showToast(message);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}