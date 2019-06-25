package anxa.com.smvideo.activities.account;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.util.ArrayList;
import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.common.SavoirMaigrirVideoConstants;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.VideoContract;
import anxa.com.smvideo.contracts.VideoResponseContract;
import anxa.com.smvideo.ui.CustomListView;
import anxa.com.smvideo.ui.VideoListAdapter;
import anxa.com.smvideo.util.VideoHelper;

/**
 * Created by aprilanxa on 14/06/2017.
 */

public class VideosFragment extends Fragment implements View.OnClickListener {

    private Context context;
    protected ApiCaller caller;

    private VideoListAdapter adapter;
    private List<VideoContract> videosList;

    private CustomListView coachingListView;

    private YouTubePlayerFragment playerFragment;
    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.context = getActivity();
        mView = inflater.inflate(R.layout.videos, null);

        caller = new ApiCaller();

        //header change
        ((TextView) (mView.findViewById(R.id.header_title_tv))).setText(getString(R.string.menu_account_videos));
        ((TextView) (mView.findViewById(R.id.header_right_tv))).setVisibility(View.INVISIBLE);

        coachingListView = (CustomListView) mView.findViewById(R.id.coachingListView);

        ((LinearLayout)mView.findViewById(R.id.youtube_layout_caption)).setVisibility(View.VISIBLE);

        videosList = new ArrayList<VideoContract>();
        if (adapter == null) {
            adapter = new VideoListAdapter(context, videosList, this);
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

        getVideosFromAPI();
//        getVideosFromAPI2/();

        return mView;
    }

    @Override
    public void onClick(final View v) {

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

    private void getVideosFromAPI(){
        caller.GetVideos(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {

                //INITIALIZE ALL ONCLICK AND API RELATED PROCESS HERE TO AVOID CRASHES
                if (output != null) {
                    VideoResponseContract c = (VideoResponseContract) output;

                    if (c != null && c.Data != null && c.Data.Videos != null) {
                        for (VideoContract v : c.Data.Videos) {
//                            if (v.VideoSource != null && v.VideoSource.equalsIgnoreCase("youtube")) {
                            videosList.add(v);
//                            }
                        }

                        ApplicationData.getInstance().exerciseVideoList = videosList;
                        VideoHelper.sort("index", videosList);
                        videosList.get(0).IsSelected = true;
                        adapter.updateItems(videosList);

                        RefreshPlayer(mView, videosList.get(0));
                    }
                }
            }
        });
    }

    private void RefreshPlayer(final View v, final VideoContract video) {
        playerFragment.initialize(SavoirMaigrirVideoConstants.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {
                if (video.VideoUrl != null) {

                    youTubePlayer.cueVideo(video.VideoUrl);

                    ((TextView) (mView.findViewById(R.id.videoTitle))).setText(video.Title);
                    ((TextView) (mView.findViewById(R.id.videoDesc))).setText(video.Description);
//                    ((TextView) (mView.findViewById(R.id.videoDuration))).setText(video.Duration);
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
                //Toast.makeText(YouTubePlayerFragmentActivity.this, "Error while initializing YouTubePlayer.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getVideosFromAPI2(){
        caller.GetFreeDiscover(new AsyncResponse() {
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

                        ApplicationData.getInstance().discoverVideoList = videosList;
                        VideoHelper.sort("index", videosList);
                        videosList.get(0).IsSelected = true;
                        adapter.updateItems(videosList);

                        RefreshPlayer(mView, videosList.get(0));
                    }
                }
            }
        });
    }
}

