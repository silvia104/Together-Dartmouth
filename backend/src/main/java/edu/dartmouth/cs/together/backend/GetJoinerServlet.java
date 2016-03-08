package edu.dartmouth.cs.together.backend;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
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
 * get all joiners of a event
 */
public class GetJoinerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        long eventId = Long.parseLong(req.getParameter(Event.ID_KEY));
        List<Long> userIds = EventJoinerDataSource.entitiesToUserId(
                EventJoinerDataSource.queryByEventId(eventId));
        //add owner to the top of the list;
        Entity entity = EventDataSource.queryById(eventId);
        PrintWriter out = resp.getWriter();

        if (entity!=null) {
            userIds.add(0, (Long)entity.getProperty(Event.OWNER_KEY));
            List<User> userList = UserDataSource.queryByIdList(userIds);
            JSONArray userJsonArray = new JSONArray();
            for (User user : userList) {
                userJsonArray.put(user.userToJson());
            }
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            out.print(userJsonArray.toString());
        } else {
            out.print("Event no loner exists!");
        }
        out.flush();

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
