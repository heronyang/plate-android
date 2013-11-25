package tw.plate;

import android.app.ListActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RestaurantActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        ListView listView = (ListView) findViewById(R.id.lv_restaurant);
        /*
        setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_restaurant,Constants.RESTAURANT_LIST));
        listView.setTextFilterEnabled(true);*/

        //Displaying ListView items
        ArrayAdapter<String> files = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, Constants.RESTAURANT_LIST);
        listView.setAdapter(files);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.restaurant, menu);
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


}
