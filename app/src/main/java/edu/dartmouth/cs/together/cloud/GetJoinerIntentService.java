package edu.dartmouth.cs.together.cloud;

import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.dartmouth.cs.together.data.Event;
import edu.dartmouth.cs.together.data.EventDataSource;
import edu.dartmouth.cs.together.data.User;
import edu.dartmouth.cs.together.data.UserDataSource;
import edu.dartmouth.cs.together.utils.Globals;

/**
 * Created by TuanMacAir on 3/3/16.
 */
public class GetJoinerIntentService extends BaseIntentSerice {
    public GetJoinerIntentService() {
        super("GetJoinerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(this.getClass().getName(), "Service started");
        long eventId = intent.getLongExtra(Event.ID_KEY, -1);
        String uploadState = "";
        try {
            Map<String, String> params = new HashMap<>();
            params.put(Event.ID_KEY, ""+eventId);
            // post add request
            String response = ServerUtilities.post(Globals.SERVER_ADDR + "/getjoiner.do", params);
            if(response.contains(":")) {
                List<User> users = parseJosonArray(response);
                new UserDataSource(getApplicationContext()).insertUsers(users);
                for (int i = 1; i < users.size(); i++) {
                    new EventDataSource(getApplicationContext())
                            .insertEventJoinerRelation(eventId, users.get(i).getId());
                }
                sendBroadcast(new Intent(Globals.RELOAD_JOINER_DATA));
            } else {
                uploadState = response;
            }
        } catch (Exception e1) {
            uploadState = "Sync failed: " + e1.getMessage();
            Log.e(this.getClass().getName(), "data posting error " + e1);
        }
        if (uploadState.length() > 0) {
            showToast(uploadState);
        }

    }

    private List<User> parseJosonArray(String data){
        List<User> result = new ArrayList<>();
        if (data.length() > 0) {
            data = data.substring(0, data.length() - 1);
            try {
                final JSONArray users = new JSONArray(data);
                final int n = users.length();
                for (int i = 0; i < n; ++i) {
                    final JSONObject user = users.getJSONObject(i);
                    result.add(new User(user));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}

