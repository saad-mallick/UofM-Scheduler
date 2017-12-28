package saadandaakash.uofmscheduler.Utitilies;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Aakash on 12/26/2017.
 */

public class Utility {

    public static String getStringFromURL(String url) throws IOException, SocketTimeoutException {
        URL api_url = new URL(url);

        // Read and store the result line by line then return the entire string.
        BufferedReader reader = new BufferedReader( new InputStreamReader(api_url.openStream()));
        StringBuilder info = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            info.append(line.trim());
        }
        reader.close();

        return info.toString();
    }

    public static JSONArray getJSONArray(String url) throws JSONException, IOException {
        String info = getStringFromURL(url);
        return new JSONArray(info);
    }

    public static JSONObject getJSONObject(String url) throws JSONException, IOException {
        String info = getStringFromURL(url);
        return new JSONObject(info);
    }

    public static Map<String, String> readMapFromFile(Activity activity, String filename, String key_str, String value_str) {
        try {
            // read from json into JSONArray
            InputStream is = activity.getAssets().open("json/" + filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            JSONArray arr = new JSONArray(json);

            // convert JSONArray to Map
            Map<String, String> school_map = new TreeMap<>();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                // get key and value from JSON, put into map
                String key = obj.getString(key_str);
                String value = obj.getString(value_str);
                school_map.put(key, value);
            }
            return school_map;

        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        catch (JSONException j) {
            j.printStackTrace();
            return new TreeMap<>();
        }
    }

    public static void hideKeyboard(Context context) {
        InputMethodManager inputManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = ((Activity) context).getCurrentFocus();
        if(v != null) inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


}
