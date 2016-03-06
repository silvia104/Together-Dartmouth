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
 * Created by foxmac on 16/3/2.
 */
public class MessageDataSource {
    //only two manipulations: add and delete
    private static SQLiteDatabase mDB = null;
    private DBHelper mDBHelper;

    public MessageDataSource(Context context){
        mDBHelper = DBHelper.getInstance(context);
    }

    public void open(){
        if(mDB == null || !mDB.isOpen()){
            mDB = mDBHelper.getWritableDatabase();
        }
    }

    public void close(){
        mDBHelper.close();
    }

    public long insertMessage(Message message){
        open();
        long id =  -1;
        ContentValues values = getMessageContent(message);
        try {
        id = mDB.insert(MessageTable.TABLE_NAME, null, values);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        finally {
            close();
            return id;
        }


    }

    public void deleteRecord(long id) {
        if (id == -1) return;
        open();
        mDB.delete(MessageTable.TABLE_NAME,
                MessageTable.COLUMNS.ID.colName() + " = " + id, null);
    }


    public List<Message> getAllRecords() {
        open();
        List<Message> records = new ArrayList<>();

        Cursor cursor = mDB.query(MessageTable.TABLE_NAME,
                null, null, null, null, null, null);
        if(cursor.getCount()>0) {
            cursor.moveToFirst();
        }
        while (!cursor.isAfterLast()) {
            Message record = cursorToRecord(cursor);
            Log.d(getClass().getName(), "get comment = " + cursorToRecord(cursor).toString());
            records.add(record);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return records;

    }

    public long updateIsRead(long id, Message message){
        if (id == -1) return id;
        int updated = 0;
        open();
        try{
            ContentValues values = getMessageContent(message);
            updated = mDB.update(MessageTable.TABLE_NAME, values,
                    MessageTable.COLUMNS.ID.colName() + "=" + id, null);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        close();
        if (updated>0) return id;
        else return -1;
    }

    private Message cursorToRecord(Cursor cursor){

        Message message = new Message();
        message.setMsgId(cursor.getLong(MessageTable.COLUMNS.ID.index()));
        message.setMsgType(cursor.getInt(MessageTable.COLUMNS.MSG_TYPE.index()));
        message.setMsgTime(cursor.getLong(MessageTable.COLUMNS.TIME.index()));
        message.setIsRead(cursor.getInt(MessageTable.COLUMNS.IS_READ.index()) > 0);

        message.setEventId(cursor.getInt(MessageTable.COLUMNS.EVENT_ID.index()));
        message.setEventShortDesc(cursor.getString(MessageTable.COLUMNS.EVENT_SHORT_DESC.index()));

        message.setUserId(cursor.getInt(MessageTable.COLUMNS.USER_ID.index()));
        message.setUserName(cursor.getString(MessageTable.COLUMNS.USER_NAME.index()));

        message.setQaId(cursor.getInt(MessageTable.COLUMNS.QA_ID.index()));
        message.setQuestion(cursor.getString(MessageTable.COLUMNS.QUESTION.index()));
        message.setAnswer(cursor.getString(MessageTable.COLUMNS.ANSWER.index()));

        return message;
    }

    private ContentValues getMessageContent(Message message){

        ContentValues values = new ContentValues();

        //These fields must not be null
        values.put(MessageTable.COLUMNS.MSG_TYPE.colName(), message.getMsgType());
        values.put(MessageTable.COLUMNS.TIME.colName(), message.getMsgTime());
        values.put(MessageTable.COLUMNS.IS_READ.colName(), (!message.getIsRead())?0:1);
        values.put(MessageTable.COLUMNS.EVENT_ID.colName(), message.getEventId());
        values.put(MessageTable.COLUMNS.EVENT_SHORT_DESC.colName(), message.getEventShortDesc());

        //these can be null, but here if there's no input for the fields, they'll be initial values
        values.put(MessageTable.COLUMNS.QA_ID.colName(), message.getQaId());
        values.put(MessageTable.COLUMNS.QUESTION.colName(), message.getQuestion());
        values.put(MessageTable.COLUMNS.ANSWER.colName(), message.getAnswer());

        values.put(MessageTable.COLUMNS.USER_ID.colName(), message.getUserId());
        values.put(MessageTable.COLUMNS.USER_NAME.colName(), message.getUserName());

        return values;
    }


}
