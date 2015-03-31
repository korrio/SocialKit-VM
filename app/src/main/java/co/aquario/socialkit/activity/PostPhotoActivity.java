package co.aquario.socialkit.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.aquario.socialkit.MainApplication;
import co.aquario.socialkit.R;
import co.aquario.socialkit.util.AndroidMultiPartEntity;
import co.aquario.socialkit.util.PathManager;
import co.aquario.socialkit.util.PrefManager;

public class PostPhotoActivity extends Activity implements OnClickListener {

	Context context;

	Button post_photo;
	ImageView imageView;
	EditText photoText;

	File tempFile;

	AQuery aq;

    public static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";
    private Uri photoUri;
    private int photoSize;


    public static void openWithPhotoUri(Activity openingActivity, Uri photoUri) {
        Intent intent = new Intent(openingActivity, PostPhotoActivity.class);
        intent.putExtra(ARG_TAKEN_PHOTO_URI, photoUri);
        openingActivity.startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_TAKEN_PHOTO_URI, photoUri);
    }

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_photo);

		//String path = getIntent().getExtras().getString("photo");
		//String rotate = getIntent().getExtras().getString("rotate");

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            photoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size_tablet);;
        } else {
            photoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size);;
        }

        if (savedInstanceState == null) {
            photoUri = getIntent().getParcelableExtra(ARG_TAKEN_PHOTO_URI);

        } else {
            photoUri = savedInstanceState.getParcelable(ARG_TAKEN_PHOTO_URI);

        }

        tempFile = new File(PathManager.getPath(context, photoUri));

		context = this;
		aq = new AQuery(context);

		post_photo = (Button) findViewById(R.id.button_recent);
        photoText = (EditText) findViewById(R.id.comment_box);
		imageView = (ImageView) findViewById(R.id.image);

        imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                loadThumbnailPhoto();
                return true;
            }
        });

		post_photo.setOnClickListener(this);

	}

    private void loadThumbnailPhoto() {
        imageView.setScaleX(0);
        imageView.setScaleY(0);
        Picasso.with(this)
                .load(photoUri)
                .centerCrop()
                .resize(photoSize, photoSize)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageView.animate()
                                .scaleX(1.f).scaleY(1.f)
                                .setInterpolator(new OvershootInterpolator())
                                .setDuration(400)
                                .setStartDelay(200)
                                .start();
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent upIntent = new Intent(this, MainActivity.class);
			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				TaskStackBuilder.from(this).addNextIntent(upIntent)
						.startActivities();
				finish();
			} else {
				NavUtils.navigateUpTo(this, upIntent);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    public String url = "https://www.vdomax.com/ajax.php?t=post&a=new&user_id=6&token=123456&user_pass=039a726ac0aeec3dde33e45387a7d4ac";
    public long totalSize;
    public ProgressDialog dialog;
    public String statusText;

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.button_recent) {
            statusText = photoText.getText().toString()
                    .replace("\n", "%0A");

            Pattern pattern = Pattern.compile("\\s");
            Matcher matcher = pattern.matcher(statusText);
            boolean found = matcher.find();
            boolean isWhitespace = statusText.matches("^\\s*$");

            if (statusText.length() == 0 || statusText.trim().equals("") || found || isWhitespace) {
                //photoText.setError("กรุณาพิมพ์ข้อความก่อนส่ง");
                Log.e("YEAH", statusText.length() + " " + statusText.trim() + " " + found + " " + isWhitespace);
            }

            statusText = photoText.getText().toString();

			dialog = new ProgressDialog(context);
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.setInverseBackgroundForced(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setTitle("Uploading");
			dialog.setMessage("กำลังอัพโหลดรูปภาพ..");
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setIndeterminate(false);
			dialog.setMax(100);

            new UploadFileToServer().execute();

		}

	}

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            dialog.show();
            dialog.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            //progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            dialog.setProgress(progress[0]);

            // updating percentage value
            dialog.setTitle(String.valueOf(progress[0]) + "%");
            //dialog.setText();
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                                dialog.setProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = tempFile;

                PrefManager pref = MainApplication.get(getApplicationContext()).getPrefManager();
                String userId = pref.userId().getOr("1301");


                Charset chars = Charset.forName("UTF-8");

                entity.addPart("timeline_id", new StringBody(userId));
                entity.addPart("recipient_id", new StringBody(""));
                entity.addPart("text",
                        new StringBody(statusText,chars));
                entity.addPart("photos[]", new FileBody(sourceFile));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("HEYHEY", "Response from server: " + result);

            // showing the server response in an alert dialog
            dialog.dismiss();
            Intent i = new Intent(PostPhotoActivity.this,MainActivity.class);
            startActivity(i);
            finish();

        }

    }

}
