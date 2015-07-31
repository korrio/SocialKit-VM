/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.aquario.socialkit.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.VideoSurfaceView;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioCapabilitiesReceiver;
import com.google.android.exoplayer.util.Util;

import co.aquario.socialkit.R;
import co.aquario.socialkit.activity.FullScreenVideoCacheActivity;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.widget.exoplayer.DemoPlayer;
import co.aquario.socialkit.widget.exoplayer.HlsRendererBuilder;

/**
 * An activity that plays media using {@link DemoPlayer}.
 */
public class ExoSurfaceFragment extends BaseFragment implements SurfaceHolder.Callback, OnClickListener,
    DemoPlayer.Listener,
    AudioCapabilitiesReceiver.Listener {

  private MediaController mediaController;
  private VideoSurfaceView surfaceView;

  private DemoPlayer player;
  private boolean playerNeedsPrepare;
  private long playerPosition;

  private String VIDEO_URL;

  private AudioCapabilitiesReceiver audioCapabilitiesReceiver;
  private AudioCapabilities audioCapabilities;

  public static ExoSurfaceFragment newInstance(String url){
    ExoSurfaceFragment mFragment = new ExoSurfaceFragment();
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

  // Activity lifecycle

    TextView statusText;
    ImageView btnFullscreen;

    private View mTopView;
    private View mBottomView;

    private float mLastMotionX;
    private float mLastMotionY;
    private int startX;
    private int startY;
    private int threshold;
    private boolean isClick = true;

    String mUsername;
    String mName;

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.activity_simple_exo_player, container, false);

      mTopView = rootView.findViewById(R.id.top_layout);
      mBottomView = rootView.findViewById(R.id.bottom_layout);

    //VIDEO_URL = "http://stream-1.vdomax.com:1935/vod/__definst__/mp4:youlove/youlove_xxx_7043.mp4/playlist.m3u8";
    statusText = (TextView) rootView.findViewById(R.id.status);
      btnFullscreen = (ImageView) rootView.findViewById(R.id.fullscreen_btn);
      btnFullscreen.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent fullscreenIntent = new Intent(getActivity(), FullScreenVideoCacheActivity.class);
              fullscreenIntent.putExtra("VIDEO_URL", VIDEO_URL);
              fullscreenIntent.putExtra("USERNAME", mUsername);
              fullscreenIntent.putExtra("NAME", mName);
              startActivity(fullscreenIntent);
              //releasePlayer();
          }
      });
    View root = rootView.findViewById(R.id.root);
    root.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent event) {

          final float x = event.getX();
          final float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mLastMotionX = x;
            mLastMotionY = y;
            startX = (int) x;
            startY = (int) y;
          //toggleControlsVisibility();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          view.performClick();
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
        }
        return true;
      }
    });
    root.setOnKeyListener(new OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
          return mediaController.dispatchKeyEvent(event);
        }
        return false;
      }
    });
    audioCapabilitiesReceiver = new AudioCapabilitiesReceiver(getActivity().getApplicationContext(), this);


    surfaceView = (VideoSurfaceView) rootView.findViewById(R.id.surface_view);
    surfaceView.getHolder().addCallback(this);

    mediaController = new MediaController(getActivity());
    mediaController.setAnchorView(surfaceView);

    preparePlayer();

    return rootView;

    //DemoUtil.setDefaultCookieManager();
  }

  @Override
  public void onResume() {
    super.onResume();

      preparePlayer();
    //configureSubtitleView();

    // The player will be prepared on receiving audio capabilities.
    audioCapabilitiesReceiver.register();
  }

  @Override
  public void onPause() {
    super.onPause();
    if (!true) {
      releasePlayer();
    } else {
      player.setBackgrounded(true);
        player.release();
    }
    audioCapabilitiesReceiver.unregister();

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    releasePlayer();
  }

  // OnClickListener methods

  @Override
  public void onClick(View view) {
    //if (view == retryButton) {
      //preparePlayer();
    //}
  }

  // AudioCapabilitiesReceiver.Listener methods

  @Override
  public void onAudioCapabilitiesChanged(AudioCapabilities audioCapabilities) {
    boolean audioCapabilitiesChanged = !audioCapabilities.equals(this.audioCapabilities);
    if (player == null || audioCapabilitiesChanged) {
      this.audioCapabilities = audioCapabilities;
      releasePlayer();
      preparePlayer();
    } else if (player != null) {
      player.setBackgrounded(false);
    }
  }

  // Internal methods



  private void preparePlayer() {

    if (player == null) {
      String userAgent = Util.getUserAgent(getActivity(), "ExoPlayerDemo");
      player = new DemoPlayer(new HlsRendererBuilder(getActivity(), userAgent, VIDEO_URL.toString(), null,
              audioCapabilities));
      player.addListener(this);
      player.seekTo(playerPosition);
      playerNeedsPrepare = true;
      //mediaController.setMediaPlayer(player.getPlayerControl());
      //mediaController.setEnabled(false);

    }
    if (playerNeedsPrepare) {
      player.prepare();
      playerNeedsPrepare = false;
      updateButtonVisibilities();
    }
    player.setSurface(surfaceView.getHolder().getSurface());
    player.setPlayWhenReady(true);
  }

  private void releasePlayer() {
    if (player != null) {
      playerPosition = player.getCurrentPosition();
      player.release();
      player = null;
      //eventLogger.endSession();
      //eventLogger = null;
    }
  }

  // DemoPlayer.Listener implementation

  @Override
  public void onStateChanged(boolean playWhenReady, int playbackState) {
    if (playbackState == ExoPlayer.STATE_ENDED) {
      //showControls();
    }
    String text = "playWhenReady=" + playWhenReady + ", playbackState=";
    switch(playbackState) {
      case ExoPlayer.STATE_BUFFERING:
        text += "buffering";
        break;
      case ExoPlayer.STATE_ENDED:
        text += "ended";
        break;
      case ExoPlayer.STATE_IDLE:
        text += "idle";
        break;
      case ExoPlayer.STATE_PREPARING:
        text += "preparing";
        break;
      case ExoPlayer.STATE_READY:
        text += "ready";
        break;
      default:
        text += "unknown";
        break;
    }

      //if(statusText != null)
          //statusText.setText(text);


    //playerStateTextView.setText(text);
    updateButtonVisibilities();
  }

  @Override
  public void onError(Exception e) {

    playerNeedsPrepare = true;
    updateButtonVisibilities();
    //showControls();
  }

  @Override
  public void onVideoSizeChanged(int width, int height, float pixelWidthAspectRatio) {
    //shutterView.setVisibility(View.GONE);
    surfaceView.setVideoWidthHeightRatio(
        height == 0 ? 1 : (width * pixelWidthAspectRatio) / height);
  }

  // User controls

  private void updateButtonVisibilities() {
    //retryButton.setVisibility(playerNeedsPrepare ? View.VISIBLE : View.GONE);
    //videoButton.setVisibility(haveTracks(DemoPlayer.TYPE_VIDEO) ? View.VISIBLE : View.GONE);
    //audioButton.setVisibility(haveTracks(DemoPlayer.TYPE_AUDIO) ? View.VISIBLE : View.GONE);
    //textButton.setVisibility(haveTracks(DemoPlayer.TYPE_TEXT) ? View.VISIBLE : View.GONE);
  }


//  private void toggleControlsVisibility()  {
//    if (mediaController.isShowing()) {
//      mediaController.hide();
//      //debugRootView.setVisibility(View.GONE);
//    } else {
//      showControls();
//    }
//  }
//
//  private void showControls() {
//    mediaController.show(0);
//    //debugRootView.setVisibility(View.VISIBLE);
//  }

  // DemoPlayer.TextListener implementation


  // SurfaceHolder.Callback implementation

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    if (player != null) {
      player.setSurface(holder.getSurface());
    }
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    // Do nothing.
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    if (player != null) {
      player.blockingClearSurface();
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
            //mHandler.removeCallbacks(hideRunnable);
            //mHandler.postDelayed(hideRunnable, HIDE_TIME);
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
