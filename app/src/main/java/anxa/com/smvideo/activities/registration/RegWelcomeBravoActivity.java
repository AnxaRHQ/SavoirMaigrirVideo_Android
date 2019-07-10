package anxa.com.smvideo.activities.registration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.NpnaActivity;
import anxa.com.smvideo.activities.account.LandingPageAccountActivity;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.LoginContract;
import anxa.com.smvideo.contracts.UserDataResponseContract;

/**
 * Created by aprilanxa on 13/09/2017.
 */

public class RegWelcomeBravoActivity extends Activity  {

    ImageView coach_iv;
    ProgressBar progressBar;
    private ApiCaller apiCaller;
    private LoginContract loginContract;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.reg_welcome_premium);

        ((TextView) (findViewById(R.id.reg_header).findViewById(R.id.header_title))).setText(getString(R.string.WELCOME_TITLE));

        coach_iv = (ImageView) findViewById(R.id.welcome_coach_iv);
        progressBar = (ProgressBar) findViewById(R.id.welcome_progressBar);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * OnClick Methods
     **/
    public void proceedToNextStep(View view) {
        loginToAPI();
    }
    private void loginToAPI(){
        progressBar.setVisibility(View.VISIBLE);
         loginContract = new LoginContract();
        loginContract.Email = ApplicationData.getInstance().regUserProfile.getEmail();
        loginContract.Password = ApplicationData.getInstance().regUserProfile.getPassword();
        loginContract.Check_npna = true;

        apiCaller = new ApiCaller();

        apiCaller.PostLogin(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                progressBar.setVisibility(View.GONE);

                if (output != null) {
                    UserDataResponseContract c = (UserDataResponseContract) output;

                    if (c != null) {
                        if(c.Message.equalsIgnoreCase("Failed")){
                            displayToastMessage(getString(R.string.ALERTMESSAGE_LOGIN_FAILED));
                        }
                        else if(c.Message.equalsIgnoreCase("NoAccess")){
                            ApplicationData.getInstance().userDataContract = c.Data;
                            ApplicationData.getInstance().regId = c.Data.Id;
                            ApplicationData.getInstance().saveLoginCredentials(loginContract.Email, loginContract.Password);
                            goToNpnaPage();
                        }else {
                            ApplicationData.getInstance().userDataContract = c.Data;
                            ApplicationData.getInstance().regId = c.Data.Id;

                            ApplicationData.getInstance().setIsLogin(getBaseContext(), true);
                            ApplicationData.getInstance().setAnxamatsSessionStart(getBaseContext(), System.currentTimeMillis());
                            ApplicationData.getInstance().saveLoginCredentials(loginContract.Email, loginContract.Password);

                            goToAccountLandingPage();
                        }
                    }else{
                        displayToastMessage(getString(R.string.ALERTMESSAGE_LOGIN_FAILED));
                    }
                }

            }
        }, loginContract);

    }

    private void goToAccountLandingPage(){
        ApplicationData.getInstance().accountType = "account";
        Intent mainIntent = new Intent(this, LandingPageAccountActivity.class);
        startActivity(mainIntent);
    }
    private void goToNpnaPage()
    {
        Intent npna = new Intent(getApplicationContext(), NpnaActivity.class);
        startActivity(npna);
    }

    /**
     * Private Methods
     **/


    public void goBackToPreviousPage(View view) {
        finish();
    }



    private void proceedToDashboard(){
        Intent main = new Intent(getApplicationContext(), LandingPageAccountActivity.class);
        startActivity(main);
        finish();
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
}
