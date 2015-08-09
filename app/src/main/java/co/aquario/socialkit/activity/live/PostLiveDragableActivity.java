package co.aquario.socialkit.activity.live;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;

import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggablePanel;
import com.inthecheesefactory.lib.fblike.widget.FBLikeView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.aquario.chatapp.fragment.ChatWidgetFragmentClient;
import co.aquario.socialkit.BaseActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.util.DensityUtil;


public class PostLiveDragableActivity extends BaseActivity {

    @InjectView(R.id.iv_thumbnail)
    public  ImageView thumbnailImageView;
    @InjectView(R.id.draggable_panel)
    public DraggablePanel draggablePanel;

    String userId;
    private PostLiveStreamingFragment surfaceFragment;
    //private VideoViewNativeFragment videoViewFragment;

    float width;
    float height;


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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FBLikeView.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_dragable);
        ButterKnife.inject(this);

        width = DensityUtil.getWidthInPx(this);
        height = DensityUtil.getHeightInPx(this);

        initClipFragment();
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

    private void initClipFragment() {

        surfaceFragment = PostLiveStreamingFragment.newInstance();
        //surfaceFragment.setArguments(data2);

        //videoViewFragment = toolbar VideoViewNativeFragment();
        //videoViewFragment.setArguments(data2);
    }


    /**
     * Initialize and configure the DraggablePanel widget with two fragments and some attributes.
     */
    private void initDraggablePanel() {
        draggablePanel.setFragmentManager(getSupportFragmentManager());
        draggablePanel.setTopFragment(surfaceFragment);

        userId = "241";

        ChatWidgetFragmentClient chatFragment = ChatWidgetFragmentClient.newInstance(Integer.parseInt(VMApp.mPref.userId().getOr("0")),Integer.parseInt(userId),1);
        draggablePanel.setBottomFragment(chatFragment);
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

            }

            @Override
            public void onClosedToRight() {

            }
        });
    }

}
