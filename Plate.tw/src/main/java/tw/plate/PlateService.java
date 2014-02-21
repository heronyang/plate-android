package tw.plate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public class PlateService {
    public class Recommendation {
        public String name;
        public String pic_uri;
        public int price;
        public String restaurant_name;
    }
    public class RecommendationResponse {
        public int success;
        public List<Recommendation> list;
    }

    public class Restaurant {
        public int location;
        public String name;
        public int rest_id;
        public String description;
        public boolean is_open;
        public String closed_reason;
    }

    public class RestaurantResponse {
        public int success;
        public List<Restaurant> list;
    }

    public class Meal {
        public int meal_price;
        public String meal_name;
        public int meal_id;
    }
    public class MenuResponse {
        public int success;
        public List<Meal> meal_list;
    }

    public class Order {
        public int number_slip;
        public int number_slip_index;
        public int rest_id;
        public String rest_name;
        public int status;
        public Date time;
    }
    public class StatusResponse {
        public boolean success;
        public List<Order> list;
    }

    public class OrderItem {
        public int amount;
        public int meal_id;
        public String meal_name;
        public int meal_price;
    }
    public class StatusDetailResponse {
        public boolean success;
        public List<OrderItem> list;
    }

    public static class MealAmount {
        public int meal_id;
        public int amount;
    }

    // API1: new added
    public class OrderGetResponse {
        public OrderV1 last_order;
        public List<OrderItemV1> order_items;
    }

    public class OrderV1 {
        /* FIXME: ctime, mtime should be in Data format
         * retrofit failed if using Date now
        * */
        public Date ctime;
        public Date mtime;
        public Restaurant restaurant;
        public int pos_slip_number;
        public int status;
    }

    public class OrderItemV1 {
        public Meal meal;
        public int amount;

        public OrderItemV1(Meal _meal, int _amount) {
            meal = _meal;
            amount = _amount;
        }
    }

    public class OrderPostResponse {
        public int number_slip;
    }

    public class CurrentNSResponse {
        public int current_ns;
    }

    public class CurrentCookingOrdersResponse {
        public int current_cooking_orders;
    }

    public class ErrorResponse {
        public String error_msg;
    }

    interface PlateTWAPI1 {
        @FormUrlEncoded
        @POST("/1/register")
        void register(@Field("phone_number") String phone_number,
                      @Field("password") String password,
                      @Field("password_type") String password_type,
                      @Field("gcm_registration_id") String gcm_registration_id,
                      Callback<Response> cb);
        @FormUrlEncoded
        @POST("/1/login")
        void login(@Field("username") String username,
                   @Field("password") String password,
                   Callback<Response> cb);

        @GET("/1/order_get")
        void orderGet(Callback<OrderGetResponse> cb);

        @FormUrlEncoded
        @POST("/1/order_post")
        void orderPost(@Field("order") String order,
                       Callback<OrderPostResponse> cb);

        @GET("/1/current_ns")
        void current_ns(@Query("rest_id") int rest_id,
                        Callback<CurrentNSResponse> cb);

        @GET("/1/current_cooking_orders")
        void current_cooking_orders(@Query("rest_id") int rest_id,
                        Callback<CurrentCookingOrdersResponse> cb);
    }

    interface PlateTWOldAPI {
        @GET("/suggestions.php")
        void recommendations(Callback<RecommendationResponse> cb);

        @GET("/restaurants.php")
        void restaurants(@Query("location") int location, Callback<RestaurantResponse> cb);

        @GET("/menu.php")
        void menu(@Query("rest_id") int rest_id, Callback<MenuResponse> cb);

        @FormUrlEncoded
        @POST("/status.php")
        void status(@Field("username") String username, Callback<StatusResponse> cb);

        @FormUrlEncoded
        @POST("/status_detail.php")
        void status_detail(@Field("number_slip_index") int number_slip_index, Callback<StatusDetailResponse> cb);

        @FormUrlEncoded
        @POST("/order.php")
        void order(@Field("username") String username,
                   @Field("rest_id") int rest_id,
                   @Field("order") /* json: [ (meal_id, amount) ...] */ String orders,
                   Callback<Response> cb);

        @FormUrlEncoded
        @POST("/cancel.php")
        void cancel(@Field("number_slip_index") int number_slip_index,
                    Callback<Response> cb);
    }

    //public static final String TEST_USERNAME = "android@plate.tw";

    private static RestAdapter restAdapter, restAdapterV1;
    private static PlateTWOldAPI plateTW;
    private static PlateTWAPI1 plateTWV1;

    private static class DateTimeDeserializer implements JsonDeserializer<Date> {
        private SimpleDateFormat sdf;

        public DateTimeDeserializer() {
			/* http://stackoverflow.com/questions/7910734/gsonbuilder-setdateformat-for-2011-10-26t202959-0700
			 * http://developer.android.com/reference/java/text/SimpleDateFormat.html
			 * http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
			 * */
            //FIXME: server : "yyyy-MM-dd'T'HH:mmZ", local : "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZZZZZ";
            String fmt;
            if (System.getProperty("java.runtime.name").equals("Android Runtime")) {
                //fmt = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZZZZZ";
                fmt = "yyyy-MM-dd'T'HH:mmZ";
            } else {
                fmt = "yyyy-MM-dd'T'HH:mmZ";
            }
            sdf = new SimpleDateFormat(fmt, Locale.US);
        }

        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx)
                throws JsonParseException {
            try {
                String s = json.getAsJsonPrimitive().getAsString();
                String dateWithoutMicros = s.substring(0, s.length() - 9) + s.substring(s.length() - 6);
                return sdf.parse(dateWithoutMicros);
            } catch (ParseException e) {
                throw new JsonParseException(e.getMessage());
            }
        }
    }

    public static PlateTWOldAPI getOldAPI(String url) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTimeDeserializer())
                .create();
        restAdapter = new RestAdapter.Builder()
                .setServer(url)
                .setConverter(new GsonConverter(gson))
                .build();
        plateTW = restAdapter.create(PlateTWOldAPI.class);
        return plateTW;
    }

    public static PlateTWAPI1 getAPI1(String url) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateTimeDeserializer())
                .create();
        restAdapterV1 = new RestAdapter.Builder()
                .setServer(url)
                .setConverter(new GsonConverter(gson))
                .build();
        plateTWV1 = restAdapterV1.create(PlateTWAPI1.class);
        return plateTWV1;
    }
}

