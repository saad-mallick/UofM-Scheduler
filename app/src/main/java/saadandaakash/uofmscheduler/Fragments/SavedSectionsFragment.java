package saadandaakash.uofmscheduler.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import saadandaakash.uofmscheduler.Adapters.ItemTouchHelperAdapter;
import saadandaakash.uofmscheduler.R;
import saadandaakash.uofmscheduler.Utitilies.Section;
import saadandaakash.uofmscheduler.Utitilies.Utility;

public class SavedSectionsFragment extends Fragment {

    public static ArrayList<Section> savedSections = null;

    public static SavedSectionsFragment newInstance() {
        SavedSectionsFragment fragment = new SavedSectionsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout into the specified container
        //True if you can inflate the layout and then attach it directly to the root of the
        //container
        //False if you want to inflate the layout and then return that View
        return inflater.inflate(R.layout.saved_sections_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadFile(getActivity());

        // set up and populate the recycler view for the saved courses list
        final RecyclerAdapter adapter = new RecyclerAdapter(getActivity(), savedSections);
        final RecyclerView sectionsList = (RecyclerView) getView().findViewById(R.id.savedSectionsList);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(sectionsList);

        sectionsList.setLayoutManager(new LinearLayoutManager(getContext()));
        sectionsList.setAdapter(adapter);

        // make the sort icon sort the list when clicked
        ImageView sortIcon = (ImageView) getView().findViewById(R.id.sortIcon);
        sortIcon.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                if (savedSections != null) {
                    Collections.sort(savedSections, new Section());
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public void onStop(){
        super.onStop();
        updateFile(getActivity());
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
                    inflate(R.layout.saved_sections_row, parent, false);

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
            else {
                viewHolder.rowView.setBackgroundColor(getResources().getColor(R.color.transparent));
            }

            // - replace the contents of the view with that view
            String title = current_section.subjectCode +
                    " " + current_section.catalogNumber +
                    " Section " + current_section.sectionNumber +
                    " (" + current_section.sectionType + ")";
            viewHolder.header.setText(title);

            String meetingDisplay = current_section.getMeetings();
            viewHolder.meetingInfo.setText(meetingDisplay);

            String instructors = current_section.getInstructors();
            viewHolder.instructors.setText(instructors);

            View.OnClickListener clickListener = new View.OnClickListener() {
                public void onClick(View v) {
                    SectionDetailsFragment fragment = SectionDetailsFragment.newInstance(current_section);
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
            public TextView header, meetingInfo, instructors;

            public ViewHolder(View rowView) {
                super(rowView);
                this.rowView = rowView;
                header = (TextView) rowView.findViewById(R.id.sectionTitle);
                meetingInfo = (TextView) rowView.findViewById(R.id.meetingInfo);
                instructors = (TextView) rowView.findViewById(R.id.instructors);

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
            //updateFile(getActivity());
            notifyItemRemoved(position);
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            System.out.println("MOVING ITEM FROM " + fromPosition + " TO " + toPosition);
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(savedSections, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(savedSections, i, i - 1);
                }
            }
            //updateFile(getActivity());
            notifyItemMoved(fromPosition, toPosition);
            return true;
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

    public void saveSection(Section section, Activity activity){
        if(savedSections == null){
            savedSections = new ArrayList<>();
        }
        // check to make sure the section isn't already in the list before saving
        for (Section s : savedSections) {
            if (s.compare(s, section) == 0) {
                return;
            }
        }
        savedSections.add(section);
    }

    public void updateFile(Activity activity){
        try {
            JSONArray jsonArray = new JSONArray();
            for (Section section : savedSections) {
                JSONObject object = new JSONObject();

                object.put("SubjectCode", section.subjectCode);
                object.put("CatalogNumber", section.catalogNumber);
                object.put("SectionNumber", section.sectionNumber);
                object.put("SectionType", section.sectionType);

                JSONArray meetingsArray = new JSONArray();
                for (Section.Meeting meeting : section.meetings) {
                    System.out.println(meeting.instructors.toString());
                    JSONObject meetingJSON = meeting.getJSONFromMeeting();
                    if (meetingJSON != null) {
                        meetingsArray.put(meetingJSON);
                    }
                }
                object.put("Meetings", meetingsArray);

                jsonArray.put(object);
            }

            Utility.writeToFile(activity, jsonArray.toString(), Utility.FILENAME);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void loadFile(Activity activity){
        if(savedSections == null) {
            savedSections = new ArrayList<>();
            try {
                // get the JSONArray of saved sections
                String jsonArrayString = Utility.readFromFile(activity, Utility.FILENAME);
                JSONArray jsonArray = new JSONArray(jsonArrayString);

                // read data from the JSONArray into section objects
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject sectionObject = jsonArray.getJSONObject(i);

                    JSONArray meetingsArray = sectionObject.getJSONArray("Meetings");
                    ArrayList<Section.Meeting> meetings = new ArrayList<>();
                    for (int j = 0; j < meetingsArray.length(); j++) {
                        JSONObject meetingObject = meetingsArray.getJSONObject(j);
                        meetings.add(new Section.Meeting(meetingObject));
                    }

                    Section addSection = new Section(
                            sectionObject.getString("SubjectCode"),
                            sectionObject.getString("CatalogNumber"),
                            sectionObject.getString("SectionNumber"),
                            sectionObject.getString("SectionType"),
                            meetings);

                    savedSections.add(addSection);
                }
            } catch (Exception e) {}
        }

    }

}
