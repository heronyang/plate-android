package tw.plate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RegisterActivity extends Activity {

    private String phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
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
            Log.d(Constants.LOG_TAG, "Wrong phone number input");
            return;
        }

        // generate
        SecureRandom random = new SecureRandom();
        String password = new BigInteger(130, random).toString(32);

        // save back
        SharedPreferences sp = getSharedPreferences("account", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.clear();
        ed.putString(Constants.SP_TAG_PASSWORD, password).commit();
        ed.putString(Constants.SP_TAG_PHONE_NUMBER, phone_number).commit();

        Log.d(Constants.LOG_TAG, "phone_number = " + phone_number + "\tPS = " + password);

        // register
        callRegisterAPI(phone_number, password);
    }

    private void callRegisterAPI(String phone_number, String password) {

        PlateService.PlateTWOldAPI plateTW;
        plateTW = PlateService.getOldAPI(Constants.API_URI_PREFIX);

        PlateService.PlateTWAPI1 plateTWV1;
        plateTWV1 = PlateService.getAPI1(Constants.API_URI_PREFIX);

        plateTWV1.register(phone_number, password, Constants.PASSWORD_TYPE, new Callback<Response>() {
            @Override public void success(Response r, Response response) {
                popupMessageAndExit();
            }

            @Override public void failure(RetrofitError error) {
                Log.d(Constants.LOG_TAG, "Can't Register");
            }
        });

    }

    private void popupMessageAndExit() {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.register_success_message)
                .setTitle(R.string.register_success_title);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivityIntent);
            }
        });

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        // 4. Show
        dialog.show();
    }

}
