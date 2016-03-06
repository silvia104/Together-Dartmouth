package edu.dartmouth.cs.together.data;

/**
 * Created by foxmac on 16/3/2.
 */
public class MessageTable {
    public static final String TABLE_NAME = "message";


enum COLUMNS{
    ID (0,"_idx"," integer primary key autoincrement not null"),
    MSG_TYPE(1,"type"," int not null"),
    TIME(2,"time"," integer not null"),
    IS_READ(3, "read", " integer not null"),
    EVENT_ID(4,"event_id"," integer not null"),
    EVENT_SHORT_DESC(5, "event_short_desc", " string not null"),
    USER_ID(6, "user_id", " integer"),
    USER_NAME(7, "user_name", " string"),
    QA_ID(8, "qa", " integer"),
    QUESTION(9, "q", " string"),
    ANSWER(10, "a", " string");

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
                + IS_READ.colName + IS_READ.command + ", "
                + EVENT_ID.colName + EVENT_ID.command + ", "
                + EVENT_SHORT_DESC.colName + EVENT_SHORT_DESC.command + ", "
                + USER_ID.colName + USER_ID.command + ", "
                + USER_NAME.colName + USER_NAME.command + ", "
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
