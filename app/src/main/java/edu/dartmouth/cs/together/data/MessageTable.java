package edu.dartmouth.cs.together.data;

/**
 * Created by foxmac on 16/3/2.
 */
public class MessageTable {
    public static final String TABLE_NAME = "message";
//    private int msgId;
//    private long eventId;
//    private int msgType;
//    private long msgTime;
//    private String msgDescription;
//    private boolean isRead;
//    private int qaId;
enum COLUMNS{
    ID (0,"_idx"," integer primary key autoincrement not null"),
    MSG_TYPE(1,"type"," int not null"),
    TIME(2,"time"," integer not null"),
    DESCRIPTION(3, "description", " string not null"),
    IS_READ(4, "read", " integer not null"),
    EVENT_ID(5,"event_id"," integer not null"),
    EVENT_SHORT_DESC(6, "event_short_desc", " string not null"),
    QA_ID(7, "qa", " integer"),
    QUESTION(8, "q", " string not null"),
    ANSWER(9, "a", " string not null");

    private final int index;
    private final String colName;
    private final String command;
    COLUMNS(int index,String name, String command){
        this.index = index;
        this.colName = name;
        this.command = command;
    }

    public int index(){return index;}
    public String colName(){return colName;}
    public String command(){return command;}
    static String createCommand() {
        return ID.colName + ID.command +", "
                + MSG_TYPE.colName + MSG_TYPE.command +", "
                + TIME.colName + TIME.command +", "
                + DESCRIPTION.colName + DESCRIPTION.command + ", "
                + IS_READ.colName + IS_READ.command + ", "
                + EVENT_ID.colName + EVENT_ID.command + ", "
                + EVENT_SHORT_DESC.colName + EVENT_SHORT_DESC.command + ", "
                + QA_ID.colName + QA_ID.command + ", "
                + QUESTION.colName + QUESTION.command + ", "
                + ANSWER.colName + ANSWER.command;
    }
}
    public static final String TABLE_CREATE_COMMAND = "create table "
            + TABLE_NAME + "("
            + COLUMNS.createCommand()
            +");";

}
