package anxa.com.smvideo.activities.free;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.registration.RegistrationMainObjectiveActivity;

/**
 * Created by josephollero on 6/2/2017.
 */

public class MonCompteActivity extends Fragment implements View.OnClickListener
{
    private Context context;
    private ImageView backButton;
    private Button header_right;

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

        backButton = (ImageView) mView.findViewById(R.id.header_menu_back);
        backButton.setVisibility(View.VISIBLE);
        backButton.setImageDrawable(null);
        backButton.setBackground(context.getResources().getDrawable(R.drawable.ic_menu_white_24dp));

        header_right = (Button) (mView.findViewById(R.id.header_menu_iv));
        header_right.setBackgroundResource(0);
        header_right.setText(R.string.login_registration_button);
        header_right.setTextColor(getResources().getColor(R.color.text_orange));
        header_right.setOnClickListener(this);

        return mView;
    }
    @Override
    public void onClick(final View v)
    {
        if (v == header_right)
        {
            goToRegistrationPage();
        }
    }

    private void goToRegistrationPage()
    {
        Intent mainIntent = new Intent(context, RegistrationMainObjectiveActivity.class);
        startActivity(mainIntent);
    }
}
