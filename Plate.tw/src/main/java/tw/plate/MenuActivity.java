package tw.plate;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MenuActivity extends ListActivity {

    private List<PlateService.Meal> mealList = new ArrayList<PlateService.Meal>();
    private CustomAdapter customAdapter;

    private List<Pair<PlateService.Meal, Integer>> orderList = new ArrayList<Pair<PlateService.Meal, Integer>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        int restId = intent.getIntExtra("restId", 0);

        //
        PlateService.PlateTWOldAPI plateTW;
        plateTW = PlateService.getOldAPI(Constants.API_URI_PREFIX);
        plateTW.menu(restId, new Callback<PlateService.MenuResponse>() {
            @Override
            public void success(PlateService.MenuResponse ms, Response response) {
                for (PlateService.Meal m : ms.meal_list) {
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

        ArrayAdapter<String> spAdapter;
        String [] spinnerItems = new String[Constants.MAX_AMOUNT];
        for(int i=0 ; i<spinnerItems.length ; i++)  spinnerItems[i] = "" + i;
        spAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinnerItems);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        customAdapter = new CustomAdapter(this, spAdapter);
        lv.setAdapter(customAdapter);
    }

    private class CustomAdapter extends BaseAdapter // listview
    {
        LayoutInflater inflater;
        ArrayAdapter<String> spAdapter;

        int [] amounts = new int[mealList.size()];

        public CustomAdapter(Context context, ArrayAdapter<String> _adapter)
        {
            inflater = LayoutInflater.from(context);
            spAdapter = _adapter;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return mealList.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public int getSelectedAmountAtPosition(int position) {
            if (position > mealList.size()) return 0;
            return amounts[position];
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(final int arg0, View convertview, ViewGroup arg2) {
            ViewHolder viewHolder = null;
            Log.d(Constants.LOG_TAG, "arg0 >> " + arg0);
            if(convertview == null)
            {
                convertview = inflater.inflate(R.layout.listrow_menu, null);

                // creating instance
                viewHolder=new ViewHolder();
                viewHolder.sp = (Spinner) convertview.findViewById(R.id.listrow_menu_spinner);
                viewHolder.tv = (TextView) convertview.findViewById(R.id.listrow_menu_tv);

                //
                convertview.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder)convertview.getTag();
            }


            // set values
            String meal_name = mealList.get(arg0).meal_name;
            viewHolder.tv.setText(meal_name);
            viewHolder.sp.setAdapter(spAdapter);
            viewHolder.sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    amounts[arg0] = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Callback method to be invoked when the selection disappears from this view.
                    // The selection can disappear for instance when touch is activated or when the adapter becomes empty.
                }
            });
            viewHolder.sp.setSelection(amounts[arg0]);


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

    private void confirmOrder() {
        View view = findViewById(android.R.id.content);
        Intent confirmOrderIntent = new Intent(view.getContext(), ConfirmOrder.class);
        collectResults();

        // for test
        int s = orderList.size();
        Log.d(Constants.LOG_TAG, "Final List in this page (menu)");
        for( int i=0 ; i<s ; i++ ){
            Log.d(Constants.LOG_TAG, "meal name: " + orderList.get(i).first.meal_name + "\tamount : " + orderList.get(i).second);
        }

        // FIXME: next step here

        startActivity(confirmOrderIntent);
    }

    private void collectResults() {
        int s = mealList.size();

        Log.d(Constants.LOG_TAG, "s = " + s);
        for(int i=0 ; i<s ; i++) {
            int amount = customAdapter.getSelectedAmountAtPosition(i);
            if (amount != 0) {
                Pair<PlateService.Meal, Integer> orderItem = new Pair(mealList.get(i), amount);
                orderList.add(orderItem);
            }
        }
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
