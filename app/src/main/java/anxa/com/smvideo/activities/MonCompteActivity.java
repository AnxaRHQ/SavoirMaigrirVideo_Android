package anxa.com.smvideo.activities;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import anxa.com.smvideo.R;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.VideoResponseContract;

/**
 * Created by josephollero on 6/2/2017.
 */

public class MonCompteActivity extends Fragment {

    private Context context;
    protected ApiCaller caller;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.context = getActivity();

        View mView = inflater.inflate(R.layout.mon_compte, null);
        caller = new ApiCaller();


        //header change
        ((TextView) ((RelativeLayout) mView.findViewById(R.id.headermenu)).findViewById(R.id.header_title_tv)).setText(getString(R.string.menu_mon_compte));



        return mView;
    }

    public void button_onclick(View view){
        Uri uriUrl = Uri.parse("http://www.yahoo.com");
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);

    }


}
