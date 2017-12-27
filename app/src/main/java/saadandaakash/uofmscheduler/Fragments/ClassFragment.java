package saadandaakash.uofmscheduler.Fragments;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saadandaakash.uofmscheduler.R;
import saadandaakash.uofmscheduler.Utility;
import saadandaakash.uofmscheduler.customTextView;

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
    private ArrayList<Section> sections = new ArrayList<>();

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

        getSections();

        ListView list = (ListView)getView().findViewById(R.id.sectionsList);

        CustomAdapter adapter = new CustomAdapter(getActivity(), sections);
        list.setAdapter(adapter);

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

    //EFFECTS: Will fill the ArrayList with the correct data about the sections
    //         It is up to you if you want to use an ArrayList or not. However, I recommend that
    //         you stick with ArrayList and make the type a triple or a pair if you want to store
    //         data with more than one attribute
    private void getSections(){
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        String url = "http://umich-schedule-api.herokuapp.com/v4/get_sections?" +
                "term_code=" + termCode +
                "&school=" + schoolCode +
                "&subject=" + subjectCode +
                "&catalog_num=" + catalog_number;

        try {
            JSONArray infoFromAPI = Utility.getJSONArray(url);
            // go through course info array and find open sections
            for (int i = 0; i < infoFromAPI.length(); i++) {
                JSONObject infoObject = infoFromAPI.getJSONObject(i);
                // TODO: Adapt this to allow for any size meetings array
                JSONObject meetingObject = infoObject.getJSONArray("Meetings").
                        getJSONObject(0);

                // create section object with info from JSON, add to list
                sections.add(
                        new Section(
                                infoObject.getString("ClassTopic"),
                                infoObject.getString("SectionType"),
                                infoObject.getString("SectionNumber"),
                                infoObject.getInt("CreditHours"),
                                infoObject.getInt("EnrollmentTotal"),
                                infoObject.getInt("AvailableSeats"),
                                meetingObject.getString("Days"),
                                meetingObject.getString("Times")
                        )
                );

            }
        } catch (Exception e){
            System.out.println("API ERROR: Course information not found");
            e.printStackTrace();
        }
    }

    private class CustomAdapter extends ArrayAdapter {
        ArrayList<Section> sections;
        Activity context;

        public CustomAdapter(Activity context, ArrayList<Section> sections){
            super(context, R.layout.courses_fragment_sectional_layout, sections);
            sections.add(new Section("", "", "", 0, 0, 0, "", ""));
            notifyDataSetChanged();
            this.sections = sections;
            this.context = context;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent){
            // TODO: figure out what 'View Holder' pattern is
            LayoutInflater inflater = context.getLayoutInflater();
            if(position == 0){
                View classView = inflater.inflate(R.layout.class_fragment_sectional_layout, null, false);

                customTextView courseLabel = (customTextView)classView.findViewById(R.id.courseLabel);
                String label = subjectCode + " " + catalog_number;
                courseLabel.setText(label);

                customTextView courseTitleText = (customTextView)classView.findViewById(R.id.courseTite);
                courseTitleText.setText(courseTitle);

                customTextView description = (customTextView)classView.findViewById(R.id.des);
                description.setText(getDescription());

                customTextView requirements = (customTextView)classView.findViewById(R.id.preqs);
                requirements.setText(getRequirements());

                return classView;
            } else {
                View rowView = inflater.inflate(R.layout.sections_fragment_layout, null, true);

            /*
             * Section [Num] ([LEC/LAB/DIS])
             * [Days] [Times]
             * [Available seats] / [Total seats]
             */
                Section currentSection = sections.get(position-1);
                TextView sectionTitle = (TextView) rowView.findViewById(R.id.sectionTitle);
                String title = "Section " + currentSection.sectionNumber +
                        " (" + currentSection.sectionType + ")";
                sectionTitle.setText(title);

                TextView meetingsInfo = (TextView) rowView.findViewById(R.id.meetingsInfo);
                String meetings = currentSection.meetings.days + " " + currentSection.meetings.times;
                meetingsInfo.setText(meetings);

                TextView enrollmentInfo = (TextView) rowView.findViewById(R.id.enrollmentInfo);
                String enrollment = "Available Seats: " + currentSection.availableSeats + " / " +
                        currentSection.enrollmentTotal;
                enrollmentInfo.setText(enrollment);

                return rowView;
            }
        }
    }

    private class Section {

        private class Meetings {
            String days;
            String times;

            public Meetings(String days, String times) {
                this.days = days;
                this.times = times;
            }
        }

        private String classTopic, sectionType, sectionNumber;
        private int creditHours, enrollmentTotal, availableSeats;
        private Meetings meetings;

        public Section(String classTopic, String sectionType, String sectionNumber,
                       int creditHours, int enrollmentTotal, int availableSeats,
                       String days, String times) {
            this.classTopic = classTopic;
            this.sectionType = sectionType;
            this.sectionNumber = sectionNumber;
            this.creditHours = creditHours;
            this.enrollmentTotal = enrollmentTotal;
            this.availableSeats = availableSeats;

            this.meetings = new Meetings(days, times);
        }

    }

}
