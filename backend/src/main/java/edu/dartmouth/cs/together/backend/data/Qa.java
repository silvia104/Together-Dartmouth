package edu.dartmouth.cs.together.backend.data;

/**
 * Created by TuanMacAir on 2/27/16.
 */
public class Qa {
    public static final String Qa_PARENT_ENTITY_KEY = "QaParent";
    public static final String Qa_PARENT_ENTITY_NAME = "QaParent";
    public static final String Qa_ENTITY_NAME = "Qa";

    public static final String Q_KEY = "question";
    public static final String A_KEY = "answer";
    public static final String ID_KEY = "question_id";

    private long mEventId;
    private String mQ="";
    private String mA="";
    private long mQaId;
    public Qa() {

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

    public void setAnswer(String a) {
        mA = a;
    }
}
