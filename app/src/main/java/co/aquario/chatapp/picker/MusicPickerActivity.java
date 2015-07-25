package co.aquario.chatapp.picker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.aquario.socialkit.R;
import co.aquario.socialkit.activity.post.PostSoundCloudActivity;
import co.aquario.socialkit.search.soundcloud.MusicTrack;
import co.aquario.socialkit.search.soundcloud.SoundCloud;
import co.aquario.socialkit.search.soundcloud.SoundCloudService;
import co.aquario.socialkit.search.soundcloud.TracksAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MusicPickerActivity extends Activity implements SearchView.OnQueryTextListener{


    private TracksAdapter mAdapter;
    private List<MusicTrack> mMusicTracks;
    private TextView mSelectedTitle;
    private TextView mSelectedSubtitle;
    private ImageView mSelectedThumbnail;
    private MediaPlayer mMediaPlayer;
    private Toolbar mPlayerToolbar;
    private ImageView mPlayerStateButton;
    private ProgressBar mProgressBar;
    private SearchView mSearchView;
    private List<MusicTrack> mPreviousMusicTracks;
    String from;

    Context context;
    SoundCloudService service ;

    private Toolbar toolbar;
    void setupToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        if(toolbar != null) {
//            setSupportActionBar(toolbar);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setTitle("Find Music");
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_soundcloud);

        setupToolbar();

        context = this;

        if(getIntent() !=null) {
            from = getIntent().getAction();
            Log.e("from",from);
        }

        final Button btnSearch = (Button) findViewById(R.id.sc_search_btn);
        final EditText searchEt = (EditText) findViewById(R.id.sc_query);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = searchEt.getText().toString();
                searchTrack(query);
            }
        });

        mPlayerToolbar = (Toolbar)findViewById(R.id.player_toolbar);
        mMediaPlayer = new MediaPlayer();
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

        mSelectedTitle = (TextView)findViewById(R.id.selected_title);
        mSelectedSubtitle = (TextView) findViewById(R.id.selected_subtitle);
        mSelectedThumbnail = (ImageView)findViewById(R.id.selected_thumbnail);

        mPlayerStateButton = (ImageView)findViewById(R.id.player_state);
        mPlayerStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSongState();
            }
        });
        mProgressBar = (ProgressBar)findViewById(R.id.player_progress);
        mProgressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.songs_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMusicTracks = new ArrayList<MusicTrack>();
        mAdapter = new TracksAdapter(this, mMusicTracks);

        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        MusicTrack selectedMusicTrack = mMusicTracks.get(position);
                        mSelectedTitle.setText(selectedMusicTrack.mTitle);
                        mSelectedSubtitle.setText(selectedMusicTrack.user.username);
                        Picasso.with(MusicPickerActivity.this).load(selectedMusicTrack.getArtworkURL()).into(mSelectedThumbnail);

                        if (mMediaPlayer.isPlaying()) {
                            mMediaPlayer.stop();
                        }
                        mMediaPlayer.reset();
                        toggleProgressBar();
                        mPlayerToolbar.setVisibility(View.VISIBLE);

                        try {
                            mMediaPlayer.setDataSource(selectedMusicTrack.mStreamURL + "?client_id=" + SoundCloudService.CLIENT_ID);
                            mMediaPlayer.prepareAsync();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (from.equals("post")) {
                            Intent i = new Intent(getApplicationContext(), PostSoundCloudActivity.class);
                            i.putExtra("soundcloud_uri", selectedMusicTrack.mStreamURL);
                            i.putExtra("soundcloud_title", selectedMusicTrack.mTitle);
                            i.putExtra("soundcloud_subtitle", selectedMusicTrack.user.username);
                            i.putExtra("artwork_url", selectedMusicTrack.getArtworkURL());

                            startActivity(i);
                            finish();
                        } else {
                            Intent i = new Intent();
                            i.putExtra("soundcloud_uri", selectedMusicTrack.mStreamURL);
                            i.putExtra("soundcloud_title", selectedMusicTrack.mTitle);
                            i.putExtra("artwork_url", selectedMusicTrack.getArtworkURL());
                            //i.putExtra("LOCATION",marker.)
                            setResult(-1, i);
                            finish();
                        }
                    }
                })
        );

        service = SoundCloud.getService();
        searchTrack("bodyslam");

        /*
        service.getRecentSongs(toolbar SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(toolbar Date()), toolbar Callback<List<Track>>() {
            @Override
            public void success(List<Track> tracks, Response response) {
               updateTracks(tracks);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed call: " + error.toString());
            }
        });
        */
    }

    public void searchTrack(String query) {
        service.searchSongs(query, new Callback<List<MusicTrack>>() {
            @Override
            public void success(List<MusicTrack> musicTracks, Response response) {
                updateTracks(musicTracks);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("fail", "Failed call: " + error.toString());
            }
        });
    }

    private void updateTracks(List<MusicTrack> musicTracks){
        mMusicTracks.clear();
        mMusicTracks.addAll(musicTracks);
        mAdapter.notifyDataSetChanged();
        Log.e("heyhey", mMusicTracks.size() + "");
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchView.clearFocus();
        SoundCloud.getService().searchSongs(query, new Callback<List<MusicTrack>>() {
            @Override
            public void success(List<MusicTrack> musicTracks, Response response) {
                updateTracks(musicTracks);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), "Something went wrong..", Toast.LENGTH_LONG).show();
            }
        } );
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mMediaPlayer != null){
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the search; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setOnQueryTextListener(this);
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), new MenuItemCompat.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mPreviousMusicTracks = new ArrayList<MusicTrack>(mMusicTracks);
                mSearchView.setIconified(false);
                mSearchView.requestFocus();
                Log.d("expand ", " expand" + mSearchView.isFocused());
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                updateTracks(mPreviousMusicTracks);
                mSearchView.clearFocus();
                Log.d("collapse ", " expand" + mSearchView.isFocused());
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

