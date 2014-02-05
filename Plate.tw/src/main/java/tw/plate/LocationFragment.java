package tw.plate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class LocationFragment extends Fragment{
    private View v;
    private CustomAdapter customAdapter;
    private String url;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        v = inflater.inflate(R.layout.locations_frag, container, false);
        updateLocationList();
        setupUpSpinner();


        //Testing error reporting
        Button bt = (Button) v.findViewById(R.id.btn_err_trigger);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Constants.LOG_TAG,"clicked!!");
                Log.d(Constants.LOG_TAG,"throwing exception!!");
                throw new RuntimeException("Dying on purpose");
            }
        });

        return v;
    }

    private void setupUpSpinner(){
        //FIXME using the actionbar, intent 

    }

    private void updateLocationList(){
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

                convertview.setTag(viewHolder);
            }
            else
            {
                viewHolder=(ViewHolder)convertview.getTag();
            }

            viewHolder.tv_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent restIntent = new Intent(view.getContext(),RestaurantActivity.class);
                    restIntent.putExtra("locationId", arg0);
                    startActivity(restIntent);
                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    //view.setBackgroundColor(getResources().getColor(R.color.fresh_orange));
                    Log.d("PlateLog", "clicked");

                }
            });

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
