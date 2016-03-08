package edu.dartmouth.cs.together.backend.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
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
 * Handles datastore operations.
 * keeps track of all registered devices and activities
 *
 */
public class QaDataSource {
    private static final Logger mLogger = Logger
            .getLogger(QaDataSource.class.getName());
    private static final DatastoreService mDatastore = DatastoreServiceFactory
            .getDatastoreService();

    // generate parent key for activity record
    private static Key getKey() {
        return KeyFactory.createKey(Qa.Qa_PARENT_ENTITY_KEY, Qa.Qa_PARENT_ENTITY_NAME);
    }


    // add activity record to datastore
    public static boolean add(Qa Qa) {
        // don't add record if it exists already
        Entity found = queryById(Qa.getId());
        if (found !=null) {
            update(found, Qa);
            return true;
        }
        Key parentKey = getKey();

        Entity entity = new Entity(Qa.Qa_ENTITY_NAME, parentKey);
        entity.setProperty(Qa.ID_KEY, Qa.getId());
        entity.setProperty(Qa.Q_KEY, Qa.getQuestion());
        entity.setProperty(Qa.A_KEY, Qa.getAnswer());
        entity.setProperty(Event.ID_KEY, Qa.getEventId());
        mDatastore.put(entity);
        return true;
    }

    public static boolean update(Entity entity, Qa Qa) {
        if (entity==null) return false;
        entity.setProperty(Qa.A_KEY, Qa.getAnswer());
        mDatastore.put(entity);
        return true;
    }

    // delete record from datastore
    public static boolean delete(long id) {
        // you can also use name to get key, then use the key to delete the
        // entity from datastore directly
        // because name is also the entity's key

        // query
        Query.Filter filter = new Query.FilterPredicate(Qa.ID_KEY,
                Query.FilterOperator.EQUAL, id);

        Query query = new Query(Qa.Qa_ENTITY_NAME);
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

    public static boolean deleteByEventId(long id) {
        List<Entity> result = queryByEventId(id);
        boolean ret = false;

        if (result != null) {
            // delete
            List<Key> keys = new ArrayList<>();
            for (Entity entity : result) {
                keys.add(entity.getKey());
            }
            mDatastore.delete(keys);
            ret = true;
        }

        return ret;
    }

    public static Entity queryById(long id) {
        Query query = new Query(Qa.Qa_ENTITY_NAME);
        Entity entity = null;
        query.setFilter(new Query.FilterPredicate(Qa.ID_KEY,
                Query.FilterOperator.EQUAL,
                id));
        query.setAncestor(getKey());

        PreparedQuery pq = mDatastore.prepare(query);
        entity = pq.asSingleEntity();

        return entity;
    }
    public static List<Entity> queryByEventId(long id) {
        List<Entity> result = new ArrayList<>();
        Query query = new Query(Qa.Qa_ENTITY_NAME);
        query.setFilter(new Query.FilterPredicate(Event.ID_KEY,
                Query.FilterOperator.EQUAL,
                id));
        query.setAncestor(getKey());
        PreparedQuery pq = mDatastore.prepare(query);
        for(Entity entity : pq.asIterable()){
            result.add(entity);
        }
        return result;
    }

    public static List<Qa> entityListToQaList(List<Entity> entities){
        List<Qa> qas = new ArrayList<>();
        if (entities == null){return qas;}

        for (Entity e : entities){
            qas.add(getQaFromEntity(e));
        }
        return qas;
    }

    //if the record is already in,
    private static Qa getQaFromEntity(Entity entity) {

        if (entity == null) {
            return null;
        }
        Qa Qa = new Qa();
        Qa.setId((Long) entity.getProperty(Qa.ID_KEY));
        Qa.setQuestion((String) entity.getProperty(Qa.Q_KEY));
        Qa.setEventId((long) entity.getProperty(Event.ID_KEY));
        Qa.setAnswer((String) entity.getProperty(Qa.A_KEY));
        return Qa;
    }

}
