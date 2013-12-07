package tw.plate;


final public class Constants {

    /* Names */
    public static final String[] CANTEEN_LIST = new String[]{"第 二 餐 廳", "女 二 餐 廳", "第 一 餐 廳", "其  他"};
    public static final String API_URI_PREFIX = "http://10.0.2.2:8000"; // Developing Mode

    /* Numbers */
    public static final int MAX_AMOUNT = 4;

    /* TAGS */
    public static final String LOG_TAG = "PlateLog";
    public static final String SP_TAG_PASSWORD = "MY_PASSWORD";
    public static final String SP_TAG_PHONE_NUMBER = "MY_PHONE_NUMBER";

    /* Settings */
    public static final String PASSWORD_TYPE = "raw";

    /* Enum */
    public static final int FIRST_TIME = 0, SP_SAVED_BUT_LOGIN_FAIL = 1;

    private Constants(){}
}