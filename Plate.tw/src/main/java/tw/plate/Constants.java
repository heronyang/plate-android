package tw.plate;


final public class Constants {

    /* Names */
    public static final String[] CANTEEN_LIST = new String[]{"女 二 餐 廳", "第 二 餐 廳", "第 一 餐 廳", "其  他"};
    public static final String API_URI_PREFIX = "https://api.plate.tw"; // Release Mode

    //public static final String API_URI_PREFIX = "http://10.0.2.2:8000"; // Developing Mode
    //public static final String API_URI_PREFIX = "http://192.168.0.121:8080"; // Heron's Local Developing Mode
    //public static final String API_URI_PREFIX = "http://192.168.1.228:8080"; // Heron's Local Developing Mode
    //public static final String API_URI_PREFIX = "http://192.168.1.28:8080"; // Heron's Local Developing Mode
    //public static final String API_URI_PREFIX = "http://172.18.80.3:8080"; // Heron's Local Developing Mode
    //public static final String API_URI_PREFIX = "http://api-dev.plate.tw:8080"; // Release Mode

    public static final String OFFICIAL_WEBSITE = "http://plate.tw";
    public static final String PLATE_SERVICE_MAILBOX = "plate-service@googlegroups.com";
    public static final String GOOGLE_PLAY_SERVICE = "https://play.google.com/store/apps/details?id=com.google.android.gms";
    public static final String PLATE_DEV_MAILBOX = "dev-plate-tw@googlegroups.com";

    public static final String ACRA_BACKEND_SERVER = "http://dev.plate.tw:5984/acra-plate-android/_design/acra-storage/_update/report";

    /* Numbers */
    public static final int MAX_AMOUNT = 6;

    /* TAGS */
    public static final String LOG_TAG = "PlateLog";
    public static final String SP_TAG_PASSWORD = "MY_PASSWORD";
    public static final String SP_TAG_PHONE_NUMBER = "MY_PHONE_NUMBER";

    /* Settings */
    public static final String PASSWORD_TYPE = "raw";

    /* Enum */
    public static final int FIRST_TIME = 0, SP_SAVED_BUT_LOGIN_FAIL = 1, SECOND_CHANCE_TO_REGISTER = 2;
    public static final int ORDER_EMPTY = -1;

    /* Time */
    public static final int FLIP_BACK_TIME = 3000; // ms
    public static final int PRESSED_TIME = 1000; // ms
    public static final int WELCOME_PAGE_DELAY_TIME = 3000; // ms

    private Constants(){}
}
