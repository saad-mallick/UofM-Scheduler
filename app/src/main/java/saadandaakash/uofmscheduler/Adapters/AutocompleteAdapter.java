package saadandaakash.uofmscheduler.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

import saadandaakash.uofmscheduler.R;

public class AutocompleteAdapter extends ArrayAdapter {

    private Context context;
    private final ArrayList<String> data = new ArrayList<>();
    private ArrayList<String> filteredData;

    //change this to ArrayList<String> data_array or you will pay for it later, I promise
    public AutocompleteAdapter(Context context, ArrayList<String> data) {
        super(context, R.layout.drop_down_format, data);
        this.context = context;
        for (String i : data) {
            this.data.add(i);
        }
        filteredData = this.data;
    }

    public void add_data(ArrayList<String> data) {
        for (String i : data) {
            this.data.add(i);
        }
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.drop_down_format, null, false);
        TextView courseName = (TextView) rowView.findViewById(R.id.text1);

        try {
            courseName.setText(filteredData.get(position));
        } catch (Exception ex){
            //   courseName.setText("");
            //   courseName.setTextSize(0);
            //   courseName.setPadding(0, 0, 0, 0);
        }

        return rowView;
    }

    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence)
            {
                System.out.println(charSequence);
                System.out.println(data);

                FilterResults results = new FilterResults();
                ArrayList<String> resultingData = new ArrayList<>();

                // check to make sure that entry is not just whitespace
                if (charSequence != null && !charSequence.toString().trim().isEmpty()) {
                    for (String i : data) {
                        if (charSequence.toString().toLowerCase()
                                .equals(i.substring(0, charSequence.length()).toLowerCase())) {
                            resultingData.add(i);
                        }
                    }
                }
                results.values = resultingData;
                results.count = resultingData.size();
                return results;
            }


            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults)
            {
                if (filterResults.count == 0) {
                    notifyDataSetInvalidated();
                }
                else {
                    filteredData = (ArrayList<String>) filterResults.values;
                    clear();
                    for (String save : filteredData) {
                        add(save);
                    }
                    System.out.println(filteredData);
                    notifyDataSetChanged();
                }
            }
        };
    }

    @Override
    public String toString(){
        return filteredData.toString();
    }

}