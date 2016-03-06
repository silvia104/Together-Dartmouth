package edu.dartmouth.cs.together.backend;

import edu.dartmouth.cs.together.backend.data.User;
import edu.dartmouth.cs.together.backend.data.UserDataSource;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by TuanMacAir on 3/1/16.
 */
public class NewUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(NewUserServlet.class.getName());

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String action = req.getParameter("action");
        String email = req.getParameter("mail");
        try {

            if (action.equals("code")) {
                PrintWriter out = resp.getWriter();
                if (UserDataSource.queryByAccount(email) == null) {
                    String code = sendConfirmCode(email);
                    out.print(code);
                } else {
                    out.print("user exists!");
                }
                out.flush();
            } else if (action.equals(Globals.ACTION_ADD)) {
                String jsonString = req.getParameter("json");
                JSONObject jsonObject = new JSONObject(jsonString);
                User user = new User(jsonObject);
                boolean ret = UserDataSource.add(user);
                if (!ret) {
                    PrintWriter out = resp.getWriter();
                    out.print("user exists!");
                    out.flush();
                }
                /*
                    MessagingEndpoint msg = new MessagingEndpoint();
                    List<String> deviceList = new ArrayList<>();
                    deviceList.add(user.getDevice());
                    msg.sendMessage(deviceList, "New User Added:" + ret);
                */
            } else if (action.equals(Globals.ACTION_UPDATE)) {
                String jsonString = req.getParameter("json");
                JSONObject jsonObject = new JSONObject(jsonString);
                User user = new User(jsonObject);
                Entity entity = UserDataSource.queryByAccount(user.getAccount());
                if (entity!= null) {
                    UserDataSource.update(entity, user);
                } else {
                    PrintWriter out = resp.getWriter();
                    out.print("user not found!");
                    out.flush();
                }
            } else if (action.equals(Globals.ACTION_POLL)){
                String jsonString = req.getParameter("json");
                JSONObject jsonObject = new JSONObject(jsonString);
                User user = new User(jsonObject);
                Entity entity = UserDataSource.queryByAccount(user.getAccount());
                if (entity!=null){
                    if (user.getPassword().equals(
                            (String) entity.getProperty(User.PASSWORD_KEY))){
                        UserDataSource.add(user);
                    } else {
                        PrintWriter out = resp.getWriter();
                        out.print("wrong password");
                        out.flush();
                    }
                } else {
                    PrintWriter out = resp.getWriter();
                    out.print("user not found!");
                    out.flush();
                }
            }
        } catch (JSONException e) {
            log.warning(e.toString());
        }


    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        doGet(req, resp);
    }

    private String sendConfirmCode(String toAddress) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String msgBody = "Confirmation Code: " + Long.toString(System.currentTimeMillis() % 9999L);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("silviazhou104@gmail.com", "Silvia Zhou"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(toAddress));
            msg.setSubject("Your ToGather account has been confirmed");
            msg.setText(msgBody);
            Transport.send(msg);

        } catch (AddressException e) {
            log.warning(e.toString());
        } catch (MessagingException e) {
            log.warning(e.toString());
        } catch (UnsupportedEncodingException e) {
            log.warning(e.toString());
        }
        return msgBody;
    }
}
