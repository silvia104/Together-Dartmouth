package edu.dartmouth.cs.together.data;

/**
 * Created by TuanMacAir on 3/1/16.
 */
public class UserTable {
    public static final String TABLE_NAME = "users";
    enum COLUMNS{
        ID (0,"_idx"," integer primary key autoincrement not null"),
        EMAIL(1,"email"," string not null"),
        RATE(2,"rate"," real not null"),
        PHOTO(3,"photo"," blob not null");
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
                    + EMAIL.colName + EMAIL.command +", "
                    + RATE.colName + RATE.command +", "
                    + PHOTO.colName + PHOTO.command;
        }
    }
    public static final String TABLE_CREATE_COMMAND = "create table "
            + TABLE_NAME + "("
            + COLUMNS.createCommand()
            +");";
}
