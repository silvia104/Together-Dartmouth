package edu.dartmouth.cs.together.backend;

import com.google.appengine.api.datastore.Entity;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.dartmouth.cs.together.backend.data.User;
import edu.dartmouth.cs.together.backend.data.UserDataSource;

/**
 * Created by di on 3/7/2016.
 */
public class RatingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        long userId = Long.parseLong(req.getParameter("id"));
        Float score = Float.parseFloat(req.getParameter("rate"));
        int category = Integer.parseInt(req.getParameter("cate"));
        List<Long> userIds = new ArrayList<>();
        userIds.add(userId);
        List<User> userList = UserDataSource.queryByIdList(userIds);
        User updateuser=userList.get(0);
        String rate = updateuser.getRate();
        String s[] = rate.split(",");
        List<Integer>number=new ArrayList<Integer>();
        List<Float>ratenum=new ArrayList<Float>();
        for(int i=0;i<s.length/2;i++){
            number.add(Integer.parseInt(s[i]));
        }
        for(int i=s.length/2;i<s.length;i++){
            ratenum.add(Float.parseFloat(s[i]));
        }
        int curnum=number.get(category);
        Float curscore=ratenum.get(category);
        Float newRate=(curnum*curscore+score)/(curnum+1);
        number.set(category,curnum+1);
        ratenum.set(category,newRate);
        String newrate = new String();
        for(int i=0;i<s.length/2;i++){
            newrate+=number.get(i);
            newrate+=",";
        }
        for(int i=0;i<s.length/2;i++){
            newrate+=ratenum.get(i);
            if(i!=s.length/2-1)
            newrate+=",";
        }
        updateuser.setRate(newrate);
        Entity entity = UserDataSource.queryByAccount(updateuser.getAccount());
        if (entity!= null) {
            UserDataSource.update(entity, updateuser);
        } else {
            PrintWriter out = resp.getWriter();
            out.print("user not found!");
            out.flush();
        }
        resp.setContentType("text");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(newrate);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}