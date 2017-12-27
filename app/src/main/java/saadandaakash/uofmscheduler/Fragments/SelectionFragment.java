package saadandaakash.uofmscheduler.Fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AutoCompleteTextView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import saadandaakash.uofmscheduler.Adapters.AutocompleteAdapter;
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
    public AutoCompleteTextView editSubject;

    private static Map<String, String> schoolData;
    private static TreeMap<String, String> subjectData = new TreeMap<>();

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
            editSubject = (AutoCompleteTextView)getView().findViewById(R.id.enterSubject);


            // set font for button and input fields
            Typeface t = Typeface.createFromAsset(getActivity().getAssets(),
                    "fonts/Quicksand-Regular.otf");
            submitButton.setTypeface(t);
            editSubject.setTypeface(t);

            // create map of schools and their codes
            schoolData = Utility.readMapFromFile(
                    getActivity(),
                    "schools.json",
                    "SchoolCode",
                    "SchoolDescr");
            final Set<String> SCHOOLS = schoolData.keySet();

            // go through each school and get the subjects 
            for (String schoolCode : SCHOOLS) {
                // read subjects from file
                Map<String, String> subject = Utility.readMapFromFile(
                        getActivity(),
                        schoolCode + ".json",
                        "SubjectCode",
                        "SubjectDescr"
                );
                //System.out.println(subject);
                for (Map.Entry<String, String> entry : subject.entrySet()) {
                    subjectData.putAll(subject);
                    //System.out.println(entry.getValue());
                }
            }
            ArrayList<String> subjects = new ArrayList<>();
            for (Map.Entry<String, String> entry : subjectData.entrySet()) {
                subjects.add(entry.getKey() + " - " + entry.getValue());
            }

            // create array adapter for subject field, currently empty
            // will be populated once the school is entered
            final AutocompleteAdapter subject_adapter = new AutocompleteAdapter(getActivity(), subjects);
            editSubject.setAdapter(subject_adapter);
            editSubject.setThreshold(1);


            // submit button clicked
            submitButton.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View view) {

                            // get school and subject values
                            subjectCode = editSubject.getText().toString();
                            subjectCode = subjectCode.substring(0, subjectCode.indexOf(' ')).toUpperCase();

                            System.out.println("SUBJECT: " + subjectCode);

                            // check to make sure something was entered before switching fragments
                            if (subjectCode != null && !subjectCode.trim().isEmpty()) {

                                CoursesFragment fragment = CoursesFragment.newInstance(subjectCode);
                                Utility.hideKeyboard(getActivity());

                                FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.container, fragment)
                                        .addToBackStack("SELECTION FRAGMENT")
                                        .commit();
                            }
                        }
                    }
            );

            /*
            // EFFECTS: when the focus moves from school field to subject field, populate
            //          the subject adapter with the school department names
            editSchool.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        subject_adapter.clear();
                        subject_adapter.notifyDataSetInvalidated();
                    }
                    if (!hasFocus) {
                        subject_adapter.clear();
                        subject_adapter.notifyDataSetInvalidated();

                        String school = editSchool.getText().toString();
                        schoolCode = schoolData.get(school);

                        // check if the school entered is valid
                        if (schoolCode != null) {
                            try {
                                ArrayList<String> data = updateAdapterList();
                                subject_adapter.add_data(data);

                                // reset adapter filter
                                subject_adapter.getFilter().filter(editSubject.getText(), null);
                                subject_adapter.notifyDataSetChanged();
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                subject_adapter.clear();
                            }
                        }
                    }
                }
            });
            */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
