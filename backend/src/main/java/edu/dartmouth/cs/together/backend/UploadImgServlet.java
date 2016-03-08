package edu.dartmouth.cs.together.backend;

/**
 * Created by TuanMacAir on 3/5/16.
 * get blob sent by client
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Entities;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import edu.dartmouth.cs.together.backend.data.User;
import edu.dartmouth.cs.together.backend.data.UserDataSource;


public class UploadImgServlet extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        try {
            List<BlobKey> blobs = blobstoreService.getUploads(req).get("myFile");
            BlobKey blobKey = blobs.get(0);
            BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
            String fileName = blobInfo.getFilename();

            ImagesService imagesService = ImagesServiceFactory.getImagesService();
            ServingUrlOptions servingOptions = ServingUrlOptions.Builder.withBlobKey(blobKey);
            String servingUrl = imagesService.getServingUrl(servingOptions);


            long userId = Long.parseLong(fileName.substring(0, fileName.indexOf('.')));
            Entity entity = UserDataSource.queryById(userId);
            if (entity!= null){
                String photoKey = (String)entity.getProperty(User.PHOTO_KEY);
                if (photoKey!=null){
                    blobstoreService.delete(new BlobKey(photoKey));
                }
            }
            Map<String, String> param = new HashMap<>();
            param.put(User.PHOTO_KEY,blobKey.getKeyString());
            param.put(User.PHOTO_URL_KEY,servingUrl);
            UserDataSource.update(entity, param);

            res.setStatus(HttpServletResponse.SC_OK);
            res.setContentType("application/json");

            JSONObject json = new JSONObject();

            json.put(User.PHOTO_URL_KEY, servingUrl);
           // json.put("blobKey", blobKey.getKeyString());

            PrintWriter out = res.getWriter();
            out.print(json.toString());
            out.flush();
            out.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }


}
