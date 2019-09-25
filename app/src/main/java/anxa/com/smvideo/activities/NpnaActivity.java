package anxa.com.smvideo.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.util.AppUtil;

/**
 * Created by angelaanxa on 11/10/2017.
 */

public class NpnaActivity extends BaseVideoActivity {
    private static int NPNA_OFFERACTIVITY = 333;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.npna);
        //header change
        ((TextView) (this.findViewById(R.id.header_title_tv))).setText(R.string.app_name);
        ((Button) (this.findViewById(R.id.header_menu_iv))).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.header_menu_back)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.npna_intro)).setText(Html.fromHtml(getResources().getString(R.string.npna_intro).replace("#subscriptionenddate#", AppUtil.getEditWeightDateFormat(AppUtil.toDate(Long.valueOf(ApplicationData.getInstance().userDataContract.DietProfiles.get(0).CoachingEndDate).longValue())))));
        ((TextView) findViewById(R.id.npna_intro2)).setText(Html.fromHtml(getResources().getString(R.string.npna_intro2)));
        //ClearCookies();
    }

    public void goToPremiumPayment(View view)
    {
        Intent mainContentBrowser = new Intent(getApplicationContext(), NpnaOfferActivity.class);
        mainContentBrowser.putExtra("UPGRADE_PAYMENT", false);
        startActivityForResult(mainContentBrowser, NPNA_OFFERACTIVITY);
    }
}
