package tw.plate;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

public class WelcomeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getSupportActionBar().hide();

        int delay_time = Constants.FLIP_BACK_TIME;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                nextPage();
            }
        }, delay_time);
    }

    private void nextPage() {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        registerIntent.putExtra("message_type", Constants.FIRST_TIME);
        registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(registerIntent);
    }
}
