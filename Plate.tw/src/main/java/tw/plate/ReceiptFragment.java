package tw.plate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ReceiptFragment extends Fragment {

    private int rest_id = Constants.ORDER_EMPTY;
    private int current_ns = Constants.ORDER_EMPTY;
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        v = inflater.inflate(R.layout.receipt_frag, container, false);

        final View button = v.findViewById(R.id.bn_current_ns);
        button.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* DO SOMETHING UPON THE CLICK */
                    Log.d(Constants.LOG_TAG, "clicked");

                    PlateService.PlateTWAPI1 plateTWV1;
                    plateTWV1 = PlateService.getAPI1(Constants.API_URI_PREFIX);

                    plateTWV1.current_ns(rest_id, new Callback<PlateService.CurrentNSResponse>() {
                        @Override
                        public void success(PlateService.CurrentNSResponse r, Response response) {
                            current_ns = r.current_ns;
                            showCurrentNS();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d(Constants.LOG_TAG, "Can't get the current ns" + error.getResponse().getStatus());
                        }
                    });

                }
            }
        );

        return v;
    }

    private void showCurrentNS() {
        final Button button = (Button)getView().findViewById(R.id.bn_current_ns);
        button.setText("No. " + current_ns);

        // flip back
        int delay_time = Constants.FLIP_BACK_TIME;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setText(getString(R.string.receipt_button_text));
            }
        }, delay_time);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.d(Constants.LOG_TAG, "Time to setup login");
            loginSession();
        } else {
            // Do your Work
        }
    }

    private void loginSession() {
        if (accountInAppNotSet()) {
            Intent registerInent = new Intent(getActivity(), RegisterActivity.class);
            registerInent.putExtra("message_type", Constants.FIRST_TIME);
            startActivity(registerInent);
        } else {
            // get user's phone_number and password to login
            SharedPreferences sp = getActivity().getSharedPreferences("account", 0);
            String phone_number = sp.getString(Constants.SP_TAG_PHONE_NUMBER, null);
            String password = sp.getString(Constants.SP_TAG_PASSWORD, null);

            Log.d(Constants.LOG_TAG, "Account Info is already set: PN:" + phone_number + "\tPW:" + password);
            login(phone_number, password);
        }
    }

    private void login(String phone_number, String password) {
        PlateService.PlateTWOldAPI plateTW;
        plateTW = PlateService.getOldAPI(Constants.API_URI_PREFIX);

        PlateService.PlateTWAPI1 plateTWV1;
        plateTWV1 = PlateService.getAPI1(Constants.API_URI_PREFIX);

        plateTWV1.login(phone_number, password, new Callback<Response>() {
            @Override
            public void success(Response r, Response response) {
                updateReceiptContent();
            }

            @Override
            public void failure(RetrofitError error) {
                Intent registerInent = new Intent(getActivity(), RegisterActivity.class);
                registerInent.putExtra("message_type", Constants.SP_SAVED_BUT_LOGIN_FAIL);
                startActivity(registerInent);
            }
        });
    }

    private void updateReceiptContent() {
        TextView tv = (TextView)getView().findViewById(R.id.tvReceipt);
        tv.setText("Login Succeeded!");


        PlateService.PlateTWOldAPI plateTW;
        plateTW = PlateService.getOldAPI(Constants.API_URI_PREFIX);

        PlateService.PlateTWAPI1 plateTWV1;
        plateTWV1 = PlateService.getAPI1(Constants.API_URI_PREFIX);

        plateTWV1.orderGet(new Callback<PlateService.OrderGetResponse>() {
            @Override
            public void success(PlateService.OrderGetResponse orderGetResponse, Response response) {
                // if empty
                Log.d(Constants.LOG_TAG, "Ok, success!");
                if (response.getStatus() == 204) {
                    TextView tv = (TextView) getView().findViewById(R.id.tv_message);
                    tv.setText(getString(R.string.receipt_noorder));
                }
                // if show
                else {
                    PlateService.OrderV1 lo = orderGetResponse.last_order;
                    List<PlateService.OrderItemV1> orderItems = orderGetResponse.order_items;
                    Log.d(Constants.LOG_TAG, String.format("%s %s %s %d %d", lo.ctime, lo.mtime, lo.restaurant.name, lo.pos_slip_number, lo.status));

                    displayResponse(orderItems,lo);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(Constants.LOG_TAG, "Failed! retrofit  " + error.getMessage() + error.getResponse().getStatus());
            }
        });
    }
    private void displayResponse(List<PlateService.OrderItemV1> orderItems,PlateService.OrderV1 lo){
        //TextView tv = (TextView) getView().findViewById(R.id.tvReceipt);
        //TextView tv_price = (TextView) getView().findViewById(R.id.tv_receipt_price);
        TextView tv_number = (TextView) getView().findViewById(R.id.tv_slip_num);
        TextView tv_rest = (TextView) getView().findViewById(R.id.tv_rec_restaurant);
        TextView tv_time = (TextView) getView().findViewById(R.id.tv_order_time);
        TextView tv_total = (TextView) getView().findViewById(R.id.tv_total);
        ListView lv = (ListView) v.findViewById(R.id.lv_receipt);

        List<String> mealString = new ArrayList<String>();
        List<String> amountString = new ArrayList<String>();
        List<String> priceString = new ArrayList<String>();
        int nOrderItem = orderItems.size(), i;
        int totalPrice = 0;
        for (i = 0; i < nOrderItem; i++) {
            PlateService.Meal meal = orderItems.get(i).meal;
            int amount = orderItems.get(i).amount;
            int price = amount*orderItems.get(i).meal.meal_price;
            totalPrice+=price;
            mealString.add(i,meal.meal_name + " ");
            amountString.add(i,amount+" 份");
            priceString.add(i,price+" 元");
        }
        String stringTotalPrice = getString(R.string.total_amount)+" "+totalPrice+" 元\n";
        // int slipNumber = lo.pos_slip_number;

        tv_time.setText(lo.ctime);
        tv_number.setText("" + lo.pos_slip_number);
        tv_rest.setText(lo.restaurant.name);
        rest_id = lo.restaurant.rest_id;
        tv_total.setText(stringTotalPrice);

        CustomAdapter customAdapter = new CustomAdapter(this.getActivity(),mealString,amountString,priceString);
        lv.setAdapter(customAdapter);
        lv.setDivider(null);
        lv.setDividerHeight(0);
        //tv.setText(outputString);
        //tv_price.setText(stringPrice);


    }

    private boolean accountInAppNotSet() {
        SharedPreferences sp = getActivity().getSharedPreferences("account",
                0);
        return !(sp.contains(Constants.SP_TAG_PHONE_NUMBER) &&
                 sp.contains(Constants.SP_TAG_PASSWORD));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieHandler.setDefault(new CookieManager());
    }

    public static ReceiptFragment newInstance(){
        ReceiptFragment receiptFragment = new ReceiptFragment();
        return receiptFragment;
    }

    public void onBackPressed() {
        Intent mainIntent = new Intent(getActivity(),MainActivity.class);
        startActivity(mainIntent);
        return;
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
                convertview = inflater.inflate(R.layout.listrow_receipt, null);

                viewHolder=new ViewHolder();
                viewHolder.tv_mealname = (TextView) convertview.findViewById(R.id.tvReceipt);
                viewHolder.tv_mealamount = (TextView) convertview.findViewById(R.id.tvAmount);
                viewHolder.tv_mealprice = (TextView) convertview.findViewById(R.id.tv_receipt_price);

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

}
