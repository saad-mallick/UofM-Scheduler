package saadandaakash.uofmscheduler.Fragments;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import saadandaakash.uofmscheduler.R;
import saadandaakash.uofmscheduler.Utitilies.Utility;

public class SectionInfoFragment extends Fragment {
    private String termCode;
    private String subjectCode;
    private String catalogNumber;
    private String sectionNumber;

    private Map<String, String> sectionDetails;
    private ArrayList<Meeting> meetings = new ArrayList<>();

    private final ArrayList<String> keys = new ArrayList<>(Arrays.asList(
            "SectionType", "CourseTitle", "AvailableSeats",
            "EnrollmentTotal", "CourseDescr", "CreditHours", "ClassTopic"));

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

                        // [SUBJECT] [CATALOG NUM]: Section [SECTION NUM]
                        // e.g. IB 101: Section 001
                        TextView sectionTitle = (TextView) getView().findViewById(R.id.sectionTitle);
                        String title = subjectCode + " " + catalogNumber +
                                ": Section " + sectionNumber;
                        sectionTitle.setText(title);

                        // [CLASS TOPIC]
                        // e.g. Possible Ways to get Penalized on the Extended Essay
                        TextView classTopic = (TextView) getView().findViewById(R.id.classTopic);
                        String topic = sectionDetails.get("ClassTopic");
                        if (topic != null && !topic.equals("")) {
                            classTopic.setText(topic);
                        } else {
                            topic = sectionDetails.get("CourseTitle");
                            classTopic.setText(topic);
                        }

                        // [LEC/DIS/LAB/REC/SEM]
                        // e.g. DIS
                        TextView sectionType = (TextView) getView().findViewById(R.id.sectionType);
                        sectionType.setText(sectionDetails.get("SectionType"));

                        // Available Seats: [AVAILABLE] / [TOTAL]
                        // e.g. Available Seats: 41 / 42
                        TextView availableSeats = (TextView) getView().findViewById(R.id.availableSeats);
                        String availableSeats_str = "Available Seats: " + sectionDetails.get("AvailableSeats") +
                                " / " + sectionDetails.get("EnrollmentTotal");
                        availableSeats.setText(availableSeats_str);

                        // Credits: [CREDITS]
                        // e.g. Credits: 2
                        final TextView courseDescriptionHeader = (TextView) getView().
                                findViewById(R.id.courseDescriptionHeader);
                        courseDescriptionHeader.setText("Course Description");
                        courseDescriptionHeader.setCompoundDrawablesWithIntrinsicBounds(
                                0, 0, R.drawable.expand, 0);

                        final TextView courseDescription = (TextView) getView().findViewById(R.id.courseDescription);
                        String description = sectionDetails.get("CourseDescr").
                                replaceAll("\\u2019", "'");
                        courseDescription.setText(description);

                        courseDescriptionHeader.setOnClickListener(
                                new View.OnClickListener() {
                                    public void onClick(View view) {
                                        if (courseDescription.getVisibility() == View.VISIBLE) {
                                            courseDescription.setVisibility(View.GONE);
                                            courseDescriptionHeader.setCompoundDrawablesWithIntrinsicBounds(
                                                    0, 0, R.drawable.expand, 0);
                                        } else if (courseDescription.getVisibility() == View.GONE) {
                                            courseDescription.setVisibility(View.VISIBLE);
                                            courseDescriptionHeader.setCompoundDrawablesWithIntrinsicBounds(
                                                    0, 0, R.drawable.collapse, 0);
                                        }
                                    }
                                }
                        );

                        // Meetings
                        // We can't use a ListView in a ScrollView, so instead we are going to
                        // start with an empty linear layout, and keep creating new views per meeting
                        // object and add them to the linear layout
                        LinearLayout layout = (LinearLayout) getView().findViewById(R.id.meetings);

                        TextView meetingsHeader = (TextView) getView().findViewById(R.id.meetings_header);
                        meetingsHeader.setText("Meetings");

                        // go through each meeting object and create a new view with the
                        // fields filled in
                        for (int position = 0; position < meetings.size(); position++) {
                            LayoutInflater inflater = getActivity().getLayoutInflater();
                            View rowView = inflater.inflate(R.layout.meeting_details, null, true);

                            // Alternate background colors
                            if (position % 2 == 0) {
                                rowView.setBackgroundColor(getResources().getColor(R.color.lightGray));
                            }
                            /*
                                Days: [Days]
                                Times: [Times]
                                Instructor(s): [Instructors]
                                Location: [Location]
                            */
                            Meeting currentMeeting = meetings.get(position);

                            // display days
                            TextView display_days = (TextView) rowView.findViewById(R.id.days);
                            String days = "Days:  " + currentMeeting.days;
                            display_days.setText(days);

                            // display times
                            TextView display_times = (TextView) rowView.findViewById(R.id.times);
                            String times = "Times: " + currentMeeting.times;
                            display_times.setText(times);

                            // display instructors
                            TextView display_instructors = (TextView) rowView.findViewById(R.id.instructors);
                            // join instructors array into form Instructors: [Instructor 1], [Instructor 2], ...
                            String instructors = "Instructors: " + TextUtils.join(", ", currentMeeting.instructors);
                            display_instructors.setText(instructors);

                            // display location
                            TextView display_location = (TextView) rowView.findViewById(R.id.location);
                            String location = "Location: " + currentMeeting.location;
                            display_location.setText(location);

                            // add the newly created view to the end of the linear layout
                            layout.addView(rowView);
                        }

                        // Save Button
                        Button saveButton = (Button) getView().findViewById(R.id.saveButton);
                        saveButton.setTypeface(Typeface.createFromAsset(
                                getActivity().getAssets(),
                                "fonts/Quicksand-Regular.otf")
                        );
                        // this is so the button doesn't show up before the other info
                        saveButton.setVisibility(View.VISIBLE);
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
                // keeps instructors separate in case we need them individually later
                JSONArray instructorsArray = meeting.getJSONArray("Instructor");
                String[] instructors = new String[instructorsArray.length()];
                for (int j = 0; j < instructorsArray.length(); j++) {
                    instructors[j] = instructorsArray.getString(j);
                }

                // add new Meeting object to official arraylist
                /* e.g. Days: Mo Tu We Th Fr
                        Times: whenever we have math lol
                        Instructors: Charlie Jones
                        Location: DLL
                 */
                meetings.add(new Meeting(
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

    // Meeting class holds basic meeting info
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

}
