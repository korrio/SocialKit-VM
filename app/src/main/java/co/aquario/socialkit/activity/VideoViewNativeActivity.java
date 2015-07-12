package co.aquario.socialkit.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import co.aquario.socialkit.VMApplication;
import co.aquario.socialkit.R;
import co.aquario.socialkit.util.EndpointManager;
import co.aquario.socialkit.util.PrefManager;
import de.hdodenhof.circleimageview.CircleImageView;


public class VideoViewNativeActivity extends Activity {

    String vidAddress = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
    boolean loaded = false;
    private String path = "";
    private VideoView mVideoView;
    private EditText mEditText;
    private Button button1;
    private Button start;
    private CircleImageView mProfileImageView;
    private ImageView mImageView;
    private TextView mProfileNameTextView;
    private ImageView mLogo;
    private MediaPlayer mediaPlayer;
    private LinearLayout.LayoutParams paramsNotFullscreen; //if you're using RelativeLatout

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        //if (!LibsChecker.checkVitamioLibs(this))
          //  return;

        if(getIntent() != null)
            path = getIntent().getExtras().getString("url");
        else
            path = "";

        Log.e("myurl",path);

        setContentView(R.layout.activity_video_view_native);
        mVideoView = (VideoView) findViewById(R.id.surface_view);

        mImageView = (ImageView) findViewById(R.id.image);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //slideOnClick();
            }
        });
        mProfileImageView = (CircleImageView) findViewById(R.id.profile_image);
        mProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //slideOnClick();
            }
        });
        mProfileNameTextView = (TextView) findViewById(R.id.profile_name);
        mLogo = (ImageView) findViewById(R.id.logo);

        /*
        button1.setOnClickListener(toolbar View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideo("http://server-a.vdomax.com:8080/record/Nuchiko-260115_20:55:13.flv");
            }
        });

        start.setOnClickListener(toolbar View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideo("rtmp://150.107.31.12:1935/live/korrio");
            }
        });
        */

        PrefManager pref = VMApplication.get(this).getPrefManager();

        String avatarUrl = pref.avatar().getOr("");
        String coverUrl = pref.cover().getOr("");
        String name = pref.name().getOr("");
        String userId = pref.userId().getOr("6");

        setTitle(name);
        //mTitle.setText(name);
        Picasso.with(this).load(EndpointManager.getAvatarPath(coverUrl)).into(mImageView);
        Picasso.with(this).load(EndpointManager.getAvatarPath(avatarUrl)).into(mProfileImageView);


        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                path = path.replace("_ori.mp4",".mp4");
                if(!loaded)
                    openVideo(path);
                loaded = true;
                return false;
            }
        });

        if (path.equals("")) {
            // Tell the user to provide a media file URL/path.
            path = "http://server-a.vdomax.com:8080/record/Nuchiko-260115_20:55:13.flv";
            //Toast.makeText(VideoViewFragment.this, "Please edit VideoViewDemo Activity, and set path" + " variable to your media file URL/path", Toast.LENGTH_LONG).show();
            mVideoView.setVideoPath(path);
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.requestFocus();

            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    //mediaPlayer.setPlaybackSpeed(1.0f);
                }
            });

            //return;
        } else {
			/*
			 * Alternatively,for streaming media you can use
			 * mVideoView.setVideoURI(Uri.parse(URLstring));
			 */
            mVideoView.setVideoPath(path);
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.requestFocus();

            //mVideoView.addTimedTextSource(Environment.getExternalStorageDirectory() + "/12.srt");

            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    //mediaPlayer.setPlaybackSpeed(1.0f);
                }
            });
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);


        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) //To fullscreen
        {
            paramsNotFullscreen = (LinearLayout.LayoutParams) mVideoView.getLayoutParams();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(paramsNotFullscreen);
            params.setMargins(0, 0, 0, 0);
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            mVideoView.setLayoutParams(params);

            mLogo.setVisibility(View.GONE);
            mProfileImageView.setVisibility(View.GONE);
            mProfileNameTextView.setVisibility(View.GONE);


        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            mVideoView.setLayoutParams(paramsNotFullscreen);
            mLogo.setVisibility(View.VISIBLE);
            mProfileImageView.setVisibility(View.VISIBLE);
            mProfileNameTextView.setVisibility(View.VISIBLE);
        }
    }

    public void startPlay() {
        String url = mEditText.getText().toString();
        path = url;
        if (!TextUtils.isEmpty(url)) {
            mVideoView.setVideoPath(url);
        }
    }

    public void openVideo(String mypath) {
        mVideoView.setVideoPath(mypath);
    }


}