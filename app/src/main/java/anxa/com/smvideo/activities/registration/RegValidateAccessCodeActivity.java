package anxa.com.smvideo.activities.registration;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.ui.CustomDialog;


/**
 * Created by aprilanxa on 13/09/2017.
 */

public class RegValidateAccessCodeActivity extends Activity implements View.OnClickListener {

    String telNum;

    EditText validateCode_et;
    TextView validateCode_telNum;

    ProgressBar progressBar;
    CustomDialog dialog;

    ApiCaller caller;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.reg_validateaccesscode);

        caller = new ApiCaller();

        telNum = getIntent().getStringExtra("telNum");

        System.out.print("Validate telNum: " + telNum);

        ((TextView)(findViewById(R.id.reg_header).findViewById(R.id.header_title))).setText(getString(R.string.PREMIUM_ACCESS_HEADER_TITLE));

        validateCode_et = (EditText) findViewById(R.id.validatecode_et);
        validateCode_telNum = (TextView) findViewById(R.id.validate_telnum_tv);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_validateCode);
        progressBar.setVisibility(View.GONE);

        if (telNum!=null){
            validateCode_telNum.setText(telNum);
        }else{
            validateCode_telNum.setText("");
        }
    }

    /**Button OnClick**/
    public void validateAccessCode(View view){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        caller.PostValidateCode(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                if (output!=null) {
                    progressBar.setVisibility(View.GONE);
                    proceedToWelcomePage();
                }

            }
        }, telNum, ApplicationData.getInstance().regUserProfile.getRegId(), validateCode_et.getText().toString());

    }

    public void resendAccessCode(View view){
        showCustomDialog();
    }

    @Override
    public void onClick(View v) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            if (v.getId() == R.id.YesButton) {
                resendAccessCodeToAPI();
            } else if (v.getId() == R.id.NoButton) {
                dialog.dismiss();
            }else if(v.getId() == R.id.OtherButton){
                dialog.dismiss();
            }
        }
    }

    /**Private Methods**/
    private void proceedToWelcomePage(){
        Intent mainIntent = new Intent(this, RegWelcomeBravoActivity.class);
        this.startActivity(mainIntent);
    }

    private void resendAccessCodeToAPI(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        caller.PostRequestCode(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                if (output!=null) {
                    progressBar.setVisibility(View.GONE);
                }

            }
        }, telNum.replace(" ", ""), ApplicationData.getInstance().regUserProfile.getRegId());
    }

    private void showCustomDialog() {
        String confirmationMessage = getResources().getString(R.string.PREMIUM_TEL_NO_CONFIRMATION_DES) + "\n" + telNum;
        dialog = new CustomDialog(this, null, getResources().getString(R.string.btn_ok), getResources().getString(R.string.btn_cancel), false, confirmationMessage, getResources().getString(R.string.PREMIUM_TEL_NO_CONFIRMATION_TITLE), this);
        dialog.show();
    }

    public void goBackToPreviousPage(View view) {
        finish();
    }

}