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
        ContentValues values = getMessageContent(message);
        return mDB.insert(MessageTable.TABLE_NAME, null, values);

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

        cursor.moveToFirst();
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
        message.setMsgDescription(cursor.getString(MessageTable.COLUMNS.DESCRIPTION.index()));
        message.setIsRead(cursor.getInt(MessageTable.COLUMNS.IS_READ.index()) > 0);
        message.setQaId(cursor.getInt(MessageTable.COLUMNS.QA_ID.index()));

        return message;
    }

    private ContentValues getMessageContent(Message message){

        ContentValues values = new ContentValues();

        values.put(MessageTable.COLUMNS.MSG_TYPE.colName(), message.getMsgType());
        values.put(MessageTable.COLUMNS.TIME.colName(), message.getMsgTime());
        values.put(MessageTable.COLUMNS.DESCRIPTION.colName(), message.getMsgContent());
        values.put(MessageTable.COLUMNS.IS_READ.colName(), (!message.getIsRead())?0:1);
        values.put(MessageTable.COLUMNS.EVENT_ID.colName(), message.getEventId());
        values.put(MessageTable.COLUMNS.QA_ID.colName(), message.getQaId());
        return values;
    }


}
