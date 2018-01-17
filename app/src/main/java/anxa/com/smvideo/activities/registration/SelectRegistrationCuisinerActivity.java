package anxa.com.smvideo.activities.registration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.MainLandingPageActivity;
import anxa.com.smvideo.activities.SplashActivity;
import anxa.com.smvideo.activities.account.LandingPageAccountActivity;
import anxa.com.smvideo.activities.free.LandingPageActivity;
import anxa.com.smvideo.common.SavoirMaigrirVideoConstants;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.BaseContract;
import anxa.com.smvideo.contracts.LoginContract;
import anxa.com.smvideo.contracts.PaymentOrderGoogleContract;
import anxa.com.smvideo.contracts.PaymentOrderResponseContract;
import anxa.com.smvideo.contracts.TVRegistrationUpdateContract;
import anxa.com.smvideo.contracts.UserDataResponseContract;
import anxa.com.smvideo.util.IabBroadcastReceiver;
import anxa.com.smvideo.util.IabHelper;
import anxa.com.smvideo.util.IabResult;
import anxa.com.smvideo.util.Inventory;
import anxa.com.smvideo.util.Purchase;

/**
 * Created by angelaanxa on 10/24/2017.
 */

public class SelectRegistrationCuisinerActivity extends Activity {
    Button btnSave;
    CheckBox cbCookingNoTime;
    CheckBox cbCooking30min;
    CheckBox cbCooking30min1hr;
    CheckBox cbCooking1hr;
    CheckBox cbInterested;
    CheckBox cbNotInterested;

    LinearLayout progressLayout;
    ApiCaller caller;

    private LoginContract loginContract;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_selectcuisiner);
        if(ApplicationData.getInstance().regUserProfile == null)
        {
            goToLanding();
        }

        //header change
        ((TextView) (this.findViewById(R.id.header_title_tv))).setText(R.string.registration_myProfileHeader);
        ((TextView) (this.findViewById(R.id.header_right_tv))).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.header_menu_iv)).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.header_menu_back)).setVisibility(View.GONE);

        btnSave = (Button)findViewById(R.id.save_btn);
         cbCookingNoTime = (CheckBox)findViewById(R.id.cbCookingNoTime);
         cbCooking30min = (CheckBox)findViewById(R.id.cbCooking30min);
         cbCooking30min1hr = (CheckBox)findViewById(R.id.cbCooking30min1hr);
         cbCooking1hr = (CheckBox)findViewById(R.id.cbCooking1hr);
         cbInterested = (CheckBox)findViewById(R.id.cbInterested);
         cbNotInterested = (CheckBox)findViewById(R.id.cbNotInterested);
        caller = new ApiCaller();

        progressLayout = (LinearLayout) findViewById(R.id.progress);
        progressLayout.setVisibility(View.GONE);

    }

    public void validateForm(View view) {


        //disable submit button
        btnSave.setEnabled(false);


        if (validateForm()) {
            startProgress();

            //uncomment this after
            updateRegistrationApi();
//            callOptinPage();
        } else {
            //form not validated
            btnSave.setEnabled(true);
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
    private boolean validateForm()
    {
        if((cbCookingNoTime.isChecked() || cbCooking30min.isChecked() || cbCooking30min1hr.isChecked()
                || cbCooking1hr.isChecked()) && (cbInterested.isChecked() || cbNotInterested.isChecked()))
        {
            if(cbCookingNoTime.isChecked())
            {
                ApplicationData.getInstance().regUserProfile.setCuisiner(getString(R.string.cooking_notime));
            }
            if(cbCooking30min.isChecked())
            {
                ApplicationData.getInstance().regUserProfile.setCuisiner(getString(R.string.cooking_30min));
            }
            if(cbCooking30min1hr.isChecked())
            {
                ApplicationData.getInstance().regUserProfile.setCuisiner(getString(R.string.cooking_30minto1hr));
            }
            if(cbCooking1hr.isChecked())
            {
                ApplicationData.getInstance().regUserProfile.setCuisiner(getString(R.string.cooking_1hr));
            }
            if(cbInterested.isChecked())
            {
                ApplicationData.getInstance().regUserProfile.setNoCookingTrial(true);
            }
            if(cbNotInterested.isChecked())
            {
                ApplicationData.getInstance().regUserProfile.setNoCookingTrial(false);
            }
            return true;
        }

        return false;
    }
    private void goToLanding()
    {
        Intent mainIntent = new Intent(this, LandingPageActivity.class);
        startActivity(mainIntent);
        finish();
    }
    private void updateRegistrationApi()
    {
        TVRegistrationUpdateContract contract = new TVRegistrationUpdateContract();
        contract.regId = ApplicationData.getInstance().regUserProfile.getRegId();
        contract.firstname = ApplicationData.getInstance().regUserProfile.getFirstname();
        contract.surname = ApplicationData.getInstance().regUserProfile.getLastname();
        contract.height = ApplicationData.getInstance().regUserProfile.getHeight();
        contract.weight = ApplicationData.getInstance().regUserProfile.getInitialWeight();
        contract.targetWeight = ApplicationData.getInstance().regUserProfile.getTargetWeight();
        if(ApplicationData.getInstance().regUserProfile.getGender().equalsIgnoreCase(getString(R.string.mon_compte_sexe_fem)))
        {
            contract.gender = false;
        }
        else
        {
            contract.gender = true;
        }
        contract.birthday = ApplicationData.getInstance().regUserProfile.getBday();
        contract.coachingProfile = ApplicationData.getInstance().regUserProfile.getCoaching();
        contract.mealProfile = ApplicationData.getInstance().regUserProfile.getMealProfile();
        contract.cookingPrepTime = ApplicationData.getInstance().regUserProfile.getCuisiner();
        contract.noCookingTrial = ApplicationData.getInstance().regUserProfile.getNoCookingTrial();
        caller.PostRegistrationUpdate(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                if (output != null) {

                    BaseContract responseContract = (BaseContract) output;


                    if (responseContract.Message.equalsIgnoreCase("Successful")) {
                        autoLogin();
                    }
                }
            }
        }, contract, Integer.parseInt(ApplicationData.getInstance().regUserProfile.getRegId()));
    }
    private void autoLogin() {
        //login user in the background to obtain user details
        loginContract = new LoginContract();

        loginToAPI();
    }

    private void loginToAPI() {

        loginContract.Email = ApplicationData.getInstance().getSavedUserName();
        loginContract.Password = ApplicationData.getInstance().getSavedPassword();
        loginContract.Check_npna = true;



        caller.PostLogin(new AsyncResponse() {
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
                            //updateGoogleOrder();
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


        if (ApplicationData.getInstance().isLoggedIn(getBaseContext())) {
            ApplicationData.getInstance().accountType = "account";
            Intent mainIntent = new Intent(this, LandingPageAccountActivity.class);
            startActivity(mainIntent);
            finish();

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
    public void setCheckBoxValue(View view) {
        switch (view.getId()) {
            case R.id.cbInterested:
                if (cbInterested.isChecked()) {
                    cbNotInterested.setChecked(false);

                }
                break;
            case R.id.cbNotInterested:
                if (cbNotInterested.isChecked()) {
                    cbInterested.setChecked(false);
                }
                break;
            case R.id.cbCookingNoTime:
                if (cbCookingNoTime.isChecked()) {
                    cbCooking30min.setChecked(false);
                    cbCooking30min1hr.setChecked(false);
                    cbCooking1hr.setChecked(false);
                }
                break;
            case R.id.cbCooking30min:
                if (cbCooking30min.isChecked()) {
                    cbCookingNoTime.setChecked(false);
                    cbCooking30min1hr.setChecked(false);
                    cbCooking1hr.setChecked(false);
                }
                break;
            case R.id.cbCooking30min1hr:
                if (cbCooking30min1hr.isChecked()) {
                    cbCookingNoTime.setChecked(false);
                    cbCooking30min.setChecked(false);
                    cbCooking1hr.setChecked(false);
                }
                break;
            case R.id.cbCooking1hr:
                if (cbCooking1hr.isChecked()) {
                    cbCookingNoTime.setChecked(false);
                    cbCooking30min.setChecked(false);
                    cbCooking30min1hr.setChecked(false);
                }
                break;
            case R.id.tv_cuisiner1:
                if (cbCookingNoTime.isChecked()) {
                    cbCookingNoTime.setChecked(false);
                    cbCooking30min.setChecked(false);
                    cbCooking30min1hr.setChecked(false);
                    cbCooking1hr.setChecked(false);
                }
                else
                {
                    cbCookingNoTime.setChecked(true);
                    cbCooking30min.setChecked(false);
                    cbCooking30min1hr.setChecked(false);
                    cbCooking1hr.setChecked(false);
                }
                break;
            case R.id.tv_cuisiner2:
                if (cbCooking30min.isChecked()) {
                    cbCookingNoTime.setChecked(false);
                    cbCooking30min.setChecked(false);
                    cbCooking30min1hr.setChecked(false);
                    cbCooking1hr.setChecked(false);
                }
                else
                {
                    cbCookingNoTime.setChecked(false);
                    cbCooking30min.setChecked(true);
                    cbCooking30min1hr.setChecked(false);
                    cbCooking1hr.setChecked(false);
                }
                break;
            case R.id.tv_cuisiner3:
                if (cbCooking30min1hr.isChecked()) {
                    cbCookingNoTime.setChecked(false);
                    cbCooking30min.setChecked(false);
                    cbCooking30min1hr.setChecked(false);
                    cbCooking1hr.setChecked(false);
                }
                else
                {
                    cbCookingNoTime.setChecked(false);
                    cbCooking30min.setChecked(false);
                    cbCooking30min1hr.setChecked(true);
                    cbCooking1hr.setChecked(false);
                }
                break;
            case R.id.tv_cuisiner4:
                if (cbCooking1hr.isChecked()) {
                    cbCookingNoTime.setChecked(false);
                    cbCooking30min.setChecked(false);
                    cbCooking30min1hr.setChecked(false);
                    cbCooking1hr.setChecked(false);
                }
                else
                {
                    cbCookingNoTime.setChecked(false);
                    cbCooking30min.setChecked(false);
                    cbCooking30min1hr.setChecked(false);
                    cbCooking1hr.setChecked(true);
                }
                break;
            case R.id.tv_interested:
                if (cbInterested.isChecked()) {
                    cbNotInterested.setChecked(false);
                    cbInterested.setChecked(false);
                }
                else
                {
                    cbNotInterested.setChecked(false);
                    cbInterested.setChecked(true);
                }
                break;
            case R.id.tv_NotInterested:
                if (cbNotInterested.isChecked()) {
                    cbInterested.setChecked(false);
                    cbNotInterested.setChecked(false);
                }
                else
                {
                    cbNotInterested.setChecked(true);
                    cbInterested.setChecked(false);
                }
                break;
            default:
        }
    }
    public void onBackPressed(View view) {
        super.onBackPressed();
    }

}
