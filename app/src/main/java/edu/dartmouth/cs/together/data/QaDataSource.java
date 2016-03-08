package edu.dartmouth.cs.together.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TuanMacAir on 3/1/16.
 */
public class QaDataSource {
    private static SQLiteDatabase mDB = null;
    private DBHelper mDBHelper;
    public QaDataSource(Context context) {
        // the DBHelper is a singleton, and mDB is a static member variable
        // so it doesn't matter if Qa has more than one ActivityDataSource instance.
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

    public void insertQas(List<Qa> qas){
        open();
        // Start the transaction.
        mDB.beginTransaction();
        try{
            for (Qa qa : qas) {
                ContentValues values = getQaDetails(qa);
                long id = mDB.insertWithOnConflict(QaTable.TABLE_NAME, null, values,
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

    public void insertQa(Qa qa){
        open();
        // Start the transaction.
        try{
            ContentValues values = getQaDetails(qa);
            mDB.insertWithOnConflict(QaTable.TABLE_NAME, null, values,
                    SQLiteDatabase.CONFLICT_REPLACE);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return ;
    }


    public long deletQa(long id) {
        if (id == -1) return 0;
        open();
        int count = 0;
        try {
           count = mDB.delete(QaTable.TABLE_NAME,
                   QaTable.COLUMNS.QA_ID.colName() + " = " + id, null);
        } catch (SQLiteException e){
            e.printStackTrace();
        }
        if (count > 0) return id;
        return -1;
    }

    public long deletQaByEventId(long id) {
        if (id == -1) return 0;
        open();
        int count = 0;
        try {
            count = mDB.delete(QaTable.TABLE_NAME,
                    QaTable.COLUMNS.EVENT_ID.colName() + " = " + id, null);
        } catch (SQLiteException e){
            e.printStackTrace();
        }
        if (count > 0) return id;
        return -1;
    }

    public long updateQa(long id, ContentValues values){
        if (id == -1) return id;
        int updated = 0;
        open();
        try{
            updated = mDB.update(QaTable.TABLE_NAME, values,
                    QaTable.COLUMNS.QA_ID.colName() + "=" + id, null);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        if (updated>0) return id;
        else return -1;
    }

    public Qa queryQaById(long id){
        open();
        Qa Qa = null;
        Cursor cursor =null;
        try {
            cursor = mDB.query(QaTable.TABLE_NAME,
                    null, QaTable.COLUMNS.QA_ID.colName() + "=?",
                    new String[]{String.valueOf(id)}, null, null, null, null);
            if (cursor.getCount()>0) {
                cursor.moveToFirst();
                Qa = cursorToQa(cursor);
            }
        } catch (SQLiteException e){
            e.printStackTrace();
        }finally {
            // Make sure to close the cursor
            if (cursor!=null) cursor.close();
        }

        return Qa;
    }
    public List<Qa> queryQaByEventId(long id){
        open();
        List<Qa> result = new ArrayList<>();
        Cursor cursor =null;
        try {
            cursor = mDB.query(QaTable.TABLE_NAME,
                    null, QaTable.COLUMNS.EVENT_ID.colName() + "=?",
                    new String[]{String.valueOf(id)}, null, null, null, null);
            if (cursor.getCount()>0) {
                cursor.moveToFirst();
                while(!cursor.isAfterLast()) {
                    result.add(cursorToQa(cursor));
                    cursor.moveToNext();
                }
            }
        } catch (SQLiteException e){
            e.printStackTrace();
        }finally {
            // Make sure to close the cursor
            if (cursor!=null) cursor.close();
        }

        return result;
    }

    private Qa cursorToQa(Cursor cursor) {
        Qa qa = new Qa();
        qa.setAnswer(cursor.getString(QaTable.COLUMNS.ANSWER.index()));
        qa.setQuestion(cursor.getString(QaTable.COLUMNS.QUESTION.index()));
        qa.setId(cursor.getLong(QaTable.COLUMNS.QA_ID.index()));
        qa.setEventId(cursor.getLong(QaTable.COLUMNS.EVENT_ID.index()));

        return qa;
    }

    private ContentValues getQaDetails(Qa qa){
        ContentValues values = new ContentValues();
        values.put(QaTable.COLUMNS.QA_ID.colName(), qa.getId());
        values.put(QaTable.COLUMNS.QUESTION.colName(), qa.getQuestion());
        values.put(QaTable.COLUMNS.ANSWER.colName(), qa.getAnswer());
        values.put(QaTable.COLUMNS.EVENT_ID.colName(), qa.getEventId());

        return values;
    }

}
