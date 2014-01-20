package tw.plate;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by heron on 1/20/14.
 */
public class Cart {

    class CartOrderItem {
        public int meal_price;
        public String meal_name;
        public int meal_id;
        public int amount;

        public CartOrderItem(int _meal_price, String _meal_name, int _meal_id, int _amount) {
            meal_price = _meal_price;
            meal_name = _meal_name;
            meal_id = _meal_id;
            amount = _amount;
        }
    }

    private ArrayList<CartOrderItem> cartOrderItems;
    private String restaurant_name;

    /* Constructor */
    public Cart() {
        if (cartOrderItems == null) {
            cartOrderItems = new ArrayList<CartOrderItem>();
        }
    }

    public void clearOrderItems() {
        cartOrderItems.clear();
    }

    public ArrayList<CartOrderItem> getOrderItems() {
        return cartOrderItems;
    }

    public void addOrderItem(int _meal_price, String _meal_name, int _meal_id, int _amount) {
        CartOrderItem coi = new CartOrderItem(_meal_price, _meal_name, _meal_id, _amount);
        cartOrderItems.add(coi);
    }

    public void setRestaurant_name(String _restaurant_name) {
        restaurant_name = _restaurant_name;
    }
    public String getRestaurant_name() {
        return restaurant_name;
    }

    public int getNumberOfItems() {
        return cartOrderItems.size();
    }

    public boolean isEmpty() {
        return (cartOrderItems.size() <= 0);
    }
}
