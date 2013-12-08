package tw.plate;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
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
    private ArrayList<String> mealNames = new ArrayList<String>();
    private ArrayList<Integer> mealPrices = new ArrayList<Integer>()
            , mealID = new ArrayList<Integer>()
            , mealAmount = new ArrayList<Integer>();

    int restId;
    String restName;
    private CustomAdapter customAdapter;

    // private List<Pair<PlateService.Meal, Integer>> orderList = new ArrayList<Pair<PlateService.Meal, Integer>>();
    // private OrderList orderList = new OrderList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        restId = intent.getIntExtra("restId", 0);
        restName = intent.getStringExtra("restName");

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

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateMenuList() {
        TextView tv_category = (TextView) findViewById(R.id.tv_category);
        ListView lv = (ListView) findViewById(android.R.id.list);

        tv_category.setText(getResources().getString(R.string.menu_list_category));

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

        public class ViewHolder
        {
            Spinner sp;
            TextView tv_name;
            TextView tv_price;
        }

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
                viewHolder.tv_name = (TextView) convertview.findViewById(R.id.listrow_menu_tv);
                viewHolder.tv_price = (TextView) convertview.findViewById(R.id.tv_listrow_price);
                convertview.setTag(viewHolder);
            }
            else
            {
                viewHolder=(ViewHolder)convertview.getTag();
            }


            // set values
            String meal_name = mealList.get(arg0).meal_name;
            viewHolder.tv_name.setText(meal_name);
            int meal_price = mealList.get(arg0).meal_price;
            viewHolder.tv_price.setText(""+meal_price+" NTD");
            viewHolder.tv_price.setTextColor(getResources().getColor(R.color.foreground_1));

            viewHolder.sp.setAdapter(spAdapter);
            viewHolder.sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    amounts[arg0] = position;
                    Log.d(Constants.LOG_TAG, "pos: " + arg0 + "amount: " + amounts[arg0]);
                    if(amounts[arg0]!=0){
                        ((TextView)parentView.getChildAt(0)).setTextColor(getResources().getColor(R.color.background_3));
                    }else{
                        ((TextView)parentView.getChildAt(0)).setTextColor(getResources().getColor(R.color.gray_unseen));

                    }
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


    }

    @Override
    protected void onListItemClick(ListView lv, View view, int position, long id) {
        super.onListItemClick(lv, view, position, id);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        //menu.findItem(R.id.action_bar_title).setTitle(restName);
        String title = getActionBar().getTitle().toString();
        title += ":    "+restName;
        getActionBar().setTitle(title);
        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.light_gray)));
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
        Intent confirmOrderIntent = new Intent(view.getContext(), ConfirmOrderActivity.class);
        collectResults();

        // for test
        int s = mealNames.size();
        Log.d(Constants.LOG_TAG, "Final List in this page (menu)");
        for( int i=0 ; i<s ; i++ ){
            Log.d(Constants.LOG_TAG, "meal name: " + mealNames.get(i) + "\tamount : " + mealAmount.get(i));
        }

        // put
        confirmOrderIntent.putStringArrayListExtra("orderMealNames", mealNames);
        confirmOrderIntent.putIntegerArrayListExtra("orderMealPrice", mealPrices);
        confirmOrderIntent.putIntegerArrayListExtra("orderMealID", mealID);
        confirmOrderIntent.putIntegerArrayListExtra("orderMealAmount", mealAmount);
        confirmOrderIntent.putExtra("restName",restName);

        startActivity(confirmOrderIntent);
    }

    private void collectResults() {
        int s = mealList.size();
        Log.d(Constants.LOG_TAG, "s = " + s);

        mealNames.clear();
        mealPrices.clear();
        mealID.clear();
        mealAmount.clear();

        for(int i=0 ; i<s ; i++) {
            int amount = customAdapter.getSelectedAmountAtPosition(i);
            if (amount != 0) {
                mealNames.add(mealList.get(i).meal_name);
                mealPrices.add(mealList.get(i).meal_price);
                mealID.add(mealList.get(i).meal_id);
                mealAmount.add(amount);
            }
        }
    }

//    private List<Pair<PlateService.Meal, Integer>> orderList = new ArrayList<Pair<PlateService.Meal, Integer>>();

}
