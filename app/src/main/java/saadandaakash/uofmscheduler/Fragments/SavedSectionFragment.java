package saadandaakash.uofmscheduler.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import android.support.v4.app.Fragment;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import saadandaakash.uofmscheduler.R;

/**
 * Created by Aakash on 12/28/2017.
 */


public class SavedSectionFragment extends Fragment {

    //This is the .txt file that will store out information
    private static final String FILENAME = "SavedInformation.txt";

    //Index 1 of this list will correspond to the line 1 of the file. When we delete line 1 of
    //sections, we will also remove it from the file too.
    private ArrayList<String> sections;

    public static SavedSectionFragment newInstance() {
        SavedSectionFragment fragment = new SavedSectionFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout into the specified container
        //True if you can inflate the layout and then attach it directly to the root of the
        //container
        //False if you want to inflate the layout and then return that View
        return inflater.inflate(R.layout.saved_sections_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        ArrayList<Section> sections = new ArrayList<>();
        sections.add(new Section("EECS", "280", "001", "LEC",
                "Mo We", "8:00-9:30"));
        sections.add(new Section("ENGLISH", "140", "001", "SEM",
                "Mo We", "2:00-3:00"));
        sections.add(new Section("ENGLISH", "140", "003", "SEM",
                "Tu Th", "9:00-10:00"));

        SavedSectionAdapter adapter = new SavedSectionAdapter(getActivity(), sections);
        ListView sectionsList = (ListView) getView().findViewById(R.id.savedSectionsList);
        sectionsList.setAdapter(adapter);

        /*
        TODO: Read info from saved course file into adapter, set adapter to list
         */

    }

    // Adapter for list view that will display meetings
    private class SavedSectionAdapter extends ArrayAdapter {
        ArrayList<Section> savedSections;
        Activity context;
        //private RecyclerView.ViewHolder viewHolder;

        public SavedSectionAdapter(Activity context, ArrayList<Section> savedSections) {
            super(context, R.layout.saved_sections_fragment, savedSections);
            this.savedSections = savedSections;
            this.context = context;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.saved_sections, null, true);

            // makes every other meeting light gray background to distinguish
            if (position % 2 == 0) {
                rowView.setBackgroundColor(getResources().getColor(R.color.lightGray));
            }

            /*
            TODO: Go through list of sections and display the relevant info
             */
            final Section current_section = savedSections.get(position);

            TextView header = (TextView) rowView.findViewById(R.id.sectionTitle);
            String title = current_section.subjectCode +
                    " " + current_section.catalogNumber +
                    " Section " + current_section.sectionNumber +
                    " (" + current_section.sectionType + ")";
            header.setText(title);

            TextView meetingInfo = (TextView) rowView.findViewById(R.id.meetingInfo);
            String meetingDisplay = current_section.meeting.days +
                    " " + current_section.meeting.times;
            meetingInfo.setText(meetingDisplay);

            /*
            TODO: Set onClickListener to go to that section details page for each saved section
             */

            View.OnClickListener clickListener = new View.OnClickListener() {
                public void onClick(View v) {
                    SectionInfoFragment fragment = SectionInfoFragment.newInstance("2170",
                            current_section.subjectCode, current_section.catalogNumber,
                            current_section.sectionNumber);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, fragment)
                            .addToBackStack("SECTIONINFO FRAGMENT")
                            .commit();
                }
            };
            rowView.setOnClickListener(clickListener);

            return rowView;
        }
    }

    public static void save(String string, Activity currentActivity){
        try {
            FileOutputStream fos = currentActivity.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private class Section {

        // Meeting class holds basic meeting info
        private class Meeting {
            private String days, times;

            public Meeting(String days, String times) {
                this.days = days;
                this.times = times;
            }
        }

        private String subjectCode, catalogNumber, sectionNumber, sectionType;
        private Meeting meeting;

        public Section(String subjectCode, String catalogNumber, String sectionNumber, String sectionType,
                       String days, String times) {
            this.subjectCode = subjectCode;
            this.catalogNumber = catalogNumber;
            this.sectionNumber = sectionNumber;
            this.sectionType = sectionType;

            this.meeting = new Meeting(days, times);
        }
    }
}
