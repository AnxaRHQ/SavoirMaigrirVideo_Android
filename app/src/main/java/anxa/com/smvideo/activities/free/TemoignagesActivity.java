package anxa.com.smvideo.activities.free;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.util.ArrayList;
import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.registration.RegistrationActivity;
import anxa.com.smvideo.activities.registration.RegistrationFormActivity;
import anxa.com.smvideo.common.SavoirMaigrirVideoConstants;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.VideoContract;
import anxa.com.smvideo.contracts.VideoResponseContract;
import anxa.com.smvideo.ui.CustomListView;
import anxa.com.smvideo.ui.VideoListAdapter;
import anxa.com.smvideo.util.VideoHelper;

/**
 * Created by angelaanxa on 5/24/2017.
 */

public class TemoignagesActivity extends Fragment implements View.OnClickListener {

    private Context context;
    protected ApiCaller caller;

    private CustomListView customListView;
    private VideoListAdapter adapter;
    private List<VideoContract> videosList;

    private YouTubePlayerFragment playerFragment;
    View mView;

    private static final int BROWSERTAB_ACTIVITY = 1111;
    private TextView header_right;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.context = getActivity();

        mView = inflater.inflate(R.layout.temoignages, null);
        caller = new ApiCaller();

        //header change
        ((TextView) (mView.findViewById(R.id.header_title_tv))).setText(getString(R.string.menu_temoignages));
        header_right = (TextView) (mView.findViewById(R.id.header_right_tv));
        header_right.setOnClickListener(this);

        //ui
        customListView = (CustomListView) mView.findViewById(R.id.testimonialListView);
        videosList = new ArrayList<VideoContract>();

        if (adapter == null) {
            adapter = new VideoListAdapter(context, videosList, this);
        }
        customListView.setAdapter(adapter);

        //Initializing and adding YouTubePlayerFragment
        FragmentManager fm = getFragmentManager();
        String tag = YouTubePlayerFragment.class.getSimpleName();
        playerFragment = (YouTubePlayerFragment) fm.findFragmentByTag(tag);

        FragmentTransaction ft = fm.beginTransaction();
        playerFragment = YouTubePlayerFragment.newInstance();
        ft.replace(R.id.youtube_layout, playerFragment, tag);
        ft.commit();

        caller.GetFreeTestimonials(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                //INITIALIZE ALL ONCLICK AND API RELATED PROCESS HERE TO AVOID CRASHES
                if (output != null) {
                    VideoResponseContract c = (VideoResponseContract) output;

                    if (c != null && c.Data != null && c.Data.Videos != null) {
                        for (VideoContract v : c.Data.Videos) {
                            if (v.VideoSource != null && v.VideoSource.equalsIgnoreCase("youtube")) {
                                videosList.add(v);
                            }
                        }

                        ApplicationData.getInstance().testimonialVideoList = videosList;
                        VideoHelper.sort("index", videosList);
                        videosList.get(0).IsSelected = true;
                        adapter.updateItems(videosList);

                        RefreshPlayer(mView, videosList.get(0));
                    }
                }
            }
        });

        return mView;
    }

    @Override
    public void onClick(final View v) {
        if(v==header_right){
            goToRegistrationPage();
        }else {
            FragmentManager fm = getFragmentManager();
            String tag = YouTubePlayerFragment.class.getSimpleName();
            playerFragment = (YouTubePlayerFragment) fm.findFragmentByTag(tag);
            if (playerFragment != null) {
                FragmentTransaction ft = fm.beginTransaction();
                playerFragment = YouTubePlayerFragment.newInstance();
                ft.replace(R.id.youtube_layout, playerFragment, tag);
                ft.commit();
            }

            final String videoId = (String) v.getTag(R.id.video_id);

            for (int i = 0; i < videosList.size(); i++) {
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

    private void RefreshPlayer(final View v, final VideoContract video) {

        playerFragment.initialize(SavoirMaigrirVideoConstants.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {
                if (video.VideoId != null) {

                    youTubePlayer.cueVideo(String.valueOf(video.VideoId));

                    ((TextView) (mView.findViewById(R.id.videoTitle))).setText(video.Title);
                    ((TextView) (mView.findViewById(R.id.videoDesc))).setText(video.Description);
                    ((TextView) (mView.findViewById(R.id.videoDuration))).setText(video.Duration);
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
                            if (!b){
                                getActivity().setRequestedOrientation(
                                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            }
                        }
                    });
                }

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                //Toast.makeText(YouTubePlayerFragmentActivity.this, "Error while initializing YouTubePlayer.", Toast.LENGTH_SHORT).show();
            }


        });

    }

    private void goToRegistrationPage() {
        Intent mainIntent = new Intent(context, RegistrationFormActivity.class);
        startActivity(mainIntent);
    }
}
