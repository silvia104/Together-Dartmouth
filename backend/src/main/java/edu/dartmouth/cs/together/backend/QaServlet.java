package edu.dartmouth.cs.together.backend;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.apphosting.client.datastoreservice.app.EntityV4Normalizer;

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
import edu.dartmouth.cs.together.backend.data.Qa;
import edu.dartmouth.cs.together.backend.data.QaDataSource;
import edu.dartmouth.cs.together.backend.data.UserDataSource;

/**
 * Created by TuanMacAir on 3/2/16.
 */
public class QaServlet extends HttpServlet {
    private static int Add = 0;
    private static int Update = 1;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        String jsonString = req.getParameter("json");
        String actionString = req.getParameter("action");

        try {
            if (jsonString!= null) {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (actionString.equals(Globals.ACTION_ADD)) {
                    Qa qa = new Qa();
                    qa.setEventId(jsonObject.getLong(Event.ID_KEY));
                    qa.setQuestion(jsonObject.getString(Qa.Q_KEY));
                    qa.setId(jsonObject.getLong(Qa.ID_KEY));
                    QaDataSource.add(qa);
                    sendMessage(qa.getEventId(), Add, qa.getId(), qa.getQuestion());
                } else if (actionString.equals(Globals.ACTION_UPDATE)) {
                    Qa qa = new Qa();
                    qa.setEventId(jsonObject.getLong(Event.ID_KEY));
                    qa.setAnswer(jsonObject.getString(Qa.A_KEY));
                    qa.setId(jsonObject.getLong(Qa.ID_KEY));
                    QaDataSource.add(qa);
                    sendMessage(qa.getEventId(), Update, qa.getId(), qa.getQuestion());
                }
            }else if (actionString.equals(Globals.ACTION_POLL)) {
                String eventIdStr = req.getParameter(Event.ID_KEY);
                long eventId = Long.parseLong(eventIdStr);
                List<Qa> QaList = QaDataSource.entityListToQaList(
                        QaDataSource.queryByEventId(eventId));
                JSONArray QaJsonArray = new JSONArray();
                for (Qa Qa : QaList) {
                    JSONObject json = new JSONObject();
                    json.put(Qa.ID_KEY, Qa.getId());
                    json.put(Qa.Q_KEY, Qa.getQuestion());
                    json.put(Qa.A_KEY, Qa.getAnswer());
                    json.put(Event.ID_KEY, Qa.getEventId());
                    QaJsonArray.put(json);
                }
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                PrintWriter out = resp.getWriter();
                out.print(QaJsonArray.toString());
                out.flush();
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }

    private void sendMessage(long eventId, int type, long questionId, String q){
        List<String> deviceList;
        List<Long> userList = new ArrayList<>();
        String message = null;
        Entity entity = EventDataSource.queryById(eventId);
        if (entity ==null){
            message = "Event no longer exist!:"+eventId;
        }else {
            if (type == Add) {
                userList.add((long) entity.getProperty(Event.OWNER_KEY));
                message = "Question Posted:" + eventId + "," + questionId + "," + q;
            } else if (type == Update) {
                userList.addAll(EventJoinerDataSource.entitiesToUserId(
                        EventJoinerDataSource.queryByEventId(eventId)));
                message = "Question Answered:" + eventId + "," + questionId + "," + q;
            }
        }
        deviceList = UserDataSource.queryDeviceByUserId(userList);
        MessagingEndpoint msg = new MessagingEndpoint();
        try {
            msg.sendMessage(deviceList, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

