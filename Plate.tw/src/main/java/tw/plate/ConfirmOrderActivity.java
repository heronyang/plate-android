package tw.plate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ConfirmOrderActivity extends Activity implements PlateServiceManager.PlateManagerCallback{

    //OrderList passedOrderList;
    /*
    private ArrayList<String> mealNames = new ArrayList<String>();
    private ArrayList<Integer> mealPrices = new ArrayList<Integer>()
            , mealID = new ArrayList<Integer>()
            , mealAmount = new ArrayList<Integer>();
    String restName;
    */

    String orderJsonRequest;

    PlateServiceManager plateServiceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        plateServiceManager = ((Plate) this.getApplication()).getPlateServiceManager();

        setContentView(R.layout.activity_confirm_order);
        /*
        Intent intent = getIntent();

        mealNames = intent.getStringArrayListExtra("orderMealNames");
        mealPrices = intent.getIntegerArrayListExtra("orderMealPrice");
        mealID = intent.getIntegerArrayListExtra("orderMealID");
        mealAmount = intent.getIntegerArrayListExtra("orderMealAmount");
        restName = intent.getStringExtra("restName");

        //
        for(int i=0; i<mealNames.size(); i++)
            Log.d(Constants.LOG_TAG,"Passed order at position 0: Meal Name: "
                    + mealNames.get(i) + "Amount: " + mealAmount.get(i) +"pieces" );
                    */

        listviewAndTotalAmountSetup();
        buttonSetup();
    }

    private void listviewAndTotalAmountSetup() {

        int totalAmount = 0;

        ListView lv = (ListView) findViewById(R.id.lv_confim);
        TextView tv_restaurant = (TextView) findViewById(R.id.tv_co_rest_name);
        TextView tv_total_amount = (TextView)findViewById(R.id.tvTotalAmount);

        Cart cart = ((Plate)getApplication()).getCart();
        ArrayList<Cart.CartOrderItem> cos = cart.getOrderItems();
        int l = cart.getNumberOfItems(), i;

        List<String> mealString = new ArrayList<String>();
        List<String> amountString = new ArrayList<String>();
        List<String> priceString = new ArrayList<String>();

        for( i=0 ; i<l ; i++) {
            int meal_price = cos.get(i).meal_price,
                amount = cos.get(i).amount;

            mealString.add(cos.get(i).meal_name + " ");
            amountString.add(amount + " 份");
            priceString.add(meal_price + " 元");

            totalAmount += meal_price * amount;
        }
        tv_restaurant.setText(cart.getRestaurant_name());

        /*
        int l = mealNames.size(), i;
        List<String> mealString = new ArrayList<String>();
        List<String> amountString = new ArrayList<String>();
        List<String> priceString = new ArrayList<String>();
        //String [] priceData = new String[l];
        for( i=0 ; i<l ; i++) {
            //rowData[i] = rowDataFormat;
            mealString.add(mealNames.get(i) + " ");
            amountString.add(mealAmount.get(i) + " 份");
            priceString.add(mealPrices.get(i) + " 元");
            totalAmount += mealPrices.get(i) * mealAmount.get(i);
        }

        tv_restaurant.setText(restName);
        */

        CustomAdapter customAdapter = new CustomAdapter(this,mealString,amountString,priceString);
        lv.setAdapter(customAdapter);
        lv.setDivider(null);
        lv.setDividerHeight(0);

        tv_total_amount.setText(totalAmount + " 元  ");
    }


    private void buttonSetup() {

        Button btCancel = (Button) findViewById(R.id.bt_cancel);
        Button btOk = (Button) findViewById(R.id.bt_ok);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupDoubleConfirmMessage();
            }
        });
    }

    private void popupDoubleConfirmMessage () {
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmOrderActivity.this);

        builder.setMessage(R.string.confirm_order_warning_message)
                .setTitle(R.string.confirm_order_warning_title);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button

                orderJsonRequest = buildOrderJsonString();
                Log.d(Constants.LOG_TAG, "request : " + orderJsonRequest);

                loginThenSubmitFinalOrder();

            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // If cancel, do nothing
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loginThenSubmitFinalOrder() {
        //loginSession();
        SharedPreferences sp = getSharedPreferences("account", 0);
        plateServiceManager.login(this);
    }

    /*
    private void submitFinalOrder(){
        Log.d(Constants.LOG_TAG, "Order Submit Starts, Order String : " + orderJsonRequest);

        PlateService.PlateTWOldAPI plateTW;
        plateTW = PlateService.getOldAPI(Constants.API_URI_PREFIX);

        PlateService.PlateTWAPI1 plateTWV1;
        plateTWV1 = PlateService.getAPI1(Constants.API_URI_PREFIX);

        plateTWV1.orderPost(orderJsonRequest, new Callback<PlateService.OrderPostResponse>() {
            @Override
            public void success(PlateService.OrderPostResponse r, Response response) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmOrderActivity.this);
                builder.setMessage(R.string.final_info_success_message)
                        .setTitle(R.string.final_info_success_title);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                        mainActivityIntent.putExtra("fragPosition", 1);
                        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainActivityIntent);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(Constants.LOG_TAG, error.getResponse().getReason());
                AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmOrderActivity.this);
                builder.setMessage(R.string.final_info_fail_message)
                        .setTitle(R.string.final_info_fail_title);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    // ----------- Login Related Functions : END ------------
    // FIXME: there may be better way to make this part be seperated

    private void loginSession() {
        if (accountInAppNotSet()) {
            View view = findViewById(android.R.id.content);
            Intent registerInent = new Intent(view.getContext(), RegisterActivity.class);
            registerInent.putExtra("message_type", Constants.FIRST_TIME);
            startActivity(registerInent);
        } else {
            // get user's phone_number and password to login
            SharedPreferences sp = getSharedPreferences("account", 0);
            String phone_number = sp.getString(Constants.SP_TAG_PHONE_NUMBER, null);
            String password = sp.getString(Constants.SP_TAG_PASSWORD, null);

            Log.d(Constants.LOG_TAG, "Account Info is already set: PN:" + phone_number + "\tPW:" + password);
            login(phone_number, password);
        }
    }

    private void login(String phone_number, String password) {
        CookieHandler.setDefault(new CookieManager());

        PlateService.PlateTWOldAPI plateTW;
        plateTW = PlateService.getOldAPI(Constants.API_URI_PREFIX);

        PlateService.PlateTWAPI1 plateTWV1;
        plateTWV1 = PlateService.getAPI1(Constants.API_URI_PREFIX);

        plateTWV1.login(phone_number, password, new Callback<Response>() {
            @Override public void success(Response r, Response response) {
                submitFinalOrder();
            }

            @Override public void failure(RetrofitError error) {
                View view = findViewById(android.R.id.content);
                Intent registerInent = new Intent(view.getContext(), RegisterActivity.class);
                registerInent.putExtra("message_type", Constants.SP_SAVED_BUT_LOGIN_FAIL);
                startActivity(registerInent);
            }
        });
    }

    private boolean accountInAppNotSet() {
        SharedPreferences sp = getSharedPreferences("account", 0);
        return !(sp.contains(Constants.SP_TAG_PHONE_NUMBER) &&
                sp.contains(Constants.SP_TAG_PASSWORD));
    }
    // ----------- Login Related Functions : END ------------
    */

    private String buildOrderJsonString() {

        //JSONObject object = new JSONObject();
        String result = "[";

        Cart cart = ((Plate)getApplication()).getCart();
        ArrayList<Cart.CartOrderItem> cos = cart.getOrderItems();
        int s = cart.getNumberOfItems(), i;

        for (i=0 ; i<s ; i++) {
            result += "{\"amount\":" + cos.get(i).amount + ", \"meal_id\": " + cos.get(i).meal_id + "}";
            if (i != s-1)   result += ", ";
        }
        /*
        int s = mealNames.size(), i;
        for (i=0 ; i<s ; i++) {
            result += "{\"amount\":" + mealAmount.get(i) + ", \"meal_id\": " + mealID.get(i) + "}";
            if (i != s-1)   result += ", ";
        }
        */
        result += "]";

        return result;
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
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(Constants.LOG_TAG, "home pressed");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(Constants.LOG_TAG, "system back pressed");
        finish();
    }

    private class CustomAdapter extends BaseAdapter {

        LayoutInflater inflater;
        List<String> mealName = new ArrayList<String>();
        List<String> mealAmount = new ArrayList<String>();
        List<String> mealPrice = new ArrayList<String>();

        public class ViewHolder{
            TextView tv_mealname;
            TextView tv_mealamount;
            TextView tv_mealprice;
        }
        public CustomAdapter(Context context, List<String> mealName, List<String> mealAmount, List<String> mealPrice){
            inflater = LayoutInflater.from(context);
            this.mealName = mealName;
            this.mealAmount = mealAmount;
            this.mealPrice = mealPrice;
        }
        //to disable listview click
        @Override
        public boolean isEnabled(int position) {
            return false;
        }
        public int getCount(){
            return mealName.size();
        }
        public Object getItem(int position){
            return mealName.get(position);
        }
        public long getItemId(int position){
            return position;
        }
        public View getView(final int arg0, View convertview, ViewGroup arg2) {
            ViewHolder viewHolder = null;
            if(convertview == null)
            {
                convertview = inflater.inflate(R.layout.listrow_confirm_order, null);

                viewHolder=new ViewHolder();
                viewHolder.tv_mealname = (TextView) convertview.findViewById(R.id.tv_mealname);
                viewHolder.tv_mealamount = (TextView) convertview.findViewById(R.id.tv_amount);
                viewHolder.tv_mealprice = (TextView) convertview.findViewById(R.id.tv_price);

                convertview.setTag(viewHolder);
            }
            else
            {
                viewHolder=(ViewHolder)convertview.getTag();
            }
            // set values
            viewHolder.tv_mealname.setText(mealName.get(arg0));
            viewHolder.tv_mealamount.setText(mealAmount.get(arg0));
            viewHolder.tv_mealprice.setText(mealPrice.get(arg0));

            return convertview;
        }

    }

    /* Callbacks */
    @Override
    public void updateRestaurantList() { throw new UnsupportedOperationException(); }
    @Override
    public void updateMenuList() { throw new UnsupportedOperationException(); }

    @Override
    public void loginSucceed() {
        plateServiceManager.orderPost(orderJsonRequest, this);
    }
    @Override
    public void loginFailed() {
        // go to register page
        View view = findViewById(android.R.id.content);
        Intent registerIntent = new Intent(view.getContext(), RegisterActivity.class);
        registerIntent.putExtra("message_type", Constants.SP_SAVED_BUT_LOGIN_FAIL);
        startActivity(registerIntent);
    }
    @Override
    public void notRegistered() {
        // go to register page
        View view = findViewById(android.R.id.content);
        Intent registerIntent = new Intent(view.getContext(), RegisterActivity.class);
        registerIntent.putExtra("message_type", Constants.FIRST_TIME);
        startActivity(registerIntent);
    }

    @Override
    public void orderPostSucceed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmOrderActivity.this);
        builder.setMessage(R.string.final_info_success_message)
                .setTitle(R.string.final_info_success_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                mainActivityIntent.putExtra("fragPosition", 1);
                mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainActivityIntent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    public void orderPostFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmOrderActivity.this);
        builder.setMessage(R.string.final_info_fail_message)
                .setTitle(R.string.final_info_fail_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void registerSucceed() { throw new UnsupportedOperationException(); }
    @Override
    public void registerFailed() { throw new UnsupportedOperationException(); }

    @Override
    public void orderGetSucceed(PlateService.OrderGetResponse orderGetResponse) { throw new UnsupportedOperationException(); }
    @Override
    public void orderGetSucceedEmpty() { throw new UnsupportedOperationException(); }
    @Override
    public void orderGetFailed() { throw new UnsupportedOperationException(); }

    @Override
    public void currentNsSucceed(int current_ns) { throw new UnsupportedOperationException(); }
    @Override
    public void currentNsFailed() { throw new UnsupportedOperationException(); }
}
