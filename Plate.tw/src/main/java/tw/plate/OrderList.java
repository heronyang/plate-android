package tw.plate;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Hung on 12/3/13.
 */

public class OrderList implements Parcelable {
    //private List<PlateService.OrderItemV1> data;
    private List<PlateService.Meal> mealData;
    private List<Integer> amountData;
    //Constructor
    public OrderList(){
        //data = new ArrayList<PlateService.OrderItemV1>();
        mealData = new ArrayList<PlateService.Meal>();
        amountData = new ArrayList<>();
    }

    public void setOrderList(PlateService.Meal meal, int num){
        //PlateService.OrderItemV1 orderItem = new PlateService();
        //orderItem.meal = meal;
        //orderItem.amount = num;
        //data.add(orderItem);
        //data.add(orderItem);

        mealData.add(meal);
        amountData.add(num);
    }

    public PlateService.Meal getMealOrderList(int pos){
        return mealData.get(pos);
    }
    public Integer getAmountOrderList(int pos){
        return amountData.get(pos);
    }

    public int size(){ return mealData.size(); }

    private OrderList(Parcel in){
        //private List<Pair<PlateService.Meal, Integer>> data = new ArrayList<Pair<PlateService.Meal, Integer>>();
        //List<PlateService.OrderItemV1> inData  = new ArrayList<>();
        in.readList(mealData, null);
        in.readList(amountData, null);
        //data = inData;
    }
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags){
        Log.d(Constants.LOG_TAG, "write to Parcel " + flags);
        //out.writeValue(data);
        out.writeList(mealData);
        out.writeList(amountData);
    }

    public static final Parcelable.Creator<OrderList> CREATOR = new Creator<OrderList>(){
        public OrderList createFromParcel(Parcel in){
            return new OrderList(in);
        }

        public OrderList[] newArray(int size){
            return new OrderList[size];
        }
    };
}
