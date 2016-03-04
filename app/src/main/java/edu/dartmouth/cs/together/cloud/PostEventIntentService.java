package edu.dartmouth.cs.together.cloud;

import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.ads.mediation.customevent.CustomEventNative;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.data.EventDataSource;
import edu.dartmouth.cs.together.data.MyOwnEventTable;
import edu.dartmouth.cs.together.utils.Globals;


/**
 * Created by TuanMacAir on 2/20/16.
 * intent service to upload record
 */
public class PostEventIntentService extends BaseIntentSerice {
    public static final String ACTION_KEY = "action_key";
    public PostEventIntentService() {
        super("PostEventIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(this.getClass().getName(), "Service started");
        String action = intent.getStringExtra(ACTION_KEY);
        long eventId = intent.getLongExtra(Event.ID_KEY, -1);
        Event event = new EventDataSource(getApplicationContext()).queryMyOwnEventById(eventId);
        String uploadState = "";
        // Upload all entries in a json array
        try {
                JSONObject json = new JSONObject();
                json.put(Event.ID_KEY,event.getEventId());
                json.put(Event.CATEGORY_KEY, event.getCategoryIdx());
                json.put(Event.SHORT_DESC_KEY, event.getShortdesc());
                json.put(Event.LATITUDE_KEY, event.getLatLng().latitude);
                json.put(Event.LONGITUDE_KEY, event.getLatLng().longitude);
                json.put(Event.LOCATION_KEU, event.getLocation());
                json.put(Event.TIME_MILLIS_KEY, event.getTimeMillis());
                json.put(Event.DURATION_KEY, event.getDuration());
                json.put(Event.OWNER_KEY, event.getOwner());
                json.put(Event.LIMIT_KEY, event.getLimit());
                json.put(Event.LONG_DESC_KEY, event.getLongDesc());
            try {
                Map<String, String> params = new HashMap<>();
                params.put("json", json.toString());
                params.put("action",action);
                // post add request
                ServerUtilities.post(Globals.SERVER_ADDR + "/addevent.do", params);
                ContentValues values = new ContentValues();
                values.put(MyOwnEventTable.COLUMNS.STATUS.colName(), Event.STATUS_POSTED);
                new EventDataSource(getApplicationContext()).updateMyOwnEvent(event.getEventId(),
                        values);
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
