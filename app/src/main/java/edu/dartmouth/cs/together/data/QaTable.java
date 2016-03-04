package edu.dartmouth.cs.together.data;

/**
 * Created by TuanMacAir on 3/1/16.
 */
public class QaTable {
    public static final String TABLE_NAME = "qa";
    public enum COLUMNS{
        ID (0,"_idx"," integer primary key autoincrement not null"),
        QUESTION(1,"question"," string not null"),
        ANSWER(2,"answer"," string not null"),
        EVENT_ID(3,"event_id"," integer not null"),
        QA_ID(4,"qa_id", " integer unique not null");
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
                    + QUESTION.colName + QUESTION.command +", "
                    + ANSWER.colName + ANSWER.command +", "
                    + EVENT_ID.colName + EVENT_ID.command + ", "
                    + QA_ID.colName + QA_ID.command;
        }
    }
    public static final String TABLE_CREATE_COMMAND = "create table "
            + TABLE_NAME + "("
            + COLUMNS.createCommand()
            +");";
}
