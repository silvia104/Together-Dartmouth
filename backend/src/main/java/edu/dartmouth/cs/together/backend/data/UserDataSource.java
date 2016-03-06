package edu.dartmouth.cs.together.backend.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

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
        Entity found = queryByAccount(user.getAccount());
        if (found !=null) {
            String deviceId = (String)found.getProperty(User.DEVICE_KEY);
            if (deviceId.equals(user.getDevice())){
                mLogger.log(Level.INFO, "user exists");
                return false;
            } else {
                update(found, user);
                return true;
            }
        }
        Key parentKey = getKey();

        Entity entity = new Entity(User.USER_ENTITY_NAME, parentKey);
        entity.setProperty(User.ACCOUNT_KEY, user.getAccount());
        entity.setProperty(User.PASSWORD_KEY, user.getPassword());
        entity.setProperty(User.RATE_KEY, user.getRate());
        entity.setProperty(User.DEVICE_KEY, user.getDevice());
        entity.setProperty(User.ID_KEY, user.getId());
        mDatastore.put(entity);
        return true;
    }

    public static boolean update(Entity entity, User user) {
        if (entity==null) return false;
        setEntityFromUser(entity, user);
        mDatastore.put(entity);
        return true;
    }

    private static void setEntityFromUser(Entity entity, User user) {
        entity.setProperty(User.ID_KEY, user.getId());
        entity.setProperty(User.ACCOUNT_KEY, user.getAccount());
        entity.setProperty(User.RATE_KEY, user.getRate());
        entity.setProperty(User.DEVICE_KEY, user.getDevice());
        //TODO: photo?
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

    public static Entity queryByAccount(String account) {
        Query query = new Query(User.USER_ENTITY_NAME);
        Entity entity = null;
        query.setFilter(new Query.FilterPredicate(User.ACCOUNT_KEY,
                Query.FilterOperator.EQUAL,
                account));
        query.setAncestor(getKey());

        PreparedQuery pq = mDatastore.prepare(query);
        entity = pq.asSingleEntity();

        return entity;
    }

    public static Entity queryById(long userId) {
        Query query = new Query(User.USER_ENTITY_NAME);
        Entity entity = null;
        query.setFilter(new Query.FilterPredicate(User.ID_KEY,
                Query.FilterOperator.EQUAL,
                userId));
        query.setAncestor(getKey());

        PreparedQuery pq = mDatastore.prepare(query);
        entity = pq.asSingleEntity();

        return entity;
    }



    public static List<User> queryByIdList(List<Long> userIds) {
        List<User> result = new ArrayList<>();
        Query query = new Query(User.USER_ENTITY_NAME);
        if(userIds.size()>0) {
            query.setFilter(new Query.FilterPredicate(User.ID_KEY,
                    Query.FilterOperator.IN,
                    userIds));
            query.setAncestor(getKey());

            PreparedQuery pq = mDatastore.prepare(query);
            for (Entity entity : pq.asIterable()) {
                result.add(getUserFromEntity(entity));
            }
        }

        return result;
    }

    //if the record is already in,
    public static User getUserFromEntity(Entity entity) {

        if (entity == null) {
            return null;
        }
        User user = new User();
        user.setId((Long) entity.getProperty(User.ID_KEY));
        user.setAccount((String) entity.getProperty(User.ACCOUNT_KEY));
        user.setPassword((String) entity.getProperty(User.PASSWORD_KEY));
        user.setRate((Double) entity.getProperty(User.RATE_KEY));
        user.setDevice((String) entity.getProperty(User.DEVICE_KEY));
        //TODO: add photo;
        return user;
    }

    public static List<String> queryDeviceByUserId(List<Long> userIds){
        List<String> result = new ArrayList<>();
        if (userIds.size()==0){
            return result;
        }
        Query query = new Query(User.USER_ENTITY_NAME);
        // get every record from datastore, no filter
        query.setFilter(new Query.FilterPredicate(User.ID_KEY,
                Query.FilterOperator.IN,
                userIds));
        // set query's ancestor to get strong consistency
        query.setAncestor(getKey());

        PreparedQuery pq = mDatastore.prepare(query);
        for (Entity entity : pq.asIterable()){
            result.add((String) entity.getProperty(User.DEVICE_KEY));
        }
        return result;
    }
}
