package saadandaakash.uofmscheduler.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import saadandaakash.uofmscheduler.Adapters.ItemTouchHelperAdapter;
import saadandaakash.uofmscheduler.R;
import saadandaakash.uofmscheduler.Utitilies.Utility;

/**
 * Created by Aakash on 12/28/2017.
 */


public class SavedSectionFragment extends Fragment {

    public static ArrayList<Section> savedSections = null;
    public static ArrayList<String> sectionKeys;

    public static SavedSectionFragment newInstance() {
        SavedSectionFragment fragment = new SavedSectionFragment();
        sectionKeys = new ArrayList<String>();
        sectionKeys.add("subjectCode");
        sectionKeys.add("catalogNumber");
        sectionKeys.add("sectionNumber");
        sectionKeys.add("sectionType");
        sectionKeys.add("days");
        sectionKeys.add("times");
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

    public void saveSection(String subjectCode, String catalogNumber, String sectionNumber,
                            String sectionType, String days, String times, Activity activity){
        if(savedSections == null){
            savedSections = new ArrayList<Section>();
        }

        Section savedSection = new Section(subjectCode, catalogNumber, sectionNumber,
                sectionType, days, times);
        savedSections.add(savedSection);
        updateFile(activity);
    }

    public void updateFile(Activity activity){
        try {
            JSONArray jsonArray = new JSONArray();
            for (Section s : savedSections) {
                JSONObject object = new JSONObject();
                ArrayList<String> data = s.getItems();
                for (int i = 0; i < sectionKeys.size(); i++) {
                    object.put(sectionKeys.get(i), data.get(i));
                }
                jsonArray.put(object);
            }
            Utility.writeToFile(activity, jsonArray.toString(), Utility.FILENAME);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadFile(){
        if(savedSections == null) {
            savedSections = new ArrayList<>();
            try {
                String jsonArrayString = Utility.readFromFile(getActivity(), Utility.FILENAME);
                JSONArray jsonArray = new JSONArray(jsonArrayString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Section addSection = new Section(object.getString("subjectCode"), object.getString("catalogNumber"),
                            object.getString("sectionNumber"), object.getString("sectionType"), object.getString("days"),
                            object.getString("times"));
                    savedSections.add(addSection);
                }
            } catch (Exception e) {}
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        loadFile();

        RecyclerAdapter adapter = new RecyclerAdapter(getActivity(), savedSections);

        final RecyclerView sectionsList = (RecyclerView) getView().findViewById(R.id.savedSectionsList);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(sectionsList);

        sectionsList.setLayoutManager(new LinearLayoutManager(getContext()));
        sectionsList.setAdapter(adapter);

    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>
            implements ItemTouchHelperAdapter {

        private ArrayList<Section> savedSections;
        private Activity context;
        RecyclerView parentRecyclerView;

        public RecyclerAdapter(Activity context, ArrayList<Section> savedSections) {
            this.context = context;
            this.savedSections = savedSections;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View itemLayoutView = context.getLayoutInflater().
                    inflate(R.layout.saved_sections, parent, false);

            // create ViewHolder
            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {

            // - get data from your view at this position
            final Section current_section = savedSections.get(position);

            // makes every other meeting light gray background to distinguish
            if (position % 2 == 0) {
                viewHolder.rowView.setBackgroundColor(getResources().getColor(R.color.lightGray));
            }

            // - replace the contents of the view with that view
            String title = current_section.subjectCode +
                    " " + current_section.catalogNumber +
                    " Section " + current_section.sectionNumber +
                    " (" + current_section.sectionType + ")";
            viewHolder.header.setText(title);

            String meetingDisplay = current_section.meeting.days +
                    " " + current_section.meeting.times;
            viewHolder.meetingInfo.setText(meetingDisplay);

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
            viewHolder.rowView.setOnClickListener(clickListener);

        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView parentRecyclerView) {
            super.onAttachedToRecyclerView(parentRecyclerView);

            this.parentRecyclerView = parentRecyclerView;
        }

        // inner class to hold a reference to each item of RecyclerView
        public class ViewHolder extends RecyclerView.ViewHolder {

            public View rowView;
            public TextView header, meetingInfo;

            public ViewHolder(View rowView) {
                super(rowView);
                this.rowView = rowView;
                header = (TextView) rowView.findViewById(R.id.sectionTitle);
                meetingInfo = (TextView) rowView.findViewById(R.id.meetingInfo);

            }
        }

        // Return the size of your itemsData (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return savedSections.size();
        }

        @Override
        public void onItemDismiss(int position) {
            savedSections.remove(position);
            updateFile(getActivity());
            notifyItemRemoved(position);
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(savedSections, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(savedSections, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }

    }


    // Adapter for list view that will display section info
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

        public ArrayList<String> getItems(){
            ArrayList<String> items = new ArrayList<String>();
            items.add(subjectCode);
            items.add(catalogNumber);
            items.add(sectionNumber);
            items.add(sectionType);
            items.add(meeting.days);
            items.add(meeting.times);
            return items;
        }
    }

    private class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
        private final ItemTouchHelperAdapter adapter;

        public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }


        @Override
        public boolean onMove(RecyclerView recyclerView,
                              RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            adapter.onItemMove(viewHolder.getAdapterPosition(),
                    target.getAdapterPosition());
            return true;
        }
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder,
                             int direction) {
            adapter.onItemDismiss(viewHolder.getAdapterPosition());
        }
    }
}
