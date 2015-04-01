package co.aquario.socialkit.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggablePanel;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.aquario.socialkit.R;
import co.aquario.socialkit.fragment.LIvePosterFragment;
import co.aquario.socialkit.fragment.VideoViewFragment;

/**
 * Sample activity created to show a video from YouTube using a YouTubePlayer.
 *
 * @author Pedro Vicente Gómez Sánchez.
 */
public class VitamioActivity extends BaseActivity {

    private static final String YOUTUBE_API_KEY = "AIzaSyC1rMU-mkhoyTvBIdTnYU0dss0tU9vtK48";
    private static String VIDEO_KEY = "";

    private static final String VIDEO_POSTER_TITLE = "X-Men: Days of Future Past";


    @InjectView(R.id.iv_thumbnail)
    ImageView thumbnailImageView;
    @InjectView(R.id.draggable_panel)
    DraggablePanel draggablePanel;

    String profileName = "";
    String title = "";
    String userProfile = "";
    String description= "";
    String userId = "";
    String cover = "";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Initialize the Activity with some injected data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_sample);
        ButterKnife.inject(this);

        VIDEO_KEY = getIntent().getStringExtra("id");
        profileName = getIntent().getStringExtra("name");
        title = getIntent().getStringExtra("title");
        userProfile = getIntent().getStringExtra("avatar");
        cover = getIntent().getStringExtra("cover");
        description = getIntent().getStringExtra("desc");
        userId = getIntent().getStringExtra("userId");
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

    }

    private void initializeDraggablePanel() {
        Bundle data2 = new Bundle();
        VideoViewFragment oneFragment = new VideoViewFragment();
        data2.putString("urlLive", VIDEO_KEY);
        oneFragment.setArguments(data2);
        draggablePanel.setFragmentManager(getSupportFragmentManager());
        draggablePanel.setTopFragment(oneFragment);
        //MainFragment fragment = new MainFragment();
        Bundle data = new Bundle();
        LIvePosterFragment moviePosterFragment = new LIvePosterFragment();
        moviePosterFragment.setPoster(cover);
        moviePosterFragment.setPosterTitle(VIDEO_POSTER_TITLE);
        data.putString("name",profileName);
        data.putString("avatar",userProfile);
        data.putString("title",title);
        data.putString("desc",description);
        data.putString("userId",userId);
        moviePosterFragment.setArguments(data);
        draggablePanel.setBottomFragment(moviePosterFragment);
        draggablePanel.initializeView();
        Picasso.with(this)
                .load(cover)
                .into(thumbnailImageView);
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

            }

            @Override
            public void onClosedToRight() {

            }
        });
    }


}
