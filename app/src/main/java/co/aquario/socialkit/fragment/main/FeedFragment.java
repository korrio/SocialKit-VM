package co.aquario.socialkit.fragment.main;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.tumblr.bookends.Bookends;

import java.io.IOException;
import java.util.ArrayList;

import co.aquario.socialkit.MainApplication;
import co.aquario.socialkit.R;
import co.aquario.socialkit.activity.LoginActivity;
import co.aquario.socialkit.activity.MainActivity;
import co.aquario.socialkit.activity.PostStatusActivity2;
import co.aquario.socialkit.activity.SoundCloudActivity;
import co.aquario.socialkit.activity.TakePhotoActivity;
import co.aquario.socialkit.activity.YoutubeSearchActivity;
import co.aquario.socialkit.adapter.ButtonItemAdapter;
import co.aquario.socialkit.adapter.FeedAdapter;
import co.aquario.socialkit.event.FollowRegisterEvent;
import co.aquario.socialkit.event.FollowUserSuccessEvent;
import co.aquario.socialkit.event.GetUserProfileEvent;
import co.aquario.socialkit.event.GetUserProfileSuccessEvent;
import co.aquario.socialkit.event.LoadTimelineEvent;
import co.aquario.socialkit.event.LoadTimelineSuccessEvent;
import co.aquario.socialkit.event.LogoutEvent;
import co.aquario.socialkit.event.PostCommentSuccessEvent;
import co.aquario.socialkit.event.PostLoveEvent;
import co.aquario.socialkit.event.PostLoveSuccessEvent;
import co.aquario.socialkit.event.PostShareEvent;
import co.aquario.socialkit.event.PostShareSuccessEvent;
import co.aquario.socialkit.event.RefreshEvent;
import co.aquario.socialkit.event.UnfollowUserSuccessEvent;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.PostStory;
import co.aquario.socialkit.search.soundcloud.SoundCloudService;
import co.aquario.socialkit.util.PrefManager;
import co.aquario.socialkit.util.Utils;
import co.aquario.socialkit.widget.EndlessRecyclerOnScrollListener;
import co.aquario.socialkit.widget.RoundedTransformation;


public class FeedFragment extends BaseFragment {


    public static final String USER_ID = "USER_ID";
    public static final String IS_HOMETIMELINE = "IS_HOMETIMELINE";

    private static String TYPE = "";
    private static int PER_PAGE = 20;
    public ArrayList<PostStory> list = new ArrayList<>();
    public FeedAdapter adapter;
    public Bookends<FeedAdapter> bAdapter;
    public RelativeLayout fabLayout;
    public RecyclerView mRecyclerView;
    PrefManager pref;
    Button btnLove;
    Button btnComment;
    Button btnShare;
    int pShare;
    View myHeader;
    ImageView avatar;
    ImageView cover;
    TextView titleTv;
    TextView usernameTv;
    TextView bioTv;
    TextView countPost;
    TextView countFollowing;
    TextView countFollower;
    TextView countFriend;
    Button btnFollow;
    String imageTitle;
    String nameTitle;
    String coverUrl;

    // @InjectView(R.id.empty)
    //TextView mEmptyView;
    String username;
    boolean isFollowing;
    private int currentPage = 1;
    private boolean isRefresh = false;
    private boolean isLoadding = false;
    private String userId = "";
    // home_timeline = including others post
    // user_timeline = only the user's post
    private boolean isHomeTimeline;
    private SwipeRefreshLayout swipeLayout;
    private FloatingActionButton postPhotoBtn;
    private FloatingActionButton postVideoBtn;
    private FloatingActionButton postYoutubeBtn;
    private FloatingActionButton postSoundCloudBtn;
    private FloatingActionButton postStatusBtn;
    private Toolbar mPlayerToolbar;
    private MediaPlayer mMediaPlayer;
    private ImageView mPlayerStateButton;
    private ProgressBar mProgressBar;

    public static FeedFragment newInstance(String userId, boolean isHomeTimeline) {
        FeedFragment mFragment = new FeedFragment();
		Bundle mBundle = new Bundle();
		mBundle.putString(USER_ID, userId);
        mBundle.putBoolean(IS_HOMETIMELINE, isHomeTimeline);
        mFragment.setArguments(mBundle);
		return mFragment;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(USER_ID);
            isHomeTimeline = getArguments().getBoolean(IS_HOMETIMELINE);
        } else {
            userId = prefManager.userId().getOr("0");
            isHomeTimeline = true;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId), TYPE, 1, PER_PAGE, isHomeTimeline));
                //String loadMoreUrl = "http://api.vdomax.com/search/channel/a?from=0&limit=10";
                //aq.ajax(loadMoreUrl, JSONObject.class, fragment, "getJson");
                //Log.e("5555","onRefresh");
            }
        });

        if (!userId.equals(prefManager.userId().getOr("6"))) {
            ApiBus.getInstance().post(new GetUserProfileEvent(userId));
        }
    }

    private void toggleSongState() {
        if (mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            mPlayerStateButton.setImageResource(R.drawable.ic_play);
        }else{
            mMediaPlayer.start();
            toggleProgressBar();
            mPlayerStateButton.setImageResource(R.drawable.ic_pause);
        }
    }

    private void toggleProgressBar() {
        if (mMediaPlayer.isPlaying()){
            mProgressBar.setVisibility(View.INVISIBLE);
            mPlayerStateButton.setVisibility(View.VISIBLE);
        }else{
            mProgressBar.setVisibility(View.VISIBLE);
            mPlayerStateButton.setVisibility(View.INVISIBLE);
        }
    }

    public void playTrack(String uri,String title) {
        mPlayerToolbar.setVisibility(View.VISIBLE);

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.reset();
        toggleProgressBar();

        try {
            mMediaPlayer.setDataSource(uri + "?client_id=" + SoundCloudService.CLIENT_ID);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(mMediaPlayer != null){
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void setEmptyText(CharSequence emptyText) {
        //mEmptyView.setText(emptyText);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        // Create your views, whatever they may be
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.scroll);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(linearLayoutManager);

        myHeader = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.activity_profile, mRecyclerView, false);

        setEmptyText(getString(R.string.no_story));

        btnLove = (Button) rootView.findViewById(R.id.btn_love);
        btnShare = (Button) rootView.findViewById(R.id.btn_share);

        fabLayout = (RelativeLayout) rootView.findViewById(R.id.layoutMenu);
        mPlayerToolbar = (Toolbar) rootView.findViewById(R.id.player_toolbar);
        mPlayerStateButton = (ImageView)rootView.findViewById(R.id.player_state);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.player_progress);
        mProgressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        mPlayerStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSongState();
            }
        });

        mPlayerToolbar.setVisibility(View.GONE);

        mMediaPlayer = new MediaPlayer();

        //toggleProgressBar();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                toggleSongState();
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayerStateButton.setImageResource(R.drawable.ic_play);
            }
        });

        postPhotoBtn = (FloatingActionButton) fabLayout.findViewById(R.id.action_photo);
        postVideoBtn = (FloatingActionButton) fabLayout.findViewById(R.id.action_video);
        postYoutubeBtn = (FloatingActionButton) fabLayout.findViewById(R.id.action_youtube);
        postSoundCloudBtn = (FloatingActionButton) fabLayout.findViewById(R.id.action_soundcloud);
        postStatusBtn = (FloatingActionButton) fabLayout.findViewById(R.id.action_write_post);

        postSoundCloudBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SoundCloudActivity.class);
                startActivity(i);
            }
        });

        postYoutubeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), YoutubeSearchActivity.class);
                startActivity(i);

            }
        });

        postPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((MainActivity) getActivity()).selectImage();
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                startingLocation[0] += v.getWidth() / 2;
                TakePhotoActivity.startCameraFromLocation(startingLocation, getActivity(), false);
                /*
                TakePhotoFragmentUseless fragment = TakePhotoFragmentUseless.newInstance(startingLocation, getActivity());
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(R.id.sub_container, fragment).addToBackStack(null);
                transaction.commit();
                */
                //overridePendingTransition(0, 0);
            }
        });
        postVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).selectVideo();
            }
        });

        postStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PostStatusActivity2.class);
                startActivity(i);

            }
        });



        //aq = new AQuery(getActivity());

        adapter = new FeedAdapter(getActivity(), list, this, isHomeTimeline);

        adapter.OnItemLoveClick(new FeedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!isHomeTimeline)
                    position--;
                ApiBus.getInstance().post(new PostLoveEvent(userId, list.get(position).postId));
            }
        });

        adapter.OnItemShareClick(new FeedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (isHomeTimeline)
                    position--;

                pShare = position;

                new MaterialDialog.Builder(getActivity())
                        .title(R.string.socialNetworks)
                        .adapter(new ButtonItemAdapter(getActivity(), R.array.socialNetworks),
                                new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                        Toast.makeText(getActivity(), "Clicked item " + which, Toast.LENGTH_SHORT).show();
                                        switch (which) {
                                            case 0:
                                                // Share to vdomax
                                                ApiBus.getInstance().post(new PostShareEvent(pref.userId().getOr("6"), list.get(pShare).postId));
                                                break;
                                            case 1:
                                                break;
                                            default:
                                                break;

                                        }
                                    }
                                })
                        .show();

                //ApiBus.getInstance().post(new PostShareEvent(pref.userId().getOr("6"), list.get(position).postId));
            }
        });



        // load more
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page) {
                currentPage = page;
                isRefresh = false;
                if (!isLoadding)
                    ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId), TYPE, page, PER_PAGE, isHomeTimeline));
                isLoadding = true;
                Log.e("thispageis", page + "");
            }
        });

        /*
        mRecyclerView.setPadding(
                mRecyclerView.getPaddingLeft(),
                mRecyclerView.getPaddingTop() + Utils.dpToPx(48), // + tabs height
                mRecyclerView.getPaddingRight(),
                mRecyclerView.getPaddingBottom()
        );
        */

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {

            final int DISTANCE = 3;

            float startY = 0;
            float dist = 0;
            boolean isFabHide = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    startY = event.getY();
                } else if (action == MotionEvent.ACTION_MOVE) {
                    dist = event.getY() - startY;

                    if ((Utils.pxToDp((int) dist, getActivity()) <= -DISTANCE) && !isFabHide) {
                        isFabHide = true;
                        hideFloatingButton();
                    } else if ((Utils.pxToDp((int) dist, getActivity()) > DISTANCE) && isFabHide) {
                        isFabHide = false;
                        showFloatingButton();
                    }

                    if ((isFabHide && (Utils.pxToDp((int) dist, getActivity()) <= -DISTANCE))
                            || (!isFabHide && (Utils.pxToDp((int) dist, getActivity()) > 0))) {
                        startY = event.getY();
                    }
                } else if (action == MotionEvent.ACTION_UP) {
                    startY = 0;
                }

                return false;
            }
        });


        // Add them as headers / footers
        bAdapter = new Bookends<>(adapter);
        bAdapter.addHeader(myHeader);

        if (!userId.equals(prefManager.userId().getOr("6"))) {

            mRecyclerView.setAdapter(bAdapter);
        } else {
            mRecyclerView.setAdapter(adapter);
        }

        ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId),TYPE,1,PER_PAGE,isHomeTimeline));
        //ApiBus.getInstance().post(new LoadTimelineEvent(32,"photo",1,50));
        //aq.ajax(urlMain, JSONObject.class, this, "getJson");

		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        //MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
        return rootView;
    }

    @Subscribe public void onRefreshFeed(RefreshEvent event) {
        ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId), TYPE, 1, PER_PAGE, isHomeTimeline));
        Log.e("onRefreshFeed", "true");
    }

    /**
     * Save Fragment's State here
     */
    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        // For example:
        //outState.putString("text", tvSample.getText().toString());
    }

    /**
     * Restore Fragment's State here
     */
    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        // For example:
        //tvSample.setText(savedInstanceState.getString("text"));
    }

    @Subscribe public void onLoadTimelineSuccess(LoadTimelineSuccessEvent event) {
        if(isRefresh)
            list.clear();
        isRefresh = false;
        list.addAll(event.getTimelineData().getPosts());
        bAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        swipeLayout.setRefreshing(false);
        isLoadding = false;

    }

    @Subscribe public void onLogout(LogoutEvent event) {
        MainApplication.logout();
        Intent login = new Intent(getActivity(), LoginActivity.class);
        startActivity(login);
        getActivity().finish();
    }

    @Subscribe public void onPostLoveSuccessEvent(PostLoveSuccessEvent event) {
        //Toast.makeText(getActivity(), "Loved", Toast.LENGTH_SHORT).show();

    }

    @Subscribe public void onPostShareSuccessEvent(PostShareSuccessEvent event) {
        //Toast.makeText(getActivity(), "Shared", Toast.LENGTH_SHORT).show();
    }

    @Subscribe public void onPostCommentSuccessEvent(PostCommentSuccessEvent event) {
        //Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onLoadProfile(final GetUserProfileSuccessEvent event) {

        Log.e("eventHey:", event.getUser().getId());

        btnFollow = (Button) myHeader.findViewById(R.id.btn_follow);

        avatar = (ImageView) myHeader.findViewById(R.id.user_avatar);
        cover = (ImageView) myHeader.findViewById(R.id.user_cover);

        titleTv = (TextView) myHeader.findViewById(R.id.user_name);
        //usernameTv = (TextView) myHeader.findViewById(R.id.user_username);
        bioTv = (TextView) myHeader.findViewById(R.id.user_des);

        titleTv.setText(Html.fromHtml(event.getUser().getName()));
        //usernameTv.setText("@" + event.getUser().getUsername());
        bioTv.setText(Html.fromHtml(event.getUser().getAbout()));

        if (Html.fromHtml(event.getUser().getName()).toString().length() >= 0)
            getActivity().setTitle(Html.fromHtml(event.getUser().getName()).toString());

        Picasso.with(getActivity())
                .load(event.getUser().getCoverUrl())
                .fit().centerCrop()
                .into(cover);

        Log.e("userCoverNa", event.getUser().getCoverUrl());

        Picasso.with(getActivity())
                .load(event.getUser().getAvatarUrl())
                .centerCrop()
                .resize(100, 100)
                .transform(new RoundedTransformation(50, 4))
                .into(avatar);

        countPost = (TextView) myHeader.findViewById(R.id.countPost);
        countFollowing = (TextView) myHeader.findViewById(R.id.countFollowing);
        countFollower = (TextView) myHeader.findViewById(R.id.countFollower);
        countFriend = (TextView) myHeader.findViewById(R.id.countFriend);

        countPost.setText(event.getCount().post + "");
        countFollowing.setText(event.getCount().following + "");
        countFollower.setText(event.getCount().follower + "");
        countFriend.setText(event.getCount().friend + "");

        isFollowing = event.getUser().getIsFollowing();

        initButton(isFollowing, btnFollow);

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFollowing) {
                    toggleUnfollow(btnFollow);

                } else {
                    toggleFollowing(btnFollow);
                }
                ApiBus.getInstance().post(new FollowRegisterEvent(event.getUser().getId()));
                isFollowing = !isFollowing;
            }
        });


    }

    public void initButton(boolean following, View v) {
        Button button = (Button) v;

        isFollowing = following;

        if (following) {
            toggleFollowing(button);
        } else {
            toggleUnfollow(button);
        }


    }


    public void showFloatingButton() {
        AnimatorSet animSet = new AnimatorSet();

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(fabLayout
                , View.TRANSLATION_Y, 0);

        animSet.playTogether(anim1);
        animSet.setDuration(300);
        animSet.start();
    }

    public void hideFloatingButton() {
        AnimatorSet animSet = new AnimatorSet();

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(fabLayout
                , View.TRANSLATION_Y, fabLayout.getHeight());

        animSet.playTogether(anim1);
        animSet.setDuration(300);
        animSet.start();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Subscribe
    public void onFollowSuccess(FollowUserSuccessEvent event) {

    }

    @Subscribe
    public void onUnfollowSuccess(UnfollowUserSuccessEvent event) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                mSearchCheck = true;
                break;
            case R.id.menu_sort_all:
                isRefresh = true;
                TYPE = "";
                break;
            case R.id.menu_sort_text:
                isRefresh = true;
                TYPE = "text";
                break;
            case R.id.menu_sort_photo:
                isRefresh = true;
                TYPE = "photo";
                break;
            case R.id.menu_sort_video:
                isRefresh = true;
                TYPE = "video";
                break;
            case R.id.menu_sort_youtube:
                isRefresh = true;
                TYPE = "youtube";
                break;
            case R.id.menu_sort_soundcloud:
                isRefresh = true;
                TYPE = "soundcloud";
                break;
            case R.id.menu_sort_place:
                isRefresh = true;
                TYPE = "place";
                break;
            case R.id.menu_sort_live:
                isRefresh = true;
                TYPE = "live";
                break;
            default:
                isRefresh = true;
                break;
        }
        ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId), TYPE, 1, PER_PAGE, isHomeTimeline));

        return true;
    }




}
