package anxa.com.smvideo.activities.account;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.BaseVideoActivity;
import anxa.com.smvideo.activities.MainActivity;
import anxa.com.smvideo.activities.registration.SelectRegistrationCuisinerActivity;
import anxa.com.smvideo.common.SavoirMaigrirVideoConstants;
import anxa.com.smvideo.common.WebkitURL;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.DietProfilesDataContract;
import anxa.com.smvideo.contracts.PaymentOrderGoogleContract;
import anxa.com.smvideo.contracts.PaymentOrderResponseContract;
import anxa.com.smvideo.contracts.UserDataContract;
import anxa.com.smvideo.contracts.UserDataResponseContract;
import anxa.com.smvideo.models.NavItem;
import anxa.com.smvideo.ui.DrawerListAdapter;
import anxa.com.smvideo.util.AppUtil;
import anxa.com.smvideo.util.CommonHelper;
import anxa.com.smvideo.util.IabBroadcastReceiver;
import anxa.com.smvideo.util.IabHelper;
import anxa.com.smvideo.util.IabResult;
import anxa.com.smvideo.util.Inventory;
import anxa.com.smvideo.util.Purchase;

/**
 * Created by aprilanxa on 14/06/2017.
 */

public class LandingPageAccountActivity extends Fragment implements View.OnClickListener , IabBroadcastReceiver.IabBroadcastListener {

    protected ApiCaller caller;
    String userName = "";
    TextView initial_weight_tv, target_weight_tv, lost_weight_tv;
    ProgressBar weightProgressBar, landingProgressBar;
    private ImageView header_info_iv;
    private Button contact_btn;

    IabHelper mHelper;
    IabBroadcastReceiver mBroadcastReceiver;
    static final String TAG = "SMVideo";
    // Does the user have the premium upgrade?
    boolean mIsPremium = false;
    // Will the subscription auto-renew?
    boolean mAutoRenewEnabled = false;
    String mSubscribedSku = "";
    boolean mSubscribedTo = false;
    private PaymentOrderGoogleContract paymentOrderGoogleContract;
    private Context context;
    View mView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = getActivity();
        caller = new ApiCaller();
        mView = inflater.inflate(R.layout.menu_page_account, null);

        initial_weight_tv = (TextView) mView.findViewById(R.id.initial_weight_tv);
        target_weight_tv = (TextView) mView.findViewById(R.id.target_weight_tv);
        lost_weight_tv = (TextView) mView.findViewById(R.id.lost_weight_tv);

        weightProgressBar = (ProgressBar) mView.findViewById(R.id.weight_landing_progressBar);
        landingProgressBar = (ProgressBar) mView.findViewById(R.id.landing_account_progressBar);
        landingProgressBar.setVisibility(View.VISIBLE);

      /*  header_info_iv = (ImageView) mView.findViewById(R.id.header_info_iv);
        header_info_iv.setOnClickListener(this);*/
// DrawerLayout


        caller = new ApiCaller();

        caller.GetAccountUserData(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                if (output != null) {
                    UserDataResponseContract c = (UserDataResponseContract) output;
                    //INITIALIZE ALL ONCLICK AND API RELATED PROCESS HERE TO AVOID CRASHES

                    if (c != null && c.Data != null) {

                        System.out.println("GetAccountUserData: " + c.Data);
                        ApplicationData.getInstance().userDataContract = c.Data;

                        userName = c.Data.FirstName;

//                        String welcome_message = getString(R.string.welcome_account_1).replace("%@", userName) + getString(R.string.welcome_account_2);
//                        ((TextView) (mView.findViewById(R.id.welcome_message_account_tv))).setText(welcome_message);

                        if (c.Data.DietProfiles != null) {
                            for (DietProfilesDataContract dietProfilesDataContract : c.Data.DietProfiles) {
                                if (dietProfilesDataContract.CoachingStartDate != null && !dietProfilesDataContract.CoachingStartDate.equalsIgnoreCase("null")) {
                                    ApplicationData.getInstance().dietProfilesDataContract = dietProfilesDataContract;
                                    break;
                                }
                            }
                            ApplicationData.getInstance().currentWeekNumber = AppUtil.getCurrentWeekNumber(Long.parseLong(ApplicationData.getInstance().dietProfilesDataContract.CoachingStartDate), new Date());
//                            welcome_message = welcome_message.replace("%d", Integer.toString(ApplicationData.getInstance().currentWeekNumber));
                            //String welcome_message = getString(R.string.welcome_account_1).replace("%@", userName).concat(getString(R.string.welcome_account_2).replace("%f", AppUtil.getCurrentDayName(AppUtil.getCurrentDayNumber()))).concat(getString(R.string.welcome_account_3).replace("%d", Integer.toString(ApplicationData.getInstance().currentWeekNumber)));

                            //((TextView) (mView.findViewById(R.id.welcome_message_account_tv))).setText(welcome_message);
                        }
                        updateProgressBar();
                    }

                    landingProgressBar.setVisibility(View.GONE);

                }

            }
        });

        ApplicationData.getInstance().showLandingPage = false;

        //initialize on click (transfer inside api call if there will be one in the future
        ((Button) mView.findViewById(R.id.LandingConsultationButton)).setOnClickListener(this);
        ((Button) mView.findViewById(R.id.LandingRepasButton)).setOnClickListener(this);
        ((Button) mView.findViewById(R.id.LandingRecettesAccountButton)).setOnClickListener(this);
        ((Button) mView.findViewById(R.id.LandingConseilsButton)).setOnClickListener(this);
        ((Button) mView.findViewById(R.id.LandingExercicesButton)).setOnClickListener(this);
        ((Button) mView.findViewById(R.id.LandingSuiviButton)).setOnClickListener(this);
        ((Button) mView.findViewById(R.id.LandingMonCompteButton)).setOnClickListener(this);
        ((Button) mView.findViewById(R.id.LandingCarnetAccountButton)).setOnClickListener(this);
        ((Button) mView.findViewById(R.id.LandingMessagesButton)).setOnClickListener(this);
        ((ImageView) mView.findViewById(R.id.LandingImage1_account)).setOnClickListener(this);
        ((ImageView) mView.findViewById(R.id.LandingImage2_account)).setOnClickListener(this);
        ((ImageView) mView.findViewById(R.id.LandingImage3_account)).setOnClickListener(this);
        ((ImageView) mView.findViewById(R.id.LandingImage4_account)).setOnClickListener(this);
        ((ImageView) mView.findViewById(R.id.LandingImage5_account)).setOnClickListener(this);
        ((ImageView) mView.findViewById(R.id.LandingImage6_account)).setOnClickListener(this);
        ((ImageView) mView.findViewById(R.id.LandingImage7_account)).setOnClickListener(this);
        ((ImageView) mView.findViewById(R.id.LandingImage8_account)).setOnClickListener(this);
        ((ImageView) mView.findViewById(R.id.LandingImage9_account)).setOnClickListener(this);



        contact_btn = (Button) mView.findViewById(R.id.contact_account);
        contact_btn.setPaintFlags(contact_btn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        contact_btn.setOnClickListener(this);

//((TextView) (this.mView.findViewById(R.id.registrationform2_headerTitle))).setText(R.string.inscription_headerTitle);
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlQ6C20yTohzxhmSNwK01P9ipib2U60vnqYaozFYTJnMsl3zUBHivPo8F5+nIPJNwqYLTuBcGF93hObY5KGp+aPCWvH2s3XHwQoHwgVhreHevlv60qj3i/74yzFude4WhKstESBO3zyQC2SKa8dHqz7gdakfPukI44ZAzykr4eqfJG4UCppcVyxBLE3piKyC+6Y63w34Ljy96NvxLoCUKRNWwp8FoutOOpidisLxtBfxDT3MKzcMAUYbD954kZ+COkvqHXg9eb9eiuclPE9eirhReigSUySQEUsJrA37z/XJflGaA3btf8FxLwOLU3VHVMKif06Yfoj93focRy2KJNwIDAQAB";

        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(context, base64EncodedPublicKey);

        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    //complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(LandingPageAccountActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                context.registerReceiver(mBroadcastReceiver, broadcastFilter);
//
//                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    //complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });
        return mView;
    }
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                //complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SavoirMaigrirVideoConstants.SKU_JMC_3MONTHS);
            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

            // First find out which subscription is auto renewing
            Purchase jmc_3months = inventory.getPurchase(SavoirMaigrirVideoConstants.SKU_JMC_3MONTHS);
            Purchase jmc_6months = inventory.getPurchase(SavoirMaigrirVideoConstants.SKU_JMC_6MONTHS);
            if (jmc_3months != null && jmc_3months.isAutoRenewing()) {
                mSubscribedSku = SavoirMaigrirVideoConstants.SKU_JMC_3MONTHS;
                mAutoRenewEnabled = true;

                paymentOrderGoogleContract = new PaymentOrderGoogleContract();
                paymentOrderGoogleContract.setOrderId(jmc_3months.getOrderId());
                paymentOrderGoogleContract.setProductId(jmc_3months.getSku());
                System.out.println("Purchase successful time: " + jmc_3months.getPurchaseTime() );
                paymentOrderGoogleContract.setPurchaseDateMs(jmc_3months.getPurchaseTime());
                paymentOrderGoogleContract.setPurchaseToken(jmc_3months.getToken());
                paymentOrderGoogleContract.setDescription(getString(R.string.incription_3mois));
                //alert("User is currently subscribed to: " + getString(R.string.incription_3mois) + "\nAutorenewable: " + mAutoRenewEnabled);
            } else if (jmc_6months != null && jmc_6months.isAutoRenewing()) {
                mSubscribedSku = SavoirMaigrirVideoConstants.SKU_JMC_6MONTHS;
                mAutoRenewEnabled = true;

                paymentOrderGoogleContract = new PaymentOrderGoogleContract();
                paymentOrderGoogleContract.setOrderId(jmc_6months.getOrderId());
                paymentOrderGoogleContract.setProductId(jmc_6months.getSku());
                System.out.println("Purchase successful time: " + jmc_6months.getPurchaseTime());
                paymentOrderGoogleContract.setPurchaseDateMs((jmc_6months.getPurchaseTime() ));
                paymentOrderGoogleContract.setPurchaseToken(jmc_6months.getToken());
                paymentOrderGoogleContract.setDescription(getString(R.string.incription_6mois));
                //alert("User is currently subscribed to: " + getString(R.string.incription_6mois) + "\nAutorenewable: " + mAutoRenewEnabled);
            } else {
                mSubscribedSku = "";
                mAutoRenewEnabled = false;
//                alert("User does not have any subscription");
            }

            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
            mSubscribedTo =  (jmc_3months != null && verifyDeveloperPayload(jmc_3months))
                    || (jmc_6months != null && verifyDeveloperPayload(jmc_6months));
            if(mSubscribedTo && ApplicationData.getInstance().regId > 0)
            {
                updateGoogleOrder();
            }
            Log.d(TAG, "User " + (mSubscribedTo ? "HAS" : "DOES NOT HAVE")
                    + "  subscription.");
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };
    /**
     * Verifies the developer payload of a purchase.
     */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.LandingRepasButton || v.getId() == R.id.LandingImage1_account) {
            goToRepasPage();
        } else if (v.getId() == R.id.LandingConsultationButton || v.getId() == R.id.LandingImage2_account) {
            goToConsultationPage();
        } else if (v.getId() == R.id.LandingRecettesAccountButton || v.getId() == R.id.LandingImage3_account) {
            goToMessagesPage();
        } else if (v.getId() == R.id.LandingCarnetAccountButton || v.getId() == R.id.LandingImage4_account) {
            goToCoachingPage();
        } else if (v.getId() == R.id.LandingMessagesButton || v.getId() == R.id.LandingImage5_account) {
            goToVideosPage();
        } else if (v.getId() == R.id.LandingConseilsButton || v.getId() == R.id.LandingImage6_account) {
            goToWeightPage();
        } else if (v.getId() == R.id.LandingExercicesButton || v.getId() == R.id.LandingImage7_account) {
            goToCommunityPage();
        } else if (v.getId() == R.id.LandingSuiviButton || v.getId() == R.id.LandingImage8_account) {
            goToFichesPage();
        } else if (v.getId() == R.id.LandingMonCompteButton || v.getId() == R.id.LandingImage9_account) {
            goToAmbassadricePage();
        } else if (v == header_info_iv) {
            goToAproposPage();
        } else if (v == contact_btn) {
            goToContactPage();
        } 
    }


    public void goToCoachingPage() {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Coaching;
        Intent mainIntent = new Intent(context, MainActivity.class);
        startActivity(mainIntent);
    }

    public void goToRepasPage() {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Repas;
        Intent mainIntent = new Intent(context, MainActivity.class);
        startActivity(mainIntent);
    }


    public void goToMessagesPage() {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Messages;
        goToFragmentPage(new MessagesAccountFragment());
    }

    public void goToConseilsPage() {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Consultation;
        Intent mainIntent = new Intent(context, MainActivity.class);
        startActivity(mainIntent);

    }

    public void goToVideosPage() {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Videos;
        Intent mainIntent = new Intent(context, MainActivity.class);
        startActivity(mainIntent);
    }
    public void goToWeightPage() {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Weight;
        Intent mainIntent = new Intent(context, MainActivity.class);
        startActivity(mainIntent);

    }
    public void goToCommunityPage() {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Communaute;
        Intent mainIntent = new Intent(context, MainActivity.class);
        startActivity(mainIntent);

    }
    public void goToAmbassadricePage() {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Ambassadrice;
        Intent mainIntent = new Intent(context, MainActivity.class);
        startActivity(mainIntent);

    }
    public void goToFichesPage() {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Fiches;
        Bundle bundle = new Bundle();
        bundle.putString("header_title", getString(R.string.menu_account_webinars));
        bundle.putString("webkit_url", WebkitURL.fichesWebkitUrl);
        goToWebkitPage(ApplicationData.SelectedFragment.Account_Fiches, bundle);
    }

    public void goToConsultationPage() {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Consultation;
        Bundle bundle = new Bundle();
        bundle.putString("header_title", getString(R.string.menu_account_webinars));
        bundle.putString("webkit_url", WebkitURL.webinarWebkitUrl);
        goToWebkitPage(ApplicationData.SelectedFragment.Account_Consultation, bundle);
    }
    private void goToFragmentPage(Fragment fragment) {
       FragmentManager fragmentManager = getFragmentManager();
        if (getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT") != null) {
            fragmentManager.beginTransaction().remove(getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT")).commit();
        } else {
        }

        try {

            fragmentManager.beginTransaction().replace(R.id.mainContent, fragment, "CURRENT_FRAGMENT").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void goToWebkitPage(ApplicationData.SelectedFragment selectedFragment, Bundle bundle) {
        ApplicationData.getInstance().selectedFragment = selectedFragment;

        Fragment fragment = new WebkitFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragment.setArguments(bundle);

        if (getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT") != null) {
            fragmentManager.beginTransaction().remove(getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT")).commit();
        } else {
        }

        try {

            fragmentManager.beginTransaction().replace(R.id.mainContent, fragment, "CURRENT_FRAGMENT").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
     private void goToDtsPage(ApplicationData.SelectedFragment selectedFragment, Bundle bundle) {
        ApplicationData.getInstance().selectedFragment = selectedFragment;

        Fragment fragment = new DtsWebkitFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragment.setArguments(bundle);

        if (getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT") != null) {
            fragmentManager.beginTransaction().remove(getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT")).commit();
        } else {
        }

        try {

            fragmentManager.beginTransaction().replace(R.id.mainContent, fragment, "CURRENT_FRAGMENT").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    private void updateProgressBar() {
        initial_weight_tv.setText(Float.toString(ApplicationData.getInstance().dietProfilesDataContract.StartWeightInKg) + " kg");
        target_weight_tv.setText(Float.toString(ApplicationData.getInstance().dietProfilesDataContract.TargetWeightInKg) + " kg");

        float lost_weight = ApplicationData.getInstance().dietProfilesDataContract.StartWeightInKg - ApplicationData.getInstance().dietProfilesDataContract.CurrentWeightInKg;
        //(StartWeightInKg - CurrentWeightInKg) / (StartWeightInKg - TargetWeightInKg)) * 100
        float lost_percentage = (lost_weight / (ApplicationData.getInstance().dietProfilesDataContract.StartWeightInKg - ApplicationData.getInstance().dietProfilesDataContract.TargetWeightInKg)) * 100;
        String lost_weight_message = getResources().getString(R.string.lost_weight_text) + " " + String.format("%.2f", lost_weight) + " kg (" + String.format("%.0f", lost_percentage) + "%)";
        lost_weight_tv.setText(lost_weight_message);

        weightProgressBar.setProgress((int) lost_percentage);
    }

    private void goToAproposPage() {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Apropos;
        Intent mainIntent = new Intent(context, MainActivity.class);
        startActivity(mainIntent);
    }

    private void goToConditionsPage() {
        Intent mainContentBrowser = new Intent(context, BrowserActivity.class);
        mainContentBrowser.putExtra("HEADER_TITLE", getResources().getString(R.string.apropos_menu2));
        mainContentBrowser.putExtra("URL_PATH", WebkitURL.conditionsURL);
        startActivity(mainContentBrowser);
    }
    private void goToContactPage() {
        Intent mainContentBrowser = new Intent(context, BrowserActivity.class);
        mainContentBrowser.putExtra("HEADER_TITLE", getResources().getString(R.string.contact));

        mainContentBrowser.putExtra("URL_PATH", WebkitURL.contactURL);

        startActivity(mainContentBrowser);
    }
    private void updateGoogleOrder()
    {
        caller.PostGoogleOrderUpdate(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {

                if (output != null) {
                    System.out.println("submitOrderToAPI: " + output);
                    PaymentOrderResponseContract responseContract = (PaymentOrderResponseContract) output;


                }

            }
        }, paymentOrderGoogleContract, ApplicationData.getInstance().regId);
    }

    @Override
    public void receivedBroadcast() {
        Log.d("SMVideo", "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            //complain("Error querying inventory. Another async operation in progress.");
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        if (mBroadcastReceiver != null) {
            context.unregisterReceiver(mBroadcastReceiver);
        }

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }
}
