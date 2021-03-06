package tw.plate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceiptFragment extends Fragment{

    private int rest_id = Constants.ORDER_EMPTY;
    private String current_ns;
    private Tools tools;
    View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        v = inflater.inflate(R.layout.receipt_frag, container, false);

        final View currNumButton = v.findViewById(R.id.bn_current_ns);
        final View refreshButton = v.findViewById(R.id.btn_refresh);
        tools = new Tools();
        current_ns = "...";
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(Constants.LOG_TAG,"refresh clicked");
                updateReceiptContent();

                /*
                PlateServiceManager plateServiceManager = ((Plate)getActivity().getApplication()).getPlateServiceManager();
                plateServiceManager.login(getActivity());
                */

                /*
                refreshButton.setBackgroundResource(R.drawable.rounded_rectangle_frame_pressed);
                int delay_time =Constants.PRESSED_TIME;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        refreshButton.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_frame));
                    }
                }, delay_time);
                */
            }
        });

        currNumButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                /* DO SOMETHING UPON THE CLICK */
                    Log.d(Constants.LOG_TAG, "clicked");
                    PlateServiceManager plateServiceManager = ((Plate) getActivity().getApplication()).getPlateServiceManager();
                    plateServiceManager.current_ns(rest_id, getActivity());
                    showCurrentNS();

                    /*currNumButton.setBackgroundResource(R.drawable.rounded_rectangle_frame_pressed);
                    int delay_time =Constants.FLIP_BACK_TIME;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void run() {
                            currNumButton.setBackground(getResources().getDrawable(R.drawable.rounded_rectangle_frame));
                        }
                    }, delay_time);
                    */
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
            //updateReceiptContent();
        }
    }

    @Override
    public void onStart() {
        // TODO: [sp] load previous data
        SharedPreferences sp = getActivity().getSharedPreferences("offline_data", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        if (sp.contains("jsonLastOrder") && sp.contains("jsonLastOrderItems")) {
            String jsonLastOrder = sp.getString("jsonLastOrder", "");
            String jsonLastOrderItems = sp.getString("jsonLastOrderItems", "");

            Type typeOrderV1 = new TypeToken<PlateService.OrderV1>() {}.getType();
            Type typeOrderItemV1 = new TypeToken<List<PlateService.OrderItemV1>>() {}.getType();

            PlateService.OrderV1 lo = gson.fromJson(jsonLastOrder, typeOrderV1);
            List<PlateService.OrderItemV1> orderItems = gson.fromJson(jsonLastOrderItems, typeOrderItemV1);

            displayResponse(orderItems, lo);
        }

        // then, update if needed
        boolean updateReceipt = getActivity().getIntent().getBooleanExtra("updateReceipt", false);
        Log.d(Constants.LOG_TAG, "updateReceipt extra: " + updateReceipt );
        if (updateReceipt) {
            updateReceiptContent();
            getActivity().getIntent().removeExtra("updateReceipt");
        }
        super.onStart();
    }

    public void updateReceiptContent() {
        Log.d(Constants.LOG_TAG, "update receipt content");
        PlateServiceManager plateServiceManager;
        plateServiceManager = ((Plate)(getActivity().getApplication())).getPlateServiceManager();
        plateServiceManager.login(getActivity());
    }

    private void displayResponse(List<PlateService.OrderItemV1> orderItems, PlateService.OrderV1 lo){

        // get UI components
        TextView tv_number = (TextView) getView().findViewById(R.id.tv_slip_num);
        TextView tv_rest = (TextView) getView().findViewById(R.id.tv_rec_restaurant);
        TextView tv_time = (TextView) getView().findViewById(R.id.tv_order_time);
        TextView tv_total = (TextView) getView().findViewById(R.id.tv_total);
        TextView tv_status = (TextView) getView().findViewById(R.id.tv_order_status);
        ListView lv = (ListView) v.findViewById(R.id.lv_receipt);

        // build strings to display (orderItems)
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
            amountString.add(i,amount+"份");
            priceString.add(i,price+"元");
        }

        // display lo's content: lo.status, lo.ctime, lo.restaurant, lo.number_slip
        String stringTotalPrice = getString(R.string.total_amount)+" "+totalPrice+" 元\n";
        String status="";
        int textColor=0;
        switch(lo.status){
            case 0:
                status = getString(R.string.status1);
                textColor = getResources().getColor(R.color.blue);
                break;
            case 1:
                status = getString(R.string.status2);
                textColor = getResources().getColor(R.color.green);
                break;
            case 2:
                status = getString(R.string.status3);
                textColor = getResources().getColor(R.color.black);
                break;
            case 3:
                status = getString(R.string.status4);
                textColor = getResources().getColor(R.color.red);
                break;
            case 4:
                status = getString(R.string.status5);
                textColor = getResources().getColor(R.color.blue);
                break;
            case 5: status = getString(R.string.status6);
                break;
            case 6: status = getString(R.string.status7);
                break;
            default: throw new RuntimeException("Order Status undefined");
        }

        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        tv_time.setText(df.format(lo.ctime));
        tv_number.setText(tools.formattedNS(lo.pos_slip_number));
        tv_rest.setText(lo.restaurant.name);
        rest_id = lo.restaurant.rest_id;

        //
        tv_total.setText(stringTotalPrice);
        tv_status.setText("("+ status +")");

        CustomAdapter customAdapter = new CustomAdapter(this.getActivity(),mealString,amountString,priceString);
        lv.setAdapter(customAdapter);
        lv.setDivider(null);
        lv.setDividerHeight(0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static ReceiptFragment newInstance(){
        ReceiptFragment receiptFragment = new ReceiptFragment();
        return receiptFragment;
    }

    public void onBackPressed() {
        Intent mainIntent = new Intent(getActivity().getApplicationContext(),MainActivity.class);
        startActivity(mainIntent);
        Log.d(Constants.LOG_TAG,"back pressed");
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
        public View getView(int arg0, View convertview, ViewGroup arg2) {
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

    public void loginSucceed() {
        // OrderGet here

        /*
        TextView tv = (TextView)getView().findViewById(R.id.tv_rec_restaurant);
        tv.setText("Login Succeeded!");
        */
    }

    public void orderGetSucceedEmpty() {
        TextView tv = (TextView) getView().findViewById(R.id.tv_total);
        tv.setText(getString(R.string.receipt_noorder));
    }

    public void orderGetSucceed(PlateService.OrderGetResponse orderGetResponse) {
        PlateService.OrderV1 lo = orderGetResponse.last_order;
        List<PlateService.OrderItemV1> orderItems = orderGetResponse.order_items;
        Log.d(Constants.LOG_TAG, String.format("%s %s %s %d %d", lo.ctime, lo.mtime, lo.restaurant.name, lo.pos_slip_number, lo.status));

        displayResponse(orderItems, lo);

        // TODO: [sp] store orderItems, lo
        storeContentToOffline(orderItems, lo);
    }

    private void storeContentToOffline(List<PlateService.OrderItemV1> orderItems, PlateService.OrderV1 lo){
        Gson gson = new Gson();
        String jsonLastOrder = gson.toJson(lo);
        String jsonLastOrderItems = gson.toJson(orderItems);

        SharedPreferences sp = getActivity().getSharedPreferences("offline_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("jsonLastOrder", jsonLastOrder).commit();
        ed.putString("jsonLastOrderItems", jsonLastOrderItems).commit();
    }

    public void notRegistered() {
        TextView tv = (TextView) getView().findViewById(R.id.tv_total);
        tv.setText(getString(R.string.notRegistered));
    }

    public void waitForRegisterCompleted() {
        TextView tv = (TextView) getView().findViewById(R.id.tv_total);
        tv.setText(getString(R.string.waitForRegisterCompleted));
    }

    public void currentNsSucceed(int _current_ns) {
        current_ns = tools.formattedNS(_current_ns);
        Log.d(Constants.LOG_TAG,"Curr NS:"+current_ns);
        showCurrentNS();
    }
}
