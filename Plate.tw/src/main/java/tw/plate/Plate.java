package tw.plate;
import android.app.Application;

/**
 * Created by heron on 1/19/14.
 */
public class Plate extends Application {
    private PlateServiceManager plateServiceManager;
    private Cart cart;

    public PlateServiceManager getPlateServiceManager() {
        if (plateServiceManager == null){
            plateServiceManager = new PlateServiceManager();
        }
        return plateServiceManager;
    }

    public Cart getCart() {
        if (cart == null) {
            cart = new Cart();
        }
        return cart;
    }
}
