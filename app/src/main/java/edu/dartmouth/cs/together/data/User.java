package edu.dartmouth.cs.together.data;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import edu.dartmouth.cs.together.utils.Globals;
import edu.dartmouth.cs.together.utils.Helper;

/**
 * Created by TuanMacAir on 2/29/16.
 */
public class User {
    public static final String ID_KEY = "user_id";
    public static final String ACCOUNT_KEY = "account";
    public static final String PASSWORD_KEY = "password";
    public static final String PHOTO_KEY = "photo";
    public static final String RATE_KEY = "rate";
    public static final String DEVICE_KEY = "device";
    public static final String PHOTO_URL_KEY = "photo_url";
    public static final String NAME_KEY = "name";

    String mEmail;
    String mName;
    String mRate;
    String mPhotoUrl;
    Bitmap mPhoto;
    String mPassword;
    long mUserID;
    public User(){

    }
    public User(String email){
        mUserID = Helper.intToUnsignedLong(email.hashCode());
        mEmail = email;
    }
    public User(JSONObject json){
        try {
            mUserID = json.getLong(User.ID_KEY);
            mEmail = json.getString(User.ACCOUNT_KEY);
            mRate = json.getString(User.RATE_KEY);
            mPhotoUrl = json.getString(User.PHOTO_URL_KEY);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void setPhotoUrl(String url){
        mPhotoUrl = url;
    }
    public String getPhotoUrl(){
        return mPhotoUrl.replace("http://0.0.0.0:8080", Globals.SERVER_ADDR);
    }
    public long getId() {
        return mUserID;
    }

    public void setId(long id) {
        mUserID = id;
    }

    public void setAccount(String account) {
        mEmail = account;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public void setRate(String i) {
        mRate = i;
    }

    public String getAccount() {
        return mEmail;
    }
    public String getPassword(){
        return mPassword;
    }
    public String getRate(){
        return mRate;
    }

    public String getName() {
        String name = mEmail.split("@")[0];
        int i = name.toLowerCase().lastIndexOf(".gr");
        if (i>0){
            name = name.substring(0,i);
        }
        name = name.replace('.',' ');
        return name;
    }
}
