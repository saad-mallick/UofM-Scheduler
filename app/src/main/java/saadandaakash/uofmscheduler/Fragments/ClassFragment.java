package saadandaakash.uofmscheduler.Fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import saadandaakash.uofmscheduler.R;
import saadandaakash.uofmscheduler.Utility;

/**
 * Created by Saad on 12/26/2017.
 */

public class ClassFragment extends Fragment {
    /*
    * TODO: Add a back button, so users can navigate back to the original list
    *
    * */

    private String termCode;
    private String schoolCode;
    private String subjectCode;
    private String catalog_number;
    private String courseTitle;

    public static ClassFragment newInstance(String termCode, String courseArea, String courseNumber,
                                            String courseTitle, String school){
        ClassFragment fragment = new ClassFragment();
        fragment.termCode = termCode;
        fragment.schoolCode = school;
        fragment.subjectCode = courseArea;
        fragment.catalog_number = courseNumber;
        fragment.courseTitle = courseTitle;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        //Inflate the layout into the specified container
        //True if you can inflate the layout and then attach it directly to the root of the
        //container
        //False if you want to inflate the layout and then return that View
        return inflater.inflate(R.layout.class_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        TextView courseLabel = (TextView)getView().findViewById(R.id.courseLabel);
        String label = subjectCode + " " + catalog_number;
        courseLabel.setText(label);

        TextView courseTitleText = (TextView)getView().findViewById(R.id.courseTite);
        courseTitleText.setText(courseTitle);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String description = getDescription();
                final String requirements = getRequirements();
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView descr = (TextView) getView().findViewById(R.id.des);
                            TextView req = (TextView) getView().findViewById(R.id.preqs);
                            descr.setText(description);
                            req.setText(requirements);

                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        /*
        TextView description = (TextView)getView().findViewById(R.id.des);
        description.setText(getDescription());

        TextView requirements = (TextView)getView().findViewById(R.id.preqs);
        requirements.setText(getRequirements());
        */

        Button chooseSections = (Button) getView().findViewById(R.id.chooseSections);

        chooseSections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SectionsFragment fragment = SectionsFragment.newInstance(schoolCode, subjectCode, catalog_number);
                Utility.hideKeyboard(getActivity());

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
            }
        });
    }

    public String getDescription() {

        String url = "http://umich-schedule-api.herokuapp.com/v4/g" +
                "et_course_description?term_code=" + termCode + "&school_code=" + schoolCode
                + "&subject=" + subjectCode + "&catalog_num=" + catalog_number;
        try {
            return Utility.getStringFromURL(url);
        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }
    public String getRequirements(){
        String url = "http://umich-schedule-api.herokuapp.com/v4/" +
                "get_additional_info?term_code=" + termCode + "&school_code=" + schoolCode
                + "&subject=" + subjectCode + "&catalog_num=" + catalog_number;
        try {
            return Utility.getStringFromURL(url);
        }
        catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

}
