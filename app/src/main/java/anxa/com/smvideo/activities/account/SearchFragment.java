package anxa.com.smvideo.activities.account;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.compat.BuildConfig;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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



public class SearchFragment extends BaseFragment implements View.OnClickListener {

    ImageButton forwardBrowserButton, backBrowserButton, refreshBrowserButton;
    String URLPath = "";
    public String contentString = "";

    ProgressBar myProgressBar;

    View mView;
    private Context context;
    protected ApiCaller caller;
    private ImageView backButton;
    private EditText searchText;
    private VideoEnabledWebView mainContentWebView;
    private VideoEnabledWebChromeClient webChromeClient;
    private Button cancelButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.context = getActivity();
        mView = inflater.inflate(R.layout.layout, null);

        searchText =   ((EditText) mView.findViewById(R.id.searchText));
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchText, InputMethodManager.SHOW_IMPLICIT);
        cancelButton = ((Button) mView.findViewById(R.id.cancelSearchButton));

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(String.valueOf(v.getText()));
                    return true;
                }
                return false;
            }
        });
        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    cancelButton.setVisibility(View.GONE);
                }else{
                    cancelButton.setVisibility(View.VISIBLE);
                }
            }
        });
        return mView;

    }

    private void performSearch(String kw)
    {
        Fragment fragment = new DtsWebkitFragment();

        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Search;
        Bundle bundle = new Bundle();
        bundle.putString("webkit_url", WebkitURL.searchUrl.replace("%s", kw));
        bundle.putString("header_title", "");
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getFragmentManager();
        if (getFragmentManager().findFragmentByTag("SEARCH_FRAGMENT") != null) {
            fragmentManager.beginTransaction().remove(getFragmentManager().findFragmentByTag("SEARCH_FRAGMENT")).commit();
        } else {
        }

        try {

            fragmentManager.beginTransaction().replace(R.id.mainContentSearch, fragment, "SEARCH_FRAGMENT").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        hideKeyboard();
    }
    private void hideKeyboard() {
        // hide virtual keyboard
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        if (v == backButton) {
            removeFragment();
        }else if(v == cancelButton){
            searchText.getText().clear();
        }
    }
}

