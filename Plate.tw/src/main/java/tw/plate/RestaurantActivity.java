package tw.plate;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import retrofit.RetrofitError;

public class RestaurantActivity extends ListActivity implements PlateServiceManager.PlateManagerCallback {

    String [] restaurantNames;
    CustomAdapter customAdapter;
    String restStatus = "open";

    PlateServiceManager plateServiceManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        Intent intent = getIntent();
        int locationId = intent.getIntExtra("locationId", 0) + 1;   // start from 1
        Log.d(Constants.LOG_TAG, "This place: " + Integer.toString(locationId));

        plateServiceManager = ((Plate) this.getApplication()).getPlateServiceManager();
        plateServiceManager.restaurants(locationId, this);
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
            final PlateService.Restaurant restaurant = restaurantList.get(arg0);
            if(convertview == null)
            {
                if(!restaurant.is_open){
                    convertview = inflater.inflate(R.layout.listrow_restaurant_unavail, null);
                    //viewHolder.tv_restaurant.setBackground(getResources().getDrawable(R.drawable.rectangle_frame_unavail));
                }
                else{
                    convertview = inflater.inflate(R.layout.listrow_restaurant, null);
                }
                viewHolder=new ViewHolder();
                viewHolder.tv_restaurant = (TextView) convertview.findViewById(R.id.tv_listrow_restaurant);

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
                    Log.d(Constants.LOG_TAG, "description: " + restaurant.description);
                    AlertDialog.Builder builder = new AlertDialog.Builder(RestaurantActivity.this);

                    String msg = "";
                    if (!restaurant.is_open) {
                        msg += "* 餐廳目前休息中：" + restaurant.closed_reason + "\n";
                        restStatus = "closed";
                    }else {
                        restStatus = "open";
                    }
                    msg += restaurant.description;

                    builder.setMessage(msg)
                            .setTitle(restaurant.name + " : " + getString(R.string.restaurant_info_popup_title));
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            viewMenu(restaurant);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            return convertview;
        }

    }

    private void viewMenu(PlateService.Restaurant restaurant) {
        Intent menuIntent = new Intent(this, MenuActivity.class);
        menuIntent.putExtra("restId", restaurant.rest_id);
        menuIntent.putExtra("restName", restaurant.name);
        menuIntent.putExtra("restStatus",restStatus);
        startActivity(menuIntent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
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
            String rest_status_str = "";
            if (restaurantList.get(i).is_open) {
                rest_status_str = getString(R.string.rest_status_open);
            } else {
                rest_status_str = getString(R.string.rest_status_close);
            }

            restaurantNames[i] = restaurantList.get(i).name + rest_status_str;
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
        builder.setMessage(R.string.restaurant_list_empty_message)
                .setTitle(R.string.restaurant_list_empty_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button

                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
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
    public void orderPostFailed(String errorMsg, int errorStatus) { throw new UnsupportedOperationException(); }
    @Override
    public void orderGetSucceed(PlateService.OrderGetResponse orderGetResponse) { throw new UnsupportedOperationException(); }
    @Override
    public void orderGetSucceedEmpty() { throw new UnsupportedOperationException(); }
    @Override
    public void orderGetFailed() { throw new UnsupportedOperationException(); }
    @Override
    public void registerSucceed() { throw new UnsupportedOperationException(); }
    @Override
    public void registerFailed(RetrofitError error) { throw new UnsupportedOperationException(); }

    @Override
    public void currentNsSucceed(int current_ns) { throw new UnsupportedOperationException(); }
    @Override
    public void currentNsFailed() { throw new UnsupportedOperationException(); }
    @Override
    public void currentCookingOrdersSucceed(int current_cooking_orders) { throw new UnsupportedOperationException(); }



    @Override
    public void networkError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.network_error_message)
                .setTitle(R.string.network_error_title);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                System.exit(0);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
