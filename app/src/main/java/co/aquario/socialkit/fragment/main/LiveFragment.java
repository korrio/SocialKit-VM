package co.aquario.socialkit.fragment.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggablePanel;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.aquario.chatapp.fragment.ChatWidgetFragmentClient;
import co.aquario.socialkit.R;
import co.aquario.socialkit.fragment.SurfaceFragment;
import co.aquario.socialkit.model.Channel;

/**
 * Sample activity created to show a video from YouTube using a YouTubePlayer.
 *
 * @author Pedro Vicente Gómez Sánchez.
 */
public class LiveFragment extends BaseFragment {

    public final static String LOCATION = "stream_url";
    public final static String QUALITY = "quality";

    private static String VIDEO_PATH = "";

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
    String mQuality = "HQ";
    private String mLocation;

    private Channel channel;
    private Fragment fragment;

    public static LiveFragment newInstance(Channel channel) {
        LiveFragment mFragment = new LiveFragment();
        Bundle mBundle = new Bundle();
        mBundle.putParcelable("KEY_CHANNEL",Parcels.wrap(channel));
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            channel = Parcels.unwrap(getArguments().getParcelable("KEY_CHANNEL"));
            VIDEO_PATH = "http://150.107.31.13:1935/live/"+channel.username+"/playlist.m3u8";
            profileName = channel.name;
            title = channel.name;
            userProfile = channel.getAvatarUrl();
            cover = channel.getCoverUrl();
            description = "@"+channel.username;
            userId = channel.id;
        }
        fragment = this;



    }

    /**
     * Initialize the Activity with some injected data.
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_watch_dragable, container, false);

        ButterKnife.inject(this,rootView);

        initializeVideoPlayerFragment();
        initializeDraggablePanel();
        hookDraggablePanelListeners();

        return rootView;


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
    private void initializeVideoPlayerFragment() {

        //mLocation = getIntent().getStringExtra(LOCATION);

//        Log.e("location",mLocation);

        //mVideoPlayerFragment = VideoPlayerFragment();
        //mVideoPlayerFragment.loadMediaUrl(mLocation);

    }



    private void initializeDraggablePanel() {
        Bundle data2 = new Bundle();
        SurfaceFragment oneFragment = new SurfaceFragment();
        data2.putString("PATH", VIDEO_PATH);
        oneFragment.setArguments(data2);

        draggablePanel.setFragmentManager(getActivity().getSupportFragmentManager());
        draggablePanel.setTopFragment(oneFragment);

/*
        Bundle data = toolbar Bundle();
        LivePosterFragment moviePosterFragment = toolbar LivePosterFragment();
        moviePosterFragment.setPoster(cover);
        moviePosterFragment.setPosterTitle("DUMMY");
        data.putString("name",profileName);
        data.putString("avatar",userProfile);
        data.putString("title",title);
        data.putString("desc",description);
        data.putString("userId",userId);
        moviePosterFragment.setArguments(data);
        draggablePanel.setBottomFragment(moviePosterFragment);
        */

        //SimpleChatFragment simpleChatFragment = SimpleChatFragment.newInstance(profileName);

        ChatWidgetFragmentClient chatFragment = ChatWidgetFragmentClient.newInstance(Integer.parseInt(prefManager.userId().getOr("0")),Integer.parseInt(userId),1);



        //chatFragment.setArguments(chatBundle);

        draggablePanel.setBottomFragment(chatFragment);
        draggablePanel.initializeView();
        Picasso.with(getActivity())
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
