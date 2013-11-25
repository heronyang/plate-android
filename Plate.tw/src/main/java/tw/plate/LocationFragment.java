package tw.plate;

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


/**
 * Created by Hung on 11/23/13.
 */
public class LocationFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.locations_frag, container, false);
        TextView tv = (TextView) v.findViewById(R.id.tv_location);
        tv.setText(getArguments().getString("msg"));

        //Displaying ListView items
        ArrayAdapter<String> files = new ArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, Constants.CANTEEN_LIST);

        ListView lv = (ListView) v.findViewById(R.id.lv_canteen);
        lv.setAdapter(files);

        //OnClickListener for each dinning hall
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent restActivity = new Intent(view.getContext(),RestaurantActivity.class);
                startActivity(restActivity);
            }
        });

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
