package co.aquario.socialkit.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import co.aquario.socialkit.R;
import co.aquario.socialkit.activity.FullScreenVideoCacheActivity;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.util.DensityUtil;
import co.aquario.socialkit.widget.videocontroller.FullScreenVideoView;
import co.aquario.socialkit.widget.videocontroller.LightnessController;
import co.aquario.socialkit.widget.videocontroller.VolumnController;

/**
 * Created by Mac on 3/10/15.
 */
public class FullScreenVideoFragment extends BaseFragment implements View.OnClickListener{
    private static final String LOG_TAG = "FullScreenVideoCacheActivity";
    private static final String VIDEO_CACHE_NAME = "X4gPz_110559_60765d771b815d6faadf2f978fb8fcfe_ori.mp4";
    private String VIDEO_URL = "http://150.107.31.13:1935/live/youlove/playlist.m3u8";

    //private static final String VIDEO_URL = "http://stream-1.vdomax.com:1935/vod/__definst__/mp4:110559/110559_720p.mp4/playlist.m3u8";


    //private static final String VIDEO_URL ="https://www.vdomax.com/clips/2015/05/X4gPz_110559_60765d771b815d6faadf2f978fb8fcfe_ori.mp4";

    //private VideoView videoView;

    //private String videoUrl = "https://www.vdomax.com/clips/2015/05/X4gPz_110559_60765d771b815d6faadf2f978fb8fcfe_ori.mp4";



    private ProgressBar progressBar;
    // private HttpProxyCache proxyCache;

    private FullScreenVideoView mVideo;
    private View mTopView;
    private View mBottomView;
    private SeekBar mSeekBar;
    private ImageView mPlay;
    private TextView mPlayTime;
    private TextView mDurationTime;

    private AudioManager mAudioManager;

    private float width;
    private float height;

    private int playTime;


    private static final int HIDE_TIME = 5000;

    private VolumnController volumnController;

    private int orginalLight;

    String mName = "";
    String mUsername = "";
    TextView infoTv;
    ImageView fullscreen_btn;

    public static FullScreenVideoFragment newInstance(String url){
        FullScreenVideoFragment mFragment = new FullScreenVideoFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString("PATH", url);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            VIDEO_URL = getArguments().getString("PATH");

        //if (!LibsChecker.checkVitamioLibs(getActivity()))
        //return;

        Log.e("onCreate", VIDEO_URL);


    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_video_cache, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.setMax(100);
        fullscreen_btn = (ImageView) rootView.findViewById(R.id.fullscreen_btn);
        infoTv = (TextView) rootView.findViewById(R.id.info);
        infoTv.setText(mName);

        volumnController = new VolumnController(getActivity());
        mVideo = (FullScreenVideoView) rootView.findViewById(R.id.videoview);
        mPlayTime = (TextView) rootView.findViewById(R.id.play_time);
        mDurationTime = (TextView) rootView.findViewById(R.id.total_time);
        mPlay = (ImageView) rootView.findViewById(R.id.play_btn);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.seekbar);
        mTopView = rootView.findViewById(R.id.top_layout);
        mBottomView = rootView.findViewById(R.id.bottom_layout);

        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        width = DensityUtil.getWidthInPx(getActivity());
        height = DensityUtil.getHeightInPx(getActivity());
        threshold = DensityUtil.dip2px(getActivity(), 18);

        orginalLight = LightnessController.getLightness(getActivity());

        mPlay.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);

        playVideo(VIDEO_URL);

        fullscreen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), FullScreenVideoCacheActivity.class);
                i.putExtra("VIDEO_URL",VIDEO_URL);
                i.putExtra("NAME",mName);
                i.putExtra("USERNAME",mUsername);
                startActivity(i);
            }
        });

        return rootView;
    }
    private void playVideo(final String url) {

//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });

        //
        //Log.e("1111",url);

        if(url != null){
            mVideo.setVideoURI(Uri.parse(url));
            mVideo.setVideoPath(url);
            mVideo.requestFocus();
            mVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mVideo.setVideoWidth(mp.getVideoWidth());
                    mVideo.setVideoHeight(mp.getVideoHeight());

                    mVideo.start();
                    if (playTime != 0) {
                        mVideo.seekTo(playTime);
                    }

                    //mHandler.removeCallbacks(hideRunnable);
                    //mHandler.postDelayed(hideRunnable, HIDE_TIME);
                    mDurationTime.setText(formatTime(mVideo.getDuration()));
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            //         mHandler.sendEmptyMessage(1);
                        }
                    }, 0, 1000);
                }
            });
            mVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlay.setImageResource(R.drawable.video_btn_down);
                    mPlayTime.setText("00:00");
                    mSeekBar.setProgress(0);
                }
            });
            mVideo.setOnTouchListener(mTouchListener);
        }else{

        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            height = DensityUtil.getWidthInPx(getActivity());
            width = DensityUtil.getHeightInPx(getActivity());
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            width = DensityUtil.getWidthInPx(getActivity());
            height = DensityUtil.getHeightInPx(getActivity());
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // mHandler.removeMessages(0);
        // mHandler.removeCallbacksAndMessages(null);

//        if (proxyCache != null) {
//            proxyCache.shutdown();
//        }
    }
    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //mHandler.postDelayed(hideRunnable, HIDE_TIME);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //mHandler.removeCallbacks(hideRunnable);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (fromUser) {
                int time = progress * mVideo.getDuration() / 100;
                mVideo.seekTo(time);
            }
        }
    };

    private void backward(float delataX) {
        int current = mVideo.getCurrentPosition();
        int backwardTime = (int) (delataX / width * mVideo.getDuration());
        int currentTime = current - backwardTime;
        mVideo.seekTo(currentTime);
        mSeekBar.setProgress(currentTime * 100 / mVideo.getDuration());
        mPlayTime.setText(formatTime(currentTime));
    }

    private void forward(float delataX) {
        int current = mVideo.getCurrentPosition();
        int forwardTime = (int) (delataX / width * mVideo.getDuration());
        int currentTime = current + forwardTime;
        mVideo.seekTo(currentTime);
        mSeekBar.setProgress(currentTime * 100 / mVideo.getDuration());
        mPlayTime.setText(formatTime(currentTime));
    }

    private void volumeDown(float delatY) {
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int down = (int) (delatY / height * max * 3);
        int volume = Math.max(current - down, 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        int transformatVolume = volume * 100 / max;
        volumnController.show(transformatVolume);
    }

    private void volumeUp(float delatY) {
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int up = (int) ((delatY / height) * max * 3);
        int volume = Math.min(current + up, max);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        int transformatVolume = volume * 100 / max;
        volumnController.show(transformatVolume);
    }

    private void lightDown(float delatY) {
        int down = (int) (delatY / height * 255 * 3);
        int transformatLight = LightnessController.getLightness(getActivity()) - down;
        LightnessController.setLightness(getActivity(), transformatLight);
    }

    private void lightUp(float delatY) {
        int up = (int) (delatY / height * 255 * 3);
        int transformatLight = LightnessController.getLightness(getActivity()) + up;
        LightnessController.setLightness(getActivity(), transformatLight);
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (mVideo.getCurrentPosition() > 0) {
                        mPlayTime.setText(formatTime(mVideo.getCurrentPosition()));
                        int progress = mVideo.getCurrentPosition() * 100 / mVideo.getDuration();
                        mSeekBar.setProgress(progress);
                        if (mVideo.getCurrentPosition() > mVideo.getDuration() - 100) {
                            mPlayTime.setText("00:00");
                            mSeekBar.setProgress(0);
                        }
                        mSeekBar.setSecondaryProgress(mVideo.getBufferPercentage());
                    } else {
                        mPlayTime.setText("00:00");
                        mSeekBar.setProgress(0);
                    }

                    break;
                case 2:
                    if(getActivity() != null)
                    showOrHide();
                    break;

                default:
                    break;
            }
        }
    };
    private Runnable hideRunnable = new Runnable() {

        @Override
        public void run() {
            if(getActivity() != null)
            showOrHide();
        }
    };

    @SuppressLint("SimpleDateFormat")
    private String formatTime(long time) {
        DateFormat formatter = new SimpleDateFormat("mm:ss");
        return formatter.format(new Date(time));
    }

    private float mLastMotionX;
    private float mLastMotionY;
    private int startX;
    private int startY;
    private int threshold;
    private boolean isClick = true;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final float x = event.getX();
            final float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastMotionX = x;
                    mLastMotionY = y;
                    startX = (int) x;
                    startY = (int) y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = x - mLastMotionX;
                    float deltaY = y - mLastMotionY;
                    float absDeltaX = Math.abs(deltaX);
                    float absDeltaY = Math.abs(deltaY);
                    // 声音调节标识
                    boolean isAdjustAudio = false;
                    if (absDeltaX > threshold && absDeltaY > threshold) {
                        if (absDeltaX < absDeltaY) {
                            isAdjustAudio = true;
                        } else {
                            isAdjustAudio = false;
                        }
                    } else if (absDeltaX < threshold && absDeltaY > threshold) {
                        isAdjustAudio = true;
                    } else if (absDeltaX > threshold && absDeltaY < threshold) {
                        isAdjustAudio = false;
                    } else {
                        return true;
                    }
                    if (isAdjustAudio) {
                        if (x < width / 2) {
                            if (deltaY > 0) {
                                lightDown(absDeltaY);
                            } else if (deltaY < 0) {
                                lightUp(absDeltaY);
                            }
                        } else {
                            if (deltaY > 0) {
                                volumeDown(absDeltaY);
                            } else if (deltaY < 0) {
                                volumeUp(absDeltaY);
                            }
                        }

                    } else {
                        if (deltaX > 0) {
                            forward(absDeltaX);
                        } else if (deltaX < 0) {
                            backward(absDeltaX);
                        }
                    }
                    mLastMotionX = x;
                    mLastMotionY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(x - startX) > threshold
                            || Math.abs(y - startY) > threshold) {
                        isClick = false;
                    }
                    mLastMotionX = 0;
                    mLastMotionY = 0;
                    startX = (int) 0;
                    if (isClick) {
                        showOrHide();
                    }
                    isClick = true;
                    break;

                default:
                    break;
            }
            return true;
        }

    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_btn:
                if (mVideo.isPlaying()) {
                    mVideo.pause();
                    mPlay.setImageResource(R.drawable.video_btn_down);
                } else {
                    mVideo.start();
                    mPlay.setImageResource(R.drawable.video_btn_on);
                }
                break;
            default:
                break;
        }
    }

    private void showOrHide() {
        if (mTopView.getVisibility() == View.VISIBLE) {
            mTopView.clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.option_leave_from_top);
            animation.setAnimationListener(new AnimationImp() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    super.onAnimationEnd(animation);
                    mTopView.setVisibility(View.GONE);
                }
            });
            mTopView.startAnimation(animation);

            mBottomView.clearAnimation();
            Animation animation1 = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.option_leave_from_bottom);
            animation1.setAnimationListener(new AnimationImp() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    super.onAnimationEnd(animation);
                    mBottomView.setVisibility(View.GONE);
                }
            });
            mBottomView.startAnimation(animation1);
        } else {
            mTopView.setVisibility(View.VISIBLE);
            mTopView.clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.option_entry_from_top);
            mTopView.startAnimation(animation);

            mBottomView.setVisibility(View.VISIBLE);
            mBottomView.clearAnimation();
            Animation animation1 = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.option_entry_from_bottom);
            mBottomView.startAnimation(animation1);
            mHandler.removeCallbacks(hideRunnable);
            mHandler.postDelayed(hideRunnable, HIDE_TIME);
        }
    }

    private class AnimationImp implements Animation.AnimationListener {

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

    }


}
