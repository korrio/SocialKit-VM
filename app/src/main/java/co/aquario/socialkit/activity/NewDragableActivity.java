package co.aquario.socialkit.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggablePanel;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.parceler.Parcels;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.aquario.socialkit.R;
import co.aquario.socialkit.fragment.ExoSurfaceFragment;
import co.aquario.socialkit.fragment.YoutubeDetailFragment;
import co.aquario.socialkit.model.Video;


public class NewDragableActivity extends AppCompatActivity {

    private static final String YOUTUBE_API_KEY = "AIzaSyC1rMU-mkhoyTvBIdTnYU0dss0tU9vtK48";
    private static String VIDEO_KEY = "";

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
    int countShare;
    String userId;
    private YouTubePlayer youtubePlayer;
    private YouTubePlayerSupportFragment youtubeFragment;
    private ExoSurfaceFragment surfaceFragment;
    //private VideoViewNativeFragment videoViewFragment;
    private Video video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_dragable);
        ButterKnife.inject(this);

        video = Parcels.unwrap((Parcelable) getIntent().getExtras().get("obj"));

        userId = video.getpUserId();
        name = video.getpName();
        avatar = video.getpAvatar();
        title = video.getTitle();
        description = video.getDesc();
        countView = video.getView();
        countLove = video.getnLove();
        countComment = video.getnComment();
        countShare = video.getnShare();
        VIDEO_KEY = video.getYoutubeId();

        if(video.getPostType().equals("youtube")) {
            initYoutubeFragment(VIDEO_KEY);
        } else {
            initClipFragment(VIDEO_KEY);
        }

        initDraggablePanel();
        hookDraggablePanelListeners();
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

        surfaceFragment = new ExoSurfaceFragment();
        surfaceFragment.setArguments(data2);

        //videoViewFragment = toolbar VideoViewNativeFragment();
        //videoViewFragment.setArguments(data2);
    }

    /**
     * Initialize the YouTubeSupportFrament attached as top fragment to the DraggablePanel widget and
     * reproduce the YouTube video represented with a YouTube url.
     */
    private void initYoutubeFragment(final String youtubeId) {
        youtubeFragment = new YouTubePlayerSupportFragment();
        youtubeFragment.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    youtubePlayer = player;
                    youtubePlayer.loadVideo(youtubeId);
                    youtubePlayer.setShowFullscreenButton(true);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult error) {
            }
        });
    }

    /**
     * Initialize and configure the DraggablePanel widget with two fragments and some attributes.
     */
    private void initDraggablePanel() {
        draggablePanel.setFragmentManager(getSupportFragmentManager());

        if(video.getPostType().equals("youtube")) {
            draggablePanel.setTopFragment(youtubeFragment);
        } else {
            draggablePanel.setTopFragment(surfaceFragment);
        }

        YoutubeDetailFragment youtubeDetailFragment = new YoutubeDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("obj", Parcels.wrap(video));
        youtubeDetailFragment.setArguments(bundle);

        draggablePanel.setBottomFragment(youtubeDetailFragment);
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
                pauseVideo();
            }

            @Override
            public void onClosedToRight() {
                pauseVideo();
            }
        });
    }

    /**
     * Pause the video reproduced in the YouTubePlayer.
     */
    private void pauseVideo() {
        if (youtubePlayer.isPlaying()) {
            youtubePlayer.pause();
        }
    }

    /**
     * Resume the video reproduced in the YouTubePlayer.
     */
    private void playVideo() {
        if (!youtubePlayer.isPlaying()) {
            youtubePlayer.play();
        }
    }
}
