package edu.dartmouth.cs.together.backend.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by TuanMacAir on 2/20/16.
 * Handles datastore operations.
 * keeps track of all registered devices and activities
 *
 */
public class EventJoinerDataSource {
    private static final Logger mLogger = Logger
            .getLogger(EventJoinerDataSource.class.getName());
    private static final DatastoreService mDatastore = DatastoreServiceFactory
            .getDatastoreService();
    private static final String EVENT_JOINER_PARENT_ENTITY_KEY = "EventJoinerParent";
    private static final String EVENT_JOINER_PARENT_ENTITY_NAME = "EventJoinerParent";
    private static final String EVENT_JOINER_ENTITY_NAME = "EventJoiner";

    // generate parent key for activity record
    private static Key getKey() {
        return KeyFactory.createKey(EVENT_JOINER_PARENT_ENTITY_KEY,
                EVENT_JOINER_PARENT_ENTITY_NAME);
    }

    // add activity record to datastore
    public static boolean add(long eventId, long joinerId) {
        // don't add record if it exists already
        if (queryByIdPair(eventId, joinerId) != null) {
            mLogger.log(Level.INFO, "user exists");
            return false;
        }
        Key parentKey = getKey();

        Entity entity = new Entity(EVENT_JOINER_ENTITY_NAME, parentKey);
        entity.setProperty(Event.ID_KEY, eventId);
        entity.setProperty(User.ID_KEY, joinerId);
        mDatastore.put(entity);
        return true;
    }

    // delete record from datastore
    public static boolean delete(long eventId, long joinerId) {
        Entity entity = queryByIdPair(eventId, joinerId);
        boolean ret = false;
        if (entity!=null) {
            // delete
            mDatastore.delete(entity.getKey());
            ret = true;
        }
        return ret;
    }

    // query all activity records from datastore.
    // All records of each device will have a separate list
    public static Entity queryByIdPair(long eventId, long userId) {
        Entity entity = null;
        Query query = new Query(EVENT_JOINER_ENTITY_NAME);
        Query.FilterPredicate filter1 = new Query.FilterPredicate(User.ID_KEY,
                Query.FilterOperator.EQUAL,
                userId);
        Query.FilterPredicate filter2 = new Query.FilterPredicate(Event.ID_KEY,
                Query.FilterOperator.EQUAL,
                eventId);
        // get every record from datastore, no filter
        query.setFilter(new Query.CompositeFilter(Query.CompositeFilterOperator.AND, Arrays.<Query.Filter>asList(
                filter1,filter2)));
        // set query's ancestor to get strong consistency
        query.setAncestor(getKey());

        PreparedQuery pq = mDatastore.prepare(query);
        entity = pq.asSingleEntity();

        return entity;
    }

    public static List<Long> queryByEventId(long eventId) {
        List<Long> result = new ArrayList<>();
        Query query = new Query(EVENT_JOINER_ENTITY_NAME);
        query.setFilter(new Query.FilterPredicate(Event.ID_KEY,
                Query.FilterOperator.EQUAL,
                eventId));
        query.setAncestor(getKey());

        PreparedQuery pq = mDatastore.prepare(query);
        for(Entity entity : pq.asIterable()){
            result.add((long)entity.getProperty(User.ID_KEY));
        }
        return result;
    }

}
