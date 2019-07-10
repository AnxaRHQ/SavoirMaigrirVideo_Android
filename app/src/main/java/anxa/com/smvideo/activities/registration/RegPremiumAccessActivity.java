package anxa.com.smvideo.activities.registration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.util.Arrays;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.RegisterUserResponseContract;
import anxa.com.smvideo.ui.CustomDialog;


/**
 * Created by aprilanxa on 13/09/2017.
 */
public class RegPremiumAccessActivity extends Activity implements OnClickListener {

    ProgressBar progressBar;

    TextView country_tv;
    TextView code_tv;
    EditText telNum_et;

    AlertDialog.Builder builder;
    AlertDialog genericDialog;
    CustomDialog dialog;

    String userCountry;
    String telNum;

    String[] countryArray;
    String[] countryCodeArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.reg_premiumaccess);

        System.out.println("PremiumAccessActivity");

        builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setCancelable(false);

        ((TextView) (findViewById(R.id.reg_header).findViewById(R.id.header_title))).setText(getString(R.string.PREMIUM_ACCESS_HEADER_TITLE));

        country_tv = (TextView) findViewById(R.id.premium_country_tv);
        code_tv = (TextView) findViewById(R.id.premium_code_tv);
        telNum_et = (EditText) findViewById(R.id.premium_telNum_et);

        progressBar = (ProgressBar) findViewById(R.id.progressBar_premium);
        progressBar.setVisibility(View.GONE);

        country_tv.setOnClickListener(this);
        code_tv.setOnClickListener(this);

        countryArray = getResources().getStringArray(R.array.country_array);
        countryCodeArray = getResources().getStringArray(R.array.country_code_array);

        if (ApplicationData.getInstance().regUserProfile.getCountry() == null) {
            userCountry = "";
        } else {
            userCountry = ApplicationData.getInstance().regUserProfile.getCountry();
        }

        if (Arrays.asList(countryArray).indexOf(userCountry) >= 0) {
            code_tv.setText(countryCodeArray[Arrays.asList(countryArray).indexOf(userCountry)]);
            country_tv.setText(userCountry);
        } else {
            code_tv.setText("");
        }

        System.out.println("user country: " + userCountry);

    }

    @Override
    public void onClick(View v) {
        if (v == country_tv) {
            builder.setItems(R.array.country_array,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            country_tv.setText(countryArray[item]);
                            code_tv.setText(countryCodeArray[item]);
                        }
                    });

            genericDialog = builder.create();
            genericDialog.show();
        } else if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            if (v.getId() == R.id.YesButton) {
                requestAccessCodeToAPI();
            } else if (v.getId() == R.id.NoButton) {
                dialog.dismiss();
            } else if (v.getId() == R.id.OtherButton) {
                dialog.dismiss();
            }
        }
    }

    /**
     * Button
     **/
    public void sendAccessCode(View view) {
        telNum = code_tv.getText().toString() + " " + telNum_et.getText().toString();

        showCustomDialog();
    }

    /**
     * Premium Access Listener
     **/
    public void sendAccessCodeSuccess(String response) {
        System.out.println("sendAccessCode Succesful");
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });

        proceedToValidatePage();
    }

    /**
     * Private Methods
     **/
    private void showCustomDialog() {
        String confirmationMessage = getResources().getString(R.string.PREMIUM_TEL_NO_CONFIRMATION_DES) + "\n" + telNum;
        dialog = new CustomDialog(this, null, getResources().getString(R.string.btn_ok), getResources().getString(R.string.btn_cancel), false, confirmationMessage, getResources().getString(R.string.PREMIUM_TEL_NO_CONFIRMATION_TITLE), this);
        dialog.show();
    }

    private void showCustomDialog(String content) {
        dialog = new CustomDialog(this, getResources().getString(R.string.btn_ok), null, null, false, content, getResources().getString(R.string.ERROR), this);
        dialog.show();
    }

    private void requestAccessCodeToAPI() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        ApiCaller caller = new ApiCaller();


        caller.PostRequestCode(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                if (output!=null) {
                    progressBar.setVisibility(View.GONE);

                    RegisterUserResponseContract response = (RegisterUserResponseContract)output;
                    if (response.Message.equalsIgnoreCase("Successful")){
                        proceedToValidatePage();
                    }else{
                        showCustomDialog(response.Message);
                    }
                }

            }
        }, telNum.replace(" ", ""), ApplicationData.getInstance().regUserProfile.getRegId());
    }

    private void proceedToValidatePage() {
        Intent mainIntent = new Intent(this, RegValidateAccessCodeActivity.class);
        mainIntent.putExtra("telNum", telNum);
        this.startActivity(mainIntent);
    }

    public void goBackToPreviousPage(View view) {
        finish();
    }

}