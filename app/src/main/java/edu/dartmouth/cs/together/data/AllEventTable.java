package edu.dartmouth.cs.together.data;

/**
 * Created by TuanMacAir on 3/1/16.
 */
public class AllEventTable extends BaseEventTable {
    public static final String TABLE_NAME = "allevent";
    public static final String TABLE_CREATE_COMMAND = "create table "
            + AllEventTable.TABLE_NAME + "("
            + COLUMNS.createCommand()
            +");";
}
