package org.appspot.apprtc;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.appspot.apprtc.util.RoundedTransformation;
import org.webrtc.VideoRendererGui;

public class CallFragmentUI extends Fragment {
    private View controlView;
    private TextView contactView;
    private ImageButton disconnectButton;
    private ImageButton cameraSwitchButton;
    private ImageButton videoScalingButton;
    private OnCallEvents callEvents;
    private VideoRendererGui.ScalingType scalingType;
    private boolean videoCallEnabled = true;
    private TextView mTimeLabel, mTimerLabel;
    private long mStartTime = 0L;
    private Handler mHandler = new Handler();
    String timerStop1;
    ImageView mAvatar;
    Bundle args;

    /**
     * Call control interface for container activity.
     */
    public interface OnCallEvents {
        public void onCallHangUp();

        public void onCameraSwitch();

        public void onVideoScalingSwitch(VideoRendererGui.ScalingType scalingType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        controlView =
                inflater.inflate(R.layout.fragment_call_ui, container, false);

        mTimerLabel = (TextView) controlView.findViewById(R.id.textTimer);
        args = getArguments();
        // Create UI controls.
        contactView =
                (TextView) controlView.findViewById(R.id.contact_name_call);
        disconnectButton =
                (ImageButton) controlView.findViewById(R.id.button_call_disconnect);
        cameraSwitchButton =
                (ImageButton) controlView.findViewById(R.id.button_call_switch_camera);
        videoScalingButton =
                (ImageButton) controlView.findViewById(R.id.button_call_scaling_mode);
        mAvatar = (ImageView) controlView.findViewById(R.id.imageView2);


        // Add buttons click events.
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callEvents.onCallHangUp();
            }
        });

        cameraSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callEvents.onCameraSwitch();
            }
        });

        videoScalingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scalingType == VideoRendererGui.ScalingType.SCALE_ASPECT_FILL) {
                    videoScalingButton.setBackgroundResource(
                            R.drawable.ic_action_full_screen);
                    scalingType = VideoRendererGui.ScalingType.SCALE_ASPECT_FIT;
                } else {
                    videoScalingButton.setBackgroundResource(
                            R.drawable.ic_action_return_from_full_screen);
                    scalingType = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL;
                }
                callEvents.onVideoScalingSwitch(scalingType);
            }
        });
        scalingType = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL;

        return controlView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mStartTime = SystemClock.uptimeMillis();
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, 100);


        if (args != null) {
            String contactName = args.getString(CallActivity.EXTRA_ROOMID);

            contactView.setText(contactName);
            videoCallEnabled = args.getBoolean(CallActivity.EXTRA_VIDEO_CALL, true);
            mTimerLabel.setVisibility(View.GONE);
            mAvatar.setVisibility(View.GONE);
        }
        if (!videoCallEnabled) {
            cameraSwitchButton.setVisibility(View.INVISIBLE);
            mTimerLabel.setVisibility(View.VISIBLE);
            mAvatar.setVisibility(View.VISIBLE);
            String pAvatar = args.getString("avatar");
            Log.e("6666", pAvatar);
            Picasso.with(getActivity())
                    .load(pAvatar)
                    .centerCrop()
                    .resize(200, 200)
                    .transform(new RoundedTransformation(100, 4))
                    .into(mAvatar);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callEvents = (OnCallEvents) activity;
    }

    private Runnable mUpdateTimeTask = new Runnable() {

        public void run() {

            final long start = mStartTime;
            long millis = SystemClock.uptimeMillis() - start;

            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            mTimerLabel.setText("" + minutes + ":"
                    + String.format("%02d", seconds));

            timerStop1 = minutes + ":"
                    + String.format("%02d", seconds);

            mHandler.postDelayed(this, 200);

        }
    };


}
