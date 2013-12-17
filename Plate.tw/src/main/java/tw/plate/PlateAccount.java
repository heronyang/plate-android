package tw.plate;

import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by heron on 12/1/13.
 *
 * Trying to create a class for handling login session.
 *
 */
public class PlateAccount {
    /*
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

    }

    private boolean accountInAppNotSet() {
        SharedPreferences sp = getActivity().getSharedPreferences("account",
                0);
        return !(sp.contains(Constants.SP_TAG_PHONE_NUMBER) &&
                sp.contains(Constants.SP_TAG_PASSWORD));
    }
    */
}
