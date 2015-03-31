package co.aquario.socialkit.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import co.aquario.socialkit.R;

public class PostVideoActivity extends Activity {

	Context context;

	private Uri mFileUri;
	VideoView mVideoView;
	MediaController mc;

	Button uploadButton;
	EditText status;
	EditText desc;

	AQuery aq;
	String POST_ID;

    String statusText;
    String descText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = this;
		aq = new AQuery(context);

		//getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_post_video);
		uploadButton = (Button) findViewById(R.id.button_recent);
		status = (EditText) findViewById(R.id.comment_box);
		desc = (EditText) findViewById(R.id.descText);



		Intent intent = getIntent();
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			uploadButton.setVisibility(View.GONE);
			//setTitle(R.string.action_post);
		}
		mFileUri = intent.getData();

		uploadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                statusText = status.getText().toString().replace("\n", "%0A");
                descText = desc.getText().toString().replace("\n", "%0A");

                launchUploadActivity(false);

				//startUploadVideoService();
			}
		});

		reviewVideo(mFileUri);
		//getActionBar().setTitle("โพสวิดีโอ");
	}

    private void launchUploadActivity(boolean isImage){
        Intent i = new Intent(PostVideoActivity.this, UploadClipReviewActivity.class);
        i.putExtra("filePath", getRealPathFromURI(mFileUri));
        i.putExtra("isImage", isImage);
        i.putExtra("title", statusText.toString());
        i.putExtra("desc", descText.toString());
        startActivity(i);
    }

	private void reviewVideo(Uri mFileUri) {
		try {
			mVideoView = (VideoView) findViewById(R.id.videoView);
			mc = new MediaController(this);
			mVideoView.setMediaController(mc);
			mVideoView.setVideoURI(mFileUri);
			mc.show();
			mVideoView.start();
		} catch (Exception e) {
			Log.e(this.getLocalClassName(), e.toString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}




	public String getRealPathFromURI(Uri contentUri) {

		String[] proj = { MediaStore.Video.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	ProgressDialog dialog;

	private void videoPost(String message, String desc, File file) {

        String url = "https://www.vdomax.com/ajax.php?t=post&a=new&user_id=6&token=123456&user_pass=039a726ac0aeec3dde33e45387a7d4ac";
        statusText = "test from android";

        // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("timeline_id","6");
        params.put("recipient_id","");
        params.put("text", statusText);
        params.put("clips[]", file);

		dialog = new ProgressDialog(context);
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.setInverseBackgroundForced(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setTitle("Uploading video...");
		dialog.setMessage("กำลังโพสวิดีโอ...");
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setIndeterminate(false);
		dialog.setMax(100);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				int incr;
				// Do the "lengthy" operation 20 times
				for (incr = 0; incr <= 100; incr += 20) {
					// Sets the progress indicator to a max value, the
					// current completion percentage, and "determinate"
					// state
					dialog.setIndeterminate(false);
					dialog.setMax(100);
					dialog.setProgress((int) incr);
					
					try {
						// Sleep for 5 seconds
						Thread.sleep(5 * 1000);
					} catch (InterruptedException e) {
						Log.d("LOGME", "sleep failure");
					}
				}
				// When the loop is finished, updates the notification
			
			}
		}
		// Starts the thread by calling the run() method in its Runnable
		).start();

		/*
		 * MyProgressBar mProgressBar = (MyProgressBar)
		 * findViewById(R.id.progress);
		 * 
		 * mProgressBar.setOnProgressListener(new OnProgressListener() {
		 * 
		 * @Override public void onProgress(int max, int progress) {
		 * 
		 * } });
		 */
		aq.progress(dialog)
				.ajax(url, params, JSONObject.class, this, "videoCb");
		// Toast.makeText(context,
		// "Uploading video. See notification when finish",Toast.LENGTH_LONG).show();
	}
	
	

	private void uploaded(String postId) {


	}


	private void notify(String ticker, String title, String message,
			Intent intent) {

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, ticker, when);

		int id = getNotifyId();

		PendingIntent contentIntent = PendingIntent.getActivity(this, id,
                intent, 0);

		notification.setLatestEventInfo(context, title, message,
				contentIntent);

		mNotificationManager.cancelAll();

		mNotificationManager.notify(id, notification);

	}

	private int getNotifyId() {
		return 123;
	}

	public void videoCb(String url, JSONObject jo, AjaxStatus status) {
		
		if( jo.optInt("status") == 4001) {
			dialog.setProgress(100);
			POST_ID = jo.optInt("id") + "";
			uploaded(POST_ID);
			
			Toast.makeText(context, "อัพโหลดสำเร็จแล้ว", Toast.LENGTH_SHORT).show();
			finish();
		}
		

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
