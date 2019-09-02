package anxa.com.smvideo.activities.account;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.common.SavoirMaigrirVideoConstants;
import anxa.com.smvideo.common.WebkitURL;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.CoachingVideosContract;
import anxa.com.smvideo.contracts.CoachingVideosResponseContract;
import anxa.com.smvideo.contracts.VideoContract;
import anxa.com.smvideo.ui.CoachingVideoListAdapter;
import anxa.com.smvideo.ui.CustomListView;
import anxa.com.smvideo.util.AppUtil;
import anxa.com.smvideo.util.VideoHelper;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by aprilanxa on 14/06/2017.
 */

public class CoachingAccountFragment extends BaseFragment implements View.OnClickListener
{
    private Context context;
    protected ApiCaller caller;

    private CoachingVideoListAdapter adapter;
    private List<CoachingVideosContract> videosList_all;
    private List<CoachingVideosContract> videosList;
    private CustomListView coachingListView;
    private Button header_right;
    private ProgressBar progressBar;
    private TextView viewMore;

    private YouTubePlayerFragment playerFragment;

    private int currentCoachingWeekNumber;
    private int currentCoachingDayNumber;
    private int selectedCoachingWeekNumber;
    private ImageView backButton;
    private boolean fromArchive;
    private boolean sessionView;
    String headerTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        this.context = getActivity();
        mView = inflater.inflate(R.layout.coaching_account, null);

        caller = new ApiCaller();
        sessionView = false;
        IntentFilter filter = new IntentFilter();
        filter.addAction(this.getResources().getString(R.string.coaching_broadcast_string));
        context.registerReceiver(the_receiver, filter);

        if (CheckFreeUser(false))
        {
            currentCoachingWeekNumber   = 1;
            currentCoachingDayNumber    = 1;
        }
        else
        {
            currentCoachingWeekNumber   = ApplicationData.getInstance().userDataContract.WeekNumber;
            currentCoachingDayNumber    = ApplicationData.getInstance().userDataContract.DayNumber;
        }

        ApplicationData.getInstance().currentWeekNumber = currentCoachingWeekNumber;

        //header change
        headerTitle = getString(R.string.coaching_header);
        headerTitle.replace("%d", Integer.toString(currentCoachingWeekNumber));
        ((TextView) (mView.findViewById(R.id.header_title_tv))).setText(headerTitle.replace("%d", Integer.toString(currentCoachingWeekNumber)));
        header_right = (Button) (mView.findViewById(R.id.header_menu_iv));
        header_right.setBackgroundResource(0);
        header_right.setText(getString(R.string.coaching_header_right));
        header_right.setTextColor(getResources().getColor(R.color.text_orange));
        header_right.setOnClickListener(this);

        backButton = (ImageView) (mView.findViewById(R.id.header_menu_back));
        backButton.setOnClickListener(this);

        progressBar = mView.findViewById(R.id.coachingProgressbar);

        progressBar.setVisibility(VISIBLE);

        coachingListView = (CustomListView) mView.findViewById(R.id.coachingListView);

        ((LinearLayout)mView.findViewById(R.id.youtube_layout_caption)).setVisibility(VISIBLE);

        videosList = new ArrayList<CoachingVideosContract>();
        videosList_all = new ArrayList<CoachingVideosContract>();

        if (adapter == null)
        {
            adapter = new CoachingVideoListAdapter(context, videosList, this);
        }

        coachingListView.setAdapter(adapter);

        //Initializing and adding YouTubePlayerFragment
        FragmentManager fm = getFragmentManager();
        String tag = YouTubePlayerFragment.class.getSimpleName();
        playerFragment = (YouTubePlayerFragment) fm.findFragmentByTag(tag);

        FragmentTransaction ft = fm.beginTransaction();
        playerFragment = YouTubePlayerFragment.newInstance();
        ft.replace(R.id.youtube_layout, playerFragment, tag);
        ft.commit();

        selectedCoachingWeekNumber = currentCoachingWeekNumber;

        if (ApplicationData.getInstance().fromArchive) {
            updateVideosList();
            progressBar.setVisibility(GONE);
        } else {
            getCoachingVideosFromAPI();
        }

        super.onCreateView(inflater, container, savedInstanceState);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(videosList != null && videosList.size() > 0) {
            FragmentManager fm = getFragmentManager();
            String tag = YouTubePlayerFragment.class.getSimpleName();
            playerFragment = (YouTubePlayerFragment) fm.findFragmentByTag(tag);

            if (playerFragment != null) {
                FragmentTransaction ft = fm.beginTransaction();
                playerFragment = YouTubePlayerFragment.newInstance();
                ft.replace(R.id.youtube_layout, playerFragment, tag);
                ft.commit();
            }
            RefreshPlayer(mView, videosList.get(0));
        }
    }

    @Override
    public void onClick(final View v)
    {
        if (v == header_right)
        {
            proceedToArchivePage();
        }
        else if(v == backButton)
        {

                super.removeFragment();

        }
        else
        {
            FragmentManager fm = getFragmentManager();
            String tag = YouTubePlayerFragment.class.getSimpleName();
            playerFragment = (YouTubePlayerFragment) fm.findFragmentByTag(tag);

            if (playerFragment != null)
            {
                FragmentTransaction ft = fm.beginTransaction();
                playerFragment = YouTubePlayerFragment.newInstance();
                ft.replace(R.id.youtube_layout, playerFragment, tag);
                ft.commit();
            }

            final String videoId = (String) v.getTag(R.id.video_id);

            for (int i = 0; i < videosList.size(); i++)
            {
                VideoContract temp = new VideoContract();
                if (videosList.get(i).VideoUrl == videoId) {
                    RefreshPlayer(v, videosList.get(i));
                    videosList.get(i).IsSelected = true;
                } else {
                    videosList.get(i).IsSelected = false;
                }
            }
            adapter.updateItems(videosList);
        }
    }

    private void RefreshPlayer(final View v, final CoachingVideosContract video)
    {
        playerFragment.initialize(SavoirMaigrirVideoConstants.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {
                if (video.VideoUrl != null) {

                    youTubePlayer.cueVideo(video.VideoUrl);

                    ((TextView) (mView.findViewById(R.id.videoTitle))).setText(video.Title);
                    ((TextView) (mView.findViewById(R.id.videoDesc))).setText(video.Description);
                    ((TextView) (mView.findViewById(R.id.videoDuration))).setText(video.Duration);
                    ((TextView) (mView.findViewById(R.id.videoViewMore))).setVisibility(VISIBLE);
                    ((TextView) (mView.findViewById(R.id.videoViewMore))).setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {

                            Intent mainIntent = new Intent(context, WebkitActivity.class);
                            mainIntent.putExtra("isHideRightNav", "true");
                            mainIntent.putExtra("header_title", getString(R.string.nav_account_coaching));
                            String webkitUrl = WebkitURL.sessionWebkitUrl.replace("%regId", Integer.toString(ApplicationData.getInstance().userDataContract.Id)).replace("%w", Integer.toString(video.WeekNumber)).replace("%day", Integer.toString(video.DayNumber));
                            try {
                                webkitUrl = webkitUrl.replace("%sig", AppUtil.SHA1("get" + webkitUrl.split(Pattern.quote("?"))[0] + Integer.toString(ApplicationData.getInstance().userDataContract.Id) + "Au!Ui 7RCw h9p1m36"));
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            mainIntent.putExtra("webkit_url", webkitUrl);

                            startActivity(mainIntent);
                        }
                    });

                    youTubePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
                        @Override
                        public void onBuffering(boolean arg0) {
                        }

                        @Override
                        public void onPaused() {
                        }

                        @Override
                        public void onPlaying() {
                            //youTubePlayer.setFullscreen(true);
                            youTubePlayer.play();
                        }

                        @Override
                        public void onSeekTo(int arg0) {
                        }

                        @Override
                        public void onStopped() {
                            //youTubePlayer.setFullscreen(false);
                        }
                    });

                    youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                        @Override
                        public void onFullscreen(boolean b) {
                            if (!b) {
                                getActivity().setRequestedOrientation(
                                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                if (youTubePlayer.isPlaying()) {
                                    youTubePlayer.play();
                                } else {
                                }
                            } else {
                                if (youTubePlayer.isPlaying()) {
                                    youTubePlayer.play();
                                } else {
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(context, "Error while initializing YouTubePlayer.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void proceedToArchivePage()
    {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_CoachingNative;

        Intent mainIntent = new Intent(this.context, CoachingArchiveAccountActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 12345 && resultCode == Activity.RESULT_OK)
        {
            selectedCoachingWeekNumber = ApplicationData.getInstance().selectedWeekNumber;
            updateVideosList();
        }
    }

    private void updateVideosList()
    {
/*        FragmentManager fm = getFragmentManager();
        String tag = YouTubePlayerFragment.class.getSimpleName();
        playerFragment = (YouTubePlayerFragment) fm.findFragmentByTag(tag);

        if (playerFragment != null)
        {
            FragmentTransaction ft = fm.beginTransaction();
            playerFragment = YouTubePlayerFragment.newInstance();
            ft.replace(R.id.youtube_layout, playerFragment, tag);
            ft.commit();
        }*/

        videosList = new ArrayList<>();

        for (CoachingVideosContract v : ApplicationData.getInstance().coachingVideoList)
        {
            if (v.WeekNumber == selectedCoachingWeekNumber)
            {
                if (v.WeekNumber == currentCoachingWeekNumber)
                {
                    if (CheckFreeUser(false))
                    {
                        videosList.add(v);
                    }
                    else
                    {
                        if (v.DayNumber <= currentCoachingDayNumber)
                        {
                            videosList.add(v);
                        }
                    }
                }
                else
                {
                    videosList.add(v);
                }
            }
        }

        if (videosList.size() > 0)
        {


            VideoHelper.sortCoachingVideos("index", videosList);
            videosList.get(0).IsSelected = true;
            adapter.updateItems(videosList);
            RefreshPlayer(mView, videosList.get(0));
        }

        ((TextView) (mView.findViewById(R.id.header_title_tv))).setText(headerTitle.replace("%d", Integer.toString(selectedCoachingWeekNumber)));
    }

    private void getCoachingVideosFromAPI()
    {
        caller.GetAccountCoaching(new AsyncResponse()
        {
            @Override
            public void processFinish(Object output)
            {
                //INITIALIZE ALL ONCLICK AND API RELATED PROCESS HERE TO AVOID CRASHES
                if (output != null) {
                    CoachingVideosResponseContract c = (CoachingVideosResponseContract) output;

                    if (c != null && c.Data != null && c.Data.Videos != null)
                    {
                        for (CoachingVideosContract v : c.Data.Videos)
                        {
                            videosList_all.add(v);

                            if (v.WeekNumber == currentCoachingWeekNumber)
                            {
                                if (CheckFreeUser(false))
                                {
                                    videosList.add(v);
                                }
                                else
                                {
                                    if (v.DayNumber <= currentCoachingDayNumber)
                                    {
                                        videosList.add(v);
                                    }
                                }
                            }
                        }

                        ApplicationData.getInstance().coachingVideoList = videosList_all;

                        VideoHelper.sortCoachingVideos("index", videosList);
                        videosList.get(0).IsSelected = true;
                        adapter.updateItems(videosList);

                        progressBar.setVisibility(GONE);

                        RefreshPlayer(mView, videosList.get(0));
                    }
                }
            }
        }, currentCoachingWeekNumber);
    }


    private BroadcastReceiver the_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(context.getResources().getString(R.string.coaching_broadcast_string))) {
                fromArchive = true;
                selectedCoachingWeekNumber = ApplicationData.getInstance().selectedWeekNumber;
                updateVideosList();

            }
        }
    };
}
