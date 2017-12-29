package saadandaakash.uofmscheduler.Fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import saadandaakash.uofmscheduler.R;
import saadandaakash.uofmscheduler.Utitilies.Section;
import saadandaakash.uofmscheduler.Utitilies.Utility;

public class SectionDetailsFragment extends Fragment {

    private Section section;

    private final ArrayList<String> keys = new ArrayList<>(Arrays.asList(
            "SectionType", "CourseTitle", "AvailableSeats",
            "EnrollmentCapacity", "CourseDescr", "CreditHours", "ClassTopic"));

    public static SectionDetailsFragment newInstance(Section section) {
        SectionDetailsFragment fragment = new SectionDetailsFragment();
        fragment.section = section;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ProgressDialog dialog = Utility.createProgressDialog(getActivity());

        new Thread(new Runnable() {
            @Override
            public void run() {
                // update section with additional details
                getSectionDetails();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        displayHeader();
                        displayCourseDescription();
                        displayMeetings();
                        displaySaveButton();

                    }
                });
                dialog.dismiss();
            }

        }).start();
    }

    // REQUIRES: section is initialized
    // MODIFIES: section
    // EFFECTS: adds additional parameters to section, including updated meeting list
    private void getSectionDetails() {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        String url = "http://umich-schedule-api.herokuapp.com/v4/get_section_details?" +
                "term_code=" + Utility.TERMCODE +
                "&subject=" + section.subjectCode +
                "&catalog_num=" + section.catalogNumber +
                "&section_num=" + section.sectionNumber;

        try {
            JSONObject section_details = Utility.getJSONObject(url);

            section.classTopic = section_details.getString("ClassTopic");
            section.courseDescr = section_details.getString("CourseDescr");
            section.courseTitle = section_details.getString("CourseTitle");
            section.availableSeats = section_details.getString("AvailableSeats");
            section.enrollmentCapacity = section_details.getString("EnrollmentCapacity");

            // get meeting array from JSON object
            JSONArray meetingsArray = section_details.getJSONArray("Meetings");
            ArrayList<Section.Meeting> meetings_additional_info = new ArrayList<>();

            for (int i = 0; i < meetingsArray.length(); i++) {
                JSONObject meeting = meetingsArray.getJSONObject(i);
                meetings_additional_info.add(new Section.Meeting(meeting));
            }
            section.meetings = meetings_additional_info;

        } catch (Exception e) {
            System.out.println("API ERROR: Course information not found");
            e.printStackTrace();
        }
    }

    // REQUIRES: sectionDetails has been initialized
    // EFFECTS: displays subject code, catalog number, section number, class topic,
    //          section type, and available seats
    private void displayHeader() {
        // [SUBJECT] [CATALOG NUM]: Section [SECTION NUM]
        // e.g. IB 101: Section 001
        TextView sectionTitle = (TextView) getView().findViewById(R.id.sectionTitle);
        String title = section.subjectCode + " " + section.catalogNumber +
                ": Section " + section.sectionNumber;
        sectionTitle.setText(title);

        // [CLASS TOPIC]
        // e.g. Possible Ways to get Penalized on the Extended Essay
        TextView classTopic = (TextView) getView().findViewById(R.id.classTopic);
        String topic = section.classTopic;
        if (topic != null && !topic.equals("")) {
            classTopic.setText(topic);
        } else {
            topic = section.courseTitle;
            classTopic.setText(topic);
        }

        // [LEC/DIS/LAB/REC/SEM]
        // e.g. DIS
        TextView sectionType = (TextView) getView().findViewById(R.id.sectionType);
        sectionType.setText(section.sectionType);

        // Available Seats: [AVAILABLE] / [TOTAL]
        // e.g. Available Seats: 41 / 42
        TextView availableSeats = (TextView) getView().findViewById(R.id.availableSeats);
        String availableSeats_str = "Available Seats: " + section.availableSeats +
                " / " + section.enrollmentCapacity;
        availableSeats.setText(availableSeats_str);
    }

    // REQUIRES: sectionDetails has been initialized
    // EFFECTS: displays course description header, course description
    //          sets on click listener for course description header
    private void displayCourseDescription() {
        final TextView courseDescriptionHeader = (TextView) getView().
                findViewById(R.id.courseDescriptionHeader);
        courseDescriptionHeader.setText("Course Description");
        courseDescriptionHeader.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, R.drawable.expand, 0);

        final TextView courseDescription = (TextView) getView().findViewById(R.id.courseDescription);
        String description = section.courseDescr.replaceAll("\\u2019", "'");
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
    }

    // REQUIRES: sectionDetails has been initialized
    // EFFECTS: displays meetings header, meetings
    private void displayMeetings() {
        // Meetings
        // We can't use a ListView in a ScrollView, so instead we are going to
        // start with an empty linear layout, and keep creating new views per meeting
        // object and add them to the linear layout
        LinearLayout layout = (LinearLayout) getView().findViewById(R.id.meetings);

        TextView meetingsHeader = (TextView) getView().findViewById(R.id.meetings_header);
        meetingsHeader.setText("Meetings");

        // go through each meeting object and create a new view with the
        // fields filled in
        for (int position = 0; position < section.meetings.size(); position++) {
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
            Section.Meeting currentMeeting = section.meetings.get(position);

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
    }

    // REQUIRES: sectionDetails has been initialized
    // EFFECTS: displays save button, sets on click listener for save button
    private void displaySaveButton() {
        // Save Button
        Button saveButton = (Button) getView().findViewById(R.id.saveButton);
        saveButton.setTypeface(Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/Quicksand-Regular.otf")
        );
        // this is so the button doesn't show up before the other info
        saveButton.setVisibility(View.VISIBLE);

        saveButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        try {
                            SavedSectionsFragment sectionFragment = SavedSectionsFragment.newInstance();
                            sectionFragment.saveSection(section, getActivity());
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("OTHER ERROR OCCURRED");
                        }

                    }
                }
        );
    }
}
