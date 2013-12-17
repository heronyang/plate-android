package tw.plate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ReceiptFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.receipt_frag, container, false);

        return v;
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
            @Override public void success(Response r, Response response) {
                updateReceiptContent();
            }

            @Override public void failure(RetrofitError error) {
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
                TextView tv = (TextView)getView().findViewById(R.id.tvReceipt);
                TextView tv_price = (TextView) getView().findViewById(R.id.tv_receipt_price);
                TextView tv_welcome = (TextView) getView().findViewById(R.id.tv_receipt_welcome);
                TextView tv_number = (TextView) getView().findViewById(R.id.tv_slip_num);
                TextView tv_rest = (TextView) getView().findViewById(R.id.tv_rec_restaurant);
                tv_welcome.setText(getResources().getString(R.string.receipt_message));

                TextView tv_time = (TextView) getView().findViewById(R.id.tv_order_time);

                // if empty
                Log.d(Constants.LOG_TAG, "Ok, success!");
                if (response.getStatus() == 204) {
                    tv.setText("No Order!");
                }
                // if show
                else {
                    PlateService.OrderV1 lo = orderGetResponse.last_order;
                    Log.d(Constants.LOG_TAG, String.format("%s %s %s %d %d", lo.ctime, lo.mtime, lo.restaurant.name, lo.pos_slip_number, lo.status));

                    String outputString = "";
                    String stringPrice = "";
                    List<PlateService.OrderItemV1> orderItems = orderGetResponse.order_items;
                    int nOrderItem = orderItems.size(), i;
                    int totalPrice=0;
                    for ( i=0 ; i<nOrderItem ; i++ ) {
                        PlateService.Meal meal = orderItems.get(i).meal;
                        int amount = orderItems.get(i).amount;
                        int price = amount*orderItems.get(i).meal.meal_price;
                        totalPrice+=price;
                        outputString += meal.meal_name + " * " + amount +"個\n";
                        stringPrice +=  price + "元\n";
                    }
                    outputString += "\n\n"+getString(R.string.total_amount)+" "+totalPrice+"元\n";
                   // int slipNumber = lo.pos_slip_number;
                    tv_time.setText(lo.ctime);
                    tv_number.setText(""+lo.pos_slip_number);
                    tv_rest.setText(lo.restaurant.name);
                    tv.setText(outputString);
                    tv_price.setText(stringPrice);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(Constants.LOG_TAG, "Failed! retrofit  " + error.getMessage()  + error.getResponse().getStatus());
            }
        });
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
}
