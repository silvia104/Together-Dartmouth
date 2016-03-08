package edu.dartmouth.cs.together.data;

/**
 * Created by TuanMacAir on 3/1/16.
 * Updated from the server when the app is first launched
 * and when the current user have initiated new events
 */
public class MyOwnEventTable extends BaseEventTable{
    public static final String TABLE_NAME = "myownevents";


    public static final String TABLE_CREATE_COMMAND = "create table "
            + TABLE_NAME + "("
            + COLUMNS.createCommand()
            +");";


}
