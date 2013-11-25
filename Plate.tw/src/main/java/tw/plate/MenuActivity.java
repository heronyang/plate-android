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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MenuActivity extends ListActivity {

    List<PlateService.Meal> mealList = new ArrayList<PlateService.Meal>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        /*
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        */

        Intent intent = getIntent();
        int restId = intent.getIntExtra("restId", 0);
        Log.d(Constants.LOG_TAG, "This rest ID:" + Integer.toString(restId));

        PlateService.PlateTWOldAPI plateTW;
        plateTW = PlateService.getOldAPI(Constants.API_URI_PREFIX);
        plateTW.menu(restId, new Callback<PlateService.MenuResponse>() {
            @Override
            public void success(PlateService.MenuResponse ms, Response response) {
                for (PlateService.Meal m : ms.meal_list) {
                    Log.d(Constants.LOG_TAG, m.meal_name);
                    mealList.add(m);
                }
                updateMenuList();
            }

            @Override
            public void failure(RetrofitError e) {
                Log.d(Constants.LOG_TAG, "menu: failure");
            }
        });
    }

    private void updateMenuList() {
        ListView lv = (ListView) findViewById(android.R.id.list);

        // FIXME: there must be a better way to get the name array of restaurants
        int l = mealList.size(), i;
        String [] mealNames = new String[l];
        for(i=0 ; i<l ; i++) {
            mealNames[i] = mealList.get(i).meal_name;
        }

        //Displaying ListView items
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mealNames);
        lv.setAdapter(adapter);
    }


    @Override
    protected void onListItemClick(ListView lv, View view, int position, long id) {
        super.onListItemClick(lv, view, position, id);
        Log.d(Constants.LOG_TAG, "clicked on : " + Integer.toString(position));
        // TODO: Next step here
        //
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
            return rootView;
        }
    }

}
