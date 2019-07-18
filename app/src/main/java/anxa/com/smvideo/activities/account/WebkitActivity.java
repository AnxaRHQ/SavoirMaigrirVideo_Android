package anxa.com.smvideo.activities.account;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.view.View;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;

public class WebkitActivity extends Activity implements View.OnClickListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.webinar);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        WebkitFragment webkit = new WebkitFragment();
        Bundle bundle = new Bundle();

        String isHideRightNav = getIntent().getStringExtra("isHideRightNav");
        String header_title = getIntent().getStringExtra("header_title");
        String webkit_url = getIntent().getStringExtra("webkit_url");

        bundle.putString("isHideRightNav", isHideRightNav );
        bundle.putString("header_title", header_title);
        bundle.putString("webkit_url", webkit_url);
        webkit.setArguments(bundle);

        fragmentTransaction.replace(R.id.webinarXML,webkit,"CURRENT_FRAGMENT");
        fragmentTransaction.commit();

        /* Dismiss Fragment */

        ((ImageView) findViewById(R.id.header_menu_back)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        finish();
    }
}
