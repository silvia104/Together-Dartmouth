package edu.dartmouth.cs.together.backend;

import edu.dartmouth.cs.together.backend.data.User;
import edu.dartmouth.cs.together.backend.data.UserDataSource;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by TuanMacAir on 3/1/16.
 */
public class NewUserServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String jsonString = req.getParameter("json");
        // TODO: get photo
        // parse the json array from client
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            User user = new User(jsonObject);
            boolean ret = UserDataSource.add(user);
            // tell client how many records were actually added.
            MessagingEndpoint msg = new MessagingEndpoint();
            List<String> deviceList = new ArrayList<>();
            deviceList.add(user.getDevice());
            msg.sendMessage(deviceList,"New User Added:" + ret);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doGet(req, resp);
    }
}
