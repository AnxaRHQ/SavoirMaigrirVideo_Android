package anxa.com.smvideo.activities.account;


import android.Manifest;
import android.app.DownloadManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayerFragment;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.BuildConfig;
import anxa.com.smvideo.R;
import anxa.com.smvideo.common.WebkitURL;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.customview.VideoEnabledWebChromeClient;
import anxa.com.smvideo.customview.VideoEnabledWebView;
import anxa.com.smvideo.util.AppUtil;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.view.View.GONE;

public class WebkitFragment extends BaseFragment implements View.OnClickListener {

    ImageButton forwardBrowserButton, backBrowserButton, refreshBrowserButton;
    String URLPath = "";
    public String contentString = "";

    ProgressBar myProgressBar;
    private String autologinURL;

    private Context context;
    protected ApiCaller caller;
    private ImageView backButton;

    private VideoEnabledWebView mainContentWebView;
    private VideoEnabledWebChromeClient webChromeClient;
    private static final String TAG = WebkitFragment.class.getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.context = getActivity();
        mView = inflater.inflate(R.layout.webinar, null);

        if(ApplicationData.getInstance().selectedFragment == ApplicationData.SelectedFragment.Account_Consultation) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
        String headerTitle = getArguments().getString("header_title");
        String webkitBaseUrl =  getArguments().getString("webkit_url");
        boolean hideHeader = getArguments().getBoolean("hide_header", false);

        ((TextView) mView.findViewById(R.id.header_title_tv)).setText(headerTitle);
        if(hideHeader){
            mView.findViewById(R.id.header_menu_webinar).setVisibility(GONE);
        }
        Log.w("headerTitle",headerTitle);

        if (getArguments().get("isHideHeader") != null)
        {
            ((View) mView.findViewById(R.id.header_menu_webinar)).setVisibility(GONE);
        }

        if (getArguments().get("isHideRightNav") != null)
        {
            ((Button) mView.findViewById(R.id.header_menu_iv)).setVisibility(GONE);
        }
        else {
            backButton = (ImageView) ((RelativeLayout) mView.findViewById(R.id.header_menu_webinar)).findViewById(R.id.header_menu_back);
            backButton.setVisibility(View.VISIBLE);
            backButton.setOnClickListener(this);
        }

        if (ApplicationData.getInstance().selectedFragment == ApplicationData.SelectedFragment.Account_Consultation)
        {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }

//        ((ImageView) mView.findViewById(R.id.header_menu_iv)).setVisibility(View.VISIBLE);


        String webkitUrl = webkitBaseUrl.replace("%regId", Integer.toString(ApplicationData.getInstance().userDataContract.Id));
        try {
            webkitUrl = webkitUrl.replace("%sig", AppUtil.SHA1("get" + webkitBaseUrl.split(Pattern.quote("?"))[0] + Integer.toString(ApplicationData.getInstance().userDataContract.Id) + "Au!Ui 7RCw h9p1m36"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        URLPath = WebkitURL.domainURL + webkitUrl;

        // Save the web view
        mainContentWebView = (VideoEnabledWebView)mView.findViewById(R.id.maincontentWebView);

        forwardBrowserButton = (ImageButton) mView.findViewById(R.id.forward);

        forwardBrowserButton.setVisibility(mainContentWebView.canGoForward() ? View.VISIBLE : View.INVISIBLE);

        forwardBrowserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mainContentWebView.goForward();
            }

        });
        backBrowserButton = (ImageButton) mView.findViewById(R.id.back);

        backBrowserButton.setVisibility(mainContentWebView.canGoBack() ? View.VISIBLE : View.INVISIBLE);

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
        mainContentWebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                if(isStoragePermissionGranted()) {
                    DownloadManager.Request request = new DownloadManager.Request(
                            Uri.parse(url));

                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                    String str = url;
                    str = str.substring(str.lastIndexOf("filename=") + 1);
                    str = str.replace("filename=", "");
                    str = str.substring(0, str.indexOf(".pdf"));
                    str = str + ".pdf";
                    if (str != null) {
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, str);
                        DownloadManager dm = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                        dm.enqueue(request);
                        Toast.makeText(context, R.string.DOWNLOADING_FILE, //To notify the Client that the file is being downloaded
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        CookieManager.getInstance().setCookie("http://savoir-maigrir.aujourdhui.com", "produit=sid=221");
        autologinURL = WebkitURL.autoLoginURL.replace("%u", Integer.toString(ApplicationData.getInstance().userDataContract.Id));
        try {
            autologinURL = autologinURL.replace("%p", AppUtil.SHA1(Integer.toString(ApplicationData.getInstance().userDataContract.Id) + "Dxx-|%dsDaI"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mainContentWebView.loadUrl( WebkitURL.domainURL + autologinURL);

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
                System.out.println("FullBrowserActivity " + url);

                if(url.equalsIgnoreCase(WebkitURL.domainURL + "/5minparjour"))
                {
                    return true;
                }
                if (url.contains(WebkitURL.domainURL.replace("http://", ""))) {
                    return false;
                }

                //view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                if (myProgressBar != null) {
                    myProgressBar.setVisibility(View.VISIBLE);
                }

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

        super.onCreateView(inflater, container, savedInstanceState);
        return mView;

    }

    @Override
    public void onClick(View v)
    {
        if (v == backButton)
        {
            removeFragment();
        }
    }

    private  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }
}

