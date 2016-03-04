package edu.dartmouth.cs.together.backend;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.dartmouth.cs.together.backend.data.Event;
import edu.dartmouth.cs.together.backend.data.EventDataSource;
import edu.dartmouth.cs.together.backend.data.EventJoinerDataSource;
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
            JSONObject jsonObject = new JSONObject(jsonString);
            if (actionString.equals(Globals.ACTION_JOIN)){
                long eventId = jsonObject.getLong(Event.ID_KEY);
                long userId = jsonObject.getLong(User.ID_KEY);
                boolean ret = EventJoinerDataSource.add(eventId, userId);
                if (ret) {
                    userList.add(userId);
                    userList.add((long)EventDataSource.queryById(eventId)
                            .getProperty(Event.OWNER_KEY));
                    deviceList = UserDataSource.queryDeviceByUserId(userList);
                    msg.sendMessage(deviceList, "Event Joined:" + eventId + ":" + userId);
                }
            } else if(actionString.equals(Globals.ACTION_QUIT)){
                long eventId = jsonObject.getLong(Event.ID_KEY);
                long userId = jsonObject.getLong(User.ID_KEY);
                boolean ret = EventJoinerDataSource.delete(eventId, userId);
                userList.add(userId);
                userList.add((long)EventDataSource.queryById(eventId)
                        .getProperty(Event.OWNER_KEY));
                deviceList = UserDataSource.queryDeviceByUserId(userList);
                msg.sendMessage(deviceList, "Event Quited:" + eventId + ":" + userId);
            } else{
                Event event = new Event(jsonObject);
                userList.add(event.getOwner());
                deviceList = UserDataSource.queryDeviceByUserId(userList);
                if (actionString.equals(Globals.ACTION_ADD)) {
                    boolean ret = EventDataSource.add(event);
                    if(ret) {
                        msg.sendMessage(deviceList, "Event Added:" + event.getEventId());
                    }
                } else if (actionString.equals(Globals.ACTION_UPDATE)) {
                    boolean ret = EventDataSource.update(event);
                    if(ret) {
                        msg.sendMessage(deviceList, "Event Updated:" + event.getEventId());
                    }
                }else if (actionString.equals(Globals.ACTION_DELETE)) {
                    boolean ret = EventDataSource.delete(event.getEventId());
                    msg.sendMessage(deviceList, "Event Deleted:" + event.getEventId());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
