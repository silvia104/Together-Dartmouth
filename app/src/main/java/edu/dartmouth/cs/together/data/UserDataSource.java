package edu.dartmouth.cs.together.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by TuanMacAir on 3/1/16.
 */
public class UserDataSource {
    private static SQLiteDatabase mDB = null;
    private DBHelper mDBHelper;
    public UserDataSource(Context context) {
        // the DBHelper is a singleton, and mDB is a static member variable
        // so it doesn't matter if user has more than one ActivityDataSource instance.
        mDBHelper = DBHelper.getInstance(context);
    }
    public void open() {
        // don't create a new connection if the current one is open.
        if (mDB == null || !mDB.isOpen()) {
            Log.d(this.getClass().getName(), "open new DB connection");
            mDB = mDBHelper.getWritableDatabase();
        }
    }

    // DBHelper will handle unclosed DB connections.
    public void close() {
        mDBHelper.close();
    }

    public void insertUsers(List<User> users){
        open();
        // Start the transaction.
        mDB.beginTransaction();
        try{
            for (User user : users) {
                ContentValues values = getUserDetails(user);
                mDB.insertWithOnConflict(UserTable.TABLE_NAME, null, values,
                        SQLiteDatabase.CONFLICT_REPLACE);
            }
            mDB.setTransactionSuccessful();

        }catch (SQLiteException e){
            e.printStackTrace();
        }finally
        {
            mDB.endTransaction();
            // End the transaction.
        }
        return ;
    }

    public long deletUser(long id) {
        if (id == -1) return 0;
        open();
        int count = 0;
        try {
           count = mDB.delete(UserTable.TABLE_NAME,
                   UserTable.COLUMNS.USER_ID.colName() + " = " + id, null);
            //TODO: delete joiners and QAs
        } catch (SQLiteException e){
            e.printStackTrace();
        }
        if (count > 0) return id;
        return -1;
    }


    public long updateUser(long id, ContentValues values){
        if (id == -1) return id;
        int updated = 0;
        open();
        try{
            updated = mDB.update(UserTable.TABLE_NAME, values,
                    UserTable.COLUMNS.USER_ID.colName() + "=" + id, null);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        if (updated>0) return id;
        else return -1;
    }

    public User queryUserById(long id){
        open();
        User user = null;
        Cursor cursor =null;
        try {
            cursor = mDB.query(UserTable.TABLE_NAME,
                    null, UserTable.COLUMNS.USER_ID.colName() + "=?",
                    new String[]{String.valueOf(id)}, null, null, null, null);
            if (cursor.getCount()>0) {
                cursor.moveToFirst();
                user = cursorToUser(cursor);
            }
        } catch (SQLiteException e){
            e.printStackTrace();
        }finally {
            // Make sure to close the cursor
            if (cursor!=null) cursor.close();
        }

        return user;
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getLong(UserTable.COLUMNS.USER_ID.index()));
        user.setAccount(cursor.getString(UserTable.COLUMNS.EMAIL.index()));
        user.setRate(cursor.getDouble(UserTable.COLUMNS.RATE.index()));

        //TODO: user photo

        return user;
    }

    private ContentValues getUserDetails(User user){
        ContentValues values = new ContentValues();
        values.put(UserTable.COLUMNS.USER_ID.colName(), user.getId());
        values.put(UserTable.COLUMNS.EMAIL.colName(), user.getAccount());
        //TODO: user photo
        return values;
    }


    public List<User> queryJoiners(long eventId) {
        open();
        List<User> result  = new ArrayList<>();
        Cursor cursor =null;
        try {
            cursor = mDB.rawQuery("SELECT * FROM " + UserTable.TABLE_NAME + " JOIN "
                    + EventJoinerTable.TABLE_NAME + " ON "
                    + UserTable.TABLE_NAME+"."+UserTable.COLUMNS.USER_ID.colName() + "="
                    + EventJoinerTable.TABLE_NAME+"."+EventJoinerTable.COLUMNS.JOINER_ID.colName() + " AND "
                    + EventJoinerTable.TABLE_NAME+"."+EventJoinerTable.COLUMNS.EVENT_ID.colName() + "=" + eventId, null);
            if (cursor.getCount()>0) {
                cursor.moveToFirst();
                while(!cursor.isAfterLast()){
                    result.add(cursorToUser(cursor));
                    cursor.moveToNext();
                }
            }
        }catch (SQLiteException e){
            e.printStackTrace();
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }

        return result;
    }

}
