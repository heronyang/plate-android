package tw.plate;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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

        ArrayAdapter<String> adapter;

        String [] spinnerItems = new String[Constants.MAX_AMOUNT];
        for(int i=0 ; i<spinnerItems.length ; i++)  spinnerItems[i] = "" + i;

        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lv.setAdapter(new CustomAdapter(this, adapter));
    }

    private class CustomAdapter extends BaseAdapter
    {
        LayoutInflater inflater;
        ArrayAdapter<String> adapter;

        public CustomAdapter(Context context, ArrayAdapter<String> _adapter)
        {
            inflater=LayoutInflater.from(context);
            adapter = _adapter;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return mealList.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int arg0, View convertview, ViewGroup arg2) {
            ViewHolder viewHolder;
            if(convertview==null)
            {
                convertview=inflater.inflate(R.layout.listrow_menu, null);
                viewHolder=new ViewHolder();

                // setup spinner
                viewHolder.sp = (Spinner) convertview.findViewById(R.id.listrow_menu_spinner);
                viewHolder.sp.setAdapter(adapter);

                // setup textview
                viewHolder.tv = (TextView) convertview.findViewById(R.id.listrow_menu_tv);
                String meal_name = mealList.get(arg0).meal_name;
                viewHolder.tv.setText(meal_name);
            }
            else
            {
                viewHolder=(ViewHolder)convertview.getTag();
            }
            return convertview;
        }
        public class ViewHolder
        {
            Spinner sp;
            TextView tv;
        }

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

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_confirm:
                confirmOrder();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void confirmOrder(){
        Intent confirmOrderIntent = new Intent(this, ConfirmOrder.class);
        // FIXME: push intent objects here
        startActivity(confirmOrderIntent);
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
