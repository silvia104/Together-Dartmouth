package edu.dartmouth.cs.together.data;

import org.json.JSONException;
import org.json.JSONObject;

import edu.dartmouth.cs.together.utils.Helper;

/**
 * Created by TuanMacAir on 2/27/16.
 */
public class Qa {
    public static final String Q_KEY = "question";
    public static final String A_KEY = "answer";
    public static final String ID_KEY = "question_id";

    private long mEventId;
    private String mQ="";
    private String mA="";
    private long mQaId;
    public Qa(String q, String a){
        mQ = q;
        mA = a;
    }
    public Qa(String q){
        mQ = q;
        mQaId = Helper.intToUnsignedLong(mQ.hashCode());
    }

    public Qa() {

    }
    public Qa(JSONObject jsonObject){
        try {
            mEventId = jsonObject.getLong(Event.ID_KEY);
            mQ = jsonObject.getString(Q_KEY);
            mA = jsonObject.getString(A_KEY);
            mQaId = jsonObject.getLong(ID_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setAnswer(String a){
        mA = a;
    }
    public String getQuestion() {
        return mQ;
    }
    public String getAnswer() {
        return mA;
    }
    public long getId() {
        return mQaId;
    }
    public long getEventId() {
        return mEventId;
    }

    public void setQuestion(String q) {
        mQ = q;
    }

    public void setId(long id) {
        mQaId = id;
    }

    public void setEventId(long id) {
        mEventId = id;
    }
}
