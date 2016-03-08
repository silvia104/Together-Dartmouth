package edu.dartmouth.cs.together.data;

/**
 * Created by TuanMacAir on 3/1/16.
<<<<<<< HEAD
 * table to store event joined by user
=======
 * Updated from the server when the app is first launched
 * and when the current user have joined new events
>>>>>>> origin/Final
 */
public class JoinedEventTable extends BaseEventTable {

    public static final String TABLE_NAME = "joinedevent";
    public static final String TABLE_CREATE_COMMAND = "create table "
            + JoinedEventTable.TABLE_NAME + "("
            + COLUMNS.createCommand().replace("unique", "") + ", "
            + UserTable.COLUMNS.USER_ID.colName() + " integer not null, "
            + " unique (" + COLUMNS.EVENT_ID.colName()  + "," +UserTable.COLUMNS.USER_ID.colName()  +")"
            +");";
}
