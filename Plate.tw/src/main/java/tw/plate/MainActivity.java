package tw.plate;

import java.io.IOException;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, PlateServiceManager.PlateManagerCallback {

    PlateServiceManager plateServiceManager;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    //================================================================================
    // Layout Setup
    //================================================================================
    private void layout_setup() {
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowHomeEnabled(false);     // turn off display
        actionBar.setDisplayShowTitleEnabled(false);    // turn off display

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });


        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            int[] tabsResources= new int[]{R.drawable.tab_location_trans,R.drawable.tab_bill_trans};
            actionBar.addTab(
                    actionBar.newTab()
                            //.setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this)
                            .setIcon(getResources().getDrawable(tabsResources[i]))

            );
        }

        Intent intent = getIntent();
        int position = intent.getIntExtra("fragPosition",0);
        mViewPager.setCurrentItem(position);
    }


    //================================================================================
    // Runtime Override Events
    //================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CookieHandler.setDefault(new CookieManager());
        plateServiceManager = ((Plate) this.getApplication()).getPlateServiceManager();

        // if first time use this app, welcome activity is fired
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        if (!sp.contains("first_time")) {
            Log.d(Constants.LOG_TAG, "first time using this app");
            Intent welcomeIntent = new Intent(this, WelcomeActivity.class);
            welcomeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(welcomeIntent);
            finish();
        }

        layout_setup();
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.main_exit_message)
                .setTitle(R.string.main_exit_title);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // If cancel, do nothing
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        //super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
   //         selectIP();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    //================================================================================
    // Fragments
    //================================================================================

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final FragmentManager fm;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            //    return PlaceholderFragment.newInstance(position + 1);
            switch(position){
                case 0: return LocationFragment.newInstance();
                case 1: return ReceiptFragment.newInstance();
                default: return LocationFragment.newInstance();
            }

        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle
                (int position) {
            //Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.tab_location);
                case 1:
                    return getString(R.string.tab_transaction);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            //@SuppressWarnings("ConstantConditions") TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText("This is page: "+Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /*
    Helper Tools
     */
    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    private ReceiptFragment getReceiptFragment() {
        FragmentManager fm = getSupportFragmentManager();
        String tag;

        // inform receiptFragment the user is logged in
        tag = makeFragmentName(R.id.pager, 1);
        ReceiptFragment receiptFragment = (ReceiptFragment)fm.findFragmentByTag(tag);
        return receiptFragment;
    }

    /*
    PlateServiceManager Callbacks
     */
    @Override
    public void loginSucceed() {
        getReceiptFragment().loginSucceed();
        // get the last order
        plateServiceManager.orderGet(this);
    };

    @Override
    public void loginFailed() {
        //
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        registerIntent.putExtra("message_type", Constants.SP_SAVED_BUT_LOGIN_FAIL);
        startActivity(registerIntent);

        //
        getReceiptFragment().waitForRegisterCompleted();
    }

    @Override
    public void notRegistered() {
        // go to register page
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        registerIntent.putExtra("message_type", Constants.FIRST_TIME);
        startActivity(registerIntent);

        // NOTE: this may also happen when the user is not registered
        getReceiptFragment().notRegistered();
    }
    @Override
    public void orderGetSucceed(PlateService.OrderGetResponse orderGetResponse) {
        Log.d(Constants.LOG_TAG, "order get succeed");
        getReceiptFragment().orderGetSucceed(orderGetResponse);
    }

    @Override
    public void orderGetSucceedEmpty() {
        Log.d(Constants.LOG_TAG, "order get empty");
        getReceiptFragment().orderGetSucceedEmpty();
    }

    @Override
    public void orderGetFailed() {
        // do nothing so far
        // NOTE: this should be an error
        Log.d(Constants.LOG_TAG, "order get failed");

        Intent registerIntent = new Intent(this, RegisterActivity.class);
        registerIntent.putExtra("message_type", Constants.SECOND_CHANCE_TO_REGISTER);
        startActivity(registerIntent);

        // NOTE: this may also happen when the user is not registered
        getReceiptFragment().waitForRegisterCompleted();
    }


    @Override
    public void currentNsSucceed(int current_ns) {
        getReceiptFragment().currentNsSucceed(current_ns);
    }

    @Override
    public void currentNsFailed() {
        // ignore
    }

    @Override
    public void updateRestaurantList() { throw new UnsupportedOperationException(); }
    @Override
    public void updateMenuList() { throw new UnsupportedOperationException(); }
    @Override
    public void orderPostSucceed() { throw new UnsupportedOperationException(); }
    @Override
    public void orderPostFailed(int errorStatus) { throw new UnsupportedOperationException(); }

    @Override
    public void registerSucceed() { throw new UnsupportedOperationException(); }
    @Override
    public void registerFailed() { throw new UnsupportedOperationException(); }

    //================================================================================
    // End
    //================================================================================
}
