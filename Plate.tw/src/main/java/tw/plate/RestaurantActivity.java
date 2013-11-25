package tw.plate;

import android.app.ListActivity;
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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RestaurantActivity extends ListActivity {

    List<PlateService.Restaurant> restaurantList = new ArrayList<PlateService.Restaurant>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        Intent intent = getIntent();
        int locationId = intent.getIntExtra("locationId", 0) + 1;   // start from 1
        Log.d(Constants.LOG_TAG, "This place: " + Integer.toString(locationId));

        PlateService.PlateTWOldAPI plateTW;
        plateTW = PlateService.getOldAPI(Constants.API_URI_PREFIX);

        plateTW.restaurants(locationId, new Callback<PlateService.RestaurantResponse>() {
            @Override public void success(PlateService.RestaurantResponse rs, Response response) {
                for (PlateService.Restaurant r: rs.list) {
                    restaurantList.add(r);
                }
                updateRestaurantList();
            }
            @Override public void failure(RetrofitError e) {
                Log.w(Constants.LOG_TAG, "restaurants: failure");
            }
        });
    }

    @Override
    protected void onListItemClick(ListView lv, View view, int position, long id) {
        super.onListItemClick(lv, view, position, id);
        Log.d(Constants.LOG_TAG, "clicked on : " + Integer.toString(position));
        Intent menuIntent = new Intent(view.getContext(), MenuActivity.class);
        menuIntent.putExtra("restId", restaurantList.get(position).rest_id);
        startActivity(menuIntent);
    }

    private void updateRestaurantList() {
        ListView lv = (ListView) findViewById(android.R.id.list);

        // FIXME: there must be a better way to get the name array of restaurants
        int l = restaurantList.size(), i;
        String [] restaurantNames = new String[l];
        for(i=0 ; i<l ; i++) {
            restaurantNames[i] = restaurantList.get(i).name;
        }

        //Displaying ListView items
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, restaurantNames);
        lv.setAdapter(adapter);
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


}
