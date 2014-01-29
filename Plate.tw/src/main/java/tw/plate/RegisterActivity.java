package tw.plate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.GsonConverter;
import retrofit.mime.TypedInput;

import static tw.plate.GcmUtilities.SENDER_ID;
import static tw.plate.GcmUtilities.PROPERTY_APP_VERSION;
import static tw.plate.GcmUtilities.PROPERTY_REG_ID;


public class RegisterActivity extends Activity implements PlateServiceManager.PlateManagerCallback{

    private String phone_number, password;

    //================================================================================
    // GCM
    //================================================================================
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    String regid = "";

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setMessage(R.string.require_google_service_message)
                    .setTitle(R.string.require_google_service_title);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String url = Constants.GOOGLE_PLAY_SERVICE;
                    Intent intentLink = new Intent(Intent.ACTION_VIEW);
                    intentLink.setData(Uri.parse(url));
                    startActivity(intentLink);
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            return false;
        }
        return true;
    }

    private void gcm_setup() {
        checkPlayServices();

        gcm = GoogleCloudMessaging.getInstance(this);
        regid = getRegistrationId(context);

        if (regid.isEmpty()) {
            registerInBackground();
        }

        Log.d(Constants.LOG_TAG, "regid = " + regid);
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(Constants.LOG_TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        /* FIXME: there's a bug in getAppVersion, skip first
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.d(Constants.LOG_TAG, "App version changed.");
            return "";
        }
        */
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
        // Heron: send the registered ID back to api.plate.tw
        // NOTE: Since registration ID is required for registration,
        // we only submit the register API if regid is got
        Log.d(Constants.LOG_TAG, "regid = " + regid);
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void,Void,String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                Log.d(Constants.LOG_TAG, msg + "\n");
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d(Constants.LOG_TAG, msg + "\n");
            }
        }.execute();
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        //int appVersion = getAppVersion(context);
        //Log.d(Constants.LOG_TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        //editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }


    //================================================================================
    // Runtime Override Events
    //================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent intent = getIntent();
        int message_type = intent.getIntExtra("message_type", 0);

        String message;
        switch (message_type) {
            case Constants.FIRST_TIME:
                message = getString(R.string.register_first_time);
                break;
            case Constants.SP_SAVED_BUT_LOGIN_FAIL:
                message = getString(R.string.register_sp_saved_but_login_fail);
                break;
            case Constants.SECOND_CHANCE_TO_REGISTER:
                message = getString(R.string.register_second_chance_to_register);
                break;
            default:
                message = getString(R.string.register_default);
                break;
        }

        TextView tv = (TextView)findViewById(R.id.tvRegisterMessage);
        tv.setText(message);

        gcm_setup();


        // setup edit text
        EditText editText = (EditText)findViewById(R.id.phone_number_et);
        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // TODO Auto-generated method stub
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submit();
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:{
                this.finish();
                return true;
            }
            case R.id.action_submit:
                submit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkAndReceiveInputPhoneNumber() {

        final EditText et = (EditText)findViewById(R.id.phone_number_et);

        if (et.getText() == null) {
            return false;
        }

        String pattern = "^09(\\d{8})$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(et.getText().toString());

        if (!m.find()) {
            return false;
        }

        phone_number = et.getText().toString();
        return true;
    }

    private void submit() {

        if (!checkAndReceiveInputPhoneNumber()) {
            // wrong input
            //popupMessage(getString(R.string.register_wrong_input_format_title), getString(R.string.register_wrong_input_format_message));
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.register_wrong_input_format_message),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // NOTE: use registration ID as password (fast implementation)
        if (regid.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.register_wait_for_play_service_message)
                    .setTitle(R.string.register_wait_for_play_service_title);

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            return;
        }

        SecureRandom random = new SecureRandom();
        password = new BigInteger(130, random).toString(32);
        // String password = regid;

        // register
        callRegisterAPI(phone_number, password);
    }

    private void callRegisterAPI(String phone_number, String password) {

        PlateServiceManager plateServiceManager = ((Plate)this.getApplication()).getPlateServiceManager();
        plateServiceManager.register(phone_number, password, Constants.PASSWORD_TYPE, regid, this);

    }
    private void popupMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setNoMoreFirstTime();
                intentMain();
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void setNoMoreFirstTime() {
        // set the first time app use as false
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("first_time", "1").commit();
    }

    private void intentMain() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
    }

    /* Callbacks */
    @Override
    public void updateRestaurantList() { throw new UnsupportedOperationException(); };
    @Override
    public void updateMenuList() { throw new UnsupportedOperationException(); };

    @Override
    public void loginSucceed() { throw new UnsupportedOperationException(); };
    @Override
    public void loginFailed() { throw new UnsupportedOperationException(); };
    @Override
    public void notRegistered() { throw new UnsupportedOperationException(); };

    @Override
    public void orderPostSucceed() { throw new UnsupportedOperationException(); };
    @Override
    public void orderPostFailed(String errorMsg, int errorStatus) { throw new UnsupportedOperationException(); };
    @Override
    public void orderGetSucceed(PlateService.OrderGetResponse orderGetResponse) { throw new UnsupportedOperationException(); }
    @Override
    public void orderGetSucceedEmpty() { throw new UnsupportedOperationException(); }
    @Override
    public void orderGetFailed() { throw new UnsupportedOperationException(); }

    @Override
    public void registerSucceed() {
        // save back
        SharedPreferences sp = getSharedPreferences("account", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.clear();
        ed.putString(Constants.SP_TAG_PASSWORD, password).commit();
        ed.putString(Constants.SP_TAG_PHONE_NUMBER, phone_number).commit();

        Log.d(Constants.LOG_TAG, "phone_number = " + phone_number + "\tPS = " + password);

        popupMessage(getString(R.string.register_success_title), getString(R.string.register_success_message));
    };
    @Override
    public void registerFailed(RetrofitError error) {
        if (error.getResponse().getStatus() == 470) {

            Response response = error.getResponse();
            TypedInput body = response.getBody();
            Gson gson = new GsonBuilder().create();
            GsonConverter gsonConverter = new GsonConverter(gson);
            String error_msg = getString(R.string.order_fail_default_message);
            try {
                PlateService.ErrorResponse er;
                er = (PlateService.ErrorResponse)gsonConverter.fromBody(body, PlateService.ErrorResponse.class);
                error_msg = er.error_msg;
                Log.d(Constants.LOG_TAG, "error_msg >> " + er.error_msg);
            } catch (ConversionException e) {
                Log.d(Constants.LOG_TAG, "conversion error >> " + e.getMessage());
            } catch (Exception e) {
                Log.d(Constants.LOG_TAG, "other casting exception >> " + e.getMessage());
            }
            popupMessage(getString(R.string.try_again_later_title), error_msg);
            return;
        }
        popupMessage(getString(R.string.register_submit_api_error_title), getString(R.string.register_submit_api_error_message));
    };
    @Override
    public void currentNsSucceed(int current_ns) { throw new UnsupportedOperationException(); }
    @Override
    public void currentNsFailed() { throw new UnsupportedOperationException(); }
    @Override
    public void currentCookingOrdersSucceed(int current_cooking_orders) { throw new UnsupportedOperationException(); }



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
