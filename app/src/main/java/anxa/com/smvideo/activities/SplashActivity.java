package anxa.com.smvideo.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.account.LandingPageAccountActivity;
import anxa.com.smvideo.activities.free.LandingPageActivity;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.LoginContract;
import anxa.com.smvideo.contracts.UserDataResponseContract;

/**
 * Created by angelaanxa on 5/23/2017.
 */

public class SplashActivity extends Activity {

    protected boolean _active = true;
    protected int _splashTime = 3000; // time to display the splash screen in ms
    boolean canTouch;


    private ApiCaller apiCaller;
    private LoginContract loginContract;

    Intent responseIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.splash);

        if (!isUserLoggedIn()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent mainIntent;
                    if (ApplicationData.getInstance().accountType.equalsIgnoreCase("free")) {
                        mainIntent = new Intent(SplashActivity.this, LandingPageActivity.class);
                    } else {
                        mainIntent = new Intent(SplashActivity.this, MainLandingPageActivity.class);
                    }
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }
            }, _splashTime);

        } else {
            if (ApplicationData.getInstance().getSavedPassword().length() < 1 || ApplicationData.getInstance().getSavedPassword() == null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                        SplashActivity.this.startActivity(mainIntent);
                        SplashActivity.this.finish();
                    }
                }, _splashTime);
            } else {
                autoLogin();
            }
        }
    }

    private Boolean isUserLoggedIn() {
        //check the SharedPreferences if the saved value is true/false
        return ApplicationData.getInstance().isLoggedIn(this);
    }

    private void autoLogin() {
        //login user in the background to obtain user details
        loginContract = new LoginContract();

        loginToAPI();
    }

    private void loginToAPI() {

        loginContract.Email = ApplicationData.getInstance().getSavedUserName();
        loginContract.Password = ApplicationData.getInstance().getSavedPassword();
        loginContract.Check_npna = false;

        apiCaller = new ApiCaller();

        apiCaller.PostLogin(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {

                if (output != null) {
                    UserDataResponseContract c = (UserDataResponseContract) output;

                    if (c != null) {
                        if (c.Message.equalsIgnoreCase("Failed")) {
                            displayToastMessage(getString(R.string.ALERTMESSAGE_LOGIN_FAILED));
                        } else {
                            ApplicationData.getInstance().userDataContract = c.Data;
                            ApplicationData.getInstance().regId = c.Data.Id;

                            ApplicationData.getInstance().setIsLogin(getBaseContext(), true);
                            ApplicationData.getInstance().saveLoginCredentials(loginContract.Email, loginContract.Password);

                            goToAccountPage();
                        }
                    } else {
                        displayToastMessage(getString(R.string.ALERTMESSAGE_LOGIN_FAILED));
                    }
                }

            }
        }, loginContract);

    }

    private void goToAccountPage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ApplicationData.getInstance().isLoggedIn(getBaseContext())) {
                    ApplicationData.getInstance().accountType = "account";
                    responseIntent = new Intent(SplashActivity.this, LandingPageAccountActivity.class);
                } else {
                    responseIntent = new Intent(SplashActivity.this, MainLandingPageActivity.class);
                }

                SplashActivity.this.startActivity(responseIntent);
                SplashActivity.this.finish();
            }
        }, _splashTime);
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
