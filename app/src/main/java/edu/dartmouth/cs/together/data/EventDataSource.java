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
    public static final int MY_OWN_EVENT = 0;
    public static final int JOINED_EVENT =1;
    public static final int ALL_EVENT = 2;
    private static SQLiteDatabase mDB = null;
    private DBHelper mDBHelper;
    private Context mContext;
    public EventDataSource(Context context) {
        // the DBHelper is a singleton, and mDB is a static member variable
        // so it doesn't matter if user has more than one ActivityDataSource instance.
        mDBHelper = DBHelper.getInstance(context);
        mContext = context;
    }
    public void clearAllEvent(){
        mDB.execSQL("DROP TABLE IF EXISTS " + AllEventTable.TABLE_NAME);
        mDB.execSQL(AllEventTable.TABLE_CREATE_COMMAND);
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

    public long insertEvent(int eventType, Event event){
        open();
        // Start the transaction.

        mDB.beginTransaction();
        long i=-1;
        try{

            ContentValues values = getEventDetails(event);
            i = mDB.insertWithOnConflict(getTableName(eventType), null, values,
                    SQLiteDatabase.CONFLICT_REPLACE);
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


    public void insertEvents(int eventType, List<Event> events){
        open();
        // Start the transaction.

        mDB.beginTransaction();
        long i=-1;
        try{
            for (Event event:events) {
                ContentValues values = getEventDetails(event);
                mDB.insertWithOnConflict(getTableName(eventType), null, values,
                        SQLiteDatabase.CONFLICT_REPLACE);
                // return the row ID of the newly added row.
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
    public long deleteEvent(int eventType, long id) {
        if (id == -1) return 0;
        open();
        int count = 0;
        try {
           count = mDB.delete(getTableName(eventType),
                   BaseEventTable.COLUMNS.EVENT_ID.colName() + " = " + id, null);
            deleteEventJoinerRelationByEventId(id);
            new QaDataSource(mContext).deletQaByEventId(id);
        } catch (SQLiteException e){
            e.printStackTrace();
        }
        if (count > 0) return id;
        return -1;
    }

    public long updateEvent(int eventType, long id, Event event){
        open();
        if (id == -1) return id;
        int updated = 1;
        try{
            ContentValues values = getEventDetails(event);
            updated =mDB.update(getTableName(eventType), values,
                    BaseEventTable.COLUMNS.EVENT_ID.colName() + "=" + id, null);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        if (updated == 1) {
            return id;
        } else {
            return -1;
        }
    }
    public long updateEvent(int eventType, long id, ContentValues values){
        if (id == -1) return id;
        int updated = 0;
        open();
        try{
            updated = mDB.update(getTableName(eventType), values,
                    BaseEventTable.COLUMNS.EVENT_ID.colName() + "=" + id, null);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        if (updated>0) return id;
        else return -1;
    }

    public List<Event> queryEvents(int eventType){
        open();
        List<Event> events = new ArrayList<>();
        Cursor cursor =null;
        try {
            cursor = mDB.query(getTableName(eventType),
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

    public Event queryEventById(int eventType, long id){
        open();
        Event event = null;
        Cursor cursor =null;
        try {
            cursor = mDB.query(getTableName(eventType),
                    null,  BaseEventTable.COLUMNS.EVENT_ID.colName() + "=?",
                    new String[]{String.valueOf(id)}, null, null, null, null);
            if (cursor.getCount()>0) {
                cursor.moveToFirst();
                event = cursorToEvent(cursor);
            }
        } catch (SQLiteException e){
            e.printStackTrace();
        }finally {
            // Make sure to close the cursor
            if (cursor!=null) cursor.close();
        }
        return event;
    }

    public List<Event> queryEventByOwnerId(long id){
        open();
        List<Event> events = new ArrayList<>();
        Cursor cursor =null;
        try {
            cursor = mDB.query(getTableName(ALL_EVENT),
                    null,  BaseEventTable.COLUMNS.OWNER_ID.colName() + "=?",
                    new String[]{String.valueOf(id)}, null, null, null, null);
            if (cursor.getCount()>0) {
                cursor.moveToFirst();
            }
            while(!cursor.isAfterLast()) {
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

    public List<Event> queryEventByJoinerId(long joinerId){
        open();
        List<Event> events = new ArrayList<>();
        Cursor joinerCursor =null;
        try {
            joinerCursor = mDB.query(EventJoinerTable.TABLE_NAME,
                    null,  EventJoinerTable.COLUMNS.JOINER_ID.colName() + "=?",
                    new String[]{String.valueOf(joinerId)}, null, null, null, null);
            joinerCursor.moveToFirst();
            while(!joinerCursor.isAfterLast()) {
                long eventId = joinerCursor.getLong(EventJoinerTable.COLUMNS.EVENT_ID.index());
                Cursor eventCursor = mDB.query(getTableName(this.ALL_EVENT), null,
                        BaseEventTable.COLUMNS.EVENT_ID.colName() + "=?",
                        new String[]{String.valueOf(eventId)}, null, null, null, null);
                eventCursor.moveToFirst();
                while (!eventCursor.isAfterLast()) {
                    Event event = cursorToEvent(eventCursor);
                    events.add(event);
                    eventCursor.moveToNext();
                }
                if(eventCursor!=null) eventCursor.close();
                joinerCursor.moveToNext();
            }
//                event = cursorToEvent(joinerCursor);
        } catch (SQLiteException e){
            e.printStackTrace();
        }finally {
            // Make sure to close the cursor
            if (joinerCursor!=null) joinerCursor.close();
        }
        return events;

    }

    public long insertEventJoinerRelation(long eventId, long joinerId){
        open();
        long id = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(EventJoinerTable.COLUMNS.EVENT_ID.colName(),eventId);
            values.put(EventJoinerTable.COLUMNS.JOINER_ID.colName(),joinerId);
            id = mDB.insert(EventJoinerTable.TABLE_NAME, null, values);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        finally {
            return id;
        }
    }

    public int deleteEventJoinerRelationByEventId(long eventId){
        open();
        int count = 0;
        try {
            count = mDB.delete(EventJoinerTable.TABLE_NAME,
                    EventJoinerTable.COLUMNS.EVENT_ID.colName() + "=" + eventId, null);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        finally {
            return count;
        }
    }

    public int deleteEventJoinerRelation(long evnetId, long userId) {
        open();
        int count = 0;
        try {
            count = mDB.delete(EventJoinerTable.TABLE_NAME,
                    EventJoinerTable.COLUMNS.EVENT_ID.colName() + "=" + evnetId + " AND "
                            + EventJoinerTable.COLUMNS.JOINER_ID.colName() + "=" + userId, null);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        finally {
            return count;
        }

    }
    private Event cursorToEvent(Cursor cursor) {
        Event event = new Event();
        event.setOwnerId(cursor.getLong(BaseEventTable.COLUMNS.OWNER_ID.index()));
        event.setCategory(cursor.getInt(BaseEventTable.COLUMNS.CATEGORY.index()));
        event.setShortDesc(cursor.getString(BaseEventTable.COLUMNS.SHORT_DESC.index()));
        event.setLongDesc(cursor.getString(BaseEventTable.COLUMNS.LONG_DESC.index()));
        event.setLocation(cursor.getString(BaseEventTable.COLUMNS.LOCATION_DESC.index()));
        event.setLatLng(new LatLng(cursor.getDouble(BaseEventTable.COLUMNS.LATITUDE.index()),
                cursor.getDouble(BaseEventTable.COLUMNS.LONGITUDE.index())));
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(cursor.getLong(
                BaseEventTable.COLUMNS.TIME_MILLIS.index()));
        event.setDateTime(time);
        event.setDuration(cursor.getInt(BaseEventTable.COLUMNS.DURATION.index()));
        event.setStatus(cursor.getInt(BaseEventTable.COLUMNS.STATUS.index()));
        event.setLimit(cursor.getInt(BaseEventTable.COLUMNS.JOINER_LIMIT.index()));
        event.setJoinerCount(cursor.getInt(BaseEventTable.COLUMNS.JOINER_COUNT.index()));
        event.setEventId(cursor.getLong(BaseEventTable.COLUMNS.EVENT_ID.index()));

        return event;
    }

    private ContentValues getEventDetails(Event event){
        ContentValues values = new ContentValues();
        values.put(BaseEventTable.COLUMNS.CATEGORY.colName(), event.getCategoryIdx());
        values.put(BaseEventTable.COLUMNS.SHORT_DESC.colName(), event.getShortdesc());
        values.put(BaseEventTable.COLUMNS.LATITUDE.colName(), event.getLatLng().latitude);
        values.put(BaseEventTable.COLUMNS.LONGITUDE.colName(), event.getLatLng().longitude);
        values.put(BaseEventTable.COLUMNS.LOCATION_DESC.colName(), event.getLocation());
        values.put(BaseEventTable.COLUMNS.TIME_MILLIS.colName(), event.getTimeMillis());
        values.put(BaseEventTable.COLUMNS.DURATION.colName(), event.getDuration());
        values.put(BaseEventTable.COLUMNS.JOINER_LIMIT.colName(), event.getLimit());
        values.put(BaseEventTable.COLUMNS.OWNER_ID.colName(), event.getOwner());
        values.put(BaseEventTable.COLUMNS.LONG_DESC.colName(), event.getLongDesc());
        values.put(BaseEventTable.COLUMNS.JOINER_COUNT.colName(), event.getmJoinerCount());
        values.put(BaseEventTable.COLUMNS.STATUS.colName(), event.getStatus());
        values.put(BaseEventTable.COLUMNS.EVENT_ID.colName(), event.getEventId());
        return values;
    }
    private String getTableName(int eventType){
        String s = "";
        switch (eventType){
            case MY_OWN_EVENT:
                s = MyOwnEventTable.TABLE_NAME;
                break;
            case JOINED_EVENT:
                s = JoinedEventTable.TABLE_NAME;
                break;
            case ALL_EVENT:
                s = AllEventTable.TABLE_NAME;
                break;
        }
        return s;
    }
}
