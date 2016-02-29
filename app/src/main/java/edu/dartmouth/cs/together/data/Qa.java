package edu.dartmouth.cs.together.data;

/**
 * Created by TuanMacAir on 2/27/16.
 */
public class Qa {
    private long mEventId;
    private String mQ;
    private String mA;
    public Qa(String q, String a){
        mQ = q;
        mA = a;
    }
    public Qa(String q){
        mQ = q;
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
}
