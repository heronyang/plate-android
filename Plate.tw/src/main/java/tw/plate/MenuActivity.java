package tw.plate;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MenuActivity extends ListActivity implements PlateServiceManager.PlateManagerCallback{

    /*
    private ArrayList<String> mealNames = new ArrayList<String>();
    private ArrayList<Integer> mealPrices = new ArrayList<Integer>()
            , mealID = new ArrayList<Integer>()
            , mealAmount = new ArrayList<Integer>();
            */

    PlateServiceManager plateServiceManager;

    int restId;
    String restName;
    private CustomAdapter customAdapter;

    public enum Animation {
        CURL, TWIRL, ZIPPER, FADE, FLY, REVERSE_FLY, FLIP, CARDS, GROW, WAVE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        restId = intent.getIntExtra("restId", 0);
        restName = intent.getStringExtra("restName");

        plateServiceManager = ((Plate) this.getApplication()).getPlateServiceManager();
        plateServiceManager.menu(restId, this);

        /*
        PlateService.PlateTWOldAPI plateTW;
        plateTW = PlateService.getOldAPI(Constants.API_URI_PREFIX);
        plateTW.menu(restId, new Callback<PlateService.MenuResponse>() {
            @Override
            public void success(PlateService.MenuResponse ms, Response response) {
                for (PlateService.Meal m : ms.meal_list) {
                    mealList.add(m);
                }
                updateMenuList();
            }

            @Override
            public void failure(RetrofitError e) {
                Log.d(Constants.LOG_TAG, "menu: failure");
            }
        });
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class CustomAdapter extends BaseAdapter // listview
    {
        private final LayoutInflater inflater;
        private ArrayAdapter<String> spAdapter;
        List<PlateService.Meal> mealList = plateServiceManager.getMealList();
        int [] amounts = new int[mealList.size()];
        private int displayWidth;
        private int prevPosition;
        private Animation animation;


        public class ViewHolder
        {
            Spinner sp;
            TextView tv_name;
            TextView tv_price;
        }

        public CustomAdapter(Context context, ArrayAdapter<String> _adapter)
        {
            inflater = LayoutInflater.from(context);
            spAdapter = _adapter;
        }

        public void setAnimation(Animation animation){
            this.animation = animation;
        }
        public int getCount() {
            // TODO Auto-generated method stub
            return mealList.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mealList.get(position);
        }

        public int getSelectedAmountAtPosition(int position) {
            if (position > mealList.size()) return 0;
            return amounts[position];
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public int getDisplayWidth(Context context) {
            final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            return display.getWidth();
        }

        public View getView(final int arg0, View convertview, ViewGroup arg2) {
            ViewHolder viewHolder;
            Log.d(Constants.LOG_TAG, "arg0 >> " + arg0);

            if(convertview == null)
            {
                convertview = inflater.inflate(R.layout.listrow_menu, null);

                // creating instance
                viewHolder=new ViewHolder();
                viewHolder.sp = (Spinner) convertview.findViewById(R.id.listrow_menu_spinner);
                viewHolder.tv_name = (TextView) convertview.findViewById(R.id.listrow_menu_tv);
                viewHolder.tv_price = (TextView) convertview.findViewById(R.id.tv_listrow_price);
                convertview.setTag(viewHolder);
            }
            else
            {
                viewHolder=(ViewHolder)convertview.getTag();
            }

            // set values
            String meal_name = mealList.get(arg0).meal_name;
            viewHolder.tv_name.setText(meal_name);
            int meal_price = mealList.get(arg0).meal_price;
            viewHolder.tv_price.setText(""+meal_price+" NTD");

            viewHolder.sp.setAdapter(spAdapter);
            viewHolder.sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    amounts[arg0] = position;
                    Log.d(Constants.LOG_TAG, "pos: " + arg0 + "amount: " + amounts[arg0]);
                    if(amounts[arg0]!=0){
                        ((TextView)parentView.getChildAt(0)).setTextColor(getResources().getColor(R.color.background_3));
                    }else{
                        ((TextView)parentView.getChildAt(0)).setTextColor(getResources().getColor(R.color.gray_unseen));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {

                    // Callback method to be invoked when the selection disappears from this view.
                    // The selection can disappear for instance when touch is activated or when the adapter becomes empty.
                }
            });
            selectAnimation(convertview,arg0, animation);

            viewHolder.sp.setSelection(amounts[arg0]);
            return convertview;
        }

        private void selectAnimation( View convertview, int arg0, Animation animation){
            switch (animation) {
                case CARDS:
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                        AnimationFactory.doCards(convertview, arg0, prevPosition);
                    }else{
                        AnimationFactory2.doCards(convertview, arg0, prevPosition);
                    }
                    break;
                case CURL:
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                        AnimationFactory.doCurl(convertview, arg0, prevPosition, displayWidth);
                    }else{
                        AnimationFactory2.doCurl(convertview, arg0, prevPosition, displayWidth);
                    }
                    break;
                case FADE:
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                        AnimationFactory.doFade(convertview);
                    }else{
                        AnimationFactory2.doFade(convertview);
                    }
                    break;
                case FLIP:
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                        AnimationFactory.doFlip(convertview, arg0, prevPosition);
                    }else{
                        AnimationFactory2.doFlip(convertview, arg0, prevPosition);
                    }
                    break;
                case FLY:
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                        AnimationFactory.doFly(convertview, arg0, prevPosition);
                    }else{
                        AnimationFactory2.doFly(convertview, arg0, prevPosition);
                    }
                    break;
                case GROW:
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                        AnimationFactory.doGrow(convertview);
                    }else{
                        AnimationFactory2.doGrow(convertview);
                    }
                    break;
                case REVERSE_FLY:
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                        AnimationFactory.doReverseFly(convertview, arg0, prevPosition);
                    }else{
                        AnimationFactory2.doReverseFly(convertview, arg0, prevPosition);
                    }
                    break;
                case TWIRL:
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                        AnimationFactory.doTwirl(convertview, arg0, prevPosition);
                    }else{
                        AnimationFactory2.doTwirl(convertview, arg0, prevPosition);
                    }
                    break;
                case ZIPPER:
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                        AnimationFactory.doZipper(convertview, arg0, displayWidth);
                    }else{
                        AnimationFactory2.doZipper(convertview, arg0, displayWidth);
                    }
                    break;
                case WAVE:
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                        AnimationFactory.doWave(convertview, displayWidth);
                    }else{
                        AnimationFactory2.doWave(convertview, displayWidth);
                    }
                    break;
            }

        }
    }

    @Override
    protected void onListItemClick(ListView lv, View view, int position, long id) {
        super.onListItemClick(lv, view, position, id);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        //menu.findItem(R.id.action_bar_title).setTitle(restName);
        String title = getActionBar().getTitle().toString();
        title += ":    "+restName;
        getActionBar().setTitle(title);
        //getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.light_gray)));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:{
                this.finish();
                return true;
            }
            case R.id.action_confirm:
                confirmOrder();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void confirmOrder() {
        View view = findViewById(android.R.id.content);
        Intent confirmOrderIntent = new Intent(view.getContext(), ConfirmOrderActivity.class);
        collectResults();

        // for test
        //int s = mealNames.size();
        //Log.d(Constants.LOG_TAG, "Final List in this page (menu)");
        Cart cart = ((Plate)getApplication()).getCart();
        if(cart.isEmpty()){
            pleaseOrder();
        }
        else{
            /*
            for( int i=0 ; i<s ; i++ ){
                Log.d(Constants.LOG_TAG, "meal name: " + mealNames.get(i) + "\tamount : " + mealAmount.get(i));
            }
            */
            // put
            /*
            confirmOrderIntent.putStringArrayListExtra("orderMealNames", mealNames);
            confirmOrderIntent.putIntegerArrayListExtra("orderMealPrice", mealPrices);
            confirmOrderIntent.putIntegerArrayListExtra("orderMealID", mealID);
            confirmOrderIntent.putIntegerArrayListExtra("orderMealAmount", mealAmount);
            confirmOrderIntent.putExtra("restName",restName);
            */

            startActivity(confirmOrderIntent);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

    private void collectResults() {
        List<PlateService.Meal> mealList = plateServiceManager.getMealList();
        int s = mealList.size();
        Log.d(Constants.LOG_TAG, "s = " + s);


        /* put things into the cart */
        Cart cart = ((Plate)getApplication()).getCart();
        cart.clearOrderItems();

        cart.setRestaurant_name(restName);
        for(int i=0 ; i<s ; i++) {
            int amount = customAdapter.getSelectedAmountAtPosition(i);
            if (amount != 0) {
                PlateService.Meal ml = mealList.get(i);
                cart.addOrderItem(ml.meal_price, ml.meal_name, ml.meal_id, amount);
            }
        }
        /*
        mealNames.clear();
        mealPrices.clear();
        mealID.clear();
        mealAmount.clear();

        for(int i=0 ; i<s ; i++) {
            int amount = customAdapter.getSelectedAmountAtPosition(i);
            if (amount != 0) {
                mealNames.add(mealList.get(i).meal_name);
                mealPrices.add(mealList.get(i).meal_price);
                mealID.add(mealList.get(i).meal_id);
                mealAmount.add(amount);
            }
        }
        */
    }

    private void pleaseOrder(){
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.menu_no_order),
                Toast.LENGTH_SHORT).show();
    }

    /*
        Override for PlateServiceManager's Callbacks
     */
    @Override
    public void updateRestaurantList() {
        // do nothing
    }

    @Override
    public void updateMenuList() {
        TextView tv_category = (TextView) findViewById(R.id.tv_category);
        ListView lv = (ListView) findViewById(android.R.id.list);

        tv_category.setText(getResources().getString(R.string.menu_list_category));

        ArrayAdapter<String> spAdapter;
        String [] spinnerItems = new String[Constants.MAX_AMOUNT];
        for(int i=0 ; i<spinnerItems.length ; i++)  spinnerItems[i] = "" + i;
        spAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinnerItems);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        customAdapter = new CustomAdapter(this, spAdapter);
        customAdapter.setAnimation(Animation.GROW);
        lv.setAdapter(customAdapter);
    }


    @Override
    public void loginSucceed() { throw new UnsupportedOperationException(); }
    @Override
    public void loginFailed() { throw new UnsupportedOperationException(); }
    @Override
    public void notRegistered() { throw new UnsupportedOperationException(); }

    @Override
    public void orderPostSucceed() { throw new UnsupportedOperationException(); }
    @Override
    public void orderPostFailed(int errorStatus) { throw new UnsupportedOperationException(); }
    @Override
    public void orderGetSucceed(PlateService.OrderGetResponse orderGetResponse) { throw new UnsupportedOperationException(); }
    @Override
    public void orderGetSucceedEmpty() { throw new UnsupportedOperationException(); }
    @Override
    public void orderGetFailed() { throw new UnsupportedOperationException(); }

    @Override
    public void registerSucceed() { throw new UnsupportedOperationException(); }
    @Override
    public void registerFailed() { throw new UnsupportedOperationException(); }

    @Override
    public void currentNsSucceed(int current_ns) { throw new UnsupportedOperationException(); }
    @Override
    public void currentNsFailed() { throw new UnsupportedOperationException(); }
}
