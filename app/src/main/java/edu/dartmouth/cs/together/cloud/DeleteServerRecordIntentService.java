package edu.dartmouth.cs.together.cloud;

import android.content.Intent;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.utils.Globals;

/**
 * Created by TuanMacAir on 2/21/16.
 * intent service for deleting record on server datastore.
 */
public class DeleteServerRecordIntentService extends BaseIntentSerice {
    public DeleteServerRecordIntentService(){
        super("DeleteServerRecordIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(this.getClass().getName(), "Service started");
        //get id of record to delete
        Long id = intent.getLongExtra(Event.ID_KEY, -1);
        if (id == -1) return;
        String deleteState= "";
        // add parameters for the http request
        Map<String,String> params = new HashMap<>();
        params.put(Event.ID_KEY, id+"");
        params.put("source","device");
        try {
            // post delete request to server
            ServerUtilities.post(Globals.SERVER_ADDR + "/delete.do",
                    params);
        } catch (Exception e1) {
            deleteState = "Sync failed: " + e1.getMessage();
            Log.e(this.getClass().getName(), "data posting error " + e1);
        }
        if(deleteState.length() == 0) {
            deleteState = "Server side delete is done!";
        }
       showToast(deleteState);
    }
}