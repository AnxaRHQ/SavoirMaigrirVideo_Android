package anxa.com.smvideo.activities.account;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.BuildConfig;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.LoginActivity;
import anxa.com.smvideo.common.WebkitURL;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.UserDataContract;

/**
 * Created by aprilanxa on 21/09/2017.
 */

public class AproposFragment extends Fragment implements View.OnClickListener
{
    private Context context;
    protected ApiCaller caller;
    private View mView;
    private ImageView backButton;

    private TextView version_tv;
    private TableRow apropos_row, conditions_row, privacy_row, contact_row, logout_row;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        this.context = getActivity();
        mView = inflater.inflate(R.layout.apropos_account, null);

        caller = new ApiCaller();

        //header change
        ((TextView) (mView.findViewById(R.id.header_title_tv))).setText(getString(R.string.apropos_menu_title));

        backButton = (ImageView) mView.findViewById(R.id.header_menu_back);
        backButton.setVisibility(View.VISIBLE);
        backButton.setImageDrawable(null);
        backButton.setBackground(context.getResources().getDrawable(R.drawable.ic_menu_white_24dp));

        ((TextView) (mView.findViewById(R.id.header_menu_iv))).setVisibility(View.GONE);

        version_tv = (TextView) (mView.findViewById(R.id.apropos_version_tv));
        version_tv.setText("Version " + BuildConfig.VERSION_NAME);

        conditions_row = (TableRow)(mView.findViewById(R.id.conditions_row));
        privacy_row = (TableRow)(mView.findViewById(R.id.privacy_row));
        contact_row = (TableRow)(mView.findViewById(R.id.contact_row));
        logout_row = (TableRow)(mView.findViewById(R.id.logout_row));

        conditions_row.setOnClickListener(this);
        privacy_row.setOnClickListener(this);
        contact_row.setOnClickListener(this);
        logout_row.setOnClickListener(this);

        logout_row.setVisibility(View.VISIBLE);

        return mView;
    }

    @Override
    public void onClick(View v)
    {
//        if (v == apropos_row){
//            goToAproposContentPage();
//        }else
        if (v == conditions_row){
            goToConditionsPage();
        }else if (v == privacy_row){
            goToPrivacyPage();
        }else if (v == contact_row){
            goToContactPage();
        }else if (v == logout_row){
            logoutUser();
        }
    }

    public void logoutUser()
    {
        UserDataContract up = ApplicationData.getInstance().userDataContract;

        long activeTimeMilliseconds = System.currentTimeMillis() - ApplicationData.getInstance().getAnxamatsSessionStart();
        if(activeTimeMilliseconds > ApplicationData.getInstance().maximumAnxamatsSessionTime)
        {
            activeTimeMilliseconds = ApplicationData.getInstance().maximumAnxamatsSessionTime;
        }
        final long activeTimeFinal = activeTimeMilliseconds;
        Log.d("sessionstartvalue", String.valueOf(ApplicationData.getInstance().getAnxamatsSessionStart()));
        if (up != null && up.Id>0 ) {
            if ((ApplicationData.getInstance().getAnxamatsSessionStart() > 0) &&
                    (activeTimeFinal > ApplicationData.minimumAnxamatsSessionTime)) {
                caller.PostAnxamatsActiveTime(new AsyncResponse() {
                    @Override
                    public void processFinish(Object output) {
                        Log.d("USERTIMELOGGED", "Logged user time " + activeTimeFinal);

                    }
                }, activeTimeFinal, up.Id);

            }
        }
        ApplicationData.getInstance().setAnxamatsSessionStart(context, 0);
        //clear login details
        ApplicationData.getInstance().userDataContract = new UserDataContract();
        ApplicationData.getInstance().regId = 1;

        ApplicationData.getInstance().setIsLogin(context, false);
        //clear the saved login credentials
        ApplicationData.getInstance().clearLoginCredentials();

        goToLoginPage();
    }

    private void goToAproposContentPage()
    {
        Intent mainIntent = new Intent(this.getActivity(), AproposContentActivity.class);
        getActivity().startActivity(mainIntent);
    }

    private void goToConditionsPage()
    {
        Intent mainContentBrowser = new Intent(context, BrowserActivity.class);
        mainContentBrowser.putExtra("HEADER_TITLE", getResources().getString(R.string.apropos_menu2));
        if (ApplicationData.getInstance().accountType.equalsIgnoreCase("free")) {
            mainContentBrowser.putExtra("URL_PATH", WebkitURL.free_conditionsURL);
        }else {
            mainContentBrowser.putExtra("URL_PATH", WebkitURL.conditionsURL);
        }
        startActivity(mainContentBrowser);
    }

    private void goToPrivacyPage()
    {
        Intent mainContentBrowser = new Intent(context, BrowserActivity.class);
        mainContentBrowser.putExtra("HEADER_TITLE", getResources().getString(R.string.apropos_menu3));
        if (ApplicationData.getInstance().accountType.equalsIgnoreCase("free")) {
            mainContentBrowser.putExtra("URL_PATH", WebkitURL.free_privacyURL);
        }else {
            mainContentBrowser.putExtra("URL_PATH", WebkitURL.privacyURL);
        }
        startActivity(mainContentBrowser);
    }

    private void goToContactPage()
    {
        Intent mainContentBrowser = new Intent(context, BrowserActivity.class);
        mainContentBrowser.putExtra("HEADER_TITLE", getResources().getString(R.string.apropos_menu4));
        if (ApplicationData.getInstance().accountType.equalsIgnoreCase("free")) {
            mainContentBrowser.putExtra("URL_PATH", WebkitURL.free_contactURL);
        }else {
            mainContentBrowser.putExtra("URL_PATH", WebkitURL.contactURL);
        }
        startActivity(mainContentBrowser);
    }

    private void goToLoginPage()
    {
        Intent mainIntent = new Intent(context, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
    }

}
