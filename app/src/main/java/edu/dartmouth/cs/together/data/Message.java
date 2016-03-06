package edu.dartmouth.cs.together.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by foxmac on 16/2/29.
 */
public class Message {


    private long msgId;
    private long eventId;
    private int msgType;
    private long msgTime;
    private boolean isRead;
    //for quesiton, maybe there should be this question id?
    private long qaId;


    public Message(){

    }

    public Message(Long eventId, int type, long time, String description ,boolean isRead, long qaId) {
        this.eventId = eventId;
        this.msgType = type;
        this.msgTime = time;
        this.msgDescription = description;
        this.isRead = isRead;
        this.qaId = qaId;
    }

    public Long getEventId() {
        return eventId;
    }

    public int getMsgType() {
        return msgType;
    }

    public String getMsgContent() {
        return msgDescription;
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

    public void setQuestion(int qaId) {
        this.qaId = qaId;
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

    public String getMsgDescription() {
        return msgDescription;
    }

    public boolean isRead() {
        return isRead;
    }

    private String msgDescription;

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public void setMsgDescription(String msgDescription) {
        this.msgDescription = msgDescription;
    }

    public void setQaId(long qaId) {
        this.qaId = qaId;
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
}
