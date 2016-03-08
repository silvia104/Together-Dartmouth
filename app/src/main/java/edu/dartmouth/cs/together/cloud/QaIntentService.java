package edu.dartmouth.cs.together.cloud;

import android.content.ContentValues;
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
import edu.dartmouth.cs.together.data.Qa;
import edu.dartmouth.cs.together.data.QaDataSource;
import edu.dartmouth.cs.together.data.QaTable;
import edu.dartmouth.cs.together.utils.Globals;

/**
 * Created by TuanMacAir on 3/3/16.
 * intent service to handle question/answer related operations
 */
public class QaIntentService extends BaseIntentSerice {
    public QaIntentService() {
        super("QaIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String q = intent.getStringExtra(Qa.Q_KEY);
        String a = intent.getStringExtra(Qa.A_KEY);
        long qaId = intent.getLongExtra(Qa.ID_KEY, -1);
        long eventId = intent.getLongExtra(Event.ID_KEY, -1);
        List<Qa> qas;
        String uploadState = "";
        try {
            Map<String, String> params = new HashMap<>();
            // get qa list from server
            if (qaId == -1) {
                params.put(Event.ID_KEY,eventId+"");
                params.put("action",Globals.ACTION_POLL);
            } else {
                // send qa info to server
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Qa.ID_KEY, qaId);
                jsonObject.put(Event.ID_KEY, eventId);
                if (a == null) {
                    jsonObject.put(Qa.Q_KEY, q);
                    params.put("action", Globals.ACTION_ADD);
                } else {
                    jsonObject.put(Qa.A_KEY, a);
                    params.put("action", Globals.ACTION_UPDATE);
                }
                params.put("json", jsonObject.toString());
            }
            try {
                String data = ServerUtilities.post(Globals.SERVER_ADDR + "/qa.do", params);

                //update local db
                QaDataSource db =  new QaDataSource(getApplicationContext());
                if (data.length() > 0){
                    qas = parseJosonArray(data);
                    db.insertQas(qas);
                    if (qas.size() > 0){
                        // reload qa data in event activities
                        sendBroadcast(new Intent(Globals.RELOAD_QUESTION_DATA_IN_DETAIL));
                    }
                }else {
                    if (a==null) {
                        Qa qa = new Qa();
                        qa.setQuestion(q);
                        qa.setId(qaId);
                        qa.setEventId(eventId);
                        db.insertQa(qa);
                    } else {
                        // update local db
                        ContentValues values = new ContentValues();
                        values.put(QaTable.COLUMNS.ANSWER.colName(),a);
                        db.updateQa(qaId,values);
                    }
                    // reload qa data in event activities

                    sendBroadcast(new Intent(Globals.RELOAD_QUESTION_DATA_IN_DETAIL));
                }

            } catch (Exception e1) {
                uploadState = "Sync failed: " + e1.getMessage();
                Log.e(this.getClass().getName(), "data posting error " + e1);
            }
            if (uploadState.length() > 0) {
                showToast(uploadState);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // parse json string to list of qas
    private List<Qa> parseJosonArray(String data){
        List<Qa> result = new ArrayList<>();
        if (data.length() > 0) {
            data = data.substring(0, data.length() - 1);
            try {
                final JSONArray qas = new JSONArray(data);
                final int n = qas.length();
                for (int i = 0; i < n; ++i) {
                    final JSONObject qa = qas.getJSONObject(i);
                    result.add(new Qa(qa));
                }
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
