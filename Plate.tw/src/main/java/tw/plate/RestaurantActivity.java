package tw.plate;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RestaurantActivity extends ListActivity implements PlateServiceManager.PlateManagerCallback {

    String [] restaurantNames;
    CustomAdapter customAdapter;

    PlateServiceManager plateServiceManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        Intent intent = getIntent();
        int locationId = intent.getIntExtra("locationId", 0) + 1;   // start from 1
        Log.d(Constants.LOG_TAG, "This place: " + Integer.toString(locationId));

        plateServiceManager = ((Plate) this.getApplication()).getPlateServiceManager();
        plateServiceManager.restaurants(locationId, this);
        /*
        PlateService.PlateTWOldAPI plateTW;
        plateTW = PlateService.getOldAPI(Constants.API_URI_PREFIX);

        plateTW.restaurants(locationId, new Callback<PlateService.RestaurantResponse>() {
            @Override public void success(PlateService.RestaurantResponse rs, Response response) {
                for (PlateService.Restaurant r: rs.list) {
                    restaurantList.add(r);
                }
                //DISPLAY The Content
                updateRestaurantList();
            }
            @Override public void failure(RetrofitError e) {
                Log.w(Constants.LOG_TAG, "restaurants: failure");
            }
        });
        */
    }

    @Override
    protected void onListItemClick(ListView lv, View view, int position, long id) {
        super.onListItemClick(lv, view, position, id);
        Log.d(Constants.LOG_TAG, "clicked on : " + Integer.toString(position));

    }

    private class CustomAdapter extends BaseAdapter {
        LayoutInflater inflater;
        List<PlateService.Restaurant> restaurantList = plateServiceManager.getRestaurantList();
        public class ViewHolder{
            TextView tv_restaurant;
        }
        public CustomAdapter(Context context){
            inflater = LayoutInflater.from(context);
        }
        //to disable listview click
        @Override
        public boolean isEnabled(int position) {
            return false;
        }
        public int getCount(){
            return restaurantList.size();
        }
        public Object getItem(int position){
            return restaurantList.get(position);
        }
        public long getItemId(int position){
            return position;
        }
        public View getView(final int arg0, View convertview, ViewGroup arg2) {
            ViewHolder viewHolder = null;
            if(convertview == null)
            {
                convertview = inflater.inflate(R.layout.listrow_restaurant, null);

                viewHolder=new ViewHolder();
                viewHolder.tv_restaurant = (TextView) convertview.findViewById(R.id.tv_listrow_restaurant);
                viewHolder.tv_restaurant.setTextSize(getResources().getDimension(R.dimen.rest_textsize));

                convertview.setTag(viewHolder);
            }
            else
            {
                viewHolder=(ViewHolder)convertview.getTag();
            }
            // set values
            String restName = restaurantNames[arg0];
            viewHolder.tv_restaurant.setText(restName);

            viewHolder.tv_restaurant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent menuIntent = new Intent(view.getContext(), MenuActivity.class);
                    menuIntent.putExtra("restId", restaurantList.get(arg0).rest_id);
                    menuIntent.putExtra("restName", restaurantList.get(arg0).name);
                    //view.setBackgroundColor(getResources().getColor(R.color.fresh_orange));
                    startActivity(menuIntent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
            });

            return convertview;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /*
        Override for PlateServiceManager's Callbacks
     */
    @Override
    public void updateRestaurantList() {
        // FIXME: there must be a better way to get the name array of restaurants
        List<PlateService.Restaurant> restaurantList = plateServiceManager.getRestaurantList();
        int l = restaurantList.size(), i;

        // if empty, send back
        if (l<=0) {
            popupEmptyMessageAndSendBack();
        }

        restaurantNames = new String[l];
        for(i=0 ; i<l ; i++) {
            restaurantNames[i] = restaurantList.get(i).name;
        }
        //Displaying Items
        ListView lv = (ListView) findViewById(android.R.id.list);

        customAdapter = new CustomAdapter(this);
        lv.setAdapter(customAdapter);
        lv.setDivider(null);
        lv.setDividerHeight(0);
    }

    void popupEmptyMessageAndSendBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.restaurant_list_empty_messge)
                .setTitle(R.string.restaurant_list_empty_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button

                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void updateMenuList() { throw new UnsupportedOperationException(); }

    @Override
    public void loginSucceed() { throw new UnsupportedOperationException(); }
    @Override
    public void loginFailed() { throw new UnsupportedOperationException(); }
    @Override
    public void notRegistered() { throw new UnsupportedOperationException(); }

    @Override
    public void orderPostSucceed() { throw new UnsupportedOperationException(); }
    @Override
    public void orderPostFailed() { throw new UnsupportedOperationException(); }
    @Override
    public void orderGetSucceed(PlateService.OrderGetResponse orderGetResponse) { throw new UnsupportedOperationException(); }
    @Override
    public void orderGetSucceedEmpty() { throw new UnsupportedOperationException(); }
    @Override
    public void orderGetFailed() { throw new UnsupportedOperationException(); }
    @Override
    public void registerSucceed() { throw new UnsupportedOperationException(); }
    @Override
    public void registerFailed() { throw new UnsupportedOperationException(); }

    @Override
    public void currentNsSucceed(int current_ns) { throw new UnsupportedOperationException(); }
    @Override
    public void currentNsFailed() { throw new UnsupportedOperationException(); }

}
