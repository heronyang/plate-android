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

    String orderJsonRequest;

    PlateServiceManager plateServiceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        plateServiceManager = ((Plate) this.getApplication()).getPlateServiceManager();

        setContentView(R.layout.activity_confirm_order);

        listviewAndTotalAmountSetup();
        buttonSetup();

        Cart cart = ((Plate)getApplication()).getCart();
        plateServiceManager.current_cooking_orders(cart.getRestaurantId(), this);
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
        tv_restaurant.setText(cart.getRestaurantName());


        CustomAdapter customAdapter = new CustomAdapter(this,mealString,amountString,priceString);
        lv.setAdapter(customAdapter);
        lv.setDivider(null);
        lv.setDividerHeight(0);

        tv_total_amount.setText(totalAmount + " 元");
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
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    @Override
    public void orderPostFailed(String errorMsg, int errStatus) {
        String msg = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmOrderActivity.this);

        switch (errStatus) {
            case 460:
                msg += getString(R.string.ERROR_MESSAGE_EXCEED_MAX_TOTAL_PRICE);
                break;
            case 461:
                msg += getString(R.string.ERROR_MESSAGE_INCOMPLETE_ORDER);
                break;
            case 462:
                msg += getString(R.string.ERROR_MESSAGE_REST_CLOSED);
                break;
            case 463:
                msg += getString(R.string.ERROR_MESSAGE_REST_BUSY);
                break;
            case 464:
                msg += getString(R.string.ERROR_MESSAGE_REST_UNKNOWN_STATUS);
                break;
            default:
                break;
        }
        builder.setMessage(errorMsg)
                .setTitle(R.string.final_info_fail_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void currentCookingOrdersSucceed(int current_cooking_orders) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmOrderActivity.this);
        builder.setMessage(getString(R.string.current_cooking_orders_popup_body) + current_cooking_orders)
                .setTitle(R.string.current_cooking_orders_popup_title);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
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
