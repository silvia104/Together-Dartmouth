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
public class EventDataSource  {
    private static SQLiteDatabase mDB = null;
    private DBHelper mDBHelper;
    public EventDataSource(Context context) {
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

    public long insertMyOwnEvent(Event event){
        open();
        // Start the transaction.
        mDB.beginTransaction();
        long i=-1;
        try{

            ContentValues values = getEventDetails(event);
            i = mDB.insert(MyOwnEventTable.TABLE_NAME, null, values);
            // return the row ID of the newly added row.
            mDB.setTransactionSuccessful();

        }catch (SQLiteException e){
            e.printStackTrace();
        }finally
        {
            mDB.endTransaction();
            // End the transaction.
        }
        return i;
    }

    public long deleteMyOwnEvent(long id) {
        if (id == -1) return 0;
        open();
        int count = 0;
        try {
           count = mDB.delete(MyOwnEventTable.TABLE_NAME,
                    MyOwnEventTable.COLUMNS.EVENT_ID.colName() + " = " + id, null);
            //TODO: delete joiners and QAs
        } catch (SQLiteException e){
            e.printStackTrace();
        }
        if (count > 0) return id;
        return -1;
    }

    public long updateMyOwnEvent(long id, Event event){
        if (id == -1) return id;
        int updated = 0;
        open();
        try{
            ContentValues values = getEventDetails(event);
            updated = mDB.update(MyOwnEventTable.TABLE_NAME, values,
                    MyOwnEventTable.COLUMNS.EVENT_ID.colName() + "=" + id, null);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        if (updated>0) return id;
        else return -1;
    }
    public long updateMyOwnEvent(long id, ContentValues values){
        if (id == -1) return id;
        int updated = 0;
        open();
        try{
            updated = mDB.update(MyOwnEventTable.TABLE_NAME, values,
                    MyOwnEventTable.COLUMNS.EVENT_ID.colName() + "=" + id, null);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        if (updated>0) return id;
        else return -1;
    }

    public List<Event> queryMyOwnEvents(){
        open();
        List<Event> events = new ArrayList<>();
        Cursor cursor =null;
        try {
            cursor = mDB.query(MyOwnEventTable.TABLE_NAME,
                    null, null, null, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Event event = cursorToEvent(cursor);
                events.add(event);
                cursor.moveToNext();
            }
        } catch (SQLiteException e){
            e.printStackTrace();
        }finally {
            // Make sure to close the cursor
            if (cursor!=null) cursor.close();
        }

        return events;
    }

    public Event queryMyOwnEventById(long id){
        open();
        Event event = null;
        Cursor cursor =null;
        try {
            cursor = mDB.query(MyOwnEventTable.TABLE_NAME,
                    null,  MyOwnEventTable.COLUMNS.EVENT_ID.colName() + "=?",
                    new String[]{String.valueOf(id)}, null, null, null, null);

            cursor.moveToFirst();
            event = cursorToEvent(cursor);
        } catch (SQLiteException e){
            e.printStackTrace();
        }finally {
            // Make sure to close the cursor
            if (cursor!=null) cursor.close();
        }

        return event;
    }


    private Event cursorToEvent(Cursor cursor) {
        Event event = new Event();
        event.setOwnerId(cursor.getLong(MyOwnEventTable.COLUMNS.OWNER_ID.index()));
        event.setCategory(cursor.getInt(MyOwnEventTable.COLUMNS.CATEGORY.index()));
        event.setShortDesc(cursor.getString(MyOwnEventTable.COLUMNS.SHORT_DESC.index()));
        event.setLongDesc(cursor.getString(MyOwnEventTable.COLUMNS.LONG_DESC.index()));
        event.setLocation(cursor.getString(MyOwnEventTable.COLUMNS.LOCATION_DESC.index()));
        event.setLatLng(new LatLng(cursor.getDouble(MyOwnEventTable.COLUMNS.LATITUDE.index()),
                cursor.getDouble(MyOwnEventTable.COLUMNS.LONGITUDE.index())));
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(cursor.getLong(
                MyOwnEventTable.COLUMNS.TIME_MILLIS.index()));
        event.setDateTime(time);
        event.setDuration(cursor.getInt(MyOwnEventTable.COLUMNS.DURATION.index()));
        event.setStatus(cursor.getInt(MyOwnEventTable.COLUMNS.STATUS.index()));
        event.setLimit(cursor.getInt(MyOwnEventTable.COLUMNS.JOINER_LIMIT.index()));
        event.setJoinerCount(cursor.getInt(MyOwnEventTable.COLUMNS.JOINER_COUNT.index()));
        event.setEventId(cursor.getLong(MyOwnEventTable.COLUMNS.EVENT_ID.index()));

        return event;
    }

    private ContentValues getEventDetails(Event event){
        ContentValues values = new ContentValues();
        values.put(MyOwnEventTable.COLUMNS.CATEGORY.colName(), event.getCategoryIdx());
        values.put(MyOwnEventTable.COLUMNS.SHORT_DESC.colName(), event.getShortdesc());
        values.put(MyOwnEventTable.COLUMNS.LATITUDE.colName(), event.getLatLng().latitude);
        values.put(MyOwnEventTable.COLUMNS.LONGITUDE.colName(), event.getLatLng().longitude);
        values.put(MyOwnEventTable.COLUMNS.LOCATION_DESC.colName(), event.getLocation());
        values.put(MyOwnEventTable.COLUMNS.TIME_MILLIS.colName(), event.getTimeMillis());
        values.put(MyOwnEventTable.COLUMNS.DURATION.colName(), event.getDuration());
        values.put(MyOwnEventTable.COLUMNS.JOINER_LIMIT.colName(), event.getLimit());
        values.put(MyOwnEventTable.COLUMNS.OWNER_ID.colName(), event.getOwner());
        values.put(MyOwnEventTable.COLUMNS.LONG_DESC.colName(), event.getLongDesc());
        values.put(MyOwnEventTable.COLUMNS.JOINER_COUNT.colName(), event.getmJoinerCount());
        values.put(MyOwnEventTable.COLUMNS.STATUS.colName(), event.getStatus());
        values.put(MyOwnEventTable.COLUMNS.EVENT_ID.colName(), event.getEventId());
        return values;
    }
}
