package anxa.com.smvideo.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Browser;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.account.BrowserActivity;
import anxa.com.smvideo.activities.account.LandingPageAccountActivity;
import anxa.com.smvideo.common.SavoirMaigrirVideoConstants;
import anxa.com.smvideo.common.WebkitURL;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.LoginContract;
import anxa.com.smvideo.contracts.Notifications.GetNotificationsContract;
import anxa.com.smvideo.contracts.Notifications.NotificationsContract;
import anxa.com.smvideo.contracts.PaymentOrderGoogleContract;
import anxa.com.smvideo.contracts.UserDataResponseContract;
import anxa.com.smvideo.util.AppUtil;
import anxa.com.smvideo.util.IabBroadcastReceiver;
import anxa.com.smvideo.util.IabHelper;
import anxa.com.smvideo.util.IabResult;
import anxa.com.smvideo.util.Inventory;
import anxa.com.smvideo.util.Purchase;

/**
 * Created by aprilanxa on 27/07/2017.
 */

public class LoginActivity extends Activity{

    private LoginContract loginContract;
    private EditText email_et, password_et;
    private Button loginButton;

    private ApiCaller apiCaller;
    private GetNotificationsContract response;
    private ProgressBar loginProgressBar;

    private long previousDate;

    private static int NPNA_OFFERACTIVITY = 333;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main_new);
//        setContentView(R.layout.login_main);

        loginContract = new LoginContract();

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        email_et = (EditText)findViewById(R.id.login_email_et);
        if (ApplicationData.getInstance().getSavedUserName()!=null || ApplicationData.getInstance().getSavedUserName().length() > 1){
            email_et.setText(ApplicationData.getInstance().getSavedUserName());
        }

        password_et = (EditText)findViewById(R.id.login_password_et);
        password_et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (password_et.getRight() - password_et.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(password_et.getTag().equals("hidden"))
                        {
                            password_et.setTransformationMethod(new HideReturnsTransformationMethod());
                            password_et.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.password_hidden), null);
                            password_et.setTag("show");
                        }
                        else
                        {
                            password_et.setTransformationMethod(new PasswordTransformationMethod());
                            password_et.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.password_visible), null);
                            password_et.setTag("hidden");
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        loginProgressBar = (ProgressBar)findViewById(R.id.login_progressBar);
        loginProgressBar.setVisibility(View.GONE);

        loginButton = (Button)findViewById(R.id.login_login_button);
    }

    public void goBackToLandingPage(View view)
    {
//        onBackPressed();

        Intent mainIntent = new Intent(this, MainLandingPageActivity.class);
        startActivity(mainIntent);
    }

    public void validateLogin(View view)
    {
        if (validateLogin())
            loginToAPI();
    }

    private void loginToAPI()
    {
        loginProgressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        loginContract.Email = email_et.getText().toString();
        loginContract.Password = password_et.getText().toString();
        loginContract.Check_npna = true;

        apiCaller = new ApiCaller();

        apiCaller.PostLogin(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                loginProgressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);

                if (output != null) {
                    UserDataResponseContract c = (UserDataResponseContract) output;

                    if (c != null)
                    {
                        if(c.Message.equalsIgnoreCase("Failed"))
                        {
                            displayToastMessage(getString(R.string.ALERTMESSAGE_LOGIN_FAILED));
                        }
                        else if(c.Message.equalsIgnoreCase("NoAccess"))
                        {
                            ApplicationData.getInstance().userDataContract = c.Data;
                            ApplicationData.getInstance().regId = c.Data.Id;
                            ApplicationData.getInstance().saveLoginCredentials(loginContract.Email, loginContract.Password);
                            goToNpnaPage();
                        }
                        else
                        {
                            ApplicationData.getInstance().userDataContract = c.Data;
                            ApplicationData.getInstance().regId = c.Data.Id;

                            ApplicationData.getInstance().setIsLogin(getBaseContext(), true);
                            ApplicationData.getInstance().setAnxamatsSessionStart(getBaseContext(), System.currentTimeMillis());
                            ApplicationData.getInstance().saveLoginCredentials(loginContract.Email, loginContract.Password);

                            getNotifications();
                        }
                    }else{
                        displayToastMessage(getString(R.string.ALERTMESSAGE_LOGIN_FAILED));
                    }
                }

            }
        }, loginContract);
    }

    private void goToAccountLandingPage()
    {
        ApplicationData.getInstance().accountType = "account";
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Home;
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void goToNpnaPage()
    {
        Intent npna = new Intent(getApplicationContext(), NpnaActivity.class);
        startActivity(npna);
    }

    private Boolean validateLogin()
    {
        if (email_et.getText() == null || email_et.getText().length() <= 0) {
            displayToastMessage(getString(R.string.ALERTMESSAGE_LOGIN_EMPTY));
            return false;
        } else if (!AppUtil.isEmail(email_et.getText().toString())) {
            displayToastMessage(getString(R.string.ALERTMESSAGE_LOGIN_EMAIL_ERROR));
            return false;
        } else if (password_et.getText() == null || password_et.getText().length() <= 0) {
            displayToastMessage(getString(R.string.ALERTMESSAGE_LOGIN_FAILED));
            return false;
        } else {
            return true;
        }
    }

    public void displayToastMessage(final String message)
    {
        final Context context = this;
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast m = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                m.show();

            }
        });
    }

    public void goToForgetPw(View view)
    {
        ApplicationData.getInstance().accountType = "free";
        Intent mainContentBrowser = new Intent(this, BrowserActivity.class);
        mainContentBrowser.putExtra("HEADER_TITLE", getResources().getString(R.string.login_forgot_pw));
        mainContentBrowser.putExtra("URL_PATH", WebkitURL.forgetPw);
        startActivity(mainContentBrowser);
    }

    private void getNotifications()
    {
        ApplicationData.getInstance().setPreviousDate(AppUtil.getCurrentDateinLong());
        previousDate = AppUtil.getCurrentDateinLong();

        apiCaller.GetNotificationsThread(new AsyncResponse()
        {
            @Override
            public void processFinish(Object output)
            {
                response = output != null ? (GetNotificationsContract) output : new GetNotificationsContract();

                if (response != null && response.Data != null && response.Data.Notifications != null && response.Cursor != null)
                {
                    System.out.println("notifications: " + response.Data.Notifications.size());

                    ApplicationData.getInstance().setNotificationsCount(response.Data.Notifications.size());

                    List<NotificationsContract> notificationsList = (List<NotificationsContract>) response.Data.Notifications;

                    for (NotificationsContract notif : notificationsList)
                    {
                        NotificationsContract n = ApplicationData.getInstance().notificationList.get(notif.notification_id);

                        ApplicationData.getInstance().notificationList.put(notif.notification_id + "", notif);
                    }

                    int unreadCount = 0;

                    for (NotificationsContract notif : ApplicationData.getInstance().notificationList.values())
                    {
                        if (!notif.is_read) {
                            unreadCount++;
                        }
                    }

                    ApplicationData.getInstance().unreadNotifications = unreadCount;

                    goToAccountLandingPage();
                }
            }
        }, (int) previousDate);
    }
}
