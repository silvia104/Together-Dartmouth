package edu.dartmouth.cs.together.cloud;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import edu.dartmouth.cs.together.data.User;
import edu.dartmouth.cs.together.data.UserDataSource;
import edu.dartmouth.cs.together.data.UserTable;
import edu.dartmouth.cs.together.utils.Globals;

/**
 * Created by TuanMacAir on 3/6/16.
 */
public class UploadPicIntentService extends BaseIntentSerice {

    public UploadPicIntentService() {
        super("UploadPicIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        try {
            String res = getUrl(Globals.currentUser.getId());
            String json = uploadImage(res,Globals.currentUser.getId());
            if (json.length()>0){
                String photoUrl = new JSONObject(json).getString(User.PHOTO_URL_KEY);
                Globals.currentUser.setPhotoUrl(photoUrl);
                ContentValues values = new ContentValues();
                values.put(UserTable.COLUMNS.PHOTO_URL.colName(), photoUrl);
                new UserDataSource(getApplicationContext()).updateUser(Globals.currentUser.getId(),
                       values);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUrl(long userId)  throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("myFile", userId+".jpg");
        String res = ServerUtilities.post(Globals.SERVER_ADDR + "/geturl", params);
        if (res.length()>0) {
            String newres = res.replace("http://TuanMacAirs-MacBook-Air.local:8080", Globals.SERVER_ADDR);
            return newres.substring(0, newres.length() - 1);
        }
        return "";
    }

    public String uploadImage(String uploadUrl, long userId) throws IOException{
        if (uploadUrl.length() == 0) return "";
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Bitmap bitMap = BitmapFactory.decodeFile(getFileStreamPath("profile_photo.png").getPath());

        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        outStream.flush();
        byte[] bytes = outStream.toByteArray();
        String json = ServerUtilities.postImage(uploadUrl, bytes,  userId);
        if (json.length()>0) {
            json = json.substring(0, json.length() - 1);
            return json;
        }
        return "";
    }
}
