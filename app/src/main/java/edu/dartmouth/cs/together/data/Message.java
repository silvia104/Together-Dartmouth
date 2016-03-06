package edu.dartmouth.cs.together.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.dartmouth.cs.together.cloud.GcmBroadcastReceiver;
import edu.dartmouth.cs.together.utils.Globals;

/**
 * Created by foxmac on 16/2/29.
 */
public class Message {


    private long msgId;
    private long eventId;
    private long userId;
    private String userName;
    private String eventShortDesc;
    private int msgType;
    private long msgTime;
    private boolean isRead;
    private long qaId;
    private String question;
    private String answer;




    public Message(){

        this.eventId = -1;
        this.userId = -1;
        this.userName = "";
        this.eventShortDesc = "";
        this.msgType = -1;
        this.msgTime = 0;
        this.isRead = false;
        this.qaId = -1;
        this.question = "";
        this.answer = "";
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public int getMsgType() {
        return msgType;
    }

    public long getQaId() {
        return qaId;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }


    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }


    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }


    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public boolean getIsRead() {

        return isRead;
    }


    public boolean isRead() {
        return isRead;
    }


    public void setEventId(long eventId) {
        this.eventId = eventId;
    }


    public void setQaId(long qaId) {
        this.qaId = qaId;
    }


    public String getEventShortDesc() {
        return eventShortDesc;
    }

    public String getQuestion() {
        return question;
    }
    public String getAnswer() {
        return answer;

    }
    public void setEventShortDesc(String eventShortDesc) {
        this.eventShortDesc = eventShortDesc;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


    public String getDateTimeString (String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.msgTime);
        return formatter.format(calendar.getTime());
    }

    public String getMsgContent() {
        String content = "";
        switch (this.msgType){
            case Globals.MESSAGE_TYPE_NEW_JOIN:
                content += userName + " has joined " + eventShortDesc;
                break;
            case Globals.MESSAGE_TYPE_EVENT_QUIT:
                content += userName + " has quit " + eventShortDesc;
                break;
            case Globals.MESSAGE_TYPE_EVENT_CHANGE:
                content += eventShortDesc + " is modified by the initiator";
                break;
            case Globals.MESSAGE_TYPE_EVENT_CANCEL:
                content += eventShortDesc + " is canceled by the initiator";
                break;
            case Globals.MESSAGE_TYPE_NEW_ANSWER:
                content += "Question: " + question + " of event " + eventShortDesc
                        + " is answered: " + answer;
                break;
            case Globals.MESSAGE_TYPE_NEW_QUESTION:
                content += "A new question: " + question + " of event " + eventShortDesc
                 + " is put forward. ";
                break;

        }

        return content;
    }
}
