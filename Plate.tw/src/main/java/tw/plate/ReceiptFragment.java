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

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ReceiptFragment extends Fragment{

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
                startActivity(registerInent);
            }
        });
    }

    private void updateReceiptContent() {
        TextView tv = (TextView)getView().findViewById(R.id.tvReceipt);
        tv.setText("Login Succeeded!");
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


    }

    public static ReceiptFragment newInstance(){
        ReceiptFragment receiptFragment = new ReceiptFragment();
        return receiptFragment;
    }
}
