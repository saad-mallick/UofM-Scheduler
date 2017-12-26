package saadandaakash.uofmscheduler.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.json.*;

import saadandaakash.uofmscheduler.R;
import saadandaakash.uofmscheduler.Utility;

/**
 * Created by Saad on 12/22/2017.
 */

public class CoursesFragment extends ListFragment {

    private String schoolCode;
    private String subjectCode;
    private String termCode = "2170";

    //REQUIRES: A valid school code and subject code
    //EFFECTS: Creates a new instance of CoursesFragment with the correct schoolCode and subjectCode
    //         and returns it. Use instead of constructor
    public static CoursesFragment newInstance(String schoolCode, String subjectCode){
        CoursesFragment coursesFragment = new CoursesFragment();
        coursesFragment.schoolCode = schoolCode;
        coursesFragment.subjectCode = subjectCode;
        return coursesFragment;
    }

    @Override
    //EFFECTS: Sets up the list with the custom adapter
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        CustomAdapter adapter = new CustomAdapter(getActivity(), getCourses());

        /*
         * TODO: top margin of course list is too small, needs to be moved down
         */

        setListAdapter(adapter);

    }

    //EFFECTS: Fills the courses array list with correct data
    public ArrayList<Course> getCourses(){

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        String url = "http://umich-schedule-api.herokuapp.com/v4/get_catalog_numbers?" +
                "term_code=" + termCode +
                "&school=" + schoolCode +
                "&subject=" + subjectCode;

        ArrayList<Course> courses = new ArrayList<>();
        try {
            JSONArray infoFromAPI = Utility.getJSONArray(url);
            // go through course info array and find open sections
            for (int i = 0; i < infoFromAPI.length(); i++) {
                JSONObject infoObject = infoFromAPI.getJSONObject(i);
                courses.add(new Course(infoObject.getString("CourseTitle"),
                                infoObject.getString("CatalogNumber")));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return courses;
    }

    //This CustomAdapter will store information about the school code and the subject code
    //and display them. The layout is specified in courses_fragment_sectional_layout
    //I chose to make this a custom adapter in case we decide to add images or add things like
    //"fills X LSA preq" or something along those lines
    private class CustomAdapter extends ArrayAdapter {

        private final Activity context;
        private final ArrayList<Course> courses;

        public CustomAdapter(Activity context, ArrayList<Course> courses) {
            super(context, R.layout.courses_fragment_sectional_layout, courses);

            this.context = context;
            this.courses = courses;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent){
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.courses_fragment_sectional_layout, null,true);

            TextView courseName = (TextView) rowView.findViewById(R.id.name);

            Course currentCourse = courses.get(position);
            String printCourse = subjectCode + " " + currentCourse.catalogNumber + ": " +
                    currentCourse.courseName;
            courseName.setText(printCourse);

            View.OnClickListener clickListener = new View.OnClickListener() {
                public void onClick(View v) {
                    ClassFragment fragment = ClassFragment.newInstance("test");
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, fragment)
                            .commit();
                }
            };
            rowView.setOnClickListener(clickListener);
            return rowView;
        }
    }

    private class Course {
        private String courseName, catalogNumber;

        public Course(String courseName, String catalogNumber) {
            this.catalogNumber = catalogNumber;
            this.courseName = courseName;
        }
    }

}