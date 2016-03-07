package edu.dartmouth.cs.together.backend;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.dartmouth.cs.together.backend.data.Event;
import edu.dartmouth.cs.together.backend.data.EventDataSource;

/**
 * Created by di on 3/3/2016.
 */
public class UpdateServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String actionString = req.getParameter("action");

        if(actionString.equals(Globals.UPDATE_ALL)) {
            ArrayList<Event> result;
            result = EventDataSource.query();
            JSONObject arrayJSON = new JSONObject();
            JSONArray jArray = new JSONArray();
            try {
                for (Event event : result) {
                    // store each exerciseentry in a jsonobject
                    JSONObject eventJSON = new JSONObject();
                    eventJSON.put("id", event.getEventId());
                    eventJSON.put("cate", event.getCategoryIdx());
                    eventJSON.put("duration", event.getDuration());
                    eventJSON.put("shortdesc", event.getShortdesc());
                    eventJSON.put("time", event.getTimeMillis());
                    eventJSON.put("longdesc", event.getLongDesc());
                    eventJSON.put("owner", event.getOwner());
                    eventJSON.put("joincount", event.getJoinerCount());
                    eventJSON.put("limit", event.getLimit());
                    eventJSON.put("location", event.getLocation());
                    eventJSON.put("lat", event.getLat());
                    eventJSON.put("lng", event.getLng());
                    jArray.put(eventJSON);
                }
            } catch (JSONException jse) {
                jse.printStackTrace();
            }
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(jArray.toString());
        }
        else if(actionString.equals(Globals.UPDATE_USER_EVENTS)){

            JSONObject resultJson = new JSONObject();

            long userId = Long.valueOf(req.getParameter("userId"));

            ArrayList<Event> ownedEvent = EventDataSource.queryEventByOwner(userId);
            JSONArray jArrayOwner = new JSONArray();
            try {
                for (Event event : ownedEvent) {
                    // store each exerciseentry in a jsonobject
                    JSONObject eventJSON = new JSONObject();
                    eventJSON.put("id", event.getEventId());
                    eventJSON.put("cate", event.getCategoryIdx());
                    eventJSON.put("duration", event.getDuration());
                    eventJSON.put("shortdesc", event.getShortdesc());
                    eventJSON.put("time", event.getTimeMillis());
                    eventJSON.put("longdesc", event.getLongDesc());
                    eventJSON.put("owner", event.getOwner());
                    eventJSON.put("joincount", event.getJoinerCount());
                    eventJSON.put("limit", event.getLimit());
                    eventJSON.put("location", event.getLocation());
                    eventJSON.put("lat", event.getLat());
                    eventJSON.put("lng", event.getLng());
                    jArrayOwner.put(eventJSON);
                }
                resultJson.put("ownedEvent", jArrayOwner);
            } catch (JSONException jse) {
                jse.printStackTrace();
            }

            ArrayList<Event> joinedEvent = EventDataSource.queryEventByJoiner(userId);
            JSONArray jArrayJoined = new JSONArray();
            try {
                for (Event event : joinedEvent) {
                    // store each exerciseentry in a jsonobject
                    JSONObject eventJSON = new JSONObject();
                    eventJSON.put("id", event.getEventId());
                    eventJSON.put("cate", event.getCategoryIdx());
                    eventJSON.put("duration", event.getDuration());
                    eventJSON.put("shortdesc", event.getShortdesc());
                    eventJSON.put("time", event.getTimeMillis());
                    eventJSON.put("longdesc", event.getLongDesc());
                    eventJSON.put("owner", event.getOwner());
                    eventJSON.put("joincount", event.getJoinerCount());
                    eventJSON.put("limit", event.getLimit());
                    eventJSON.put("location", event.getLocation());
                    eventJSON.put("lat", event.getLat());
                    eventJSON.put("lng", event.getLng());
                    jArrayJoined.put(eventJSON);
                }
                resultJson.put("joinedEvent", jArrayJoined);
            } catch (JSONException jse) {
                jse.printStackTrace();
            }
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(resultJson.toString());
        }

    }
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doGet(req, resp);
    }

}