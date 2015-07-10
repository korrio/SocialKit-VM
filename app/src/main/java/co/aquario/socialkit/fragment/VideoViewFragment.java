/*
 * Copyright (C) 2013 yixia.com
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


//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//
//import co.aquario.socialkit.R;
//import io.vov.vitamio.LibsChecker;
//import io.vov.vitamio.MediaPlayer;
//import io.vov.vitamio.widget.MediaController;
//import io.vov.vitamio.widget.VideoView;
//
//public class VideoViewFragment extends Fragment {
//
//	/**
//	 * TODO: Set the path variable to a streaming video URL or a local media file
//	 * path.
//	 */
//	private String path = "";
//	private VideoView mVideoView;
//	private EditText mEditText;
//
//	public static VideoViewFragment newInstance(String url){
//		VideoViewFragment mFragment = toolbar VideoViewFragment();
//		Bundle mBundle = toolbar Bundle();
//		mBundle.putString("PATH", url);
//		mFragment.setArguments(mBundle);
//		return mFragment;
//	}
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		if (getArguments() != null)
//			path = getArguments().getString("PATH");
//
//		if (!LibsChecker.checkVitamioLibs(getActivity()))
//			return;
//
//		Log.e("onCreate", path);
//
//	}
//
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		View rootView = inflater.inflate(R.layout.videoview, container, false);
//		//mEditText = (EditText) rootView.findViewById(R.id.url);
//		mVideoView = (VideoView) rootView.findViewById(R.id.surface_view);
//		if (path == "") {
//			// Tell the user to provide a media file URL/path.
//			//Toast.makeText(VideoViewDemo.this, "Please edit VideoViewDemo Activity, and set path" + " variable to your media file URL/path", Toast.LENGTH_LONG).show();
//			//return;
//		} else {
//			/*
//			 * Alternatively,for streaming media you can use
//			 * mVideoView.setVideoURI(Uri.parse(URLstring));
//			 */
//			mVideoView.setVideoPath(path);
//			mVideoView.setMediaController(toolbar MediaController(getActivity()));
//			mVideoView.requestFocus();
//
//			mVideoView.setOnPreparedListener(toolbar MediaPlayer.OnPreparedListener() {
//				@Override
//				public void onPrepared(MediaPlayer mediaPlayer) {
//					// optional need Vitamio 4.0
//					mediaPlayer.setPlaybackSpeed(1.0f);
//				}
//			});
//		}
//
//		return rootView;
//
//	}
//
//	public void startPlay(View view) {
//	    String url = mEditText.getText().toString();
//	    path = url;
//	    if (!TextUtils.isEmpty(url)) {
//	        mVideoView.setVideoPath(url);
//	    }
//    }
//
//	public void openVideo(View View) {
//	  mVideoView.setVideoPath(path);
//	}
//
//}
