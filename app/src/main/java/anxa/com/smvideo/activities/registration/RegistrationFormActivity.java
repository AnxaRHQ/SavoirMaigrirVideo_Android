package anxa.com.smvideo.activities.registration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.LoginActivity;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.BaseContract;
import anxa.com.smvideo.contracts.UserDataContract;
import anxa.com.smvideo.models.RegUserProfile;

import static anxa.com.smvideo.util.AppUtil.isEmail;

/**
 * Created by ivanaanxa on 10/10/2017.
 */

public class RegistrationFormActivity extends Activity implements View.OnClickListener {

    EditText firstNameText, lastNameText, emailText;
    Button registerButton;
    LinearLayout progressLayout;
    RegUserProfile userProfile = new RegUserProfile();
    ApiCaller caller;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_form);

        firstNameText = (EditText) findViewById(R.id.reg_firstName);
        lastNameText = (EditText) findViewById(R.id.reg_lastName);
        emailText = (EditText) findViewById(R.id.reg_email);
        registerButton = ((Button) (this.findViewById(R.id.registration_continuer)));

        //header change
        ((TextView) (this.findViewById(R.id.header_title_tv))).setText(R.string.inscription);
        ((TextView) (this.findViewById(R.id.header_right_tv))).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.header_menu_iv)).setVisibility(View.GONE);
        ((ImageView) findViewById(R.id.header_menu_back)).setVisibility(View.VISIBLE);

        ((ImageView) findViewById(R.id.header_menu_back)).setOnClickListener(this);
        ((TextView) (this.findViewById(R.id.reg_login_account_exist_tv))).setOnClickListener(this);

        progressLayout = (LinearLayout) findViewById(R.id.progress);
        progressLayout.setVisibility(View.GONE);

        caller = new ApiCaller();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.header_menu_back) {
            onBackPressed();
        }else if(v.getId() == R.id.reg_login_account_exist_tv) {
            goToLoginPage();
        }
    }


    private void goToLoginPage() {
        Intent mainIntent = new Intent(this, LoginActivity.class);
        startActivity(mainIntent);
    }

    private void goToRegistrationPage2 () {
        Intent mainIntent = new Intent(this, RegistrationSelectPaymentActivity.class);
        startActivity(mainIntent);
    }
    public void validateRegistrationForm(View view) {

        //dismiss keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //disable submit button
        registerButton.setEnabled(false);


        try {
            setHideSoftKeyboard(emailText);
        } catch (Exception e) {
        }

        try {
            setHideSoftKeyboard(firstNameText);
        } catch (Exception e) {
        }

        try {
            setHideSoftKeyboard(lastNameText);
        } catch (Exception e) {
        }

        if (validateRegistrationForm()) {
            startProgress();
            checkRegistration();
            //uncomment this after
            //submitRegistrationForm();
//            callOptinPage();
        } else {
            //form not validated
            stopProgress();
        }
    }
    public void startProgress() {
        // TODO Auto-generated method stub
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                progressLayout.setVisibility(View.VISIBLE);
            }
        });
    }
    public void stopProgress() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                registerButton.setEnabled(true);

                progressLayout.setVisibility(View.INVISIBLE);
            }
        });
    }
    public Boolean validateRegistrationForm() {
        if (firstNameText.getText() == null || firstNameText.getText().length() <= 0) {
            displayToastMessage(getString(R.string.ALERTMESSAGE_FIRSTNAME_EMPTY));
            return false;
        } else if (lastNameText.getText() == null || lastNameText.getText().length() <= 0) {
            displayToastMessage(getString(R.string.ALERTMESSAGE_LASTNAME_EMPTY));
            return false;
        } else if (!isEmail(emailText.getText().toString())) {
            displayToastMessage(getString(R.string.SIGNUP_EMAIL_ERROR));
            return false;
        }  else {



            return true;
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
    private void checkRegistration()
    {  UserDataContract contract = new UserDataContract();
        contract.Email = emailText.getText().toString();
        contract.FirstName = firstNameText.getText().toString();
        contract.LastName = lastNameText.getText().toString();
        caller.CheckRegistration(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                if (output != null) {

                    BaseContract responseContract = (BaseContract) output;

                    if (responseContract.Message.equalsIgnoreCase("AccountAlreadyExist")) {
                        displayToastMessage(getString(R.string.ALERTMESSAGE_ACCOUNT_EXISTS));
                        stopProgress();
                    }
                    if (responseContract.Message.equalsIgnoreCase("Successful")) {
                        submitRegistrationForm();
                    }
                }
            }
        }, contract);
    }

    private void submitRegistrationForm() {
        userProfile.setFirstname(firstNameText.getText().toString());
        userProfile.setLastname(lastNameText.getText().toString());
        userProfile.setEmail(emailText.getText().toString());


        ApplicationData.getInstance().regUserProfile = userProfile;
        stopProgress();
        goToRegistrationPage2();
    }
    private void setHideSoftKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

}
