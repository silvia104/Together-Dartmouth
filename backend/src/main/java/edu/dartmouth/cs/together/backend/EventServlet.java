package edu.dartmouth.cs.together.backend;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.dartmouth.cs.together.backend.data.Event;
import edu.dartmouth.cs.together.backend.data.EventDataSource;


/**
 * Created by TuanMacAir on 3/2/16.
 */
public class EventServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String jsonString = req.getParameter("json");
        String actionString = req.getParameter("action");
        // parse the json array from client
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            Event event = new Event(jsonObject);
            if (actionString.equals(Globals.ACTION_ADD)) {
                boolean ret = EventDataSource.add(event);
                // tell client how many records were actually added.
                MessagingEndpoint msg = new MessagingEndpoint();
                msg.sendMessage("New Event Added:" + ret);
            }
            if (actionString.equals(Globals.ACTION_UPDATE)){
                //TODO:
                boolean ret = EventDataSource.update();
                // tell client how many records were actually added.
                MessagingEndpoint msg = new MessagingEndpoint();
                msg.sendMessage("Event "+ event.getEventId() + " Updated:" + ret);
            }
            if (actionString.equals(Globals.ACTION_DELETE)){
                boolean ret = EventDataSource.delete(event.getEventId());
                // tell client how many records were actually added.
                MessagingEndpoint msg = new MessagingEndpoint();
                msg.sendMessage("Event "+ event.getEventId() + " Deleted:" + ret);
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
