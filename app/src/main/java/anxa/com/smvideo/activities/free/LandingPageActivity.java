package anxa.com.smvideo.activities.free;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.LoginActivity;
import anxa.com.smvideo.activities.MainActivity;
import anxa.com.smvideo.activities.registration.RegistrationActivity;
import anxa.com.smvideo.activities.account.BrowserActivity;
import anxa.com.smvideo.activities.registration.RegistrationFormActivity;
import anxa.com.smvideo.activities.registration.RegistrationMainObjectiveActivity;
import anxa.com.smvideo.common.WebkitURL;

/**
 * Created by aprilanxa on 31/05/2017.
 */

public class LandingPageActivity extends Activity implements View.OnClickListener {

    private static final int BROWSERTAB_ACTIVITY = 1111;
    private ImageView header_info_iv;
    private Button contact_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_page_free);

        ApplicationData.getInstance().showLandingPage = false;

        //initialize on click (transfer inside api call if there will be one in the future
        ((Button) findViewById(R.id.LandingDiscoverButton)).setOnClickListener(this);
        ((Button) findViewById(R.id.LandingRecettesButton)).setOnClickListener(this);
        ((Button) findViewById(R.id.LandingBilanButton)).setOnClickListener(this);
        ((Button) findViewById(R.id.LandingTemoignagesButton)).setOnClickListener(this);
        ((Button) findViewById(R.id.LandingRegisterButton)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.LandingImage1)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.LandingImage2)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.LandingImage3)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.LandingImage4)).setOnClickListener(this);

/*        header_info_iv = (ImageView) findViewById(R.id.header_info_iv);
        header_info_iv.setOnClickListener(this);*/

        contact_btn = (Button) findViewById(R.id.contact_account);
        contact_btn.setPaintFlags(contact_btn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        contact_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.LandingDiscoverButton || v.getId() == R.id.LandingImage1) {
            GoToDiscoverPage();
        } else if (v.getId() == R.id.LandingRecettesButton || v.getId() == R.id.LandingImage4) {
            GoToRecettesPage();
        } else if (v.getId() == R.id.LandingBilanButton || v.getId() == R.id.LandingImage2) {
            GoToBilanPage();
        } else if (v.getId() == R.id.LandingTemoignagesButton || v.getId() == R.id.LandingImage3) {
            GoToTemoignagesPage();
        } else if (v.getId() == R.id.LandingRegisterButton) {
            goToRegistrationPage();
        }else if (v == header_info_iv) {
            goToAproposPage();
        }else if (v == contact_btn) {
            goToContactPage();
        }
    }

    private void GoToDiscoverPage() {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Decouvir;
        Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(mainIntent);

    }

    private void GoToRecettesPage() {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Recettes;
        Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(mainIntent);
    }

    private void GoToBilanPage() {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Bilan;
        Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(mainIntent);
    }

    private void GoToTemoignagesPage() {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Temoignages;
        Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(mainIntent);
    }

    private void goToRegistrationPage() {
        Intent mainIntent = new Intent(this, RegistrationMainObjectiveActivity.class);
        startActivity(mainIntent);
    }
    private void goToContactPage() {
        Intent mainContentBrowser = new Intent(this, BrowserActivity.class);
        mainContentBrowser.putExtra("HEADER_TITLE", getResources().getString(R.string.contact));

            mainContentBrowser.putExtra("URL_PATH", WebkitURL.free_contactURL);

        startActivity(mainContentBrowser);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == BROWSERTAB_ACTIVITY) {
            if (intent != null) {
                boolean isLogin = intent.getBooleanExtra("TO_LOGIN", false);
                if (isLogin) {
                    goToLoginPage();
                } else if (intent.getBooleanExtra("TO_LANDING", false)) {

                }
            }
        }
    }

    private void goToLoginPage() {
        System.out.println("goToLoginPage");
        Intent mainIntent = new Intent(this, LoginActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
    }

    @Override
    public void onBackPressed() {
    }

    private void goToAproposPage() {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Apropos;
        Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(mainIntent);
    }

    private void goToConditionsPage() {
        Intent mainContentBrowser = new Intent(this, BrowserActivity.class);
        mainContentBrowser.putExtra("HEADER_TITLE", getResources().getString(R.string.apropos_menu2));
        mainContentBrowser.putExtra("URL_PATH", WebkitURL.free_conditionsURL);
        startActivity(mainContentBrowser);
    }

}
