package anxa.com.smvideo.activities.account;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.ui.CoachingArchiveListAdapter;
import anxa.com.smvideo.ui.CustomListView;

/**
 * Created by aprilanxa on 17/07/2017.
 */

public class CoachingArchiveAccountActivity extends Activity implements View.OnClickListener {

    CustomListView weekListView;
    CoachingArchiveListAdapter adapter;
    private Button header_right;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.coaching_archive_account);

        ((TextView) findViewById(R.id.header_title_tv)).setText(getString(R.string.coaching_header_right));
        ((ImageView) findViewById(R.id.header_menu_back)).setVisibility(View.GONE);

        header_right = (Button) findViewById(R.id.header_menu_iv);
        header_right.setBackgroundResource(0);
        header_right.setText("Annuler");
        header_right.setTextColor(getResources().getColor(R.color.text_orange));
        header_right.setOnClickListener(this);

        weekListView = (CustomListView) findViewById(R.id.archiveListView);

        if (adapter == null) {
            adapter = new CoachingArchiveListAdapter(this, ApplicationData.getInstance().coachingVideoList, this);
        }

        weekListView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v)
    {
        if (v==header_right) {
            finish();
        } else {
            int weekId = (Integer) v.getTag(R.id.week_id);
            ApplicationData.getInstance().selectedWeekNumber = weekId;
            ApplicationData.getInstance().fromArchive = true;

            Intent broadint = new Intent();
            broadint.setAction(this.getResources().getString(R.string.coaching_broadcast_string));
            this.sendBroadcast(broadint);

            finish();
        }
    }
}
