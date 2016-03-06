package edu.dartmouth.cs.together;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.dartmouth.cs.together.utils.Globals;
import edu.dartmouth.cs.together.cloud.ServerUtilities;
import edu.dartmouth.cs.together.data.User;

import static android.Manifest.permission.GLOBAL_SEARCH;
import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask ;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private SharedPreferences mSharedPreference;
    @Bind(R.id.new_user_button) Button mNewUserBtn;
    @Bind(R.id.get_confirmation_code) Button mConfirmCodeBtn;
    @Bind(R.id.create_account) Button mCreateAccount;
    @Bind(R.id.new_password) TextView mNewPwd;
    @Bind(R.id.new_password_confirm) TextView mNewPwdConfirm;
    @Bind(R.id.email_login_form) View mLoginForm;
    @Bind(R.id.new_user_form) View mNewUserForm;
    @Bind(R.id.confirmation_code) TextView mConfirmCode;
    @Bind(R.id.new_email) TextView mNewEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mSharedPreference = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    @OnClick(R.id.new_user_button)
    public void onNewUserClick(View v){
        mCreateAccount.setEnabled(false);
        mLoginForm.setVisibility(View.GONE);
        mNewUserForm.setVisibility(View.VISIBLE);
        mNewEmail.requestFocus();
    }

    @OnClick(R.id.get_confirmation_code)
    public void onGetConfirmCodeClick(View v){
        if (!checkPasswords()){return;}
        if (checkEmail()) {
            showProgress(true);
            String mail = mNewEmail.getText().toString();
            new GetCodeAysncTask().execute(mail);
            mCreateAccount.setEnabled(true);
        }
    }

    @OnClick(R.id.create_account)
    public void onCreateAccountClick(){
        if (!checkPasswords()){return;}
        String confirmCode = mSharedPreference.getString(Globals.ACTION_CODE, null);
        String code = mConfirmCode.getText().toString();
        if(code.equals(confirmCode)){
            showProgress(true);
            String email = mNewEmail.getText().toString();
            String password = mNewPwd.getText().toString();
            setCurrentUser(email, password);
            new UserLoginTask(Globals.currentUser, true).execute();
        } else {
            mConfirmCode.setError("Wrong confirmation code!");
            mConfirmCode.requestFocus();
        }
    }

    private boolean checkPasswords(){
        if(mNewPwd.getText().length()<5){
            mNewPwd.setError("Password is too short!");
            mNewPwd.requestFocus();
            return false;
        }
        if(!mNewPwd.getText().toString().equals(mNewPwdConfirm.getText().toString())){
            mNewPwdConfirm.setError("Passwords don't match!!");
            return false;
        }
        return true;
    }

    private boolean checkEmail(){

        if(!isEmailValid(mNewEmail.getText().toString())){
            mNewEmail.setError("Please provide a Dartmouth email address");
            mNewEmail.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            setCurrentUser(mEmailView.getText().toString(), mPasswordView.getText().toString());
            showProgress(true);
            mAuthTask = new UserLoginTask(Globals.currentUser,false);
            mAuthTask.execute((Void) null);
        }
    }


    private void setCurrentUser(String email, String password){
        User user = new User(email);
        user.setPassword(password);
        Globals.currentUser = user;
    }

    private boolean isEmailValid(String email) {
        return email.endsWith("@dartmouth.edu");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {
        User mUser;
        boolean mIsNew;
        UserLoginTask(User user, boolean isNew) {
            mUser = user;
            mIsNew = isNew;
        }

        @Override
        protected Integer doInBackground(Void... p) {
            try {
                while(Globals.DEVICE_ID==null){
                    Thread.sleep(100);
                }
                try {
                    JSONObject json = new JSONObject();
                    json.put(User.ID_KEY, mUser.getId());
                    json.put(User.PASSWORD_KEY, mUser.getPassword());
                    json.put(User.DEVICE_KEY, Globals.DEVICE_ID);
                    json.put(User.RATE_KEY, 0.0);
                    json.put(User.ACCOUNT_KEY, mUser.getAccount());
                    String uploadState = "";
                    try {
                        Map<String, String> params = new HashMap<>();
                        params.put("json", json.toString());
                        if (mIsNew) {
                            params.put("action", Globals.ACTION_ADD);
                        }else {
                            params.put("action", Globals.ACTION_POLL);
                        }
                        // post add request
                        String result = ServerUtilities.post(Globals.SERVER_ADDR + "/adduser.do",
                                params);
                        if (result.contains("exist") || result.contains("not found")){
                            return 2;
                        } else if (result.contains("password")) {
                            return 1;
                        } else return 0;
                    } catch (Exception e1) {
                        uploadState = "Sync failed: " + e1.getMessage();
                        Log.e(this.getClass().getName(), "data posting error " + e1);
                    }
                    if (uploadState.length() > 0) {
                        Toast.makeText(getApplicationContext(), uploadState, Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Log.e(this.getClass().getName(), e.getCause().toString());
            }
            return 3;
        }

        @Override
        protected void onPostExecute(final Integer status) {
            mAuthTask = null;
            showProgress(false);

            if (status == 0) {
                SharedPreferences.Editor editor = mSharedPreference.edit();
                editor.putBoolean(Globals.LOGIN_STATUS_KEY, true);
                editor.putLong(User.ID_KEY, Globals.currentUser.getId());
                editor.putString(User.ACCOUNT_KEY,Globals.currentUser.getAccount());
                editor.commit();
                finish();
            } else if (status ==1) {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            } else if (status == 2) {
                mEmailView.setError(getString(R.string.error_incorrect_mail));
                mEmailView.requestFocus();
            } else {
                //TODO: something else went wrong!
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }


    }

    class GetCodeAysncTask extends AsyncTask<String,Void,Boolean>{

        @Override
        protected Boolean doInBackground(String... emails) {
            String code="";
            Map<String, String> params = new HashMap<>();
            params.put("action",Globals.ACTION_CODE);
            params.put("mail",emails[0]);
            try {
                code = ServerUtilities.post(Globals.SERVER_ADDR + "/adduser.do", params);
                if (code.contains("exists")){
                    return false;
                }else {
                    code = code.split(":")[1].trim();
                    mSharedPreference.edit().putString(Globals.ACTION_CODE, code).commit();
                    return true;
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            showProgress(false);
            if (!result){
                mNewEmail.setError("Email is taken!");
                mNewEmail.requestFocus();
            }else {
                Toast.makeText(getApplicationContext(), "Please check your email for code"
                        +mSharedPreference.getString(Globals.ACTION_CODE,""),
                        //TODO: to remvoe
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}

