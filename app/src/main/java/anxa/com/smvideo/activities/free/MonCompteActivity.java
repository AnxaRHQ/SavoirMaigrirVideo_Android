package anxa.com.smvideo.activities.free;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.registration.RegistrationActivity;
import anxa.com.smvideo.activities.registration.RegistrationFormActivity;

/**
 * Created by josephollero on 6/2/2017.
 */

public class MonCompteActivity extends Fragment implements View.OnClickListener {

    private Context context;
    private static final int BROWSERTAB_ACTIVITY = 1111;
    private TextView header_right;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.context = getActivity();

        View mView = inflater.inflate(R.layout.mon_compte, null);

        //header change
        ((TextView) ((RelativeLayout) mView.findViewById(R.id.headermenu)).findViewById(R.id.header_title_tv)).setText(getString(R.string.menu_mon_compte));

        ((Button) mView.findViewById(R.id.moncompte_button)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View V) {
//                Uri uriUrl = Uri.parse("https://savoir-maigrir.aujourdhui.com/orange/billing/subscribe/");
//                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
//                startActivity(launchBrowser);
                goToRegistrationPage();

            }
        });
        header_right = (TextView) (mView.findViewById(R.id.header_right_tv));
        header_right.setOnClickListener(this);
        return mView;
    }
    @Override
    public void onClick(final View v) {

        if (v == header_right) {
            goToRegistrationPage();
        }
    }
    private void goToRegistrationPage() {
        Intent mainIntent = new Intent(context, RegistrationFormActivity.class);
        startActivity(mainIntent);
    }
}
