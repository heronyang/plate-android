package tw.plate;
import android.app.Application;
import org.acra.*;
import org.acra.annotation.*;
/**
 * Created by heron on 1/19/14.
 */

//TODO make it sends to a backend system
@ReportsCrashes(
        formKey = "",
        formUri = "http://dev.plate.tw:5984/acra-myapp/_design/acra-storage/_update/report",
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        formUriBasicAuthLogin="plate-android",
        formUriBasicAuthPassword="1234",
        mode = ReportingInteractionMode.TOAST,
        forceCloseDialogAfterToast = false, // optional, default false
        resToastText = R.string.crash_toast_text
        //mailTo = Constants.PLATE_DEV_MAILBOX,
        //mailTo = "anthonyang86@yahoo.com",
        //customReportContent = { ReportField.REPORT_ID, ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT }
)
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

    @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        super.onCreate();
        ACRA.init(this);
    }

}
