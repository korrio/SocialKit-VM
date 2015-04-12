/*
 * This file is part of Popcorn Time.
 *
 * Popcorn Time is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Popcorn Time is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Popcorn Time. If not, see <http://www.gnu.org/licenses/>.
 */

package co.aquario.socialkit.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;

import java.net.URLDecoder;

import co.aquario.socialkit.R;
import co.aquario.socialkit.fragment.VideoPlayerFragment;
import co.aquario.socialkit.youtube.YouTubeData;


public class VideoTrailerPlayerActivity extends BaseActivity implements VideoPlayerFragment.Callback {

    //private Media mMedia;
    private String mLocation;
    private VideoPlayerFragment mVideoPlayerFragment;

    public static Intent startActivity(Activity activity, String youTubeUrl) {
        Intent i = new Intent(activity, VideoPlayerActivity.class);
        //i.putExtra(DATA, data);
        i.putExtra(LOCATION, youTubeUrl);
        activity.startActivity(i);
        return i;
    }

    public final static String LOCATION = "stream_url";
    public final static String DATA = "video_data";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_videoplayer);

        //mMedia = getIntent().getParcelableExtra(DATA);
        //mMedia.title += " " + getString(R.string.trailer);
        String youTubeId = getIntent().getStringExtra(LOCATION);

        mVideoPlayerFragment = (VideoPlayerFragment) getSupportFragmentManager().findFragmentById(R.id.video_fragment);
        mVideoPlayerFragment.enableSubsButton(false);

        QueryYouTubeTask youTubeTask = new QueryYouTubeTask();
        //youTubeTask.execute(YouTubeData.getYouTubeVideoId(youTubeUrl));
        youTubeTask.execute(youTubeId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //@Override
    //public Media getData() {
    //    return mMedia;
    //}

    @Override
    public String getQuality() {
        return null;
    }

    @Override
    public String getSubtitles() {
        return null;
    }

    @Override
    public String getLocation() {
        return mLocation;
    }

    private class QueryYouTubeTask extends AsyncTask<String, Void, Uri> {

        private boolean mShowedError = false;

        @Override
        protected Uri doInBackground(String... params) {
            String uriStr = null;
            String quality = "17";   // 3gpp medium quality, which should be fast enough to view over EDGE connection
            String videoId = params[0];

            if (isCancelled())
                return null;

            try {
                WifiManager wifiManager = (WifiManager) VideoTrailerPlayerActivity.this.getSystemService(Context.WIFI_SERVICE);
                TelephonyManager telephonyManager = (TelephonyManager) VideoTrailerPlayerActivity.this.getSystemService(Context.TELEPHONY_SERVICE);

                // if we have a fast connection (wifi or 3g), then we'll get a high quality YouTube video
                if (wifiManager.isWifiEnabled() && wifiManager.getConnectionInfo() != null && wifiManager.getConnectionInfo().getIpAddress() != 0) {
                    quality = "22";
                } else if (telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED &&
                        (
                                telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS ||
                                        telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSUPA ||
                                        telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSPA ||
                                        telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_HSDPA ||
                                        telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_EVDO_0 ||
                                        telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_EVDO_A
                        )
                        ) {
                    quality = "18";
                }

                if (isCancelled())
                    return null;

                ////////////////////////////////////
                // calculate the actual URL of the video, encoded with proper YouTube token
                uriStr = YouTubeData.calculateYouTubeUrl(quality, true, videoId);

                if (isCancelled())
                    return null;

            } catch (Exception e) {
                Log.e(this.getClass().getSimpleName(), "Error occurred while retrieving information from YouTube.", e);
            }

            if (uriStr != null) {
                return Uri.parse(uriStr);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Uri result) {
            super.onPostExecute(result);

            try {
                if (isCancelled())
                    return;

                if (result == null) {
                    throw new RuntimeException("Invalid NULL Url.");
                }

                mLocation = URLDecoder.decode(result.toString());
                mVideoPlayerFragment.loadMedia();
            } catch (Exception e) {
                Log.e(this.getClass().getSimpleName(), "Error playing video!", e);

                if (!mShowedError) {
                    showErrorAlert();
                }
            }
        }

        private void showErrorAlert() {
            try {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VideoTrailerPlayerActivity.this);
                alertDialogBuilder.setTitle(R.string.comm_error);
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setMessage(R.string.comm_message);

                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        VideoTrailerPlayerActivity.this.finish();
                    }
                });

                AlertDialog lDialog = alertDialogBuilder.create();
                lDialog.show();
            } catch (Exception e) {
                Log.e(this.getClass().getSimpleName(), "Problem showing error dialog.", e);
            }
        }

        @Override
        protected void onProgressUpdate(Void... pValues) {
            super.onProgressUpdate(pValues);
        }

    }

}