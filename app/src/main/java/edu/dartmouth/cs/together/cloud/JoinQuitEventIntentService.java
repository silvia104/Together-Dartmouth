package edu.dartmouth.cs.together.cloud;

import android.app.usage.UsageEvents;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import edu.dartmouth.cs.together.JoinerListActivity;
import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.data.EventDataSource;
import edu.dartmouth.cs.together.data.EventJoinerTable;
import edu.dartmouth.cs.together.data.MyOwnEventTable;
import edu.dartmouth.cs.together.data.User;
import edu.dartmouth.cs.together.utils.Globals;


/**
 * Created by TuanMacAir on 2/20/16.
 * intent service to upload record
 */
public class JoinQuitEventIntentService extends BaseIntentSerice {
    public JoinQuitEventIntentService() {
        super("JoinQuitEventIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(this.getClass().getName(), "Service started");
        String action = intent.getStringExtra(Globals.ACTION_KEY);
        long eventId = intent.getLongExtra(Event.ID_KEY, -1);
        long joinerId = intent.getLongExtra(User.ID_KEY, -1);
        String uploadState = "";
        // Upload all entries in a json array
        try {
                JSONObject json = new JSONObject();
                json.put(Event.ID_KEY,eventId);
                json.put(User.ID_KEY,joinerId);
            try {
                Map<String, String> params = new HashMap<>();
                params.put("json", json.toString());
                params.put("action", action);
                // post add request
                ServerUtilities.post(Globals.SERVER_ADDR + "/eventops.do", params);

                EventDataSource db = new EventDataSource(getApplicationContext());
                if (action.equals(Globals.ACTION_JOIN)) {
                    db.insertEventJoinerRelation(eventId, joinerId);
                } else if (action.equals(Globals.ACTION_QUIT)) {
                    db.deleteEventJoinerRealtion(eventId, joinerId);
                    db.deleteEvent(EventDataSource.JOINED_EVENT, eventId);
                }
            } catch (Exception e1) {
                uploadState = "Sync failed: " + e1.getMessage();
                Log.e(this.getClass().getName(), "data posting error " + e1);
            }
            if (uploadState.length() > 0) {
                showToast(uploadState);
            }

        } catch (JSONException e){
            Log.e(this.getClass().getName(), e.getCause().toString());
        }
        
    }

}
