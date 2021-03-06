package edu.dartmouth.cs.together.cloud;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by TuanMacAir on 2/20/16.
 */
public class ServerUtilities {
    //private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    //private static final Random random = new Random();


    /**
     * Issue a POST request to the server.
     *
     * @param endpoint
     *            POST address.
     * @param params
     *            params to post.
     *
     * @throws IOException
     *             propagated from POST.
     */
    public static String post(String endpoint,Map<String,String> params)
            throws IOException {
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        Log.d(ServerUtilities.class.getName(), endpoint + " is posted");
        // constructs the POST body using the parameters
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            conn.setConnectTimeout(BACKOFF_MILLI_SECONDS);
            conn.setReadTimeout(BACKOFF_MILLI_SECONDS);

            // handle the response
            int status = conn.getResponseCode();
            Log.d(ServerUtilities.class.getName(), "" + status);
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }

            // Get Response
            InputStream is = conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\n');
            }
            rd.close();
            return response.toString();

        }finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

}
