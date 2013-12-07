package tw.plate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class LocationFragment extends Fragment{
    private View v;
    private CustomAdapter customAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        v = inflater.inflate(R.layout.locations_frag, container, false);
        updateLocationList();

        return v;
    }

    private void updateLocationList(){
        TextView tv = (TextView) v.findViewById(R.id.tv_location);
        tv.setText(R.string.location_welcome_message);

        ListView lv = (ListView) v.findViewById(R.id.lv_canteen);
        customAdapter = new CustomAdapter(this.getActivity());
        lv.setAdapter(customAdapter);
        lv.setDivider(null);
        lv.setDividerHeight(0);

    }

    private class CustomAdapter extends BaseAdapter{
        LayoutInflater inflater;

        public class ViewHolder{
            TextView tv_location;
        }
        public CustomAdapter(Context context){
            inflater = LayoutInflater.from(context);

        }
        @Override
        public boolean isEnabled(int position) {
            return false;
        }
        public int getCount(){
            return Constants.CANTEEN_LIST.length;
        }
        public Object getItem(int position){
            return Constants.CANTEEN_LIST[position];
        }
        public long getItemId(int position){
            return position;
        }
        public View getView(final int arg0, View convertview, ViewGroup arg2) {
            ViewHolder viewHolder = null;
            if(convertview == null)
            {
                convertview = inflater.inflate(R.layout.listrow_location, null);

                viewHolder=new ViewHolder();
                viewHolder.tv_location = (TextView) convertview.findViewById(R.id.tv_listrow_location);
                viewHolder.tv_location.setTextSize(getResources().getDimension(R.dimen.box_textsize));

                viewHolder.tv_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent restIntent = new Intent(view.getContext(),RestaurantActivity.class);
                        restIntent.putExtra("locationId", arg0);
                        startActivity(restIntent);
                        view.setBackgroundColor(getResources().getColor(R.color.fresh_orange));
                        Log.d("PlateLog", "clicked");

                    }
                });

                convertview.setTag(viewHolder);
            }
            else
            {
                viewHolder=(ViewHolder)convertview.getTag();
            }
            // set values
            String locationName = Constants.CANTEEN_LIST[arg0];
            viewHolder.tv_location.setText(locationName);

            return convertview;
        }

    }

    public static LocationFragment newInstance(){
        LocationFragment locationFragment = new LocationFragment();
        Bundle b = new Bundle();
        //b.putString("msg",text);

        locationFragment.setArguments(b);
        return locationFragment;
    }
}
