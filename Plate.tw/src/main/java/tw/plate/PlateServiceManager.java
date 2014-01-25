package tw.plate;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by heron on 1/2/14.
 */
public class PlateServiceManager{

    //
    PlateService.PlateTWAPI1 plateTWV1;
    PlateService.PlateTWOldAPI plateTW;

    /* data */
    static private List<PlateService.Restaurant> restaurantList = new ArrayList<PlateService.Restaurant>();
    static private List<PlateService.Meal> mealList = new ArrayList<PlateService.Meal>();

    /* getters */
    public List<PlateService.Meal> getMealList(){
        return mealList;
    }
    public List<PlateService.Restaurant> getRestaurantList() {
        return restaurantList;
    }

    /* callback */
    public interface PlateManagerCallback {
        void updateRestaurantList();
        void updateMenuList();

        void loginSucceed();
        void loginFailed();
        void notRegistered();

        void orderPostSucceed();
        void orderPostFailed(int errStatus);

        void orderGetSucceed(PlateService.OrderGetResponse orderGetResponse);
        void orderGetSucceedEmpty();
        void orderGetFailed();

        void registerSucceed();
        void registerFailed();

        void currentNsSucceed(int current_ns);
        void currentNsFailed();

        void currentCookingOrdersSucceed(int current_cooking_orders);
    }

    /* Constructor */
    public PlateServiceManager() {
        CookieHandler.setDefault(new CookieManager());

        plateTW = PlateService.getOldAPI(Constants.API_URI_PREFIX);
        plateTWV1 = PlateService.getAPI1(Constants.API_URI_PREFIX);
    }

    /* API: restaurants */
    public void restaurants(int locationId, Activity activity) {
        final PlateManagerCallback callerActivity = (PlateManagerCallback)activity;
        //
        plateTW.restaurants(locationId, new Callback<PlateService.RestaurantResponse>() {
            @Override
            public void success(PlateService.RestaurantResponse rs, Response response) {
                restaurantList.clear();
                for (PlateService.Restaurant r: rs.list) {
                    restaurantList.add(r);
                }
                Log.d(Constants.LOG_TAG, "Update: Success");
                callerActivity.updateRestaurantList();
            }
            @Override
            public void failure(RetrofitError error) {
                // redirect to login page
                Log.d(Constants.LOG_TAG, "Update: Error : " + error.getMessage());
            }
        });
    }

    /* API: menu */
    public void menu(int restId, Activity activity) {
        final PlateManagerCallback callerActivity = (PlateManagerCallback)activity;
        //
        plateTW.menu(restId, new Callback<PlateService.MenuResponse>() {
            @Override
            public void success(PlateService.MenuResponse ms, Response response) {
                mealList.clear();
                for (PlateService.Meal m : ms.meal_list) {
                    mealList.add(m);
                }
                callerActivity.updateMenuList();
            }

            @Override
            public void failure(RetrofitError e) {
                Log.d(Constants.LOG_TAG, "menu: failure");
            }
        });
    }

    /* API: orderPost */
    public void orderPost(String orderJsonRequest, Activity activity){
        final PlateManagerCallback callerActivity = (PlateManagerCallback)activity;

        plateTWV1.orderPost(orderJsonRequest, new Callback<PlateService.OrderPostResponse>() {
            @Override
            public void success(PlateService.OrderPostResponse r, Response response) {
                callerActivity.orderPostSucceed();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(Constants.LOG_TAG,"network" + error.getResponse().getStatus() );
                Log.d(Constants.LOG_TAG, error.getResponse().getReason());
                callerActivity.orderPostFailed(error.getResponse().getStatus());
            }
        });
    }

    /* API: register */
    public void register(String phone_number, String password, String password_type, String regid, Activity activity) {
        final PlateManagerCallback callerActivity = (PlateManagerCallback)activity;
        Log.d(Constants.LOG_TAG, "regid >> " + regid);
        plateTWV1.register(phone_number, password, Constants.PASSWORD_TYPE, regid, new Callback<Response>() {
            @Override
            public void success(Response r, Response response) {
                callerActivity.registerSucceed();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(Constants.LOG_TAG, "Can't Register, status code = " + error.getResponse().getStatus());
                callerActivity.registerFailed();
            }
        });
    }

    /* API: orderGet */
    public void orderGet(Activity activity) {
        final PlateManagerCallback callerActivity = (PlateManagerCallback)activity;
        plateTWV1.orderGet(new Callback<PlateService.OrderGetResponse>() {
            @Override
            public void success(PlateService.OrderGetResponse orderGetResponse, Response response) {
                // if empty
                Log.d(Constants.LOG_TAG, "Ok, success!");
                if (response.getStatus() == 204) {
                    callerActivity.orderGetSucceedEmpty();
                }
                // if show
                else {
                    callerActivity.orderGetSucceed(orderGetResponse);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(Constants.LOG_TAG, "Failed! retrofit  " + error.getMessage() + error.getResponse().getStatus());
                callerActivity.orderGetFailed();
            }
        });
    }

    /* API: current_ns */
    public void current_ns(int rest_id, Activity activity) {
        final PlateManagerCallback callerActivity = (PlateManagerCallback)activity;
        plateTWV1.current_ns(rest_id, new Callback<PlateService.CurrentNSResponse>() {
            @Override
            public void success(PlateService.CurrentNSResponse r, Response response) {
                callerActivity.currentNsSucceed(r.current_ns);
            }
            @Override
            public void failure(RetrofitError error) {
                callerActivity.currentNsFailed();
                Log.d(Constants.LOG_TAG, "Can't get the current ns" + error.getResponse().getStatus());
            }
        });

    }

    /* API: current_cooking_orders */
    public void current_cooking_orders(int rest_id, Activity activity) {
        final PlateManagerCallback callerActivity = (PlateManagerCallback)activity;
        plateTWV1.current_cooking_orders(rest_id, new Callback<PlateService.CurrentCookingOrdersResponse>() {
            @Override
            public void success(PlateService.CurrentCookingOrdersResponse r, Response response) {
                callerActivity.currentCookingOrdersSucceed(r.current_cooking_orders);
            }
            @Override
            public void failure(RetrofitError error) {
                // just ignore at this point
                Log.d(Constants.LOG_TAG, "Can't get the current cooking orders" + error.getResponse().getStatus());
            }
        });

    }

    // ----------- Login Related Functions : END ------------
    public void login(Activity activity) {
        final PlateManagerCallback callerActivity = (PlateManagerCallback)activity;
        SharedPreferences sp = activity.getSharedPreferences("account", 0);
        if (accountInAppNotSet(sp)) {
            // jump to register page
            Log.d(Constants.LOG_TAG, "not registered");
            callerActivity.notRegistered();
            return;
        }

        String phone_number = sp.getString(Constants.SP_TAG_PHONE_NUMBER, null);
        String password = sp.getString(Constants.SP_TAG_PASSWORD, null);

        Log.d(Constants.LOG_TAG, "start to login");
        plateTWV1.login(phone_number, password, new Callback<Response>() {
            @Override public void success(Response r, Response response) {
                callerActivity.loginSucceed();
            }
            @Override public void failure(RetrofitError error) {
                callerActivity.loginFailed();
            }
        });
    }

    /* Helper Functions */
    private boolean accountInAppNotSet(SharedPreferences sp) {
        return !(sp.contains(Constants.SP_TAG_PHONE_NUMBER) &&
                 sp.contains(Constants.SP_TAG_PASSWORD));
    }
    // ----------- Login Related Functions : END ------------
}
