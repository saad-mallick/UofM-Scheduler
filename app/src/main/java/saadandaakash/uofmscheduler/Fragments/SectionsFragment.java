package saadandaakash.uofmscheduler.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saadandaakash.uofmscheduler.R;
import saadandaakash.uofmscheduler.Utility;

/**
 * Created by Saad on 12/26/2017.
 */

/*
 * TODO: make back button go to previous fragment, not just close app
 * TODO: also make another class for helper functions like close keyboard
 */

public class SectionsFragment extends ListFragment{

    //EECS 280, EECS 203 etc.
    private String termCode = "2170",
            schoolCode = null,
            subjectCode = null,
            catalog_number = null;
    private ArrayList<Section> sections = new ArrayList<>();

    //You are not allowed to make constructors in Fragments, so this is what is typically done
    public static SectionsFragment newInstance(String schoolCode, String subjectCode, String catalog_number){
        SectionsFragment fragment = new SectionsFragment();
        fragment.catalog_number = catalog_number;
        fragment.schoolCode = schoolCode;
        fragment.subjectCode = subjectCode;

        return fragment;
    }

    @Override
    //EFFECTS: Sets up the list with the custom adapter
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        //This will get the sections and fill the sections arraylist with the correct data
        getSections();

        //This will set our CustomAdapter
        SectionsFragment.CustomAdapter adapter = new SectionsFragment.CustomAdapter(getActivity(), sections);

        //And the list will be made through that adapter
        setListAdapter(adapter);
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
                Section section = new Section();

                // populate section information from JSON object
                section.availableSeats = infoObject.getInt("AvailableSeats");
                section.classTopic = infoObject.getString("ClassTopic");
                section.creditHours = infoObject.getInt("CreditHours");
                section.enrollmentTotal = infoObject.getInt("EnrollmentTotal");
                section.sectionNumber = infoObject.getString("SectionNumber");
                section.sectionType = infoObject.getString("SectionType");

                JSONObject meetingObject = infoObject.getJSONArray("Meetings").getJSONObject(0);
                section.meetings.days = meetingObject.getString("Days");
                section.meetings.times = meetingObject.getString("Times");

                sections.add(section);
                System.out.println(section.toString());

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
            this.sections = sections;
            this.context = context;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent){
            // TODO: figure out what 'View Holder' pattern is
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.sections_fragment_layout, null,true);

            /*
             * Section [Num] ([LEC/LAB/DIS])
             * [Days] [Times]
             * [Available seats] / [Total seats]
             */
            Section currentSection = sections.get(position);
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

    private class Section {
        private class Meetings {
            String days;
            String times;
        }

        private String classTopic, sectionType, sectionNumber;
        private int creditHours, enrollmentTotal, availableSeats;
        private Meetings meetings = new Meetings();

    }
}
