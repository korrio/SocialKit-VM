package co.aquario.socialkit;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import co.aquario.socialkit.util.Utils;
import retrofit.mime.TypedFile;


public class PublishActivity extends BaseActivity {
    public static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";

    @InjectView(R.id.tbFollowers)
    ToggleButton tbFollowers;
    @InjectView(R.id.tbDirect)
    ToggleButton tbDirect;
    @InjectView(R.id.ivPhoto)
    ImageView ivPhoto;
    @InjectView(R.id.etDescription)
    EditText etDesc;

    private boolean propagatingToggleState = false;
    private Uri photoUri;
    private int photoSize;

    public static void openWithPhotoUri(Activity openingActivity, Uri photoUri) {
        Intent intent = new Intent(openingActivity, PublishActivity.class);
        intent.putExtra(ARG_TAKEN_PHOTO_URI, photoUri);
        openingActivity.startActivity(intent);
    }

    Button btnPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        CustomActivityOnCrash.setShowErrorDetails(true);
        CustomActivityOnCrash.setRestartActivityClass(MainActivity.class);
        CustomActivityOnCrash.install(this);
        //toolbar.setNavigationIcon(R.drawable.ic_arrow_back_grey600_24dp);
        photoSize = getResources().getDimensionPixelSize(R.dimen.publish_photo_thumbnail_size);
        btnPost = (Button) findViewById(R.id.btn_post);

        if (savedInstanceState == null) {
            photoUri = getIntent().getParcelableExtra(ARG_TAKEN_PHOTO_URI);
        } else {
            photoUri = savedInstanceState.getParcelable(ARG_TAKEN_PHOTO_URI);
        }
        etDesc.setText(photoUri.toString());
        updateStatusBarColor();

        ivPhoto.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ivPhoto.getViewTreeObserver().removeOnPreDrawListener(this);
                loadThumbnailPhoto();
                return true;
            }
        });

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Uploading");
        dialog.setMessage("กำลังอัพโหลดรูปภาพ..");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setIndeterminate(false);
        dialog.setMax(100);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bringMainActivityToTop();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void updateStatusBarColor() {
        if (Utils.isAndroid5()) {
            getWindow().setStatusBarColor(0xff888888);
        }
    }

    private void loadThumbnailPhoto() {
        ivPhoto.setScaleX(0);
        ivPhoto.setScaleY(0);
        Picasso.with(this)
                .load(photoUri)
                .centerCrop()
                .resize(photoSize, photoSize)
                .into(ivPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                        ivPhoto.animate()
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
    protected boolean shouldInstallDrawer() {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_publish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_publish) {
            bringMainActivityToTop();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public String url = "https://www.vdomax.com/ajax.php?t=post&a=new&user_id=6&token=123456&user_pass=039a726ac0aeec3dde33e45387a7d4ac";
    public long totalSize;
    public ProgressDialog dialog;

    private void bringMainActivityToTop() {
        prepareDialog();
        String fromUserId = VMApp.mPref.userId().getOr("0");
        uploadPost(etDesc.getText().toString(), fromUserId, "");
        //new UploadFileToServer().execute();

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

    private void uploadPost(String text,String fromUserId, String toUserId) {
        //String url = "http://chat.vdomax.com/upload";
        Map<String, Object> params = new HashMap<String, Object>();
        File sourceFile = new File(photoUri.getPath());
        TypedFile typedFile = new TypedFile("multipart/form-data", sourceFile);

        params.put("timeline_id", fromUserId);
        params.put("recipient_id", toUserId);
        params.put("photos[]", sourceFile);

        AQuery aq = new AQuery(getApplicationContext());
        aq.progress(dialog).ajax(url, params, JSONObject.class, this, "uploadCb");
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_TAKEN_PHOTO_URI, photoUri);
    }

    @OnCheckedChanged(R.id.tbFollowers)
    public void onFollowersCheckedChange(boolean checked) {
        if (!propagatingToggleState) {
            propagatingToggleState = true;
            tbDirect.setChecked(!checked);
            propagatingToggleState = false;
        }
    }

    @OnCheckedChanged(R.id.tbDirect)
    public void onDirectCheckedChange(boolean checked) {
        if (!propagatingToggleState) {
            propagatingToggleState = true;
            tbFollowers.setChecked(!checked);
            propagatingToggleState = false;
        }
    }

//    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
//
//        public String url = "https://www.vdomax.com/ajax.php?t=post&a=new&user_id=6&token=123456&user_pass=039a726ac0aeec3dde33e45387a7d4ac";
//    public long totalSize = 0;
//
//        @Override
//        protected void onPreExecute() {
//            // setting progress bar to zero
//            //dialog.show();
//            //dialog.setProgress(0);
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... progress) {
//            // Making progress bar visible
//            //progressBar.setVisibility(View.VISIBLE);
//
//            // updating progress bar value
//            //dialog.setProgress(progress[0]);
//
//            // updating percentage value
//            //dialog.setTitle(String.valueOf(progress[0]) + "%");
//            //dialog.setText();
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            return uploadFile();
//        }
//
//        @SuppressWarnings("deprecation")
//        private String uploadFile() {
//            String responseString = null;
//
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httppost = new HttpPost(url);
//
//            try {
//                MultipartEntity entity = new MultipartEntity();
//
//                File sourceFile = new File(photoUri.getPath());
//
//                String userId = getPref(getApplicationContext()).userId().getOr("0");
//
//
//                Charset chars = Charset.forName("UTF-8");
//
//                String statusText = etDesc.getText().toString();
//
//                entity.addPart("timeline_id", new StringBody(userId));
//                entity.addPart("recipient_id", new StringBody(""));
//                entity.addPart("text",
//                        new StringBody(statusText,chars));
//                entity.addPart("photos[]", new FileBody(sourceFile));
//
//
//                //totalSize = entity.getContentLength();
//                httppost.setEntity(entity);
//
//                // Making server call
//                HttpResponse response = httpclient.execute(httppost);
//                HttpEntity r_entity = response.getEntity();
//
//                int statusCode = response.getStatusLine().getStatusCode();
//                if (statusCode == 200) {
//                    // Server response
//                    responseString = EntityUtils.toString(r_entity);
//                } else {
//                    responseString = "Error occurred! Http Status Code: "
//                            + statusCode;
//                }
//
//            } catch (ClientProtocolException e) {
//                responseString = e.toString();
//            } catch (IOException e) {
//                responseString = e.toString();
//            }
//
//            return responseString;
//
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            Log.e("HEYHEY", "Response from server: " + result);
//
//            // showing the server response in an alert dialog
//            dialog.dismiss();
//
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            //intent.setAction(MainActivity.ACTION_SHOW_LOADING_ITEM);
//            startActivity(intent);
//
//
//        }
//
//    }

}
