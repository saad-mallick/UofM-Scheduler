package saadandaakash.uofmscheduler.Fragments;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import saadandaakash.uofmscheduler.R;
import saadandaakash.uofmscheduler.Utility;

/**
 * Created by Saad on 12/26/2017.
 */

public class SectionInfoFragment extends Fragment {
    private String termCode;
    private String subjectCode;
    private String catalogNumber;
    private String sectionNumber;

    private Map<String, String> sectionDetails;
    private ArrayList<Meeting> meetings;

    private final ArrayList<String> keys = new ArrayList<>(Arrays.asList(
            "SectionType", "CourseTitle", "AvailableSeats",
            "EnrollmentTotal", "CourseDescr", "CreditHours" ));

    public static SectionInfoFragment newInstance(String termCode, String subjectCode, String catalogNumber,
                                            String sectionNumber) {
        SectionInfoFragment fragment = new SectionInfoFragment();
        fragment.termCode = termCode;
        fragment.subjectCode = subjectCode;
        fragment.catalogNumber = catalogNumber;
        fragment.sectionNumber = sectionNumber;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout into the specified container
        //True if you can inflate the layout and then attach it directly to the root of the
        //container
        //False if you want to inflate the layout and then return that View
        return inflater.inflate(R.layout.section_info_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                sectionDetails = getSectionDetails();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        TextView sectionTitle = (TextView) getView().findViewById(R.id.sectionTitle);
                        String title = subjectCode + " " + catalogNumber +
                                ": Section " + sectionNumber;
                        sectionTitle.setText(title);

                        TextView courseName = (TextView) getView().findViewById(R.id.sectionType);
                        courseName.setText(sectionDetails.get("SectionType"));

                        TextView availableSeats = (TextView) getView().findViewById(R.id.availableSeats);
                        String availableSeats_str = "Available Seats: " + sectionDetails.get("AvailableSeats") +
                                " / " + sectionDetails.get("EnrollmentTotal");
                        availableSeats.setText(availableSeats_str);

                        /*
                        TODO: Add rest of data to page
                         */
                    }
                });
            }

        }).start();
    }

    private Map<String, String> getSectionDetails() {
        Map<String, String> sectionInfo = new HashMap<>();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        String url = "http://umich-schedule-api.herokuapp.com/v4/get_section_details?" +
                "term_code=" + termCode +
                "&subject=" + subjectCode +
                "&catalog_num=" + catalogNumber +
                "&section_num=" + sectionNumber;

        try {
            JSONObject section_info_json = Utility.getJSONObject(url);

            // get normal information (keys in arraylist)
            for (String key : keys) {
                sectionInfo.put(key, section_info_json.getString(key));
            }

            // get meeting array from JSON object
            JSONArray meetingsArray = section_info_json.getJSONArray("Meetings");

            // go through each meeting in array
            for (int i = 0; i < meetingsArray.length(); i++) {
                JSONObject meeting = meetingsArray.getJSONObject(i);

                // get instructors array for each meeting
                JSONArray instructorsArray = meeting.getJSONArray("Instructors");
                String[] instructors = new String[instructorsArray.length()];
                for (int j = 0; j < instructorsArray.length(); j++) {
                    instructors[j] = instructorsArray.getString(j);
                }

                // add new Meeting object to official arraylist
                meetings.add( new Meeting(
                        meeting.getString("Days"),
                        meeting.getString("Times"),
                        instructors,
                        meeting.getString("Location")

                ));
            }


        } catch (Exception e) {
            System.out.println("API ERROR: Course information not found");
            e.printStackTrace();
        }
        return sectionInfo;
    }

    private class Meeting {
        private String days, times;
        private String[] instructors;
        private String location;

        public Meeting(String days, String times, String[] instructors, String location) {
            this.days = days;
            this.times = times;
            this.instructors = instructors;
            this.location = location;
        }
    }

    /*
    private class CustomAdapter extends ArrayAdapter {
        ArrayList<Section> sections;
        Activity context;
        private ViewHolder viewHolder;

        public CustomAdapter(Activity context, ArrayList<Section> sections) {
            super(context, R.layout.courses_fragment_sectional_layout, sections);
            sections.add(new Section("", "", "", 0, 0, 0, "", ""));
            notifyDataSetChanged();
            this.sections = sections;
            this.context = context;
            viewHolder = new ViewHolder(subjectCode, catalog_number, courseTitle,
                    getDescription(), getRequirements(), context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            if (position == 0) {
                return viewHolder.getClassView();
            }
            else {
                View rowView = inflater.inflate(R.layout.sections_fragment_layout, null, true);

                /*
                 * Section [Num] ([LEC/LAB/DIS/REC])
                 * Credits: [Credits]
                 * [Days] [Times]
                 * [Available seats] / [Total seats]
                 *//*
                Section currentSection = sections.get(position - 1);
                TextView sectionTitle = (TextView) rowView.findViewById(R.id.sectionTitle);
                String title = "Section " + currentSection.sectionNumber +
                        " (" + currentSection.sectionType + ")";
                sectionTitle.setText(title);

                // only display credits if type is same as "main" (first) section
                if (currentSection.sectionType.equals(sections.get(0).sectionType)) {
                    TextView creditsInfo = (TextView) rowView.findViewById(R.id.creditsInfo);
                    String credits = "Credits: " + currentSection.creditHours;
                    creditsInfo.setText(credits);
                    creditsInfo.setVisibility(View.VISIBLE);
                }

                // display meetings times and days
                TextView meetingsInfo = (TextView) rowView.findViewById(R.id.meetingsInfo);
                String meetings = currentSection.meetings.days + " " + currentSection.meetings.times;
                meetingsInfo.setText(meetings);

                // display open seats and total
                TextView enrollmentInfo = (TextView) rowView.findViewById(R.id.enrollmentInfo);
                String enrollment = "Available Seats: " + currentSection.availableSeats + " / " +
                        currentSection.enrollmentTotal;
                enrollmentInfo.setText(enrollment);

                return rowView;
            }
        }
    }*/

    /*
    private class SectionInfo {

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

        public SectionDetails(String classTopic, String sectionType, String sectionNumber,
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
    */

    /*
    private static class ViewHolder {

        String subjectCode;
        String catalog_number;
        String courseTitle;
        String courseDescription;
        String courseRequirements;

        Activity context;
        LayoutInflater inflater;

        View classView = null;

        public ViewHolder(String subjectCode, String catalog_number, String courseTitle,
                          String courseDescription, String courseRequirements, Activity context) {
            this.subjectCode = subjectCode;
            this.catalog_number = catalog_number;
            this.courseTitle = courseTitle;
            this.courseDescription = courseDescription;
            this.courseRequirements = courseRequirements;
            this.context = context;
            inflater = context.getLayoutInflater();
        }

        public View getClassView() {
            if (classView == null) {
                classView = inflater.inflate(R.layout.class_fragment_sectional_layout_left_align, null, false);

                customTextView courseLabel = (customTextView) classView.findViewById(R.id.courseLabel);
                String label = subjectCode + " " + catalog_number;
                courseLabel.setText(label);

                customTextView courseTitleText = (customTextView) classView.findViewById(R.id.courseTitle);
                courseTitleText.setText(courseTitle);

                customTextView description = (customTextView) classView.findViewById(R.id.description);
                description.setText(courseDescription);

                customTextView requirements = (customTextView) classView.findViewById(R.id.prereqs);
                requirements.setText(courseRequirements);

                return classView;
            } else {
                return classView;
            }
        }

    }
    */
}
