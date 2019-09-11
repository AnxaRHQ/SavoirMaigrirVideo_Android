package anxa.com.smvideo.activities.account;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.BuildConfig;
import anxa.com.smvideo.R;
import anxa.com.smvideo.common.WebkitURL;
import anxa.com.smvideo.customview.VideoEnabledWebChromeClient;
import anxa.com.smvideo.customview.VideoEnabledWebView;
import anxa.com.smvideo.util.AppUtil;

/**
 * Created by aprilanxa on 21/09/2017.
 */

public class BrowserActivity extends Activity implements View.OnClickListener{

    ImageButton forwardBrowserButton, backBrowserButton, refreshBrowserButton;
    String URLPath = "";
    public String contentString = "";

    ProgressBar myProgressBar;
    private ImageView backButton;

    private VideoEnabledWebView mainContentWebView;
    private VideoEnabledWebChromeClient webChromeClient;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        URLPath = WebkitURL.domainURL;

        String title = getIntent().getStringExtra("HEADER_TITLE");
        contentString = getIntent().getStringExtra("URL_PATH");

        System.out.println("BrowserActivity contentString: " + contentString);
        if (ApplicationData.getInstance().accountType.equalsIgnoreCase("free")) {
            URLPath = WebkitURL.domainURL + contentString;
        }else {
            String autologinURL = contentString.replace("%d", Integer.toString(ApplicationData.getInstance().userDataContract.Id));
            try {
                autologinURL = autologinURL.replace("%password", AppUtil.SHA1(Integer.toString(ApplicationData.getInstance().userDataContract.Id) + "Dxx-|%dsDaI"));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            URLPath = WebkitURL.domainURL + autologinURL;
        }

        setContentView(R.layout.browser_layout);

        ((TextView) findViewById(R.id.header_title_tv)).setText(title);
        ((Button) findViewById(R.id.header_menu_iv)).setVisibility(View.GONE);

        backButton = (ImageView) ((RelativeLayout) findViewById(R.id.headermenu)).findViewById(R.id.header_menu_back);
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(this);

        // Save the web view
        mainContentWebView = (VideoEnabledWebView) findViewById(R.id.maincontentWebView);

        forwardBrowserButton = (ImageButton) findViewById(R.id.forward);

        forwardBrowserButton.setVisibility(mainContentWebView.canGoForward() ? View.VISIBLE : View.INVISIBLE);

        forwardBrowserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mainContentWebView.goForward();
            }

        });
        backBrowserButton = (ImageButton) findViewById(R.id.back);

        backBrowserButton.setVisibility(mainContentWebView.canGoBack() ? View.VISIBLE : View.INVISIBLE);

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
        if (Build.VERSION.SDK_INT >= 21) {
            mainContentWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        String defaultagent = AppUtil.getDefaultUserAgent(this);

        mainContentWebView.getSettings().setUserAgentString(mainContentWebView.getSettings().getUserAgentString() + " " + BuildConfig.VERSION_NAME + " " + defaultagent);

        CookieManager.getInstance().setCookie("http://savoir-maigrir.aujourdhui.com", "produit=sid=221");

        mainContentWebView.loadUrl(URLPath);
        mainContentWebView.setWebChromeClient(webChromeClient);

        mainContentWebView.setWebViewClient(new WebViewClient() {
            private boolean testPayment = false;

            @Override
            public void onPageFinished(WebView view, String url)
            {
                // TODO Auto-generated method stub

                backBrowserButton.setVisibility(mainContentWebView.canGoBack() ? View.VISIBLE : View.INVISIBLE);
                forwardBrowserButton.setVisibility(mainContentWebView.canGoForward() ? View.VISIBLE : View.INVISIBLE);

                if (myProgressBar != null) {
                    myProgressBar.setVisibility(View.INVISIBLE);
                }

                if(url.equalsIgnoreCase(WebkitURL.domainURL + "/5minparjour")) {
                    mainContentWebView.loadUrl(URLPath);
                }

                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("BrowserActivity " + url);
                if(url.equalsIgnoreCase(WebkitURL.domainURL + "/5minparjour"))
                {
                    return true;
                }

                if (url.startsWith("tel:") || url.startsWith("mailto:")) {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                    return true;
                }

                if (url.contains(WebkitURL.domainURL.replace("http://", ""))) {
                    return false;
                }


                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                if (myProgressBar != null) {
                    myProgressBar.setVisibility(View.VISIBLE);
                }

                System.out.println("BrowserActivity onPageStarted url: " + url);
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

    @Override
    public void onClick(View v) {
        if (v == backButton) {
            finish();
        }
    }
}


