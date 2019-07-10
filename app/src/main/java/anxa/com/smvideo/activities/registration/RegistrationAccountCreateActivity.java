package anxa.com.smvideo.activities.registration;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.support.annotation.Nullable;
import android.util.Log;
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
import anxa.com.smvideo.activities.free.LandingPageActivity;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.PaymentOrderResponseContract;
import anxa.com.smvideo.contracts.RegistrationDataContract;
import anxa.com.smvideo.contracts.RegistrationResponseContract;
import anxa.com.smvideo.contracts.TVRegistrationContract;
import anxa.com.smvideo.util.IabBroadcastReceiver;
import anxa.com.smvideo.util.IabHelper;
import anxa.com.smvideo.util.IabResult;

/**
 * Created by angelaanxa on 10/20/2017.
 */

public class RegistrationAccountCreateActivity extends Activity {
    private EditText etPassword;
    private EditText etPseudo;
    private EditText etEmail;
    private Button btnValidate;

    LinearLayout progressLayout;
    ApiCaller caller;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_accountcreate);
        if(ApplicationData.getInstance().regUserProfile == null)
        {
            goToLanding();
        }
        //header change
        ((TextView) (this.findViewById(R.id.header_title_tv))).setText(R.string.menu_mon_compte);
        ((TextView) (this.findViewById(R.id.header_right_tv))).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.header_menu_iv)).setVisibility(View.GONE);
        ((ImageView) findViewById(R.id.header_menu_back)).setVisibility(View.GONE);

        etEmail = ((EditText) (this.findViewById(R.id.et_email)));
        etEmail.setText(ApplicationData.getInstance().regUserProfile.getEmail());
        etPassword = ((EditText) (this.findViewById(R.id.et_pw)));
        etPseudo = ((EditText) (this.findViewById(R.id.et_pseudo)));
        btnValidate = ((Button) (this.findViewById(R.id.btnValidateRegister)));

        progressLayout = (LinearLayout) findViewById(R.id.progress);
        progressLayout.setVisibility(View.GONE);

        caller = new ApiCaller();
    }
    public void validateRegistrationForm(View view) {

        //dismiss keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //disable submit button
        btnValidate.setEnabled(false);


        try {
            setHideSoftKeyboard(etPassword);
        } catch (Exception e) {
        }

        try {
            setHideSoftKeyboard(etPseudo);
        } catch (Exception e) {
        }



        if (validateRegistrationForm()) {
            startProgress();

            //uncomment this after
            submitRegistrationForm();
//            callOptinPage();
        } else {
            //form not validated
            btnValidate.setEnabled(true);
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
                btnValidate.setEnabled(true);

                progressLayout.setVisibility(View.INVISIBLE);
            }
        });
    }
    public Boolean validateRegistrationForm() {
        if (etPassword.getText() == null || etPassword.getText().length() <= 0) {
            displayToastMessage(getString(R.string.ALERTMESSAGE_PASSWORD_EMPTY));
            return false;
        } else if (etPseudo == null ||etPseudo.getText().length() <= 0) {
            displayToastMessage(getString(R.string.ALERTMESSAGE_PSEUDO_EMPTY));
            return false;
        } else {
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
    private void goToLanding()
    {
        Intent mainIntent = new Intent(this, LandingPageActivity.class);
        startActivity(mainIntent);
        finish();
    }
    private void goToObjective()
    {
        Intent mainIntent = new Intent(this, RegistrationObjectiveActivity.class);
        startActivity(mainIntent);
        finish();
    }
    private void setHideSoftKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
    private void submitRegistrationForm() {
        System.out.println("submitRegistrationForm ");
        final TVRegistrationContract contract = new TVRegistrationContract();
        contract.email = String.valueOf(etEmail.getText());
        //contract.username = String.valueOf(etPseudo.getText());
        contract.password = String.valueOf(etPassword.getText());
        contract.sid = 221;
        contract.siteId = 3;

        caller.PostRegistration(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                if (output != null) {
                    System.out.println("PostRegistration: " + output);
                    RegistrationResponseContract responseContract = (RegistrationResponseContract) output;

                    if (responseContract.data != null) {

                    }
                    if (responseContract.Message.equalsIgnoreCase("AccountAlreadyExist")) {
                        //ApplicationData.getInstance().regUserProfile.setUsername(contract.username);
                        stopProgress();
                        displayToastMessage(getString(R.string.ALERTMESSAGE_ACCOUNT_EXISTS));
                    }
                    if (responseContract.Message.equalsIgnoreCase("Successful")) {
                        ApplicationData.getInstance().regId = responseContract.data.RegId;
                        //ApplicationData.getInstance().regUserProfile.setUsername(contract.username);
                        ApplicationData.getInstance().regUserProfile.setEmail(contract.email);
                        ApplicationData.getInstance().regUserProfile.setRegId(String.valueOf(responseContract.data.RegId));
                        ApplicationData.getInstance().regUserProfile.setAJRegNo(String.valueOf(responseContract.data.AJRegNo));
                        ApplicationData.getInstance().saveLoginCredentials(contract.email, contract.password);
                        goToObjective();
                    }
                }
            }
        }, contract, contract.email);
    }

}
