package edu.dartmouth.cs.together.data;

/**
 * Created by TuanMacAir on 3/1/16.
 */
public class EventJoinerTable {
    public static final String TABLE_NAME = "eventjoiner";
    public enum COLUMNS{
        ID (0,"_id"," integer primary key autoincrement"),
        EVENT_ID(1,"event_id"," integer not null"),
        JOINER_ID(2,"joiner_id"," integer not null");
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
                    + EVENT_ID.colName + EVENT_ID.command +", "
                    + JOINER_ID.colName + JOINER_ID.command;
        }
    }
    public static final String TABLE_CREATE_COMMAND = "create table "
            + TABLE_NAME + "("
            + COLUMNS.createCommand()
            +");";
}
