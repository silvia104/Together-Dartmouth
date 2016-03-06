package edu.dartmouth.cs.together.backend;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.appengine.repackaged.com.google.api.client.json.Json;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.dartmouth.cs.together.backend.data.Event;
import edu.dartmouth.cs.together.backend.data.EventDataSource;
import edu.dartmouth.cs.together.backend.data.EventJoinerDataSource;
import edu.dartmouth.cs.together.backend.data.QaDataSource;
import edu.dartmouth.cs.together.backend.data.User;
import edu.dartmouth.cs.together.backend.data.UserDataSource;


/**
 * Created by TuanMacAir on 3/2/16.
 */
public class EventServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String jsonString = req.getParameter("json");
        String actionString = req.getParameter("action");
        MessagingEndpoint msg = new MessagingEndpoint();
        List<Long> userList = new ArrayList<>();
        List<String> deviceList;
        // parse the json array from client
        try {
            if (actionString.equals(Globals.ACTION_POLL)){
                long eventId = Long.parseLong(req.getParameter(Event.ID_KEY));
                Entity entity = EventDataSource.queryById(eventId);
                if (noEvent(entity, eventId, resp)){ return;}
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                PrintWriter out = resp.getWriter();
                out.print(eventToJson(EventDataSource.getEventFromEntity(entity)));
                out.flush();
                return;
            }
            JSONObject jsonObject = new JSONObject(jsonString);
            if (actionString.equals(Globals.ACTION_JOIN)){
                long eventId = jsonObject.getLong(Event.ID_KEY);
                long userId = jsonObject.getLong(User.ID_KEY);
                Entity entity = EventDataSource.queryById(eventId);
                if (noEvent(entity,eventId,resp)) return;
                Event newEvent = EventDataSource.getEventFromEntity(entity);
                if (newEvent.getJoinerCount() < newEvent.getLimit()) {
                    boolean ret = EventJoinerDataSource.add(eventId, userId);
                    if(ret) {
                        newEvent.increaseJoiner(1);
                        EventDataSource.update(newEvent);
                        PrintWriter writer = resp.getWriter();
                        writer.print(newEvent.getJoinerCount()+"");
                        writer.flush();
                        userList.add((long) entity.getProperty(Event.OWNER_KEY));
                        deviceList = UserDataSource.queryDeviceByUserId(userList);
                        msg.sendMessage(deviceList, "Event Joined:" + eventId + ":" + userId);
                    }
                }
             } else if(actionString.equals(Globals.ACTION_QUIT)){
                long eventId = jsonObject.getLong(Event.ID_KEY);
                long userId = jsonObject.getLong(User.ID_KEY);
                Entity entity = EventDataSource.queryById(eventId);
                if (noEvent(entity,eventId,resp)) return;

                boolean ret = EventJoinerDataSource.delete(eventId, userId);
                Event newEvent = EventDataSource.getEventFromEntity(entity);
                newEvent.increaseJoiner(-1);
                EventDataSource.update(newEvent);
                userList.add((long)entity.getProperty(Event.OWNER_KEY));
                deviceList = UserDataSource.queryDeviceByUserId(userList);
                msg.sendMessage(deviceList, "Event Quited:" + eventId + ":" + userId);
            } else{
                Event event = new Event(jsonObject);
                if (actionString.equals(Globals.ACTION_ADD)) {
                    boolean ret = EventDataSource.add(event);
                    if(ret) {
                        userList.add(event.getOwner());
                        deviceList = UserDataSource.queryDeviceByUserId(userList);
                        msg.sendMessage(deviceList, "Event Added:" + event.getEventId());
                    }
                } else if (actionString.equals(Globals.ACTION_UPDATE)) {
                    boolean ret = EventDataSource.update(event);
                    if(ret) {
                        userList.add(event.getOwner());
                        userList.addAll(EventJoinerDataSource.entitiesToUserId(
                                EventJoinerDataSource.queryByEventId(event.getEventId())));
                        deviceList = UserDataSource.queryDeviceByUserId(userList);
                        msg.sendMessage(deviceList, "Event Updated:" + event.getEventId());
                    }
                }else if (actionString.equals(Globals.ACTION_DELETE)) {
                    long eventId = event.getEventId();
                    userList.add(event.getOwner());
                    userList.addAll(EventJoinerDataSource.entitiesToUserId(
                            EventJoinerDataSource.queryByEventId(eventId)));
                    deviceList = UserDataSource.queryDeviceByUserId(userList);

                    boolean ret = EventDataSource.delete(eventId);
                    QaDataSource.deleteByEventId(eventId);
                    EventJoinerDataSource.deleteByEventId(eventId);

                    msg.sendMessage(deviceList, "Event Deleted:" + event.getEventId());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean noEvent(Entity entity, long eventId, HttpServletResponse resp){
        if (entity == null){
            PrintWriter writer = null;
            try {
                writer = resp.getWriter();
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer.write("failed");
            writer.flush();
            return true;
        }
        return false;
    }

    private String eventToJson(Event event){
        JSONObject json = new JSONObject();
        try {
            json.put(Event.ID_KEY, event.getEventId());
            json.put(Event.CATEGORY_KEY, event.getCategoryIdx());
            json.put(Event.SHORT_DESC_KEY, event.getShortdesc());
            json.put(Event.LATITUDE_KEY, event.getLat());
            json.put(Event.LONGITUDE_KEY, event.getLng());
            json.put(Event.LOCATION_KEU, event.getLocation());
            json.put(Event.TIME_MILLIS_KEY, event.getTimeMillis());
            json.put(Event.DURATION_KEY, event.getDuration());
            json.put(Event.OWNER_KEY, event.getOwner());
            json.put(Event.LIMIT_KEY, event.getLimit());
            json.put(Event.LONG_DESC_KEY, event.getLongDesc());
            json.put(Event.JOINER_COUNT_KEY, event.getJoinerCount());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
