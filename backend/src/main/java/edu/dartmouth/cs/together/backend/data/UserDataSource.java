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
public class UserDataSource  {
    private static final Logger mLogger = Logger
            .getLogger(UserDataSource.class.getName());
    private static final DatastoreService mDatastore = DatastoreServiceFactory
            .getDatastoreService();

    // generate parent key for activity record
    private static Key getKey() {
        return KeyFactory.createKey(User.USER_PARENT_ENTITY_KEY, User.USER_PARENT_ENTITY_NAME);
    }


    // add activity record to datastore
    public static boolean add(User user) {
        // don't add record if it exists already
        if (queryByAccount(user.getAccount(),user.getDevice()).size()>0) {
            mLogger.log(Level.INFO, "user exists");
            return false;
        }
        Key parentKey = getKey();

        Entity entity = new Entity(User.USER_ENTITY_NAME, parentKey);
        entity.setProperty(User.ACCOUNT_KEY, user.getAccount());
        entity.setProperty(User.PASSWORD_KEY, user.getPassword());
        entity.setProperty(User.RATE_KEY, user.getRate());
        entity.setProperty(User.DEVICE_KEY, user.getDevice());
        entity.setProperty(User.ID_KEY, user.getId());
        mDatastore.put(entity);
        // TODO: save photo;
        return true;
    }

    public static boolean update() {
        //Do nothing for now
        return false;
    }

    // delete record from datastore
    public static boolean delete(long id) {
        // you can also use name to get key, then use the key to delete the
        // entity from datastore directly
        // because name is also the entity's key

        // query
        Query.Filter filter = new Query.FilterPredicate(User.ID_KEY,
                Query.FilterOperator.EQUAL, id);

        Query query = new Query(User.USER_ENTITY_NAME);
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

    // query all activity records from datastore.
    // All records of each device will have a separate list
    public static List<Entity> queryByAccount(String account, String deviceId) {
            List<Entity> result = new ArrayList<>();
            Query query = new Query(User.USER_ENTITY_NAME);
            // get every record from datastore, no filter
            query.setFilter(new Query.FilterPredicate(User.ACCOUNT_KEY,
                    Query.FilterOperator.EQUAL,
                    account));
            // set query's ancestor to get strong consistency
            query.setAncestor(getKey());

            PreparedQuery pq = mDatastore.prepare(query);

            for (Entity entity : pq.asIterable()) {

                if (entity!=null){
                    if (!entity.getProperty(User.DEVICE_KEY).equals(deviceId)){
                        entity.setProperty(User.DEVICE_KEY, deviceId);
                        mDatastore.put(entity);
                    }
                    result.add(entity);
                }
            }
        return result;
    }

    //if the record is already in,
    private static User getUserFromEntity(Entity entity) {

        if (entity == null) {
            return null;
        }
        User user = new User();
        user.setId(entity.getKey().getId());
        user.setAccount((String) entity.getProperty(User.ACCOUNT_KEY));
        user.setPassword((String) entity.getProperty(User.PASSWORD_KEY));
        user.setRate((int) entity.getProperty(User.RATE_KEY));
        user.setDevice((String) entity.getProperty(User.DEVICE_KEY));

        //TODO: add photo;
        return user;
    }
}
