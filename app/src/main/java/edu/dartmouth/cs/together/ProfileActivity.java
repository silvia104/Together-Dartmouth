package edu.dartmouth.cs.together;

import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


import com.soundcloud.android.crop.Crop;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView mEmail;
    private Bitmap mProfileImage;
    private TextView mPhoneTextView;

    private Uri mImageCaptureUri;
    private byte[] mProfilePictureArray;
    //used library: https://github.com/ArthurHub/Android-Image-Cropper/wiki
    private CircleImageView mProfileImageView;
    private boolean isTakenFromCamera;
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    public static final int REQUEST_CODE_SELECT_FROM_GALLERY = 1;
    private static final String CAMERA_CLICKED_KEY = "cameraClicked";
    public static final int REQUEST_CODE_CROP_PHOTO = 2;
    private static final String IMAGE_UNSPECIFIED = "image/*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mProfileImageView = (CircleImageView) findViewById(R.id.profile_image);
//        mProfileImage = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_pic);
//        Bitmap croppedImage = Helper.getRoundedShape(mProfileImage);
//        mProfileImageView.setImageBitmap(croppedImage);
        mProfileImageView.setImageResource(R.drawable.default_profile_pic);

        mProfileImageView.setOnLongClickListener(
                new View.OnLongClickListener(){
                    public boolean onLongClick(View v){
                        displayDialog(PhotoDialogFragment.DIALOG_ID_PHOTO_PICKER);
//                        saveSnap();
                        return true;
                    }
                }
        );
        mEmail = (EditText) findViewById(R.id.profile_email);
        mEmail.setText("get email address from account ");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(savedInstanceState == null || mImageCaptureUri == null || !savedInstanceState.getBoolean(CAMERA_CLICKED_KEY)) {

            loadPhoto();
        }
        if(savedInstanceState != null) {
            mImageCaptureUri = savedInstanceState
                    .getParcelable(URI_INSTANCE_STATE_KEY);
            mProfileImageView.setImageURI(mImageCaptureUri);
        }
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
            bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
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


//            mImageCaptureUri = Crop.getOutput(result);
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
                // Construct temporary image path and name to save the taken
                // photo
                mImageCaptureUri = Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), "tmp_"
                        + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        mImageCaptureUri);
                intent.putExtra("return-data", true);
                try {
                    // Start a camera capturing activity
                    // REQUEST_CODE_TAKE_FROM_CAMERA is an integer tag you
                    // defined to identify the activity in onActivityResult()
                    // when it returns
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

    public void OnSaveClicked(View view){
        saveSnap();
        finish();
    }

    public void OnCancelClicked(View view){
        finish();
    }




}
