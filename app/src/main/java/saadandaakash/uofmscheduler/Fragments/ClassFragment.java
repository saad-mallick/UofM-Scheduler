package saadandaakash.uofmscheduler.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import saadandaakash.uofmscheduler.R;

/**
 * Created by Saad on 12/26/2017.
 */

public class ClassFragment extends Fragment {
    /*
    * TODO: Add a back button, so users can navigate back to the original list
    *
    * */

    private String course;

    public static ClassFragment newInstance(String course){
        ClassFragment fragment = new ClassFragment();
        fragment.course = course;
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
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }


}
