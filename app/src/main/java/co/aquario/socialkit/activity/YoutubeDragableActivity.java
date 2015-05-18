package co.aquario.socialkit.activity;

import android.os.Bundle;
import android.os.Parcelable;
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
import co.aquario.socialkit.fragment.DragableBottomFragment;
import co.aquario.socialkit.model.Video;


public class YoutubeDragableActivity extends BaseActivity {

    private static final String YOUTUBE_API_KEY = "AIzaSyC1rMU-mkhoyTvBIdTnYU0dss0tU9vtK48";
    private static String VIDEO_KEY = "";



    @InjectView(R.id.iv_thumbnail)
    ImageView thumbnailImageView;
    @InjectView(R.id.draggable_panel)
    DraggablePanel draggablePanel;

    private YouTubePlayer youtubePlayer;
    private YouTubePlayerSupportFragment youtubeFragment;
    String name = "";
    String title = "";
    String avatar = "";
    String description = "";
    String countView = "";
    int countLove;
    int countComment;
    int countShare;
    String userId;

    private Video video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_sample);
        ButterKnife.inject(this);

        video = Parcels.unwrap((Parcelable) getIntent().getExtras().get("obj"));

        VIDEO_KEY = video.getYoutubeId();
        userId = video.getpUserId();
        name = video.getpName();
        avatar = video.getpAvatar();
        title = video.getTitle();
        description = video.getDesc();
        countView = video.getView();
        countLove = video.getnLove();
        countComment = video.getnComment();
        countShare = video.getnShare();

        initializeYoutubeFragment();
        initializeDraggablePanel();
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

    /**
     * Initialize the YouTubeSupportFrament attached as top fragment to the DraggablePanel widget and
     * reproduce the YouTube video represented with a YouTube url.
     */
    private void initializeYoutubeFragment() {
        youtubeFragment = new YouTubePlayerSupportFragment();
        youtubeFragment.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    youtubePlayer = player;
                    youtubePlayer.loadVideo(VIDEO_KEY);
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
    private void initializeDraggablePanel() {
        draggablePanel.setFragmentManager(getSupportFragmentManager());
        draggablePanel.setTopFragment(youtubeFragment);

        DragableBottomFragment dragableBottomFragment = new DragableBottomFragment();
        //dragableBottomFragment.setPoster(VIDEO_POSTER_THUMBNAIL);
        //dragableBottomFragment.setPosterTitle(VIDEO_POSTER_TITLE);

        /*
        Bundle data = new Bundle();
        data.putString("userId",userId);
        data.putString("name", name);
        data.putString("avatar", avatar);
        data.putString("title",title);
        data.putString("desc", description);
        data.putString("view", countView);
        data.putInt("love_count", countLove);
        data.putInt("comment_count",countComment);
        data.putInt("share_count",countShare);
        */

        //dragableBottomFragment.setArguments(data);

        Bundle bundle = new Bundle();
        bundle.putParcelable("obj", Parcels.wrap(video));
        dragableBottomFragment.setArguments(bundle);

        draggablePanel.setBottomFragment(dragableBottomFragment);
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
