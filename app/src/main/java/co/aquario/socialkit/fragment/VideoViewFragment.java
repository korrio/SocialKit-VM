package co.aquario.socialkit.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import co.aquario.socialkit.R;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


public class VideoViewFragment extends Fragment {

    private String path = "";
    private VideoView mVideoView;
    private EditText mEditText;
    private static final String URL = "urlLive";

    public static VideoViewFragment newInstance(String url){
        VideoViewFragment mFragment = new VideoViewFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(URL, url);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LibsChecker.checkVitamioLibs(getActivity()))
            return;
        if (getArguments() != null)
            path = getArguments().getString(URL);
        else
            path = "";


    }

    

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_view, container, false);

        mVideoView = (VideoView) rootView.findViewById(R.id.surface_view);




        if (path.equals("")) {
            // Tell the user to provide a media file URL/path.
            path = "http://server-a.vdomax.com:8080/record/Nuchiko-260115_20:55:13.flv";
            //Toast.makeText(VideoViewFragment.this, "Please edit VideoViewDemo Activity, and set path" + " variable to your media file URL/path", Toast.LENGTH_LONG).show();
            mVideoView.setVideoPath(path);
            mVideoView.setMediaController(new MediaController(getActivity()));
            mVideoView.requestFocus();

            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    mediaPlayer.setPlaybackSpeed(1.0f);
                }
            });

            //return;
        } else {
    mVideoView.setVideoPath(path);
            mVideoView.setMediaController(new MediaController(getActivity()));
            mVideoView.requestFocus();



            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    mediaPlayer.setPlaybackSpeed(1.0f);
                }
            });
        }

        return rootView;
    }

    private LinearLayout.LayoutParams paramsNotFullscreen; //if you're using RelativeLatout

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
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


        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mVideoView.setLayoutParams(paramsNotFullscreen);

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