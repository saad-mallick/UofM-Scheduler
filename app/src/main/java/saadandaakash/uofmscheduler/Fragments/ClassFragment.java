package saadandaakash.uofmscheduler.Fragments;

import android.support.v4.app.Fragment;

/**
 * Created by Saad on 12/26/2017.
 */

public class ClassFragment extends Fragment {

    private String course;

    public static ClassFragment newInstance(String course){
        ClassFragment fragment = new ClassFragment();
        fragment.course = course;
        return fragment;
    }


}
