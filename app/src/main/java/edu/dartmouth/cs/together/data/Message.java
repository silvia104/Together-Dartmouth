package edu.dartmouth.cs.together.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by foxmac on 16/2/29.
 */
public class Message {


    private int msgId;
    private long eventId;
    private int msgType;
    private long msgTime;
    private String msgDescription;
    private boolean isRead;
    //for quesiton, maybe there should be this question id?
    private int qaId;


    public Message(Long eventId, int type, long time, String description ,boolean isRead, int qaId) {
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

    public Integer getQaId() {
        return qaId;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public void setMsgContent(String msgContent) {
        this.msgDescription = msgContent;
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
