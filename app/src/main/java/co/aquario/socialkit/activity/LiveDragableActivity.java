package co.aquario.socialkit.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggablePanel;
import com.inthecheesefactory.lib.fblike.widget.FBLikeView;

import org.parceler.Parcels;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.aquario.chatapp.fragment.ChatWidgetFragmentClient;
import co.aquario.socialkit.BaseActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.fragment.ExoSurfaceFragment;
import co.aquario.socialkit.fragment.FullScreenVideoFragment;
import co.aquario.socialkit.model.Video;
import co.aquario.socialkit.util.DensityUtil;


public class LiveDragableActivity extends BaseActivity {

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
    String url = "";
    String countView = "";
    int countLove;
    int countComment;
    int countShare;
    String userId;
//    private YouTubePlayer youtubePlayer;
//    private YouTubePlayerSupportFragment youtubeFragment;
    private FullScreenVideoFragment fullScreenVideoFragment;
    private ExoSurfaceFragment surfaceFragment;
    //private VideoViewNativeFragment videoViewFragment;
    private Video video;

    float width;
    float height;

    String username = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FBLikeView.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            height = DensityUtil.getWidthInPx(this);
            width = DensityUtil.getHeightInPx(this);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            width = DensityUtil.getWidthInPx(this);
            height = DensityUtil.getHeightInPx(this);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_dragable);
        ButterKnife.inject(this);

        video = Parcels.unwrap((Parcelable) getIntent().getExtras().get("obj"));

        width = DensityUtil.getWidthInPx(this);
        height = DensityUtil.getHeightInPx(this);

        userId = video.getpUserId();
        name = video.getpName();
        username = "";
        avatar = video.getpAvatar();
        title = video.getTitle();
        description = video.getDesc();
        countView = video.getView();
        countLove = video.getnLove();
        countComment = video.getnComment();
        countShare = video.getnShare();
        VIDEO_KEY = video.getYoutubeId();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        initClipFragment(VIDEO_KEY,name,username);


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

    private void initClipFragment(String clipPath, String name, String username) {
        Bundle data2 = new Bundle();
        data2.putString("PATH", clipPath);
        Log.e("000000", clipPath);

        fullScreenVideoFragment = FullScreenVideoFragment.newInstance(clipPath);

        //videoViewFragment = toolbar VideoViewNativeFragment();
        //videoViewFragment.setArguments(data2);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();

    }

    /**
     * Initialize and configure the DraggablePanel widget with two fragments and some attributes.
     */
    private void initDraggablePanel() {
        draggablePanel.setFragmentManager(getSupportFragmentManager());

        draggablePanel.setTopFragment(fullScreenVideoFragment);

        ChatWidgetFragmentClient chatFragment = ChatWidgetFragmentClient.newInstance(Integer.parseInt(VMApp.mPref.userId().getOr("0")),Integer.parseInt(userId),1);
        draggablePanel.setBottomFragment(chatFragment);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int height = (size.y/2) - 180;
        draggablePanel.setTopViewHeight(height);

        draggablePanel.setClickToMaximizeEnabled(true);
        //draggablePanel.setClickToMinimizeEnabled(true);


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
                //pauseVideo();
            }

            @Override
            public void onClosedToRight() {
               // pauseVideo();
            }
        });
    }


}
