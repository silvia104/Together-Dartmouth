package edu.dartmouth.cs.together.data;

import android.graphics.Bitmap;

import edu.dartmouth.cs.together.utils.Helper;

/**
 * Created by TuanMacAir on 2/29/16.
 */
public class User {
    public static final String ID_KEY = "id";
    public static final String ACCOUNT_KEY = "account";
    public static final String PASSWORD_KEY = "password";
    public static final String PHOTO_KEY = "photo";
    public static final String RATE_KEY = "rate";
    public static final String DEVICE_KEY = "device";    String mEmail;
    String mName;
    int mRate;
    Bitmap mphoto;
    String mPassword;
    long mUserID;
    public User(String email){
        mUserID = Helper.intToUnsignedLong(email.hashCode());
        mEmail = email;
    }

    public void setPhoto(){
        //TODO;
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

    public void setRate(int i) {
        mRate = i;
    }

    public String getAccount() {
        return mEmail;
    }
    public String getPassword(){
        return mPassword;
    }
    public int getRate(){
        return mRate;
    }
}
