package edu.dartmouth.cs.together;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.soundcloud.android.crop.Crop;


import de.hdodenhof.circleimageview.CircleImageView;
import edu.dartmouth.cs.together.cloud.ServerUtilities;
import edu.dartmouth.cs.together.cloud.UploadPicIntentService;
import edu.dartmouth.cs.together.data.User;
import edu.dartmouth.cs.together.data.UserDataSource;
import edu.dartmouth.cs.together.data.UserTable;
import edu.dartmouth.cs.together.utils.Globals;

/*
 * Profile Activity
 * User can check name and Email address here
 * Long click the image view can change the profile photo
 * Due to Google App Store error the image can't be uploaded
 * So the save profile button is now hided.
 * Will be implemented in the future.
 * Rating is also showed in this page.
 * Click "Rate" button and a rating dialog will be launched
 * Click the refresh button on right top then the rating and the image will be refreshed
*/


public class ProfileActivity extends AppCompatActivity {

    private TextView mEmail;
    private TextView mName;
    private Uri mImageCaptureUri;
    private CircleImageView mProfileImageView;
    private boolean isTakenFromCamera;
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    public static final int REQUEST_CODE_SELECT_FROM_GALLERY = 1;
    private static final String CAMERA_CLICKED_KEY = "cameraClicked";
    private static Float ratestar;
    private static int cateIdx;
    private UserDataSource userdata;
    RatingBar myRating;

    private long mUserId ;
    private String mUserEmail;
    private String mUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mProfileImageView = (CircleImageView) findViewById(R.id.profile_image);
        mProfileImageView.setImageResource(R.drawable.default_profile_pic);
        myRating = (RatingBar)findViewById(R.id.ratingbar);
        Intent i = getIntent();
        mUserId = i.getLongExtra(User.ID_KEY, Globals.currentUser.getId());
        userdata=new UserDataSource(getApplicationContext());

         if(savedInstanceState != null) {
             mImageCaptureUri = savedInstanceState
                    .getParcelable(URI_INSTANCE_STATE_KEY);
             new LoadPicAsyncTask(false).execute(mUserId);
         }
         if (mUserId == Globals.currentUser.getId() || mUserId == -1) {
             if (mImageCaptureUri != null) {
                 mProfileImageView.setImageURI(mImageCaptureUri);
             }else {
                 loadPhoto();
             }
             mProfileImageView.setOnLongClickListener(
                     new View.OnLongClickListener() {
                         public boolean onLongClick(View v) {
                             displayDialog(PhotoDialogFragment.DIALOG_ID_PHOTO_PICKER);
                             return true;
                         }
                     }
             );
             mUserEmail = Globals.currentUser.getAccount();
             mUserName = Globals.currentUser.getName();
         } else {
             mUserEmail = i.getStringExtra(User.ACCOUNT_KEY);
             mUserName = i.getStringExtra(User.NAME_KEY);
             new LoadPicAsyncTask(false).execute(mUserId);
         }
        ratestar = i.getFloatExtra(User.RATE_KEY, 0);
        cateIdx=i.getIntExtra("cate", 0);
        TextView rateTitle = (TextView) findViewById(R.id.profile_rate_title);
        rateTitle.setText(rateTitle.getText().toString() + Globals.categories.get(cateIdx));
        myRating.setRating(ratestar);
        mName = (TextView) findViewById(R.id.profile_name);
        mName.setText(mUserName);
        mEmail = (TextView) findViewById(R.id.profile_email);
        mEmail.setText(mUserEmail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    private void showRateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = this.getLayoutInflater().inflate(R.layout.rate, null);
        builder.setMessage(getString(R.string.confirmRateEvent))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RatingBar rate = (RatingBar)((Dialog)dialog).findViewById(R.id.myratingbar);
                        float ratescore=rate.getRating();
                        rate(ratescore);
                    }
                });
        builder.setView(v);
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }


    @Override
    //Download the profile image photo when refreshing button on menu is clicked
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh){
            new LoadPicAsyncTask(true).execute(mUserId);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCaptureUri);
    }

    private void loadPhoto(){
        try {
            FileInputStream fis = openFileInput("profile_photo.png");
            Bitmap bmap = BitmapFactory.decodeStream(fis);
            mProfileImageView.setImageBitmap(bmap);
            fis.close();
        } catch (IOException e) {
            // Default profile photo if no photo saved before.
            mProfileImageView.setImageResource(R.drawable.default_profile_pic);
        }
    }

    private void saveSnap() {
        // Commit all the changes into preference file
        // Save profile image into internal storage.
        mProfileImageView.buildDrawingCache();
        Bitmap bmap = mProfileImageView.getDrawingCache();
        try {
            FileOutputStream fos = openFileOutput(
                    "profile_photo.png", MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    //private helper to crop the picture
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            mProfileImageView.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //display diglog to take/select photos
    public void displayDialog(int id) {
        DialogFragment fragment = PhotoDialogFragment.newInstance(id);
        fragment.show(getFragmentManager(),
                getString(R.string.dialog_fragment_tag_photo_picker));
    }

    public void onPhotoPickerItemSelected(int item) {
        Intent intent;

        switch (item) {

            case PhotoDialogFragment.ID_PHOTO_PICKER_FROM_CAMERA:
                // Take photo from camera，
                // Construct an intent with action
                // MediaStore.ACTION_IMAGE_CAPTURE
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Construct temporary image path and name to save the taken photo
                mImageCaptureUri = Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), "tmp_"
                        + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        mImageCaptureUri);
                intent.putExtra("return-data", true);
                try {
                    // Start a camera capturing activity
                    // REQUEST_CODE_TAKE_FROM_CAMERA is an integer tag you
                    startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                isTakenFromCamera = true;
                break;

            case PhotoDialogFragment.ID_PHOTO_PICKER_FROM_GALLERY:

                intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                try{
                    startActivityForResult(intent, REQUEST_CODE_SELECT_FROM_GALLERY);
                }catch (ActivityNotFoundException e){
                    e.printStackTrace();
                }
                isTakenFromCamera = true;
                break;

            default:
                return;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_CODE_TAKE_FROM_CAMERA:
                // Send image taken from camera for cropping
                beginCrop(mImageCaptureUri);
                break;

            case REQUEST_CODE_SELECT_FROM_GALLERY:

                mImageCaptureUri = data.getData();
                beginCrop(mImageCaptureUri);

                break;

            case Crop.REQUEST_CROP: //We changed the RequestCode to the one being used by the library.
                // Update image view after image crop
                handleCrop(resultCode, data);
                // Delete temporary image taken by camera after crop.
                if (isTakenFromCamera) {
                    File f = new File(mImageCaptureUri.getPath());
                    if (f.exists() )
                        f.delete();
                    mImageCaptureUri = Crop.getOutput(data);
                    mProfileImageView.setImageURI(mImageCaptureUri);
                }
                break;
        }
    }

    //save the image both localled and remotely
    public void OnSaveClicked(View view){
        saveSnap();
        startService(new Intent(getApplicationContext(), UploadPicIntentService.class));
        finish();
    }

    public void OnCancelClicked(View view){
        finish();
    }

    public void OnRateClicked(View view){
        showRateDialog();
    }

    //Anonymous AsyncTask is executed to upload rating to the server
    private void rate(final float score){
        new AsyncTask<Void, Void, Integer>() {
            Map<String, String> params = new HashMap<String, String>();
            String updatedrate=null;
            // Get history and upload it to the server.
            @Override
            protected Integer doInBackground(Void... arg0) {
                try {
                    params.put("id", Long.toString(mUserId));
                    params.put("cate",Integer.toString(cateIdx));
                    params.put("rate", Float.toString(score));
                    updatedrate=ServerUtilities.post(Globals.SERVER_ADDR + "/rate.do", params);
                    ContentValues values = new ContentValues();
                    values.put(UserTable.COLUMNS.RATE.colName(), updatedrate);
                    userdata.updateUser(mUserId, values);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return 1;
            }
            @Override
            protected void onPostExecute(Integer i) {
                if(updatedrate!=null)
                myRating.setRating(getrate(updatedrate,cateIdx));
            }
        }.execute();
    }

    public float getrate(String rate, int index){
        String s[] = rate.split(",");
        List<Integer> number=new ArrayList<Integer>();
        List<Float>ratenum=new ArrayList<Float>();
        for(int i=0;i<s.length/2;i++){
            number.add(Integer.parseInt(s[i]));
        }
        for(int i=s.length/2;i<s.length;i++){
            ratenum.add(Float.parseFloat(s[i]));
        }
        return ratenum.get(index);
    }

    //Download the user image from server
    class LoadPicAsyncTask extends AsyncTask<Long, Void, Bitmap> {
        private boolean mIsRefresh;
        public LoadPicAsyncTask(boolean isRefresh){
            mIsRefresh = isRefresh;
        }
        @Override
        protected Bitmap doInBackground(Long... userIds) {
            UserDataSource db = new UserDataSource(getApplicationContext());
            Bitmap bitmap = null;
            User user = db.queryUserById(userIds[0]);

            if (!mIsRefresh) {
                bitmap=db.getBitmap(userIds[0]);
                if (bitmap == null){
                    return downloadBitmap(user);
                } else {
                    return bitmap;
                }
            } else {
                return downloadBitmap(user);
            }
        }

        private Bitmap downloadBitmap(User user){

            try {
                Bitmap bitmap = ServerUtilities.postForImg(user.getPhotoUrl());
                if (bitmap != null) {
                    new UserDataSource(getApplicationContext()).insertBitmap(bitmap,
                            user.getId());
                }
                return bitmap;
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap == null){
                mProfileImageView.setImageResource(R.drawable.default_profile_pic);
            } else {
                mProfileImageView.setImageBitmap(bitmap);
            }
        }
    }

}
