package tw.plate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by Hung on 11/23/13.
 */
public class ReceiptFragment extends Fragment{
    //Sent from database
    String restName = "姊妹\n";
    String myNumSlip = "\n15";
    String myMeal = "豬排飯  1*70 NT \n雞腿飯  2*70 \n總價:   210\n";
    String orderTime = "\n訂餐時間: 11:40";
    String currNum = "10";

    String myOrder = restName + myMeal + orderTime;

    //Some private constant variable
    private static final String ARG_FRAGMENT_MESSAGE = "fragment_msg";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.receipt_frag, container, false);
        //Writing the welcome message at the top
        TextView tv = (TextView) v.findViewById(R.id.tvReceipt);
        tv.setText(getArguments().getString(ARG_FRAGMENT_MESSAGE));

        //Display the your number slip
        TextView tv_numberSlip = (TextView) v.findViewById(R.id.tvNumSlip);
        tv_numberSlip.setText(getString(R.string.your_number_message)+ myNumSlip);

        //Display the Ordered meal
        TextView tv_orderedMeal = (TextView) v.findViewById(R.id.tv_order);
        tv_orderedMeal.setText(myOrder);

        //CurrentNumber button OnClickListener
        Button currBut = (Button) v.findViewById(R.id.bt_currentNum);
        /*
        currBut.setOnClickListener(new View.OnClickListener() {
            public void OnClick(View arg0){
                //FIX ME: Add toast and show the current number
                String server_currNum = "2";
                Toast.makeText(this,server_currNum,Toast.LENGTH_SHORT).show();
            }
        });*/

        return v;
    }

    public static ReceiptFragment newInstance(String text){
        ReceiptFragment receiptFragment = new ReceiptFragment();
        Bundle b = new Bundle();
        b.putString(ARG_FRAGMENT_MESSAGE,text);

        receiptFragment.setArguments(b);
        return receiptFragment;
    }
}
