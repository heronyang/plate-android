package tw.plate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

public class ConfirmOrder extends Activity {

    //OrderList passedOrderList;
    private ArrayList<String> mealNames = new ArrayList<String>();
    private ArrayList<Integer> mealPrices = new ArrayList<Integer>()
            , mealID = new ArrayList<Integer>()
            , mealAmount = new ArrayList<Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        Intent intent = getIntent();
        mealNames = intent.getStringArrayListExtra("orderMealNames");
        mealPrices = intent.getIntegerArrayListExtra("orderMealPrice");
        mealID = intent.getIntegerArrayListExtra("orderMealID");
        mealAmount = intent.getIntegerArrayListExtra("orderMealAmount");

        //
        for(int i=0; i<mealNames.size(); i++)
            Log.d(Constants.LOG_TAG,"Passed order at position 0: Meal Name: "
                    + mealNames.get(i) + "Amount: " + mealAmount.get(i) +"pieces" );

        listviewAndTotalAmountSetup();
        buttonSetup();
    }

    private void listviewAndTotalAmountSetup() {

        int totalAmount = 0;

        ListView lv = (ListView) findViewById(android.R.id.list);
        int l = mealNames.size(), i;
        String [] rowData = new String[l];
        for( i=0 ; i<l ; i++) {
            rowData[i] = mealNames.get(i) + " " + mealPrices.get(i) + "NTD * " + mealAmount.get(i);
            totalAmount += mealPrices.get(i) * mealAmount.get(i);
        }

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rowData);
        lv.setAdapter(adapter);

        TextView tv = (TextView)findViewById(R.id.tvTotalAmount);
        tv.setText(totalAmount + " NTD");
    }

    private void buttonSetup() {
        Button btCancel = (Button) findViewById(R.id.bt_cancel);
        Button btOk = (Button) findViewById(R.id.bt_ok);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupDoubleConfirmMessage();
            }
        });
    }

    private void popupDoubleConfirmMessage () {
        AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmOrder.this);

        builder.setMessage(R.string.confirm_order_warning_message)
                .setTitle(R.string.confirm_order_warning_title);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivityIntent);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // If cancel, do nothing
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.confirm_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(Constants.LOG_TAG, "home pressed");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(Constants.LOG_TAG, "system back pressed");
        finish();
    }
}
