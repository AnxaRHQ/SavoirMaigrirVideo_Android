package anxa.com.smvideo.activities.registration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import anxa.com.smvideo.contracts.RegistrationDataContract;
import anxa.com.smvideo.contracts.RegistrationResponseContract;
import anxa.com.smvideo.contracts.TVRegistrationContract;
import anxa.com.smvideo.contracts.UserDataContract;
import anxa.com.smvideo.models.RegUserProfile;

import static anxa.com.smvideo.util.AppUtil.isEmail;

/**
 * Created by ivanaanxa on 10/10/2017.
 */

public class RegistrationFormActivity extends Activity implements View.OnClickListener {

    final String GENDER_MALE = "1";
    final String GENDER_FEMALE = "0";
    ImageButton manButton, womanButton;
    Button registerButton;
    EditText firstNameText, lastNameText, emailText, passwordText;
    TextView countryTextView, manTextView, womanTextView;
    LinearLayout progressLayout;
    RegUserProfile userProfile = new RegUserProfile();
    ApiCaller caller;
    AlertDialog.Builder builder;
    AlertDialog genericDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_form);

        firstNameText = (EditText) findViewById(R.id.reg_firstName);
        lastNameText = (EditText) findViewById(R.id.reg_lastName);
        emailText = (EditText) findViewById(R.id.reg_email);
        passwordText = (EditText) findViewById(R.id.reg_password);

        countryTextView = (TextView) findViewById(R.id.reg_country);
        manTextView = (TextView) findViewById(R.id.reg_manTextView);
        womanTextView = (TextView) findViewById(R.id.reg_womanTextView);

        manButton = (ImageButton) findViewById(R.id.reg_manBtn);
        womanButton = (ImageButton) findViewById(R.id.reg_womanBtn);
        registerButton = ((Button) (this.findViewById(R.id.reg_submitBtn)));

        //header change
        ((TextView) (this.findViewById(R.id.header_title))).setText(R.string.inscription);

        ((ImageView) findViewById(R.id.backButton)).setOnClickListener(this);

        progressLayout = (LinearLayout) findViewById(R.id.progress);
        progressLayout.setVisibility(View.GONE);

        caller = new ApiCaller();

        builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setCancelable(false);

        countryTextView.setOnClickListener(this);
    }
    public void selectGender(View view) {
        if (view == manButton) {
            userProfile.setGender(GENDER_MALE);
            manButton.setImageDrawable(getResources().getDrawable(R.drawable.gender_man_active));
            womanButton.setImageDrawable(getResources().getDrawable(R.drawable.gender_woman));
            manTextView.setTextColor(getResources().getColor(R.color.text_orange));
            womanTextView.setTextColor(getResources().getColor(R.color.text_darkgray));
        } else if (view == womanButton) {
            userProfile.setGender(GENDER_FEMALE);
            manButton.setImageDrawable(getResources().getDrawable(R.drawable.gender_man));
            womanButton.setImageDrawable(getResources().getDrawable(R.drawable.gender_woman_active));
            manTextView.setTextColor(getResources().getColor(R.color.text_darkgray));
            womanTextView.setTextColor(getResources().getColor(R.color.text_orange));
        }

    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.header_menu_back) {
            onBackPressed();
        }
        else if (v.getId() == R.id.reg_country) {
            builder.setItems(R.array.country_array,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            String[] arr = getResources().getStringArray(
                                    R.array.country_array);
                            countryTextView.setText(arr[item]);
                            userProfile.setCountry(arr[item]);
                        }
                    });

            genericDialog = builder.create();
            genericDialog.show();
        }
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
            submitRegistrationForm();
//            callOptinPage();
        } else {
            //form not validated
            stopProgress();
        }
    }
    private void submitRegistrationForm() {
        userProfile.setFirstname(firstNameText.getText().toString());
        userProfile.setLastname(lastNameText.getText().toString());
        userProfile.setEmail(emailText.getText().toString());
        userProfile.setPassword(passwordText.getText().toString());
        userProfile.setCountry(countryTextView.getText().toString());
        userProfile.setSid(221);

        ApplicationData.getInstance().regUserProfile = userProfile;

        processRegistration(userProfile);
    }

    private void processRegistration(RegUserProfile profile) {
        System.out.println("processRegistration: " + profile);

        caller.PostRegistration(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                Log.d("PostRegisterUser", output.toString());

                if (output!=null){
                    RegistrationResponseContract responseContract = (RegistrationResponseContract) output;

                    if (responseContract.data != null) {

                    }
                    if (responseContract.Message.equalsIgnoreCase("AccountAlreadyExist")) {
                        //ApplicationData.getInstance().regUserProfile.setUsername(contract.username);
                        stopProgress();
                        displayToastMessage(getString(R.string.ALERTMESSAGE_ACCOUNT_EXISTS));
                    }
                    if (responseContract.Message.equalsIgnoreCase("Successful")){
                        RegUserProfile newUser = ApplicationData.getInstance().regUserProfile;
                        if (responseContract.data != null) {
                            RegistrationDataContract registerUserContract = (RegistrationDataContract)responseContract.data;

                            newUser.setRegId(String.valueOf(registerUserContract.RegId));
                            newUser.setAJRegNo(String.valueOf(registerUserContract.AJRegNo));
                            System.out.println("PostRegisterUser newUser" + newUser.getEmail());
                            System.out.println("PostRegisterUser newUser regId" + newUser.getRegId());

                            ApplicationData.getInstance().regUserProfile = newUser;



                                callPersonnalisationPage();

                        }
                    }else{
                        if (responseContract.MessageDetails!=null)
                            displayToastMessage(responseContract.MessageDetails);
                    }
                }

                stopProgress();

            }
        }, userProfile);

    }
    private void callPersonnalisationPage(){

        stopProgress();
        Intent mainIntent;
        if(ApplicationData.getInstance().regUserProfile.getGender() == "1")
        {
            mainIntent = new Intent(getApplicationContext(), RegPersonnalisation2Activity.class);
            mainIntent.putExtra("COACHING_NUM", 6);
        }else{
            mainIntent = new Intent(this, RegPersonnalisation1Activity.class);
        }

        finish();
        this.startActivity(mainIntent);
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
        } else if (passwordText.getText() == null || passwordText.getText().length() <= 0) {
            displayToastMessage(getString(R.string.ALERTMESSAGE_PASSWORD_EMPTY));
            return false;
        } else if (countryTextView.getText() == null || countryTextView.getText().length() <= 0) {
            displayToastMessage(getString(R.string.ALERTMESSAGE_COUNTRY_EMPTY));
            return false;
        } else if (userProfile.getGender() == null) {
            displayToastMessage(getString(R.string.ALERTMESSAGE_GENDER_EMPTY));
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

/*    private void submitRegistrationForm() {
        userProfile.setFirstname(firstNameText.getText().toString());
        userProfile.setLastname(lastNameText.getText().toString());
        userProfile.setEmail(emailText.getText().toString());


        ApplicationData.getInstance().regUserProfile = userProfile;
        stopProgress();
        goToRegistrationPage2();
    }*/
    private void setHideSoftKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

}
