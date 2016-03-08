package edu.dartmouth.cs.together.data;

/**
 * Created by TuanMacAir on 3/3/16.
 */
public class BaseEventTable {
    public enum COLUMNS{
        ID (0,"_id"," integer primary key autoincrement"),
        CATEGORY(1,"category"," integer not null"),
        SHORT_DESC(2,"short_desc"," string not null"),
        LATITUDE(3,"latitude"," real not null"),
        LONGITUDE(4,"longitude"," real not null"),
        LOCATION_DESC(5,"location_desc"," string not null"),
        TIME_MILLIS(6,"time_millis"," integer not null"),
        DURATION(7,"duration"," integer not null"),
        JOINER_LIMIT(8,"joiner_limit"," integer not null"),
        LONG_DESC(9,"long_desc"," string not null"),
        OWNER_ID(10,"owner_id"," integer not null"),
        JOINER_COUNT(11,"joiner_count"," integer not null"),
        STATUS(12,"status"," integer not null"),
        EVENT_ID(13,"event_id", " integer unique not null");
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
                    + CATEGORY.colName + CATEGORY.command +", "
                    + SHORT_DESC.colName + SHORT_DESC.command +", "
                    + LATITUDE.colName + LATITUDE.command +", "
                    + LONGITUDE.colName + LONGITUDE.command +", "
                    + LOCATION_DESC.colName + LOCATION_DESC.command +", "
                    + TIME_MILLIS.colName + TIME_MILLIS.command +", "
                    + DURATION.colName + DURATION.command +", "
                    + JOINER_LIMIT.colName + JOINER_LIMIT.command +", "
                    + LONG_DESC.colName + LONG_DESC.command +", "
                    + OWNER_ID.colName + OWNER_ID.command +", "
                    + JOINER_COUNT.colName + JOINER_COUNT.command + ", "
                    + STATUS.colName + STATUS.command + " ,"
                    + EVENT_ID.colName + EVENT_ID.command;
        }
    }
}
