package saadandaakash.uofmscheduler;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Aakash on 12/26/2017.
 */

public class Utility {

    public static JSONArray getJSONArray(String url) throws SocketTimeoutException, JSONException, IOException {

        /*
         * TODO: Catch SocketTimeoutException and print "sorry timeout" message
         */

        // Build and set timeout values for the request.
        System.out.println("HERE");
        URLConnection connection = (new URL(url)).openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(20000);
        connection.connect();

        // Read and store the result line by line then return the entire string.
        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder html = new StringBuilder();
        for (String line; (line = reader.readLine()) != null; ) {
            html.append(line.trim());
        }
        in.close();

        return new JSONArray(html.toString());
    }

    public static void hideKeyboard(Context context) {
        InputMethodManager inputManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = ((Activity) context).getCurrentFocus();
        if(v != null) inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


}
