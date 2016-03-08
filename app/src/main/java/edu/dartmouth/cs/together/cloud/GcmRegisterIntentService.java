package edu.dartmouth.cs.together.cloud;

import android.content.Intent;

import com.example.tuanmacair.myapplication.backend.registration.Registration;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import edu.dartmouth.cs.together.utils.Globals;

/**
 * Created by TuanMacAir on 3/1/16.
 */
public class GcmRegisterIntentService extends BaseIntentSerice {
    private Registration regService = null;
    private GoogleCloudMessaging gcm;
    private static String TAG="GcmRegisterIntentService";
    // set sender_ID to my own project ID
    private static final String SENDER_ID = "226851527208";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.

     */
    public GcmRegisterIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (regService == null) {
            Registration.Builder builder =
                    new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                            new AndroidJsonFactory(), null)
                            // Need setRootUrl
                            .setRootUrl(Globals.SERVER_ADDR + "/_ah/api/")
                            .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }});

            // end of optional local run code
            regService = builder.build();
        }

        String msg ;
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            }
            String regId = gcm.register(SENDER_ID);
            msg = "Device registered, registration ID_KEY=" + regId;

            // You should send the registration ID_KEY to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            regService.register(regId).execute();
            Globals.DEVICE_ID = regId;
            Globals.isRegistered = true;
            showToast("Registration is done!");
        } catch (IOException ex) {
            ex.printStackTrace();
            msg = "Error: " + ex.getMessage();
            showToast(msg);
        }
    }
}
