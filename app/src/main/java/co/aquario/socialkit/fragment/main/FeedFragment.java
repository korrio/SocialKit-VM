package co.aquario.socialkit.fragment.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.tumblr.bookends.Bookends;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import co.aquario.chatapp.ChatActivity;
import co.aquario.chatapp.picker.MusicPickerIntent;
import co.aquario.chatapp.picker.YoutubePickerActivity;
import co.aquario.socialkit.BaseActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.TakePhotoActivity2;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.activity.post.PostLiveStreamingActivity;
import co.aquario.socialkit.activity.post.PostStatusActivity2;
import co.aquario.socialkit.activity.search.SearchPagerFragment;
import co.aquario.socialkit.adapter.ButtonItemAdapter;
import co.aquario.socialkit.adapter.FeedAdapter;
import co.aquario.socialkit.event.FollowRegisterEvent;
import co.aquario.socialkit.event.FollowUserSuccessEvent;
import co.aquario.socialkit.event.GetStoryEvent;
import co.aquario.socialkit.event.GetStorySuccessEvent;
import co.aquario.socialkit.event.GetUserProfileEvent;
import co.aquario.socialkit.event.GetUserProfileSuccessEvent;
import co.aquario.socialkit.event.LoadHashtagStoryEvent;
import co.aquario.socialkit.event.LoadTimelineEvent;
import co.aquario.socialkit.event.LoadTimelineSuccessEvent;
import co.aquario.socialkit.event.PostCommentSuccessEvent;
import co.aquario.socialkit.event.PostLoveEvent;
import co.aquario.socialkit.event.PostLoveSuccessEvent;
import co.aquario.socialkit.event.PostShareEvent;
import co.aquario.socialkit.event.PostShareSuccessEvent;
import co.aquario.socialkit.event.RefreshEvent;
import co.aquario.socialkit.event.UnfollowUserSuccessEvent;
import co.aquario.socialkit.event.toolbar.SubTitleEvent;
import co.aquario.socialkit.event.toolbar.TitleEvent;
import co.aquario.socialkit.fragment.SettingFragment;
import co.aquario.socialkit.fragment.pager.HomeViewPagerFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.PostStory;
import co.aquario.socialkit.search.soundcloud.SoundCloudService;
import co.aquario.socialkit.util.PrefManager;
import co.aquario.socialkit.util.Utils;
import co.aquario.socialkit.widget.EndlessRecyclerOnScrollListener;
import co.aquario.socialkit.widget.RoundedTransformation;


public class FeedFragment extends BaseFragment implements BaseFragment.SearchListener {


    public static final String USER_ID = "USER_ID";
    public static final String POST_ID = "POST_ID";

    public static final String IS_HOMETIMELINE = "IS_HOMETIMELINE";
    public static final String IS_HASHTAG = "IS_HASHTAG";
    public static final String IS_STORY = "IS_STORY";
    public static final String IS_SEARCH = "IS_SEARCH";
    public static final String HASHTAG = "HASHTAG";

    public static final String STORY_LIST = "STORY_LIST";

    public static String HASHTAG_QUERY = "";

    private static String TYPE = "";
    private static int PER_PAGE = 20;
    public ArrayList<PostStory> list = new ArrayList<>();
    public FeedAdapter adapter;
    public Bookends<FeedAdapter> bAdapter;
    public RelativeLayout fabLayout;
    public RecyclerView mRecyclerView;
    PrefManager pref;

    // profile element
    int pShare;
    View myHeader;
    View rootView;
    ImageView avatar;
    ImageView cover;
    TextView titleTv;
    TextView usernameTv;
    //TextView bioTv;
    TextView countPost;
    TextView countFollowing;
    TextView countFollower;
    TextView countFriend;
    Button btnFollow;
    Button btnMessage;
    String imageTitle;
    String nameTitle;
    String coverUrl;

    // @InjectView(R.id.empty)
    //TextView mEmptyView;
    private boolean isRefreshable = false;
    private String username = "";
    private boolean isFollowing;
    private int currentPage = 1;
    private boolean isRefresh = false;
    private boolean isLoadding = false;
    private boolean isSearch = false;
    private String userId = "";
    private int postId;

    // home_timeline = including others post
    // user_timeline = only the user's post
    private boolean isHomeTimeline = false;
    private boolean isHashtag = false;
    private boolean isStory = false;
    private SwipeRefreshLayout swipeLayout;
    private FloatingActionButton postLive;
    private FloatingActionButton postPhotoBtn;
    private FloatingActionButton postVideoBtn;
    private FloatingActionButton postYoutubeBtn;
    private FloatingActionButton postSoundCloudBtn;
    private FloatingActionButton postStatusBtn;

    // music player
    private Toolbar mPlayerToolbar;
    private MediaPlayer mMediaPlayer;
    private ImageView mPlayerStateButton;
    private ProgressBar mProgressBar;

    public static FeedFragment newInstance(String userId, boolean isHomeTimeline,ArrayList<PostStory> storyList) {
        FeedFragment mFragment = new FeedFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(USER_ID, userId);
        mBundle.putBoolean(IS_HOMETIMELINE, isHomeTimeline);
        mBundle.putBoolean(IS_HASHTAG, false);
        mBundle.putBoolean(IS_STORY,false);
        mBundle.putBoolean(IS_SEARCH,true);
        mBundle.putParcelable(STORY_LIST, Parcels.wrap(storyList));
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    public static FeedFragment newInstance(String userId, boolean isHomeTimeline) {
        FeedFragment mFragment = new FeedFragment();
		Bundle mBundle = new Bundle();
		mBundle.putString(USER_ID, userId);
        mBundle.putBoolean(IS_HOMETIMELINE, isHomeTimeline);
        mBundle.putBoolean(IS_HASHTAG, false);
        mBundle.putBoolean(IS_STORY,false);
        mBundle.putBoolean(IS_SEARCH,false);
        mFragment.setArguments(mBundle);
		return mFragment;
	}

    public static FeedFragment newInstance(String hashtag) {
        FeedFragment mFragment = new FeedFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(HASHTAG, hashtag);
        mBundle.putBoolean(IS_HOMETIMELINE, true);
        mBundle.putBoolean(IS_HASHTAG, true);
        mBundle.putBoolean(IS_STORY,false);
        mBundle.putBoolean(IS_SEARCH,false);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    public static FeedFragment newInstance(int postId) {
        FeedFragment mFragment = new FeedFragment();
        Bundle mBundle = new Bundle();
        mBundle.putInt(POST_ID, postId);
        mBundle.putBoolean(IS_HOMETIMELINE, false);
        mBundle.putBoolean(IS_HASHTAG, false);
        mBundle.putBoolean(IS_STORY,true);
        mBundle.putBoolean(IS_SEARCH,false);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    SearchView mSearchView;
    Toolbar toolbar;

    String oldQuery = "Search..";

    void setupToolbar() {
        toolbar = ((BaseActivity) getActivity()).getToolbar();
        if(toolbar != null && getActivity().getLocalClassName().equals("MainActivity")) {
            toolbar.getMenu().clear();
            toolbar.setTitle("VDOMAX");
            toolbar.inflateMenu(R.menu.menu_main);

            mSearchView = (SearchView) toolbar.getMenu().findItem(R.id.action_search).getActionView();
            mSearchView.setQueryHint(oldQuery);
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    Utils.hideKeyboard(getActivity());
                    SearchPagerFragment fragment = new SearchPagerFragment().newInstance(VMApp.mPref.userId().getOr("0"),s);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "MAIN_SEARCH").addToBackStack(null).commit();
                    toolbar.setTitle("Search: " + s);
                    oldQuery = s;
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });

            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
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
                            TYPE = "clip";
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

                        case R.id.action_message:
                            ChatActivity.startChatActivity(getActivity(), Integer.parseInt(pref.userId().getOr("0")), Integer.parseInt(userId), 0);
                            break;
                        case R.id.action_edit_profile:
                            SettingFragment fragment = SettingFragment.newInstance(userId);
                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.add(R.id.sub_container, fragment).addToBackStack(null);
                            transaction.commit();
                            break;


                        default:
                            //isRefresh = true;
                            break;
                    }
                    //myTitanicTextView.setVisibility(View.VISIBLE);
                    switch (item.getItemId()) {
                        case R.id.action_search:

                        case R.id.menu_sort_all:

                        case R.id.menu_sort_text:

                        case R.id.menu_sort_photo:

                        case R.id.menu_sort_video:

                        case R.id.menu_sort_youtube:

                        case R.id.menu_sort_soundcloud:

                        case R.id.menu_sort_place:

                        case R.id.menu_sort_live:
                            list.clear();
                            bAdapter.notifyDataSetChanged();
                            adapter.notifyDataSetChanged();
                            if(!isHashtag)
                                swipeLayout.setRefreshing(true);

                            ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId), TYPE, 1, PER_PAGE, isHomeTimeline));
                            break;
                    }


                    return false;
                }
            });
        }

    }

    public FeedFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        pref = VMApp.get(getActivity().getApplicationContext()).getPrefManager();
        if (getArguments() != null) {

            if(!getArguments().getBoolean(IS_SEARCH)) {
                isStory = getArguments().getBoolean(IS_STORY);

                if(!getArguments().getBoolean(IS_STORY)) {
                    if(getArguments().getBoolean(IS_HASHTAG)) {
                        // HASHTAG
                        isHashtag = true;
                        isHomeTimeline = true;
                        HASHTAG_QUERY = getArguments().getString(HASHTAG);

                        userId = pref.userId().getOr("0");

                        ApiBus.getInstance().post(new LoadHashtagStoryEvent(Integer.parseInt(userId), TYPE, 1, PER_PAGE, isHomeTimeline,HASHTAG_QUERY));
                        ApiBus.getInstance().postQueue(new TitleEvent("#" + HASHTAG_QUERY));
                        ApiBus.getInstance().postQueue(new SubTitleEvent(""));

                    } else {
                        userId = getArguments().getString(USER_ID);
                        isHashtag = false;
                        isHomeTimeline = getArguments().getBoolean(IS_HOMETIMELINE);


                        if(!Utils.isNumeric(userId)) {
                            // USER_TIMELINE -> @mention
                            username = userId;
                            isHomeTimeline = false;
                            ApiBus.getInstance().post(new GetUserProfileEvent(username, true));
                        } else {
                            ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId),TYPE,1,PER_PAGE,isHomeTimeline));
                            if(!isHomeTimeline) // USER_TIMELINE -> userId
                            {
                                ApiBus.getInstance().post(new GetUserProfileEvent(userId));
                            }
                        }



                    }
                } else {
                    // POST_ID
                    postId = getArguments().getInt(POST_ID);

                    isHomeTimeline = true;
                    ApiBus.getInstance().postQueue(new TitleEvent("VDOMAX"));
                    ApiBus.getInstance().postQueue(new GetStoryEvent("" + postId));


                    //ApiBus.getInstance().postQueue(new SubTitleEvent("PostID:" + postId));
                }
            } else {
                isSearch = true;
                isHomeTimeline = true;
                userId = getArguments().getString(USER_ID);
                list = Parcels.unwrap(getArguments().getParcelable(STORY_LIST));

            }


        } else {
            userId = pref.userId().getOr("0");
            isHomeTimeline = true;
            ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId),TYPE,1,PER_PAGE,isHomeTimeline));
            //((MainActivity) getActivity()).getToolbar().setSubtitle("");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        if(isSearch) {
            adapter.notifyDataSetChanged();
        }

    }

    @Subscribe public void onGetStoryEventSuccess(GetStorySuccessEvent event) {
        if(isRefresh)
            list.clear();
        isRefresh = false;
        swipeLayout.setRefreshing(false);

        list.add(event.getPost());

        bAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        if(!isHashtag)
            swipeLayout.setRefreshing(false);
        isLoadding = false;
    }

    //animation
    View rootViewAnimation;
    int drawingStartLocation = 0;
    private void startIntroAnimation() {
        //ViewCompat.setElevation(getToolbar(), 0);
        swipeLayout.setScaleY(0.1f);
        swipeLayout.setPivotY(drawingStartLocation);
        showFloatingButton();

        swipeLayout.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //ViewCompat.setElevation(getToolbar(), Utils.dpToPx(8));
                        //animateContent();
                    }
                })
                .start();
    }

    void initSCMediaPlayer() {
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
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        this.rootView = rootView;

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                if(!isHashtag)
                    ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId), TYPE, 1, PER_PAGE, isHomeTimeline));

            }
        });

        swipeLayout.setRefreshing(true);

        fabLayout = (RelativeLayout) rootView.findViewById(R.id.layoutMenu);

        // animation
        rootViewAnimation = rootView.findViewById(R.id.root_view);
        if (savedInstanceState == null) {
            swipeLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    swipeLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }

        // Create your views, whatever they may be
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvFeed);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(linearLayoutManager);

        myHeader = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.fragment_profile, mRecyclerView, false);

        setEmptyText(getString(R.string.no_story));

        initSCMediaPlayer();


        postLive = (FloatingActionButton) fabLayout.findViewById(R.id.action_live_stream);
        postPhotoBtn = (FloatingActionButton) fabLayout.findViewById(R.id.action_photo);
        postVideoBtn = (FloatingActionButton) fabLayout.findViewById(R.id.action_video);
        postYoutubeBtn = (FloatingActionButton) fabLayout.findViewById(R.id.action_youtube);
        postSoundCloudBtn = (FloatingActionButton) fabLayout.findViewById(R.id.action_soundcloud);
        postStatusBtn = (FloatingActionButton) fabLayout.findViewById(R.id.action_write_post);

        postLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), PostLiveStreamingActivity.class);
                startActivity(i);
            }
        });

        postSoundCloudBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getActivity(), MusicPickerActivity.class);
//                startActivity(i);
                MusicPickerIntent musicPickerIntent = new MusicPickerIntent(getActivity());
                musicPickerIntent.setAction("post");
                startActivityForResult(musicPickerIntent, 300);
                //intent.setPhotoCount(9);
                //intent.setShowCamera(true);
                //getActivity().startActivityForResult(musicPickerIntent, 500);
            }
        });

        postYoutubeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent youtubePicker = new Intent(getActivity(), YoutubePickerActivity.class);
                youtubePicker.setAction("post");
                startActivityForResult(youtubePicker, 400);

            }
        });

        postPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((BaseActivity) getActivity()).buildPhotoDialog();
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                startingLocation[0] += v.getWidth() / 2;
                TakePhotoActivity2.startCameraFromLocation(startingLocation, getActivity(), false);
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
                ((BaseActivity) getActivity()).selectVideo();
            }
        });

        postStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PostStatusActivity2.class);
                i.putExtra("IS_MYHOMETIMELINE",(userId.equals(VMApp.mPref.userId().getOr("0"))));
                i.putExtra("USER_ID", userId);
                startActivityForResult(i,100);

            }
        });


        // load more
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page) {
                currentPage = page;
                isRefresh = false;
                if (!isLoadding && !isHashtag)
                    ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId), TYPE, page, PER_PAGE, isHomeTimeline));
                isLoadding = true;
                Log.e("thispageis", page + "");
            }
        });

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
                pShare = position;
                Log.e("postId", pShare + "");
                buildShare2Dialog();
            }
        });

        // Add them as headers / footers
        bAdapter = new Bookends<>(adapter);
        bAdapter.addHeader(myHeader);

        if (!isHomeTimeline) {
            mRecyclerView.setAdapter(bAdapter);
        } else {
            mRecyclerView.setAdapter(adapter);
        }

		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        return rootView;
    }

    @Subscribe public void onPostShareSuccess(PostShareSuccessEvent event) {
        Utils.showToast("Post shared to your VDOMAX");
    }

    public void buildShare2Dialog() {
        final CharSequence[] items = {"FACEBOOK", "VDOMAX"};

        final String postId = list.get(pShare).id;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Share to");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == 0) {
                    shareToFacebook(postId + "");
                    //recordVideo();
                } else if (i == 1) {
                    ApiBus.getInstance().postQueue(new PostShareEvent(userId, postId + ""));
                    //pickVideo();
                } else if (i == 2) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void shareToFacebook(String postId){

        String url = "https://graph.facebook.com/me/feed";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("message", "กำลังเล่นสนุกอยู่ที่ VDOMAX (Live Streaming) https://goo.gl/jBF5cI");
        //params.put("link","https://www.vdomax.com/story/16693");
        //https://www.vdomax.com/story/195854
        params.put("link","https://www.vdomax.com/story/" + postId);

        //Simply put a byte[] to the params, AQuery will detect it and treat it as a multi-part post
        //byte[] data = getImageData(getResources().getDrawable(R.drawable.com_parse_ui_facebook_login_logo));
        //params.put("source", data);

        //Alternatively, put a File or InputStream instead of byte[]
        //File file = getImageFile();
        //params.put("source", file);

        AQuery aq = new AQuery(getActivity());
        aq.auth(VMApp.getFacebookHandle(getActivity())).ajax(url, params, JSONObject.class, this, "shareFb");
    }

    public void shareFb(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        Log.e("hahaha", jo.toString(4));
        Utils.showToast("Post shared to Facebook complete!");
    }

    public byte[] getImageData(Drawable d) {
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        return bitmapdata;
    }

    public void buildShareDialog() {
        new MaterialDialog.Builder(getActivity())
                .title("Share to")
                .adapter(new ButtonItemAdapter(getActivity(),R.array.socialNetworks),
                        new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                switch (which) {
                                    case 0:
                                        // Share to vdomax
                                        ApiBus.getInstance().post(new PostShareEvent(pref.userId().getOr("6"), list.get(pShare).postId));

                                        break;
                                    case 1:
                                        shareToFacebook(list.get(pShare).postId);
//                                        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
//                                        shareIntent.setType("text/plain");
//                                        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,  "https://www.vdomax.com/story/" + list.get(pShare).id);
//                                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, list.get(pShare).text);
//                                        shareIntent.putExtra(Intent.EXTRA_STREAM, "https://www.vdomax.com/story/" + list.get(pShare).id);
//
//                                        PackageManager pm = getActivity().getPackageManager();
//                                        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
//                                        for (final ResolveInfo app : activityList)
//                                        {
//                                            if ((app.activityInfo.name).contains("facebook"))
//                                            {
//                                                final ActivityInfo activity = app.activityInfo;
//                                                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
//                                                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//                                                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                                                shareIntent.setComponent(name);
//                                                getActivity().startActivity(shareIntent);
//                                                break;
//                                            }
//                                        }
                                        break;
                                    default:
                                        break;

                                }
                                dialog.dismiss();

                            }
                        })
                .theme(Theme.LIGHT)
                .show();
    }

    public void showMenuBar() {
        AnimatorSet animSet = new AnimatorSet();

        HomeViewPagerFragment fragment = (HomeViewPagerFragment) getParentFragment();

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(fragment.mSlidingTabLayout
                , View.TRANSLATION_Y, 0);

        ObjectAnimator anim2 = ObjectAnimator.ofFloat(toolbar
                , View.TRANSLATION_Y, 0);

        //ObjectAnimator anim3 = ObjectAnimator.ofFloat(layoutHeader
          //      , View.TRANSLATION_Y, 0);

        animSet.playTogether(anim1, anim2);
        animSet.setDuration(300);
        animSet.start();
    }

    public void hideMenuBar() {
        AnimatorSet animSet = new AnimatorSet();

        HomeViewPagerFragment fragment = (HomeViewPagerFragment) getParentFragment();

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(fragment.mSlidingTabLayout
                , View.TRANSLATION_Y, -fragment.mSlidingTabLayout.getHeight());

        ObjectAnimator anim2 = ObjectAnimator.ofFloat(toolbar
                , View.TRANSLATION_Y, -toolbar.getHeight());

        //ObjectAnimator anim3 = ObjectAnimator.ofFloat(layoutHeader
          //      , View.TRANSLATION_Y, -layoutHeader.getHeight() * 2);

        animSet.playTogether(anim1, anim2);
        animSet.setDuration(300);
        animSet.start();
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

    //fix crash on hashtag click in nested fragment
    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();




       // myTitanicTextView.setVisibility(View.GONE);

    }

    @Subscribe public void onLoadTimelineSuccess(LoadTimelineSuccessEvent event) {
        if(isRefresh)
            list.clear();
        isRefresh = false;
        swipeLayout.setRefreshing(false);

        list.addAll(event.getTimelineData().getPosts());

        bAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
        if(!isHashtag)
            swipeLayout.setRefreshing(false);
        isLoadding = false;
       // myTitanicTextView.setVisibility(View.GONE);

    }

//    @Subscribe public void onLogout(LogoutEvent event) {
//        VMApp.logout();
//        Intent login = new Intent(getActivity(), LoginActivity.class);
//        startActivity(login);
//        getActivity().finish();
//    }

    @Subscribe public void onPostLoveSuccessEvent(PostLoveSuccessEvent event) {
        Utils.showToast("Loved");
    }

    @Subscribe public void onPostShareSuccessEvent(PostShareSuccessEvent event) {
        Utils.showToast("Shared");
    }

    @Subscribe public void onPostCommentSuccessEvent(PostCommentSuccessEvent event) {
        Utils.showToast("Comment success.");
    }

    String profileName = "";

    @Subscribe
    public void onLoadProfile(final GetUserProfileSuccessEvent event) {

        userId = event.getUser().getId();

        if(!username.equals(""))
            ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId),TYPE,1,PER_PAGE,isHomeTimeline));

        btnFollow = (Button) myHeader.findViewById(R.id.btn_follow);
        btnMessage = (Button) myHeader.findViewById(R.id.btn_message);

        avatar = (ImageView) myHeader.findViewById(R.id.user_avatar);
        cover = (ImageView) myHeader.findViewById(R.id.user_cover);

        titleTv = (TextView) myHeader.findViewById(R.id.user_name);
        //usernameTv = (TextView) myHeader.findViewById(R.id.user_username);
        //bioTv = (TextView) myHeader.findViewById(R.id.user_des);

        /*
        if(Utils.isTablet(getActivity()))
            bioTv.setVisibility(View.VISIBLE);
        else
            bioTv.setVisibility(View.GONE);
            */

        profileName = event.getUser().getName();
        username = event.getUser().getUsername();

        titleTv.setText(Html.fromHtml(event.getUser().getName()));
        //usernameTv.setText("@" + event.getUser().getUsername());
//        bioTv.setText(Html.fromHtml(event.getUser().getAbout()));

        if (Html.fromHtml(event.getUser().getName()).toString().length() >= 0) {
            ApiBus.getInstance().postQueue(new TitleEvent(event.getUser().getName()));
            ApiBus.getInstance().postQueue(new SubTitleEvent("@" + event.getUser().getUsername()));
        }

        Picasso.with(getActivity())
                .load(event.getUser().getCoverUrl())
                .fit().centerCrop()
                .into(cover);

        Picasso.with(getActivity())
                .load(event.getUser().getAvatarUrl())
                .centerCrop()
                .resize(200, 200)
                .transform(new RoundedTransformation(100, 2))
                .into(avatar);

        if(userId.equals(VMApp.mPref.userId().getOr("")))
            btnFollow.setVisibility(View.GONE);

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



        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatActivity.startChatActivity(getActivity(), Integer.parseInt(pref.userId().getOr("0")), Integer.parseInt(userId),0);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
        if(requestCode == 100)
            Utils.showToast("Post complete!");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Subscribe
    public void onFollowSuccess(FollowUserSuccessEvent event) {
        Utils.showToast("Follow " + event.getUserId() + " complete !");
    }

    @Subscribe
    public void onUnfollowSuccess(UnfollowUserSuccessEvent event) {
        Utils.showToast("Unfollow " + event.getUserId() + " complete !");
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
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

        if(mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            toggleProgressBar();

            String url = uri + "?client_id=" + SoundCloudService.CLIENT_ID;

            Log.e("souncloudUrl", url);

            try {
                mMediaPlayer.setDataSource(uri + "?client_id=" + SoundCloudService.CLIENT_ID);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            initSCMediaPlayer();
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            toggleProgressBar();

            String url = uri + "?client_id=" + SoundCloudService.CLIENT_ID;

            Log.e("souncloudUrl", url);

            try {
                mMediaPlayer.setDataSource(uri + "?client_id=" + SoundCloudService.CLIENT_ID);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                TYPE = "clip";
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
                //isRefresh = true;
                break;
        }
        //myTitanicTextView.setVisibility(View.VISIBLE);
        switch (item.getItemId()) {
            case R.id.action_search:

            case R.id.menu_sort_all:

            case R.id.menu_sort_text:

            case R.id.menu_sort_photo:

            case R.id.menu_sort_video:

            case R.id.menu_sort_youtube:

            case R.id.menu_sort_soundcloud:

            case R.id.menu_sort_place:

            case R.id.menu_sort_live:
                list.clear();
                bAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
                if(!isHashtag)
                    swipeLayout.setRefreshing(true);
                ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId), TYPE, 1, PER_PAGE, isHomeTimeline));
                break;
        }



        return true;
    }


    @Override
    public void onSearchQuery(String query) {
        Utils.hideKeyboard(getActivity());
        SearchPagerFragment fragment = new SearchPagerFragment().newInstance(VMApp.mPref.userId().getOr("0"),query);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "MAIN_SEARCH").addToBackStack(null).commit();

        //SearchActivity.startActivity(getApplicationContext(),query);
    }
}
