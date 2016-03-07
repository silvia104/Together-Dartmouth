package edu.dartmouth.cs.together.data;

/**
 * Created by TuanMacAir on 3/1/16.
 */
public class UserTable {
    public static final String TABLE_NAME = "users";
    public enum COLUMNS{
        ID (0,"_idx"," integer primary key autoincrement not null"),
        EMAIL(1,"email"," string not null"),
        RATE(2,"rate"," string not null"),
        PHOTO_URL(3,"photo_url"," string"),
        USER_ID(4,"user_id"," integer unique not null"),
        PHOTO(5,"photo"," blob");
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
                    + PHOTO_URL.colName + PHOTO_URL.command + ", "
                    + USER_ID.colName + USER_ID.command +", "
                    + PHOTO.colName() + PHOTO.command;
        }
    }
    public static final String TABLE_CREATE_COMMAND = "create table "
            + TABLE_NAME + "("
            + COLUMNS.createCommand()
            +");";
}
