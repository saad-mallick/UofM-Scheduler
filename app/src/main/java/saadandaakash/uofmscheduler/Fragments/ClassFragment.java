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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

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
    private String school;

    private String courseArea;
    private String courseNumber;
    private String courseTitle;
    private String courseDescription;
    private String courseRequirements;


    public static ClassFragment newInstance(String termCode, String courseArea, String courseNumber,
                                            String courseTitle, String school){
        ClassFragment fragment = new ClassFragment();
        fragment.termCode = termCode;
        fragment.school = school;
        fragment.courseArea = courseArea;
        fragment.courseNumber = courseNumber;
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
        getDescription();
        getRequirements();

        Typeface t = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Quicksand-Regular.otf");

        TextView courseLabel = (TextView)getView().findViewById(R.id.courseLabel);
        courseLabel.setText(courseArea + " " + courseNumber);
        courseLabel.setTypeface(t);

        TextView courseTitletv = (TextView)getView().findViewById(R.id.courseTite);
        courseTitletv.setText(courseTitle);
        courseTitletv.setTypeface(t);

        TextView des = (TextView)getView().findViewById(R.id.des);
        des.setText(courseDescription);
        des.setTypeface(t);

        TextView preqs = (TextView)getView().findViewById(R.id.preqs);
        preqs.setText(courseRequirements);
        preqs.setTypeface(t);

        Button chooseSections = (Button) getView().findViewById(R.id.chooseSections);

        chooseSections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SectionsFragment fragment = SectionsFragment.newInstance(school, courseArea, courseNumber);
                Utility.hideKeyboard(getActivity());

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
            }
        });
    }

    public void getDescription() {
        try {
            courseDescription = Utility.getStringFromURL("http://umich-schedule-api.herokuapp.com/v4/g" +
                    "et_course_description?term_code=" + termCode + "&school_code=" + school
                    + "&subject=" + courseArea + "&catalog_num=" + courseNumber);
        } catch (Exception e){}
    }
    public void getRequirements(){
        try {
            courseRequirements = Utility.getStringFromURL("http://umich-schedule-api.herokuapp.com/v4/" +
                    "get_additional_info?term_code=" + termCode + "&school_code=" + school
                    + "&subject=" + courseArea + "&catalog_num=" + courseNumber);
        } catch (Exception e){}
    }

}
