package edu.dartmouth.cs.together.backend.data;


import com.google.appengine.api.datastore.Blob;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;


/**
 * Created by TuanMacAir on 1/28/16.
 * Data model of Activity Record
 */
public class User {
    public static final String USER_PARENT_ENTITY_KEY = "UserParent";

    public static final String USER_PARENT_ENTITY_NAME = "UserParent";
    public static final String USER_ENTITY_NAME = "User";

    public static final String ID_KEY = "user_id";
    public static final String ACCOUNT_KEY = "account";
    public static final String PASSWORD_KEY = "password";
    public static final String PHOTO_KEY = "photo";
    public static final String RATE_KEY = "rate";
    public static final String DEVICE_KEY = "device";
    public static final String PHOTO_URL_KEY = "photo_url";


    private long mId;
    private String mEmail;
    private String mRate;
    private String mPhotoKey;
    private String mPassword;
    private String mDeviceId;
    private String mPhotoUrl = "NA";

    public User(){}

    public User(JSONObject json) throws JSONException{
        mId = json.getLong(ID_KEY);
        mEmail = json.getString(ACCOUNT_KEY);
        mRate = json.getString(RATE_KEY);
        mPassword = json.getString(PASSWORD_KEY);
        mDeviceId = json.getString(DEVICE_KEY);
    }

    public void setPhotoKey(String photoKey){
        mPhotoKey = photoKey;
    }

    public String getPhotoKey(){
        return mPhotoKey;
    }

    public void setPhotoUrl(String url){
        mPhotoUrl = url;
    }

    public String getPhotoUrl (){
        return mPhotoUrl;
    }
    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
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

    public String getDevice(){
        return mDeviceId;
    }
    public void setDevice(String device){
        mDeviceId = device;
    }

    public JSONObject userToJson(){
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(User.ID_KEY, mId);
            jsonObject.put(User.ACCOUNT_KEY, mEmail);
            jsonObject.put(User.RATE_KEY, mRate);
            jsonObject.put(User.PHOTO_URL_KEY, mPhotoUrl);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject;

    }
}