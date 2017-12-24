package saadandaakash.uofmscheduler;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.os.StrictMode;

import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.json.*;

/**
 * Created by Aakash on 12/22/2017.
 * Aakash, this is your fragment
 * Follow the directions below on how to change between fragments
 */

public class SelectionFragment extends Fragment {

    private String termCode = "2170";
    private String schoolCode = "";
    private String subjectCode = "";
    private String catalogNum = "";

    public Button submitButton;
    public AutoCompleteTextView editSubject, editSchool;

    private static Map<String, String> schoolData, subjectData;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        //Inflate the layout into the specified container
        //True if you can inflate the layout and then attach it directly to the root of the
        //container
        //False if you want to inflate the layout and then return that View
        return inflater.inflate(R.layout.selection_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        try{
            submitButton = (Button)getView().findViewById(R.id.submitCourseInfo);
            editSchool = (AutoCompleteTextView)getView().findViewById(R.id.enterSchool);
            editSubject = (AutoCompleteTextView)getView().findViewById(R.id.enterSubject);


            // create map of schools and their codes
            schoolData = readMapFromFile(
                    "schools.json",
                    "SchoolDescr",
                    "SchoolCode");
            final Set<String> SCHOOLS = schoolData.keySet();

            // create array adapter for school input field with school names
            final ArrayAdapter<String> school_adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_dropdown_item_1line,
                    SCHOOLS.toArray(new String[SCHOOLS.size()]));
            editSchool.setAdapter(school_adapter);
            editSchool.setThreshold(1);

            // create array adapter for subject field, currently empty
            final ArrayAdapter<String> subject_adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_dropdown_item_1line, new String[0]);
            editSubject.setAdapter(subject_adapter);
            editSubject.setThreshold(1);


            submitButton.setOnClickListener( new View.OnClickListener() {
                                                 public void onClick(View view) {

                                                     // get school and subject values
                                                     schoolCode = schoolData.get(editSchool.getText().toString());
                                                     subjectCode = editSubject.getText().toString();
                                                     hideKeyboard(getActivity());
                                                     Fragment fragment = CoursesFragment.newInstance(schoolCode, subjectCode);
                                                     FragmentManager fragmentManager = getFragmentManager();
                                                     fragmentManager.beginTransaction()
                                                             .replace(R.id.container, fragment)
                                                             .commit();
    /*                    if (!subjectCode.isEmpty() && !catalogNum.isEmpty()) {
                            TextView header = (TextView) findViewById(R.id.courseDisplayHeader);
                            TextView info = (TextView) findViewById(R.id.courseInfo);
                            try {
                                String openSections = findOpenCourses();

                                String headerMessage = subjectCode + " " + catalogNum;
                                header.setText(headerMessage);

                                info.setText(openSections);
                                info.setMovementMethod(new ScrollingMovementMethod());
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                // clear header
                                String headerMessage = "";
                                header.setText(headerMessage);

                                String errorMessage = "Sorry, the course you specified could not be found.";
                                info.setText(errorMessage);
                            }
                        }*/

                                                 }
                                             }
            );

            // EFFECTS: when the focus moves from school field to subject field, populate
            //          the subject adapter with the school department names
            editSchool.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        // check if the school field is filled
                        String school = editSchool.getText().toString();
                        if (!school.equals(null)) {
                            schoolCode = schoolData.get(school);
                            editSubject.setText("");
                        }
                        // check if the school entered is valid
                        if (schoolCode != null) {
                            try {
                                subject_adapter.clear();
                                subjectData = readMapFromFile(
                                        schoolCode + ".json",
                                        "SubjectDescr",
                                        "SubjectCode"
                                );
                                // add values to subject adapter in form
                                // [School Name (Code)]
                                for (Map.Entry<String, String> entry : subjectData.entrySet()) {
                                    subject_adapter.add(entry.getValue() + " - " + entry.getKey());
                                }
                                // reset adapter filter
                                subject_adapter.getFilter().filter(editSubject.getText(), null);
                            }
                            catch (Exception e) {
                                editSubject.setText(e.toString());
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Context context) {
        InputMethodManager inputManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = ((Activity) context).getCurrentFocus();
        if(v != null) inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public String findOpenCourses() throws Exception {

        String openClasses = "";

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        String url = "http://umich-schedule-api.herokuapp.com/v4/get_sections?" +
                "term_code=" + termCode +
                "&school=" + schoolCode +
                "&subject=" + subjectCode.toUpperCase() +
                "&catalog_num=" + catalogNum;

        JSONArray infoFromAPI = getJSONArray(url);

        // go through course info array and find open sections
        for (int i = 0; i < infoFromAPI.length(); i++) {
            JSONObject infoObject = infoFromAPI.getJSONObject(i);

            String append = "Section " + infoObject.getString("SectionNumber") + ": " +
                    String.valueOf(infoObject.getInt("AvailableSeats")) + "\n";
            openClasses += append;

        }
        // if no open classes are found...
        if (openClasses.length() == 0) {
            throw new Exception();
        }

        return openClasses;
    }

    public static JSONArray getJSONArray(String url) throws IOException, JSONException {

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

    public Map<String, String> readMapFromFile(String filename, String key_str, String value_str) {
        try {
            // read from json into JSONArray
            InputStream is = getActivity().getAssets().open(filename);
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
            return new HashMap<>();
        }
    }
}
