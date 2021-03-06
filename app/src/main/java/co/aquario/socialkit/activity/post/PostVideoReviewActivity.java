package co.aquario.socialkit.activity.post;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.androidquery.callback.AjaxStatus;
import com.dd.CircularProgressButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import co.aquario.socialkit.MainActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.event.upload.ClipPostUploadEvent;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.util.Utils;
import retrofit.mime.TypedFile;


public class PostVideoReviewActivity extends Activity {
	// LogCat tag
	private static final String TAG = MainActivity.class.getSimpleName();

	private ProgressBar progressBar;
	private String filePath = null;
    private String title = "";
    private String desc = "";
	private TextView txtPercentage;
	private ImageView imgPreview;
	private VideoView vidPreview;
	private CircularProgressButton btnUpload;
    ProgressDialog dialog;
	long totalSize = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		txtPercentage = (TextView) findViewById(R.id.txtPercentage);
		btnUpload = (CircularProgressButton) findViewById(R.id.btnUpload);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		imgPreview = (ImageView) findViewById(R.id.imgPreview);
		vidPreview = (VideoView) findViewById(R.id.videoPreview);

        prepareDialog();

		// Changing action bar background color
		//getActionBar().setBackgroundDrawable(
				//toolbar ColorDrawable(Color.parseColor(getResources().getString(
				//		R.color.action_bar))));

		// Receiving the data from previous activity
		Intent i = getIntent();

		// image or video path that is captured in previous activity
		filePath = i.getStringExtra("filePath");
        title = i.getStringExtra("title");
        desc = i.getStringExtra("desc");

		// boolean flag to identify the media type, image or video
		boolean isImage = i.getBooleanExtra("isImage", true);

		if (filePath != null) {
			// Displaying the image or video on the screen
			previewMedia(isImage);
		} else {
			Toast.makeText(getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
		}

        btnUpload.setIndeterminateProgressMode(true); // turn on indeterminate progress

        btnUpload.setProgress(0); // set progress to 0 to switch back to normal state

		btnUpload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
                btnUpload.setEnabled(false);
                String fromUserId = VMApp.mPref.userId().getOr("0");
                uploadPost(title, fromUserId, "");
				// uploading the file to server
                //btnUpload.setProgress(30);

				//new UploadFileToServer().execute();
			}
		});

	}

    void prepareDialog() {
        dialog = new ProgressDialog(getApplicationContext());
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(false);
        //dialog.setTitle(getString(R.string.uploading));
        //dialog.setMessage(getString(R.string.waiting));
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setIndeterminate(false);
        dialog.setMax(100);
    }

    String url = "https://www.vdomax.com/ajax.php?t=post&a=toolbar&user_id=6&token=123456&user_pass=039a726ac0aeec3dde33e45387a7d4ac";

    private void uploadPost(String text,String fromUserId, String toUserId) {
        //String url = "http://chat.vdomax.com/upload";
        File sourceFile = new File(filePath);
        uploadPostRetrofit(sourceFile,text,fromUserId,toUserId);
    }

    public void uploadCb(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        Log.e("hahahaha", jo.toString(4));
        if(jo.optInt("status") == 200) {
            Intent backIntent = new Intent();
            setResult(-1, backIntent);
            //startActivity(backIntent);
            finish();
        }

    }



    private void uploadPostRetrofit(File file, String text, String fromUserId, String toUserId) {
        //FileUploadService service = ServiceGenerator.createService(FileUpload.class, FileUpload.BASE_URL);

        //PostUploadService service = VMApp.buildUploadApi();
        TypedFile typedFile = new TypedFile("multipart/form-data", file);
        ApiBus.getInstance().postQueue(new ClipPostUploadEvent(text, fromUserId, toUserId, typedFile));

        Utils.showToast("Uploading video. See notification when finish");

        //finishPosting();

            Intent intent = new Intent(PostVideoReviewActivity.this,
                    MainActivity.class);
            startActivity(intent);

    }

	/**
	 * Displaying captured image/video on the screen
	 * */
	private void previewMedia(boolean isImage) {
		// Checking whether captured media is image or video
		if (isImage) {
			imgPreview.setVisibility(View.VISIBLE);
			vidPreview.setVisibility(View.GONE);
			// bimatp factory
			BitmapFactory.Options options = new BitmapFactory.Options();

			// down sizing image as it throws OutOfMemory Exception for larger
			// images
			options.inSampleSize = 8;

			final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

			imgPreview.setImageBitmap(bitmap);
		} else {
			imgPreview.setVisibility(View.GONE);
			vidPreview.setVisibility(View.VISIBLE);
			vidPreview.setVideoPath(filePath);
			// start playing
			vidPreview.start();
		}
	}

}