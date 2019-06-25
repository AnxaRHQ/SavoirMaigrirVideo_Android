package anxa.com.smvideo.activities.account;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.compat.BuildConfig;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubePlayerFragment;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.common.WebkitURL;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.customview.VideoEnabledWebChromeClient;
import anxa.com.smvideo.customview.VideoEnabledWebView;
import anxa.com.smvideo.util.AppUtil;

/**
 * Created by aprilanxa on 18/08/2017.
 */

public class FichesFragment extends Fragment {

    ImageButton forwardBrowserButton, backBrowserButton, refreshBrowserButton;
    String URLPath = "";
    public String contentString = "";

    ProgressBar myProgressBar;

    View mView;
    private Context context;
    protected ApiCaller caller;

    private VideoEnabledWebView mainContentWebView;
    private VideoEnabledWebChromeClient webChromeClient;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.context = getActivity();
        mView = inflater.inflate(R.layout.webinar, null);

        ((TextView) mView.findViewById(R.id.header_title_tv)).setText(getString(R.string.menu_account_fiches));
        ((TextView) mView.findViewById(R.id.header_right_tv)).setVisibility(View.GONE);
//        ((ImageView) mView.findViewById(R.id.header_menu_iv)).setVisibility(View.VISIBLE);


        String fichesWebkitUrl = WebkitURL.fichesWebkitUrl.replace("%regId", Integer.toString(ApplicationData.getInstance().userDataContract.Id));
        try {
            fichesWebkitUrl = fichesWebkitUrl.replace("%sig", AppUtil.SHA1("get" + WebkitURL.fichesWebkitUrl.split(Pattern.quote("?"))[0] + Integer.toString(ApplicationData.getInstance().userDataContract.Id) + "Au!Ui 7RCw h9p1m36"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        URLPath = WebkitURL.domainURL + fichesWebkitUrl;

        // Save the web view
        mainContentWebView = (VideoEnabledWebView)mView.findViewById(R.id.maincontentWebView);

        forwardBrowserButton = (ImageButton) mView.findViewById(R.id.forward);
        forwardBrowserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mainContentWebView.goForward();
            }

        });
        backBrowserButton = (ImageButton) mView.findViewById(R.id.back);
        backBrowserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mainContentWebView.goBack();
            }
        });
        refreshBrowserButton = (ImageButton) mView.findViewById(R.id.refresh);
        refreshBrowserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mainContentWebView.reload();
            }
        });

        // Initialized progress bar
        myProgressBar = (ProgressBar) mView.findViewById(R.id.progressbar);

        // Save the web view
        mainContentWebView = (VideoEnabledWebView) mView.findViewById(R.id.maincontentWebView);

        mainContentWebView.getSettings().setJavaScriptEnabled(true);

//        mainContentWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        //prevent horizontal scrolling

        mainContentWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);


        // Initialize the VideoEnabledWebChromeClient and set event handlers
        View loadingView = myProgressBar; // Your own view, read class comments
        View nonVideoLayout = mView.findViewById(R.id.registration_rl); // Your own view, read class comments
        ViewGroup videoLayout = (ViewGroup) mView.findViewById(R.id.videoLayout); // Your own view, read class comments

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

                    WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getActivity().getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14) {
                        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                } else {
                    WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getActivity().getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14) {
                        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });

        //prevent horizontal scrolling
        mainContentWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mainContentWebView.getSettings().setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= 21) {
            mainContentWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        String defaultagent = AppUtil.getDefaultUserAgent(context);

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
//                if (url.equalsIgnoreCase(WebkitURL.domainURL + WebkitURL.registrationDoneURL)) {
//                    Intent returnIntent = new Intent();
//                    returnIntent.putExtra("TO_LOGIN", true);
//                    setResult(RESULT_OK, returnIntent);
//
//                    finish();
//                } else if (url.equalsIgnoreCase(WebkitURL.domainURL + WebkitURL.loginURL)) {
//                    Intent returnIntent = new Intent();
//                    returnIntent.putExtra("TO_LOGIN", true);
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

        return mView;

    }

}
