package tw.plate;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

public class ConfirmOrder extends ActionBarActivity {
    //TEST Data Only
    String serverTime = "11:30";
    String restName = "元氣";
    String myOrder;
    String popupWarning = "This is popup warning \nThis is popup warning \nThis is popup warning \nThis is popup warning \nThis is popup warning \nThis is popup warning \n";
    //OrderList passedOrderList;
    private ArrayList<String> mealNames = new ArrayList<String>();
    private ArrayList<Integer> mealPrices = new ArrayList<Integer>()
            , mealID = new ArrayList<Integer>()
            , mealAmount = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        Intent intent = getIntent();
        Log.d(Constants.LOG_TAG,"Confirm order Activity");
        //TODO Receive the data getStringArrayExtra
        //Bundle data = getIntent().getExtras();
        //passedOrderList = (OrderList) data.getParcelable("order");
        //Log.d(Constants.LOG_TAG,"Passed order at postition 0: Meal Name: "+passedOrderList.getMealOrderList(0).meal_name +"Amount: "+passedOrderList.getAmountOrderList(0) +"pieces");
        mealNames = intent.getStringArrayListExtra("orderMealNames");
        mealPrices = intent.getIntegerArrayListExtra("orderMealPrice");
        mealID = intent.getIntegerArrayListExtra("orderMealID");
        mealAmount = intent.getIntegerArrayListExtra("orderMealAmount");

        for(int i=0; i<mealNames.size(); i++)
            Log.d(Constants.LOG_TAG,"Passed order at postition 0: Meal Name: "+mealNames.get(i) +"Amount: "+ mealAmount.get(i) +"pieces" );

        TextView tv_time = (TextView) findViewById(R.id.tv_time);
        TextView tv_rest = (TextView) findViewById(R.id.tv_rest);
        TextView tv_myOrder = (TextView) findViewById(R.id.tv_your_order);
        Button btCancel = (Button) findViewById(R.id.bt_cancel);
        Button btOk = (Button) findViewById(R.id.bt_ok);

        tv_time.setText(serverTime);
        tv_rest.setText(restName);
        //TODO create a complex-string from given data
        myOrder = "my order";
        tv_myOrder.setText(myOrder);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getBaseContext()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup, null);
                final PopupWindow popupWind = new PopupWindow(popupView, android.app.ActionBar
                        .LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT);
                TextView popupMessage = (TextView)  popupView.findViewById(R.id.tv_pop);
                popupMessage.setText(popupWarning);
                Button yes = (Button) popupView.findViewById(R.id.bt_pop_yes);
                Button no = (Button) popupView.findViewById(R.id.bt_pop_no);
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWind.dismiss();
                    }
                });
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //FIXME to the next fragment of mainactivity

                    }
                });
                //TODO Fix the popup xml and make the background darker
                popupWind.showAtLocation(view, Gravity.CENTER, 0, 0);
                popupWind.update(0, 0, 300, 300);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.confirm_order, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_confirm_order, container, false);
            return rootView;
        }
    }

}
