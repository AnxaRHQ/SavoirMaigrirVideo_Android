package anxa.com.smvideo.activities.registration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.BuildConfig;
import anxa.com.smvideo.R;
import anxa.com.smvideo.common.WebkitURL;
import anxa.com.smvideo.customview.VideoEnabledWebChromeClient;
import anxa.com.smvideo.customview.VideoEnabledWebView;
import anxa.com.smvideo.util.AppUtil;

import static anxa.com.smvideo.util.AppUtil.isEmail;

/**
 * Created by aprilanxa on 07/08/2017.
 */

public class RegistrationActivity extends Activity {

    ImageButton forwardBrowserButton, backBrowserButton, refreshBrowserButton;
    String URLPath = "";
    public String contentString = "";

    ProgressBar myProgressBar;

    private VideoEnabledWebView mainContentWebView;
    private VideoEnabledWebChromeClient webChromeClient;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        URLPath = WebkitURL.domainURL + WebkitURL.registrationURL;

        setContentView(R.layout.registration);

        // Save the web view
        mainContentWebView = (VideoEnabledWebView) findViewById(R.id.maincontentWebView);


        forwardBrowserButton = (ImageButton) findViewById(R.id.forward);
        forwardBrowserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mainContentWebView.goForward();
            }

        });
        backBrowserButton = (ImageButton) findViewById(R.id.back);
        backBrowserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mainContentWebView.goBack();
            }
        });
        refreshBrowserButton = (ImageButton) findViewById(R.id.refresh);
        refreshBrowserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mainContentWebView.reload();
            }
        });

        // Initialized progress bar
        myProgressBar = (ProgressBar) findViewById(R.id.progressbar);

        // Save the web view
        mainContentWebView = (VideoEnabledWebView) findViewById(R.id.maincontentWebView);

        mainContentWebView.getSettings().setJavaScriptEnabled(true);

//        mainContentWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        //prevent horizontal scrolling

        mainContentWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);


        // Initialize the VideoEnabledWebChromeClient and set event handlers
        View loadingView = myProgressBar; // Your own view, read class comments
        View nonVideoLayout = findViewById(R.id.registration_rl); // Your own view, read class comments
        ViewGroup videoLayout = (ViewGroup) findViewById(R.id.videoLayout); // Your own view, read class comments

        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, mainContentWebView) // See all available constructors...
        {
            // Subscribe to standard events, such as onProgressChanged()...
            @Override
            public void onProgressChanged(WebView view, int progress) {
            }
        };

        webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
            public void toggledFullscreen(boolean fullscreen) {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen) {

                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                } else {
                    WindowManager.LayoutParams attrs = getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14) {
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });

        //prevent horizontal scrolling
        mainContentWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mainContentWebView.getSettings().setJavaScriptEnabled(true);

        String defaultagent = mainContentWebView.getSettings().getUserAgentString();

        if (defaultagent == null)
            defaultagent = AppUtil.getDefaultUserAgent(this);

        mainContentWebView.getSettings().setUserAgentString(ApplicationData.getInstance().customAgent + " " + BuildConfig.VERSION_NAME + " " + defaultagent);

        CookieManager.getInstance().setCookie("http://savoir-maigrir.aujourdhui.com", "produit=sid=221");

        mainContentWebView.loadUrl(URLPath);
        mainContentWebView.setWebChromeClient(webChromeClient);

        mainContentWebView.setWebViewClient(new WebViewClient() {
            private boolean testPayment = false;

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
                if (myProgressBar != null)
                    myProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("FullBrowserActivity " + url);
                if (url.contains(WebkitURL.domainURL.replace("http://", ""))) {
                    return false;
                }

                view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //registration for JMC

                System.out.println("Registration onPageStarted url: " + url);
                System.out.println("Registration onPageStarted this: " + WebkitURL.domainURL + WebkitURL.offerURL);
                if (url.equalsIgnoreCase(WebkitURL.domainURL + WebkitURL.registrationDoneURL)) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("TO_LOGIN", true);
                    setResult(RESULT_OK, returnIntent);

                    finish();
                }else if(url.equalsIgnoreCase(WebkitURL.domainURL + WebkitURL.loginURL)){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("TO_LOGIN", true);
                    setResult(RESULT_OK, returnIntent);

                    finish();
                }
//                else if(url.equalsIgnoreCase(WebkitURL.domainURL + WebkitURL.offerURL)) {
//                    Intent returnIntent = new Intent();
//                    returnIntent.putExtra("TO_LANDING", true);
//                    setResult(RESULT_OK, returnIntent);
//
//                    finish();
//                }

            }
        });


        WebSettings webSettings = mainContentWebView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSaveFormData(true);
        webSettings.setSavePassword(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setGeolocationEnabled(true);
        webSettings.setLoadWithOverviewMode(true);

    }


}


