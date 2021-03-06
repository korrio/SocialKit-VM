package co.aquario.socialkit.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggablePanel;
import com.inthecheesefactory.lib.fblike.widget.FBLikeView;

import org.parceler.Parcels;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import co.aquario.socialkit.R;
import co.aquario.socialkit.fragment.FullScreenVideoFragment;
import co.aquario.socialkit.fragment.YoutubeDetailFragment;
import co.aquario.socialkit.model.Video;
import co.aquario.socialkit.search.youtube.YouTubeData;
import co.aquario.socialkit.search.youtube.parser.YoutubeParser;

//import com.google.android.youtube.player.YouTubeInitializationResult;
//import com.google.android.youtube.player.YouTubePlayer;
//import com.google.android.youtube.player.YouTubePlayerSupportFragment;


public class DragableActivity extends AppCompatActivity {

    private static final String YOUTUBE_API_KEY = "AIzaSyC1rMU-mkhoyTvBIdTnYU0dss0tU9vtK48";
    private static String VIDEO_KEY = "";
    String url = "";
    @InjectView(R.id.iv_thumbnail)
    public  ImageView thumbnailImageView;
    @InjectView(R.id.draggable_panel)
    public DraggablePanel draggablePanel;
    String name = "";
    String title = "";
    String avatar = "";
    String description = "";
    String countView = "";
    int countLove;
    int countComment;
    String username = "";
    int countShare;
    String userId;

    private FullScreenVideoFragment fullScreenVideoFragment;

    private Video video;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FBLikeView.onActivityResult(requestCode, resultCode, data);
    }

    boolean parseOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_dragable);
        ButterKnife.inject(this);

        video = Parcels.unwrap((Parcelable) getIntent().getExtras().get("obj"));

        userId = video.getpUserId();
        username = "";
        name = video.getpName();
        avatar = video.getpAvatar();
        title = video.getTitle();
        description = video.getDesc();
        countView = video.getView();
        countLove = video.getnLove();
        countComment = video.getnComment();
        countShare = video.getnShare();
        VIDEO_KEY = video.getYoutubeId();

        Log.e("YouTubeID", VIDEO_KEY + "");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if(video.getPostType().equals("youtube")) {
            try {
                url = YouTubeData.calculateYouTubeUrl("22", true, VIDEO_KEY);



                if(url != null && !url.equals("")) {
                    url = java.net.URLDecoder.decode(url, "UTF-8");
                    Log.e("oooooo",url);

                    initYoutubeFragment(url);
                    initDraggablePanel();
                    hookDraggablePanelListeners();
                } else {

                    YoutubeParser ytParser = new YoutubeParser();
                    ytParser.parse(VIDEO_KEY);
                    Log.e("parseOk", parseOk + "");

                    if(!parseOk) {
                        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("The video is not playable!")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        onBackPressed();
                                    }
                                })
                                .show();
                    } else {
                        url = ytParser.getUrl();
                        initYoutubeFragment(url);
                        initDraggablePanel();
                        hookDraggablePanelListeners();
                    }


                    //
                }




            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            initClipFragment(VIDEO_KEY);
            initDraggablePanel();
            hookDraggablePanelListeners();
        }
    }

    /**
     * Method triggered when the iv_thumbnail widget is clicked. This method maximize the
     * DraggablePanel.
     */
    @OnClick(R.id.iv_thumbnail)
    void onContainerClicked() {
        draggablePanel.maximize();
    }

    private void initClipFragment(String clipPath) {
        Bundle data2 = new Bundle();
        data2.putString("PATH", clipPath);

        fullScreenVideoFragment = FullScreenVideoFragment.newInstance(clipPath);
    }

    /**
     * Initialize the YouTubeSupportFrament attached as top fragment to the DraggablePanel widget and
     * reproduce the YouTube video represented with a YouTube url.
     */
//    private void initYoutubeFragment(final String youtubeId) {
//        youtubeFragment = new YouTubePlayerSupportFragment();
//        youtubeFragment.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
//
//            @Override
//            public void onInitializationSuccess(YouTubePlayer.Provider provider,YouTubePlayer player, boolean wasRestored) {
//                if (!wasRestored) {
//                    youtubePlayer = player;
//                    youtubePlayer.loadVideo(youtubeId);
//                    youtubePlayer.setShowFullscreenButton(true);
//                }
//            }
//
//            @Override
//            public void onInitializationFailure(YouTubePlayer.Provider provider,
//                                                YouTubeInitializationResult error) {
//            }
//        });
//    }
    private void initYoutubeFragment(final String url) {
        Bundle data2 = new Bundle();
        data2.putString("PATH", url);

        Log.e("000000",url);
        //data2.putString("NAME", name);
        //data2.putString("USERNAME", username);

        fullScreenVideoFragment = FullScreenVideoFragment.newInstance(url);
        //fullScreenVideoCashFragment.setArguments(data2);

        //videoViewFragment = toolbar VideoViewNativeFragment();
        //videoViewFragment.setArguments(data2);
    }

    /**
     * Initialize and configure the DraggablePanel widget with two fragments and some attributes.
     */
    private void initDraggablePanel() {
        draggablePanel.setFragmentManager(getSupportFragmentManager());

        if(video.getPostType().equals("youtube")) {
            draggablePanel.setTopFragment(fullScreenVideoFragment);
        } else {
            draggablePanel.setTopFragment(fullScreenVideoFragment);
        }

        YoutubeDetailFragment youtubeDetailFragment = new YoutubeDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("obj", Parcels.wrap(video));
        youtubeDetailFragment.setArguments(bundle);

        draggablePanel.setBottomFragment(youtubeDetailFragment);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int height = (size.y/2) -220;
        draggablePanel.setTopViewHeight(height);
        draggablePanel.initializeView();


    }

    /**
     * Hook the DraggableListener to DraggablePanel to pause or resume the video when the
     * DragglabePanel is maximized or closed.
     */
    private void hookDraggablePanelListeners() {
        draggablePanel.setDraggableListener(new DraggableListener() {
            @Override
            public void onMaximized() {
                //playVideo();
            }

            @Override
            public void onMinimized() {
                //Empty
            }

            @Override
            public void onClosedToLeft() {
               // pauseVideo();
            }

            @Override
            public void onClosedToRight() {
                //pauseVideo();
            }
        });
    }

    /**
     * Pause the video reproduced in the YouTubePlayer.
     */
//    private void pauseVideo() {
//        if (youtubePlayer.isPlaying()) {
//            youtubePlayer.pause();
//        }
//    }
//
//    /**
//     * Resume the video reproduced in the YouTubePlayer.
//     */
//    private void playVideo() {
//        if (!youtubePlayer.isPlaying()) {
//            youtubePlayer.play();
//        }
//    }
}
