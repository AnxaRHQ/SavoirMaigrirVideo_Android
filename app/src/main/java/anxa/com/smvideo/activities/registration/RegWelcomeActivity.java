package anxa.com.smvideo.activities.registration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import anxa.com.smvideo.R;
import anxa.com.smvideo.connection.ApiCaller;


/**
 * Created by aprilanxa on 13/09/2017.
 */

public class RegWelcomeActivity extends Activity {

    ApiCaller caller;

    Button payment_btn;
    Button free_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        caller = new ApiCaller();

        setContentView(R.layout.reg_payment_intro);
        payment_btn = (Button)findViewById(R.id.welcome_payment_btn);
        free_btn = (Button)findViewById(R.id.welcome_free_btn);


    }

    public void proceedToPaymentPage(View view){
       /* Intent mainIntent = new Intent(this, RegPaymentActivity.class);

        this.startActivity(mainIntent);*/
    }

    public void proceedToAccessCodePage(View view){
        Intent mainIntent = new Intent(this, RegPremiumAccessActivity.class);
        this.startActivity(mainIntent);
    }

    public void goBackToPreviousPage(View view) {
        finish();
    }

}
