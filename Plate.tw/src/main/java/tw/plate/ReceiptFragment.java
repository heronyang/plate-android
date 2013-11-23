package tw.plate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.receipt_frag, container, false);
        //Writing the welcome message at the top
        TextView tv = (TextView) v.findViewById(R.id.tvReceipt);
        tv.setText(getArguments().getString("msg"));

        //Display the your number slip
        TextView tv_numberSlip = (TextView) v.findViewById(R.id.tvNumSlip);
        tv_numberSlip.setText("你的號碼牌: "+ myNumSlip);

        //Display the Ordered meal
        TextView tv_orderedMeal = (TextView) v.findViewById(R.id.tv_order);
        tv_orderedMeal.setText(myOrder);

        //Current number button OnClickListener

        return v;
    }

    public static ReceiptFragment newInstance(String text){

        ReceiptFragment receiptFragment = new ReceiptFragment();
        Bundle b = new Bundle();
        b.putString("msg",text);

        receiptFragment.setArguments(b);
        return receiptFragment;
    }
}
