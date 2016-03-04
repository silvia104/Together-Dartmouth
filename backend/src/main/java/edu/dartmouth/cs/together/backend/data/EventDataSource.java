package edu.dartmouth.cs.together.backend.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by TuanMacAir on 2/20/16.
 * Handles datastore operations.
 * keeps track of all registered devices and activities
 *
 */
public class EventDataSource {
    private static final Logger mLogger = Logger
            .getLogger(EventDataSource.class.getName());
    private static final DatastoreService mDatastore = DatastoreServiceFactory
            .getDatastoreService();

    // generate parent key for activity record
    private static Key getKey() {
        return KeyFactory.createKey(Event.Event_PARENT_ENTITY_KEY, Event.Event_PARENT_ENTITY_NAME);
    }


    // add activity record to datastore
    public static boolean add(Event event) {
        // don't add record if it exists already
        if (queryById(event.getEventId())!=null) {
            mLogger.log(Level.INFO, "Event exists");
            return false;
        }
        Entity entity = new Entity(Event.Event_ENTITY_NAME, getKey());
        setEntityFromEvent(entity,event);
        mDatastore.put(entity);
        return true;
    }

    public static boolean update(Event event) {
        Entity entity = queryById(event.getEventId());
        if (entity == null){
            return false;
        }
        setEntityFromEvent(entity,event);
        mDatastore.put(entity);
        return true;
    }

    // delete record from datastore
    public static boolean delete(long id) {
        // you can also use name to get key, then use the key to delete the
        // entity from datastore directly
        // because name is also the entity's key

        // query
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
            //TODO: delete joiners and QA
            ret = true;
        }

        return ret;
    }

    public static Entity queryById(long id) {
        Query query = new Query(Event.Event_ENTITY_NAME);
        // get every record from datastore, no filter
        query.setFilter(new Query.FilterPredicate(Event.ID_KEY,
                Query.FilterOperator.EQUAL,
                id));
        // set query's ancestor to get strong consistency
        query.setAncestor(getKey());

        PreparedQuery pq = mDatastore.prepare(query);
        Entity entity = pq.asSingleEntity();
        return entity;
    }

    //if the record is already in,
    private static Event getEventFromEntity(Entity entity) {

        if (entity == null) {
            return null;
        }
        Event Event = new Event();
        Event.setId(entity.getKey().getId());
        Event.setCategory((int) entity.getProperty(Event.CATEGORY_KEY));
        Event.setShortDesc((String) entity.getProperty(Event.SHORT_DESC_KEY));
        Event.setLat((double) entity.getProperty(Event.LATITUDE_KEY));
        Event.setLng((double) entity.getProperty(Event.LONGITUDE_KEY));
        Event.setLocation((String) entity.getProperty(Event.LOCATION_KEU));
        Event.setTimeMillis((long) entity.getProperty(Event.TIME_MILLIS_KEY));
        Event.setDuration((int) entity.getProperty(Event.DURATION_KEY));
        Event.setLimit((int) entity.getProperty(Event.LIMIT_KEY));
        Event.setOwnerId((long) entity.getProperty(Event.OWNER_KEY));
        Event.setJoinerCount((int) entity.getProperty(Event.JOINER_COUNT_KEY));
        return Event;
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
        entity.setProperty(Event.JOINER_COUNT_KEY, 0);
    }

}