package anxa.com.smvideo.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.account.LandingPageAccountActivity;
import anxa.com.smvideo.activities.registration.RegistrationAccountCreateActivity;
import anxa.com.smvideo.activities.registration.RegistrationSelectPaymentActivity;
import anxa.com.smvideo.common.SavoirMaigrirVideoConstants;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.BaseContract;
import anxa.com.smvideo.contracts.LoginContract;
import anxa.com.smvideo.contracts.PaymentConfirmationContract;
import anxa.com.smvideo.contracts.PaymentOrderGoogleContract;
import anxa.com.smvideo.contracts.PaymentOrderResponseContract;
import anxa.com.smvideo.contracts.TVPaymentOrderContract;
import anxa.com.smvideo.contracts.TVRegistrationContinueContract;
import anxa.com.smvideo.contracts.UserDataResponseContract;
import anxa.com.smvideo.util.IabBroadcastReceiver;
import anxa.com.smvideo.util.IabHelper;
import anxa.com.smvideo.util.IabResult;
import anxa.com.smvideo.util.Inventory;
import anxa.com.smvideo.util.Purchase;

/**
 * Created by angelaanxa on 11/10/2017.
 */

public class NpnaOfferActivity extends BaseVideoActivity  implements IabBroadcastReceiver.IabBroadcastListener{
    IabHelper mHelper;
    IabBroadcastReceiver mBroadcastReceiver;
    static final String TAG = "SMVideo";
    ApiCaller caller;



    // Does the user have the premium upgrade?
    boolean mIsPremium = false;

    // Will the subscription auto-renew?
    boolean mAutoRenewEnabled = false;
    String mSubscribedSku = "";
    String mFirstChoiceSku = "";
    String mSecondChoiceSku = "";

    // Does the user have an active subscription to any plans?
    boolean mSubscribedTo = false;
    String mSelectedSubscriptionPeriod = "";
    private PaymentOrderGoogleContract paymentOrderGoogleContract;
    private TVPaymentOrderContract tvPaymentOrderContract;
    static final int RC_REQUEST = 10001;
    private LoginContract loginContract;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.npnaoffer);

        //header change
        ((TextView) (this.findViewById(R.id.header_title_tv))).setText(R.string.inscription);
        ((TextView) (this.findViewById(R.id.header_right_tv))).setVisibility(View.INVISIBLE);
        ((ImageView) findViewById(R.id.header_menu_iv)).setVisibility(View.GONE);
        ((ImageView) findViewById(R.id.header_menu_back)).setVisibility(View.GONE);

        caller = new ApiCaller();
        //((TextView) (this.findViewById(R.id.registrationform2_headerTitle))).setText(R.string.inscription_headerTitle);
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlQ6C20yTohzxhmSNwK01P9ipib2U60vnqYaozFYTJnMsl3zUBHivPo8F5+nIPJNwqYLTuBcGF93hObY5KGp+aPCWvH2s3XHwQoHwgVhreHevlv60qj3i/74yzFude4WhKstESBO3zyQC2SKa8dHqz7gdakfPukI44ZAzykr4eqfJG4UCppcVyxBLE3piKyC+6Y63w34Ljy96NvxLoCUKRNWwp8FoutOOpidisLxtBfxDT3MKzcMAUYbD954kZ+COkvqHXg9eb9eiuclPE9eirhReigSUySQEUsJrA37z/XJflGaA3btf8FxLwOLU3VHVMKif06Yfoj93focRy2KJNwIDAQAB";

        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
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
                mBroadcastReceiver = new IabBroadcastReceiver(NpnaOfferActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);
//
//                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });
    }
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
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

                alert("User is currently subscribed to: " + getString(R.string.incription_3mois) + "\nAutorenewable: " + mAutoRenewEnabled);
            } else if (jmc_6months != null && jmc_6months.isAutoRenewing()) {
                mSubscribedSku = SavoirMaigrirVideoConstants.SKU_JMC_6MONTHS;
                mAutoRenewEnabled = true;

                paymentOrderGoogleContract = new PaymentOrderGoogleContract();
                paymentOrderGoogleContract.setOrderId(jmc_6months.getOrderId());
                paymentOrderGoogleContract.setProductId(jmc_6months.getSku());
                System.out.println("Purchase successful time: " + jmc_6months.getPurchaseTime());
                paymentOrderGoogleContract.setPurchaseDateMs((jmc_6months.getPurchaseTime() ));
                paymentOrderGoogleContract.setPurchaseToken(jmc_6months.getToken());

                alert("User is currently subscribed to: " + getString(R.string.incription_6mois) + "\nAutorenewable: " + mAutoRenewEnabled);
            } else {
                mSubscribedSku = "";
                mAutoRenewEnabled = false;
//                alert("User does not have any subscription");
            }

            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
            mSubscribedTo =  (jmc_3months != null && verifyDeveloperPayload(jmc_3months))
                    || (jmc_6months != null && verifyDeveloperPayload(jmc_6months));
            Log.d(TAG, "User " + (mSubscribedTo ? "HAS" : "DOES NOT HAVE")
                    + "  subscription.");
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };
    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d("SMVideo", "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }
    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }
    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

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
    public void subscribeTo3Months(View view) {
        mSelectedSubscriptionPeriod = SavoirMaigrirVideoConstants.SKU_JMC_3MONTHS;
        purchaseItem();
    }

    public void subscribeTo6Months(View view) {
        mSelectedSubscriptionPeriod = SavoirMaigrirVideoConstants.SKU_JMC_6MONTHS;
        purchaseItem();
    }

    private void purchaseItem() {

        Log.d(TAG, "Launching purchase flow.");

    /* TODO: for security, generate your payload here for verification. See the comments on
     *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
     *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        List<String> oldSkus = null;


        //setWaitScreen(true);
        Log.d(TAG, "Launching purchase flow for jmc subscription.");
        try {
            mHelper.launchPurchaseFlow(this, mSelectedSubscriptionPeriod, IabHelper.ITEM_TYPE_SUBS,
                    oldSkus, RC_REQUEST, mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error launching purchase flow. Another async operation in progress.");
        }
        // Reset the dialog options
        mSelectedSubscriptionPeriod = "";
        mFirstChoiceSku = "";
        mSecondChoiceSku = "";
    }
    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                paymentOrderGoogleContract.setStatus(result.getResponse());

                Log.d(TAG, "Error purchasing2 : " + result.getMessage());


                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }

            Log.d(TAG, "Purchase successful.");


            tvPaymentOrderContract = new TVPaymentOrderContract();
            tvPaymentOrderContract.email = ApplicationData.getInstance().userDataContract.Email;
            tvPaymentOrderContract.firstname = ApplicationData.getInstance().userDataContract.FirstName;
            tvPaymentOrderContract.surname = ApplicationData.getInstance().userDataContract.LastName;
            tvPaymentOrderContract.sid = 221;
            tvPaymentOrderContract.siteId = 3;

            paymentOrderGoogleContract = new PaymentOrderGoogleContract();
            paymentOrderGoogleContract.setOrderId(purchase.getOrderId());
            paymentOrderGoogleContract.setProductId(purchase.getSku());
            System.out.println("Purchase successful time: " + (purchase.getPurchaseTime()));
            paymentOrderGoogleContract.setPurchaseDateMs(purchase.getPurchaseTime());
            paymentOrderGoogleContract.setPurchaseToken(purchase.getToken());
            paymentOrderGoogleContract.setStatus(0);

            if (purchase.getSku().equals(SavoirMaigrirVideoConstants.SKU_JMC_3MONTHS)) {
                paymentOrderGoogleContract.setDescription(getString(R.string.incription_3mois));
                tvPaymentOrderContract.ProductId = SavoirMaigrirVideoConstants.SKU_JMC_3MONTHS;
            } else if (purchase.getSku().equals(SavoirMaigrirVideoConstants.SKU_JMC_6MONTHS)) {
                paymentOrderGoogleContract.setDescription(getString(R.string.incription_6mois));
                tvPaymentOrderContract.ProductId = SavoirMaigrirVideoConstants.SKU_JMC_6MONTHS;
            }

            if (purchase.getSku().equals(SavoirMaigrirVideoConstants.SKU_JMC_3MONTHS)
                    || purchase.getSku().equals(SavoirMaigrirVideoConstants.SKU_JMC_6MONTHS)) {
                //alert("Thank you for subscribing to JMC!");
                mSubscribedTo = true;
                mAutoRenewEnabled = purchase.isAutoRenewing();
                mSubscribedSku = purchase.getSku();
            }

            if (paymentOrderGoogleContract != null) {
                submitOrderToAPI();
            }
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }

    private void submitOrderToAPI() {
        System.out.println("submitOrderToAPI regId: " + ApplicationData.getInstance().regId);

        caller.PostGoogleOrder(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                if (output != null) {
                    System.out.println("submitOrderToAPI: " + output);
                    PaymentOrderResponseContract responseContract = (PaymentOrderResponseContract) output;

                    if (responseContract.data != null) {

                    }
                    if (responseContract.Message.equalsIgnoreCase("Successful")) {
                        confirmGoogleOrder(responseContract.data.PaymentId);
                    }
                }
            }
        }, tvPaymentOrderContract);
    }
    private void confirmGoogleOrder(final int paymentId)
    {
        PaymentConfirmationContract contract = new PaymentConfirmationContract();
        contract.PaymentReference = paymentOrderGoogleContract.getOrderId();
        contract.PaymentId = paymentId;
        caller.PostConfirmGoogleOrder(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                if (output != null) {
                    System.out.println("submitOrderToAPI: " + output);
                    BaseContract responseContract = (BaseContract) output;

                    if (responseContract != null) {

                    }
                    if (responseContract.Message.equalsIgnoreCase("Successful")) {
                       // ApplicationData.getInstance().regUserProfile.setPaymentId(paymentId);
                        registerContinue(paymentId);
                    }
                }
            }
        }, contract);

    }
    private void registerContinue(int paymentId)
    {
        TVRegistrationContinueContract contract = new TVRegistrationContinueContract();
        contract.regId =  ApplicationData.getInstance().regId;
        contract.paiementCbId = paymentId;
        caller.PostRegisterContinue(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                if (output != null) {
                    System.out.println("registerContinue: " + output);
                    BaseContract responseContract = (BaseContract) output;

                    if (responseContract != null) {

                    }
                    if (responseContract.Message.equalsIgnoreCase("Successful")) {
                        autoLogin();
                    }
                }
            }
        }, contract);
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }
}
