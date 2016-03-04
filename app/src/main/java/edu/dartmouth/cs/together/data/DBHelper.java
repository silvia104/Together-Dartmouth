package edu.dartmouth.cs.together.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by TuanMacAir on 3/1/16.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper mHelper = null;
    private static final String DB_NAME="together.db";
    private static final int DB_VERSION = 1;
    private DBHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }
    // use getInstance to get the only instance of this class.
    synchronized
    public static DBHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new DBHelper(context);
        }
        return mHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MyOwnEventTable.TABLE_CREATE_COMMAND);
        db.execSQL(UserTable.TABLE_CREATE_COMMAND);
        db.execSQL(QaTable.TABLE_CREATE_COMMAND);
        db.execSQL(MessageTable.TABLE_CREATE_COMMAND);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // DO NOTHING FOR NOW;
    }

}
