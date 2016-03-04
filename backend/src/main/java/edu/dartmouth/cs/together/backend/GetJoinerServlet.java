package edu.dartmouth.cs.together.backend;

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
import edu.dartmouth.cs.together.backend.data.EventJoinerDataSource;
import edu.dartmouth.cs.together.backend.data.User;
import edu.dartmouth.cs.together.backend.data.UserDataSource;

/**
 * Created by TuanMacAir on 3/2/16.
 */
public class GetJoinerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        long eventId = Long.parseLong(req.getParameter(Event.ID_KEY));
        List<Long> userIds = EventJoinerDataSource.entitiesToUserId(
                EventJoinerDataSource.queryByEventId(eventId));
        List<User> userList = UserDataSource.queryByIdList(userIds);
        try {
            JSONArray userJsonArray = new JSONArray();
            for (User user : userList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(User.ID_KEY, user.getId());
                jsonObject.put(User.ACCOUNT_KEY, user.getAccount());
                jsonObject.put(User.RATE_KEY, user.getRate());
                //photo?
                userJsonArray.put(jsonObject);
            }
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();
            out.print(userJsonArray.toString());
            out.flush();
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
