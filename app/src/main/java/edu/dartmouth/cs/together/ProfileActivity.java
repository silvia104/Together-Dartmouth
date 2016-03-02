package edu.dartmouth.cs.together;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import edu.dartmouth.cs.together.utils.Helper;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImageView;
    private TextView mEmail;
    private Bitmap mProfileImage;
    private TextView mPhoneTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mProfileImageView = (ImageView) findViewById(R.id.profile_image);
        mProfileImage = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_pic);
        Bitmap croppedImage = Helper.getRoundedShape(mProfileImage);
        mProfileImageView.setImageBitmap(croppedImage);
        mEmail = (EditText) findViewById(R.id.profile_email);
        mEmail.setText("get email address from account ");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



    }

}
