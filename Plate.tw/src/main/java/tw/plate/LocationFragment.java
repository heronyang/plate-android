package tw.plate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Hung on 11/23/13.
 */
public class LocationFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.locations_frag, container, false);
        TextView tv = (TextView) v.findViewById(R.id.tvLocation);
        tv.setText(getArguments().getString("msg"));

        //Displaying ListView items
        String[] canteen = new String[] { " 第一餐廳 ", " 第二餐廳 ", " 女二餐廳 ", " 其他 " };

        ArrayAdapter<String> files = new ArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                canteen);
        ListView lv = (ListView) v.findViewById(R.id.canteen_listview);
        lv.setAdapter(files);

        //OnClickListener for each dinning hall


        return v;
    }


    public static LocationFragment newInstance(String text){

        LocationFragment locationFragment = new LocationFragment();
        Bundle b = new Bundle();
        b.putString("msg",text);

        locationFragment.setArguments(b);
        return locationFragment;
    }
}
