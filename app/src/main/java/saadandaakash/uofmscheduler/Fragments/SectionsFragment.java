package saadandaakash.uofmscheduler.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Saad on 12/26/2017.
 */

public class SectionsFragment extends ListFragment{

    //EECS 280, EECS 203 etc.
    private String course;
    private ArrayList<String> sections = new ArrayList<String>();

    //You are not allowed to make constructors in Fragments, so this is what is typically done
    public static SectionsFragment newInstance(String course){
        SectionsFragment fragment = new SectionsFragment();
        fragment.course = course;
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

    }

    private class CustomAdapter extends ArrayAdapter<String> {
        ArrayList<String> sections;
        Context context;
        public CustomAdapter(Context context, ArrayList<String> sections){
            super(pass correct values);
            this.sections = sections;
            this.context = context;
        }
    }
}
