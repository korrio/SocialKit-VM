package co.aquario.socialkit.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;

import co.aquario.socialkit.MainApplication;
import co.aquario.socialkit.R;
import co.aquario.socialkit.activity.LoginActivity;
import co.aquario.socialkit.activity.MainActivity;
import co.aquario.socialkit.activity.PostStatusActivity;
import co.aquario.socialkit.activity.SearchYoutubeActivity;
import co.aquario.socialkit.activity.SoundCloudActivity;
import co.aquario.socialkit.activity.TakePhotoActivity;
import co.aquario.socialkit.adapter.FeedAdapter;
import co.aquario.socialkit.event.LoadTimelineEvent;
import co.aquario.socialkit.event.LoadTimelineSuccessEvent;
import co.aquario.socialkit.event.LogoutEvent;
import co.aquario.socialkit.event.PostCommentSuccessEvent;
import co.aquario.socialkit.event.PostLoveEvent;
import co.aquario.socialkit.event.PostLoveSuccessEvent;
import co.aquario.socialkit.event.PostShareEvent;
import co.aquario.socialkit.event.PostShareSuccessEvent;
import co.aquario.socialkit.event.RefreshEvent;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.PostStory;
import co.aquario.socialkit.soundcloud.SoundCloudService;
import co.aquario.socialkit.util.PrefManager;
import co.aquario.socialkit.widget.EndlessRecyclerOnScrollListener;
import jp.wasabeef.recyclerview.animators.ScaleInBottomAnimator;


public class FeedFragment extends BaseFragment {

    private boolean mSearchCheck;
    public static final String USER_ID = "USER_ID";

    public ArrayList<PostStory> list = new ArrayList<>();
    public FeedAdapter adapter;
    public RelativeLayout layoutMenu;
    private int currentPage = 1;
    private boolean isRefresh = false;
    private boolean isLoadding = false;

    public static FeedFragment newInstance(String userId){
        FeedFragment mFragment = new FeedFragment();
		Bundle mBundle = new Bundle();
		mBundle.putString(USER_ID, userId);
		mFragment.setArguments(mBundle);
		return mFragment;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(USER_ID);
        } else {
            userId = prefManager.userId().getOr("1301");
        }
    }

    private SwipeRefreshLayout swipeLayout;

    private FloatingActionButton postPhotoBtn;
    private FloatingActionButton postVideoBtn;
    private FloatingActionButton postYoutubeBtn;
    private FloatingActionButton postSoundCloudBtn;
    private FloatingActionButton postStatusBtn;
    String userId;

    // home_timeline = including others post
    // user_timeline = only the user's post
    private boolean isHomeTimeline = true;
    private static final String TYPE = "";
    private static final int PER_PAGE = 20;

    PrefManager pref;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        /*
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId),TYPE,1,PER_PAGE,isHomeTimeline));
                //String loadMoreUrl = "http://api.vdomax.com/search/channel/a?from=0&limit=10";
                //aq.ajax(loadMoreUrl, JSONObject.class, fragment, "getJson");
                //Log.e("5555","onRefresh");
            }
        });
        */

    }

    private Toolbar mPlayerToolbar;
    private MediaPlayer mMediaPlayer;
    private ImageView mPlayerStateButton;
    private ProgressBar mProgressBar;

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
        //mPlayerToolbar.setVisibility(View.VISIBLE);
        if (mMediaPlayer.isPlaying()){
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

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub		
		View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        pref = MainApplication.get(getActivity()).getPrefManager();
        userId = pref.userId().getOr("6");

        Log.e("userId",userId);

        layoutMenu = (RelativeLayout) rootView.findViewById(R.id.layoutMenu);
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


        //playTrack("https://api.soundcloud.com/tracks/140000410/stream");


        postPhotoBtn = (FloatingActionButton) layoutMenu.findViewById(R.id.action_photo);
        postVideoBtn = (FloatingActionButton) layoutMenu.findViewById(R.id.action_video);
        postYoutubeBtn = (FloatingActionButton) layoutMenu.findViewById(R.id.action_youtube);
        postSoundCloudBtn = (FloatingActionButton) layoutMenu.findViewById(R.id.action_soundcloud);
        postStatusBtn = (FloatingActionButton) layoutMenu.findViewById(R.id.action_write_post);

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
                Intent i = new Intent(getActivity(), SearchYoutubeActivity.class);
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
                TakePhotoActivity.startCameraFromLocation(startingLocation, getActivity(),false);
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
                Intent i = new Intent(getActivity(), PostStatusActivity.class);
                startActivity(i);

            }
        });


        //aq = new AQuery(getActivity());

        adapter = new FeedAdapter(getActivity(), list,this);

        adapter.SetOnItemClickListener(new FeedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });


        adapter.OnItemLoveClick(new FeedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                ApiBus.getInstance().post(new PostLoveEvent(pref.userId().getOr("6"),list.get(position).postId));
            }
        });

        adapter.OnItemShareClick(new FeedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                ApiBus.getInstance().post(new PostShareEvent(pref.userId().getOr("6"),list.get(position).postId));
            }
        });

        RecyclerView recList = (RecyclerView) rootView.findViewById(R.id.scroll);
        recList.setHasFixedSize(true);

        //animate recyclerview
        recList.setItemAnimator(new ScaleInBottomAnimator());
        recList.getItemAnimator().setAddDuration(1000);
        recList.getItemAnimator().setRemoveDuration(1000);
        recList.getItemAnimator().setMoveDuration(1000);
        recList.getItemAnimator().setChangeDuration(1000);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // load more
        recList.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page) {
                currentPage = page;
                isRefresh = false;
                if (!isLoadding)
                    ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId), TYPE, page, PER_PAGE, isHomeTimeline));
                isLoadding = true;
                Log.e("thispageis",page + "");
            }
        });

        recList.setLayoutManager(linearLayoutManager);
        recList.setAdapter(adapter);

        recList.setOnTouchListener(new View.OnTouchListener() {

            final int DISTANCE = 3;

            float startY = 0;
            float dist = 0;
            boolean isMenuHide = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    startY = event.getY();
                } else if (action == MotionEvent.ACTION_MOVE) {
                    dist = event.getY() - startY;

                    if ((pxToDp((int) dist) <= -DISTANCE) && !isMenuHide) {
                        isMenuHide = true;
                        hideFloatingButton();
                    } else if ((pxToDp((int) dist) > DISTANCE) && isMenuHide) {
                        isMenuHide = false;
                        showFloatingButton();
                    }

                    if ((isMenuHide && (pxToDp((int) dist) <= -DISTANCE))
                            || (!isMenuHide && (pxToDp((int) dist) > 0))) {
                        startY = event.getY();
                    }
                } else if (action == MotionEvent.ACTION_UP) {
                    startY = 0;
                }

                return false;
            }
        });


        ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId),TYPE,1,PER_PAGE,isHomeTimeline));
        //ApiBus.getInstance().post(new LoadTimelineEvent(32,"photo",1,50));
        //aq.ajax(urlMain, JSONObject.class, this, "getJson");

		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return rootView;		
	}

    @Subscribe public void onRefreshFeed(RefreshEvent event) {
        ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId),TYPE,1,PER_PAGE,isHomeTimeline));
        Log.e("onRefreshFeed","true");
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
        adapter.notifyDataSetChanged();
        //swipeLayout.setRefreshing(false);
        isLoadding = false;

    }

    @Subscribe public void onLogout(LogoutEvent event) {
        MainApplication.logout();
        Intent login = new Intent(getActivity(), LoginActivity.class);
        startActivity(login);
        getActivity().finish();
    }

    @Subscribe public void onPostLoveSuccessEvent(PostLoveSuccessEvent event) {
        Toast.makeText(getActivity(), "Loved", Toast.LENGTH_SHORT).show();

    }

    @Subscribe public void onPostShareSuccessEvent(PostShareSuccessEvent event) {
        Toast.makeText(getActivity(), "Shared", Toast.LENGTH_SHORT).show();
    }

    @Subscribe public void onPostCommentSuccessEvent(PostCommentSuccessEvent event) {
        //Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
    }

    /*

    @Subscribe void onLoadTimelineFailed() {

    }

    @Subscribe void onLoadTimelineFailedNetwork() {

    }

    */
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.menu, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        searchView.setQueryHint("Search friends,tags,videos");

        ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                .setHintTextColor(getResources().getColor(android.R.color.white));
        searchView.setOnQueryTextListener(onQuerySearchView);

		//menu.findItem(R.id.menu_add).setVisible(true);
		menu.findItem(R.id.menu_search).setVisible(true);
		mSearchCheck = false;

	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_search:
			mSearchCheck = true;
			break;
		}		
		return true;
	}

   private SearchView.OnQueryTextListener onQuerySearchView = new SearchView.OnQueryTextListener() {
       @Override
       public boolean onQueryTextSubmit(String s) {
           return false;
       }

       @Override
       public boolean onQueryTextChange(String s) {
           if (mSearchCheck){
               // implement your search here
           }
           return false;
       }
   };

    public int pxToDp(int px) {
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        int dp = Math.round(px / (dm.densityDpi
                / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public void showFloatingButton() {
        AnimatorSet animSet = new AnimatorSet();

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(layoutMenu
                , View.TRANSLATION_Y, 0);

        animSet.playTogether(anim1);
        animSet.setDuration(300);
        animSet.start();
    }

    public void hideFloatingButton() {
        AnimatorSet animSet = new AnimatorSet();

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(layoutMenu
                , View.TRANSLATION_Y, layoutMenu.getHeight());

        animSet.playTogether(anim1);
        animSet.setDuration(300);
        animSet.start();
    }


}
