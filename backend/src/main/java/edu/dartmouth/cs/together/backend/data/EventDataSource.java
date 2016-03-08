package edu.dartmouth.cs.together.backend.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by TuanMacAir on 2/20/16.
 * Handles datastore operations of events*
 */
public class EventDataSource {
    private static final Logger mLogger = Logger
            .getLogger(EventDataSource.class.getName());
    private static final DatastoreService mDatastore = DatastoreServiceFactory
            .getDatastoreService();

    private static Key getKey() {
        return KeyFactory.createKey(Event.Event_PARENT_ENTITY_KEY, Event.Event_PARENT_ENTITY_NAME);
    }


    // add event record to datastore
    public static boolean add(Event event) {
        // don't add event if it exists already
        if (queryById(event.getEventId())!=null) {
            mLogger.log(Level.INFO, "Event exists");
            return false;
        }
        Entity entity = new Entity(Event.Event_ENTITY_NAME, getKey());
        setEntityFromEvent(entity, event);
        mDatastore.put(entity);
        return true;
    }

    public static boolean update(Event event, boolean isFromUser) {
        Entity entity = queryById(event.getEventId());
        if (entity == null){
            return false;
        }
        setEntityFromEvent(entity,event);

        if (isFromUser) {
            Entity ori = queryById(event.getEventId());
            if (ori != null) {
                entity.setProperty(Event.JOINER_COUNT_KEY,
                        ori.getProperty(Event.JOINER_COUNT_KEY));
            }
        }
        mDatastore.put(entity);
        return true;
    }

    // delete event from datastore
    public static boolean delete(long id) {

        Query.Filter filter = new Query.FilterPredicate(Event.ID_KEY,
                Query.FilterOperator.EQUAL, id);

        Query query = new Query(Event.Event_ENTITY_NAME);
        query.setFilter(filter);
        query.setAncestor(getKey());
        // Use PreparedQuery interface to retrieve results
        PreparedQuery pq = mDatastore.prepare(query);

        Entity result = pq.asSingleEntity();
        boolean ret = false;
        if (result != null) {
            // delete
            mDatastore.delete(result.getKey());
            ret = true;
        }

        return ret;
    }

    public static Entity queryById(long id) {
        Query query = new Query(Event.Event_ENTITY_NAME);
        // get specific event from datastore, no filter
        query.setFilter(new Query.FilterPredicate(Event.ID_KEY,
                Query.FilterOperator.EQUAL,
                id));
        // set query's ancestor to get strong consistency
        query.setAncestor(getKey());

        PreparedQuery pq = mDatastore.prepare(query);
        Entity entity = pq.asSingleEntity();
        return entity;
    }

    public static Event getEventFromEntity(Entity entity) {

        if (entity == null) {
            return null;
        }
        Event event = new Event();
        event.setId((Long) entity.getProperty(Event.ID_KEY));
        event.setCategory(((Long) entity.getProperty(Event.CATEGORY_KEY)).intValue());
        event.setShortDesc((String) entity.getProperty(Event.SHORT_DESC_KEY));
        event.setLat((Double) entity.getProperty(Event.LATITUDE_KEY));
        event.setLng((Double) entity.getProperty(Event.LONGITUDE_KEY));
        event.setLocation((String) entity.getProperty(Event.LOCATION_KEU));
        event.setTimeMillis((Long) entity.getProperty(Event.TIME_MILLIS_KEY));
        event.setDuration(((Long) entity.getProperty(Event.DURATION_KEY)).intValue());
        event.setLimit(((Long) entity.getProperty(Event.LIMIT_KEY)).intValue());
        event.setOwnerId((Long) entity.getProperty(Event.OWNER_KEY));
        event.setJoinerCount(((Long)entity.getProperty(Event.JOINER_COUNT_KEY)).intValue());
        return event;
    }

    private static void setEntityFromEvent(Entity entity, Event event){
        entity.setProperty(Event.ID_KEY, event.getEventId());
        entity.setProperty(Event.CATEGORY_KEY, event.getCategoryIdx());
        entity.setProperty(Event.SHORT_DESC_KEY, event.getShortdesc());
        entity.setProperty(Event.LOCATION_KEU, event.getLocation());
        entity.setProperty(Event.LATITUDE_KEY, event.getLat());
        entity.setProperty(Event.LONGITUDE_KEY, event.getLng());
        entity.setProperty(Event.LONG_DESC_KEY, event.getLongDesc());
        entity.setProperty(Event.DURATION_KEY, event.getDuration());
        entity.setProperty(Event.LIMIT_KEY, event.getLimit());
        entity.setProperty(Event.TIME_MILLIS_KEY, event.getTimeMillis());
        entity.setProperty(Event.OWNER_KEY, event.getOwner());
        entity.setProperty(Event.JOINER_COUNT_KEY, event.getJoinerCount());
    }

    public static ArrayList<Event> query() {
        ArrayList<Event> resultList = new ArrayList<Event>();
        Query query = new Query(Event.Event_ENTITY_NAME);
        // get every event from datastore, no filter
        query.setFilter(null);
        // set query's ancestor to get strong consistency
        query.setAncestor(getKey());

        PreparedQuery pq = mDatastore.prepare(query);

        for (Entity entity : pq.asIterable()) {
            Event entry = getEventFromEntity(entity);
            if (entry != null) {
                resultList.add(entry);
            }
        }
        return resultList;
    }

    public static ArrayList<Event> queryEventByOwner(long ownerId){
        ArrayList<Event> result = new ArrayList<>();
        Query query = new Query(Event.Event_ENTITY_NAME);
        query.setFilter(new Query.FilterPredicate(Event.OWNER_KEY,
                Query.FilterOperator.EQUAL,
                ownerId));
        query.setAncestor(getKey());

        PreparedQuery pq = mDatastore.prepare(query);
        //get query results as a list of entity
        for (Entity entity : pq.asIterable()) {
            Event entry = getEventFromEntity(entity);
            if (entry != null) {
                result.add(entry);
            }
        }
        return result;


    }

    public static ArrayList<Event> queryEventByJoiner(long joinerId){
        ArrayList<Event> result = new ArrayList<>();
        List<Entity> eventEntities = EventJoinerDataSource.queryByUserId(joinerId);
        List<Long> eventID = EventJoinerDataSource.entitiesToEventId(eventEntities);
        for(long id:eventID){
            Entity temp = queryById(id);
            Event event = getEventFromEntity(temp);
            result.add(event);
        }
        return result;


    }



}
