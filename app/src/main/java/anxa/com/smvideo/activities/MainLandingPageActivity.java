package anxa.com.smvideo.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.account.LandingPageAccountActivity;
import anxa.com.smvideo.activities.free.LandingPageActivity;
import anxa.com.smvideo.activities.registration.RegistrationFormActivity;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.contracts.LoginContract;

/**
 * Created by aprilanxa on 27/07/2017.
 */

public class MainLandingPageActivity extends Activity {

    private static final int BROWSERTAB_ACTIVITY = 1111;

    private ApiCaller apiCaller;
    private LoginContract loginContract;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_main_new);
//        setContentView(R.layout.landing_main);
    }

    private void goToLoginPage() {
        Intent mainIntent = new Intent(this, LoginActivity.class);
        startActivity(mainIntent);
    }

    private void goToRegistrationPage() {
        //Intent mainIntent = new Intent(this, RegistrationActivity.class);
        //startActivityForResult(mainIntent, BROWSERTAB_ACTIVITY);

        Intent mainIntent = new Intent(this, RegistrationFormActivity.class);
        startActivity(mainIntent);
    }

    private void goToFreePage() {
        ApplicationData.getInstance().accountType = "free";
        Intent mainIntent = new Intent(this, LandingPageActivity.class);
        startActivity(mainIntent);
    }

    public void goToLoginPage(View view) {
        goToLoginPage();
    }

    public void goToRegisrationPage(View view) {
        goToRegistrationPage();
    }

    public void goToFreePart(View view) {
        goToFreePage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == BROWSERTAB_ACTIVITY) {
            if (intent != null) {
                boolean isLogin = intent.getBooleanExtra("TO_LOGIN", false);
                if (isLogin) {
                    goToLoginPage();
                }else if (intent.getBooleanExtra("TO_LANDING", false)){

                }
            }
        }
    }

    public void displayToastMessage(final String message) {
        final Context context = this;
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast m = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                m.show();

            }
        });
    }

    private void goToAccountLandingPage(){
        ApplicationData.getInstance().accountType = "account";

        Intent mainIntent = new Intent(this, LandingPageAccountActivity.class);
        startActivity(mainIntent);
    }
}
