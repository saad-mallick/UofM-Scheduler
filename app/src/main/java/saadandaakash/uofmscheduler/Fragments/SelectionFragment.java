package saadandaakash.uofmscheduler.Fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.os.StrictMode;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.json.*;

import saadandaakash.uofmscheduler.Adapters.AutocompleteAdapter;
import saadandaakash.uofmscheduler.Fragments.CoursesFragment;
import saadandaakash.uofmscheduler.R;
import saadandaakash.uofmscheduler.Utility;

/**
 * Created by Aakash on 12/22/2017.
 * Aakash, this is your fragment
 * Follow the directions below on how to change between fragments
 */

public class SelectionFragment extends Fragment {

    private String termCode = "2170";
    private String schoolCode = null;
    private String subjectCode = null;
    private String catalogNum = null;

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


            // set font for button and input fields
            Typeface t = Typeface.createFromAsset(getActivity().getAssets(),
                    "fonts/Quicksand-Regular.otf");
            submitButton.setTypeface(t);
            editSchool.setTypeface(t);
            editSubject.setTypeface(t);

            // create map of schools and their codes
            schoolData = Utility.readMapFromFile(
                    getActivity(),
                    "schools.json",
                    "SchoolDescr",
                    "SchoolCode");
            final Set<String> SCHOOLS = schoolData.keySet();
            final ArrayList<String> SCHOOLSList = new ArrayList<String>();
            SCHOOLSList.addAll(SCHOOLS);
            // create array adapter for school input field with school names
            final AutocompleteAdapter school_adapter = new AutocompleteAdapter(getActivity(),
                    SCHOOLSList);
            editSchool.setAdapter(school_adapter);
            editSchool.setThreshold(1);

            // create array adapter for subject field, currently empty
            final AutocompleteAdapter subject_adapter = new AutocompleteAdapter(getActivity(), new ArrayList<String>());
            editSubject.setAdapter(subject_adapter);
            editSubject.setThreshold(1);


            submitButton.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View view) {

                            // get school and subject values
                            schoolCode = schoolData.get(editSchool.getText().toString());
                            subjectCode = editSubject.getText().toString();

                            // check to make sure something was entered before switching fragments
                            if (schoolCode != null && !schoolCode.trim().isEmpty() &&
                                    subjectCode != null && !subjectCode.trim().isEmpty()) {
                                CoursesFragment fragment = CoursesFragment.newInstance(schoolCode, subjectCode);
                                Utility.hideKeyboard(getActivity());

                                FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.container, fragment)
                                        .commit();
                            }
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
                        System.out.println("Input school: " + school);

                        if (!school.trim().isEmpty()) {
                            schoolCode = schoolData.get(school);
                            System.out.println(schoolCode);
                        }
                        // check if the school entered is valid
                        if (schoolCode != null) {
                            try {
                                subject_adapter.clear();
                                subjectData = Utility.readMapFromFile(
                                        getActivity(),
                                        schoolCode + ".json",
                                        "SubjectDescr",
                                        "SubjectCode"
                                );

                                System.out.println(subjectData);

                                // add values to subject adapter in form
                                ArrayList<String> list = new ArrayList<>();
                                for (Map.Entry<String, String> entry : subjectData.entrySet()) {
                                    list.add(entry.getValue() + " - " + entry.getKey());
                                }
                                subject_adapter.add_data(list);

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


}
