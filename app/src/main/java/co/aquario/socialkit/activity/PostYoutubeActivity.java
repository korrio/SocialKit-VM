package co.aquario.socialkit.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.aquario.socialkit.MainApplication;
import co.aquario.socialkit.R;
import co.aquario.socialkit.util.AndroidMultiPartEntity;
import co.aquario.socialkit.util.PrefManager;

public class PostYoutubeActivity extends ActionBarActivity {

    String yid;
    String title;
    String desc;
    String statusText;

    ImageView thumb;
    EditText etStatus;
    Button btnPost;
    ProgressDialog dialog;

    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_youtube);

        context = this;

        thumb = (ImageView) findViewById(R.id.thumb);
        etStatus = (EditText) findViewById(R.id.et_status);
        btnPost = (Button) findViewById(R.id.btn_post);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postYoutube();
            }
        });

        yid = getIntent().getStringExtra("yid");
        title = getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("desc");


        Picasso.with(this).load(getIntent().getStringExtra("thumb")).into(thumb);

    }

    public String url = "https://www.vdomax.com/ajax.php?t=post&a=new&user_id=6&token=123456&user_pass=039a726ac0aeec3dde33e45387a7d4ac";
    public long totalSize;

    public void postYoutube() {
        statusText = etStatus.getText().toString()
                .replace("\n", "%0A");
        //statusText.toString().trim().replaceAll("\\s+", " ");

        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(statusText);
        boolean found = matcher.find();
        boolean isWhitespace = statusText.matches("^\\s*$");

        if (statusText.length() == 0 || statusText.trim().equals("") || found || isWhitespace) {
            etStatus.setError("กรุณาพิมพ์ข้อความก่อนส่ง");
            Log.e("YEAH", statusText.length() + " " + statusText.trim() + " " + found + " " + isWhitespace);
        }

        statusText = etStatus.getText().toString();

        dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Uploading");
        dialog.setMessage("กำลังอัพโหลดวิดีโอ..");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setIndeterminate(false);
        dialog.setMax(100);

        new UploadFileToServer().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the search; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_youtube, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

                //File sourceFile = tempFile;

                PrefManager pref = MainApplication.get(getApplicationContext()).getPrefManager();
                String userId = pref.userId().getOr("1301");

                Charset chars = Charset.forName("UTF-8");
                entity.addPart("timeline_id", new StringBody(userId));
                entity.addPart("recipient_id", new StringBody(""));
                entity.addPart("text",
                        new StringBody(statusText,chars));
                entity.addPart("youtube_title",
                        new StringBody(title,chars));
                entity.addPart("youtube_description",
                        new StringBody(desc,chars));
                entity.addPart("youtube_video_id",
                        new StringBody(yid));
                //entity.addPart("photos[]", new FileBody(sourceFile));

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
            Log.e("HEYHEY", "Response from server: " + result);

            // showing the server response in an alert dialog
            dialog.dismiss();
            Intent i = new Intent(PostYoutubeActivity.this,MainActivity.class);
            startActivity(i);
            finish();
            //showAlert(result);
            super.onPostExecute(result);
        }

    }
}
