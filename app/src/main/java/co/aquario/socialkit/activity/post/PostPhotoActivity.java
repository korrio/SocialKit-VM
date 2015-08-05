package co.aquario.socialkit.activity.post;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.arabagile.typeahead.MentionAdapter;
import com.arabagile.typeahead.model.MentionUser;
import com.arabagile.typeahead.widget.TypeaheadTextView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.aquario.socialkit.MainActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.event.mention.LoadMentionListSuccessEvent;
import co.aquario.socialkit.event.mention.MentionListEvent;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.handler.PostUploadService;
import co.aquario.socialkit.model.UploadPostCallback;
import co.aquario.socialkit.util.PathManager;
import co.aquario.socialkit.util.Utils;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class PostPhotoActivity extends Activity implements OnClickListener {

    Context context;

    Button post_photo;
    ImageView imageView;
    TypeaheadTextView photoText;

    File tempFile;

    AQuery aq;

    public static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";
    private Uri photoUri;
    private int photoSize;

    @Override
    public void onBackPressed() {

        //ViewCompat.setElevation(getToolbar(), 0);
        finishPosting();
    }

    private void finishPosting() {
        contentRoot.animate()
                .translationY(Utils.getScreenHeight(this))
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        PostPhotoActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }


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

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    finish();
                    //Toast.makeText(getApplicationContext(), "Hello wolrd", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public LinearLayout contentRoot;

    @Subscribe
    public void onRequestMention(LoadMentionListSuccessEvent event) {
        List<MentionUser> users = new ArrayList<>();
        ArrayList<MentionUser> mentionList = event.response.mentions;
        for(int i = 0 ; i < mentionList.size() ; i++) {
            users.add(new MentionUser(mentionList.get(i).name, mentionList.get(i).username, mentionList.get(i).getAvatarUrl()));
        }
        MentionAdapter adapter = new MentionAdapter(this, R.layout.menu_user_mention, users);
        photoText.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApiBus.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApiBus.getInstance().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_photo);

        ApiBus.getInstance().post(new MentionListEvent(Integer.parseInt(VMApp.mPref.userId().getOr("0"))));

        final View rootView = findViewById(R.id.root_view);

        final ImageView emojiButton = (ImageView) findViewById(R.id.emoji_btn);
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);
        contentRoot = (LinearLayout) findViewById(R.id.contentRoot);

        context = this;

        prepareDialog();
        setupToolbar();
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
        photoText = (TypeaheadTextView) findViewById(R.id.et_box);
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
        popup.setSizeForSoftKeyboard();
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                photoText.append(emojicon.getEmoji());
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                photoText.dispatchKeyEvent(event);
            }
        });

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_emoji);
            }
        });

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if(popup.isShowing())
                    popup.dismiss();
            }
        });

        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                photoText.append(emojicon.getEmoji());
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                photoText.dispatchKeyEvent(event);
            }
        });


        emojiButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if(!popup.isShowing()){

                    //If keyboard is visible, simply show the emoji popup
                    if(popup.isKeyBoardOpen()){
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                    }

                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else{
                        photoText.setFocusableInTouchMode(true);
                        photoText.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(photoText, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                    }
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else{
                    popup.dismiss();
                }
            }
        });
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
    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId){
        iconToBeChanged.setImageResource(drawableResourceId);
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
                Log.e("YEAH", statusText.length() + " " + statusText.trim() + " " + found + " " + isWhitespace);
            }

            statusText = photoText.getText().toString();

            String fromUserId = VMApp.mPref.userId().getOr("0");
            uploadPost(statusText, fromUserId, "");

            //new UploadFileToServer().execute();

        }

    }

    void prepareDialog() {
        dialog = new ProgressDialog(context);
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

       // TypedFile typedFile = new TypedFile("multipart/form-data", tempFile);

        uploadPostRetrofit(tempFile, text, fromUserId, toUserId);

//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("text", text);
//        params.put("timeline_id", fromUserId);
//        params.put("recipient_id", toUserId);
//        params.put("photos[]", typedFile);
//
//        AQuery aq = new AQuery(getApplicationContext());
//        aq.progress(dialog).ajax(url, params, JSONObject.class, this, "uploadCb");
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

    PostUploadService buildUploadApi() {
        String BASE_URL = "https://www.vdomax.com";

        return new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)

                .setRequestInterceptor(new RequestInterceptor() {
                    @Override public void intercept(RequestFacade request) {
                        //request.addQueryParam("p1", "var1");
                        //request.addQueryParam("p2", "");
                    }
                })
                .build()
                .create(PostUploadService.class);
    }

    private void uploadPostRetrofit(File file, String text, String fromUserId, String toUserId) {
        //FileUploadService service = ServiceGenerator.createService(FileUpload.class, FileUpload.BASE_URL);

        PostUploadService service = buildUploadApi();
        TypedFile typedFile = new TypedFile("multipart/form-data", file);

        service.uploadPostPhoto(text, fromUserId, toUserId, typedFile, new retrofit.Callback<UploadPostCallback>() {
            @Override
            public void success(UploadPostCallback uploadCallback, Response response) {
                if(uploadCallback.status == 200)
                    Utils.showToast("Post photo success");
                else
                    Utils.showToast("Post photo failed");

                Intent i = new Intent(PostPhotoActivity.this,MainActivity.class);
                startActivity(i);
                finish();

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

//    private class SendFileTask extends AsyncTask<String, Integer, ApiResult> {
//        private ProgressListener listener;
//        private String filePath;
//        private FileType fileType;
//
//        public SendFileTask(String filePath, FileType fileType) {
//            this.filePath = filePath;
//            this.fileType = fileType;
//        }
//
//        @Override
//        protected ApiResult doInBackground(String... params) {
//            File file = new File(filePath);
//            totalSize = file.length();
//            Logger.d("Upload FileSize[%d]", totalSize);
//            listener = new ProgressListener() {
//                @Override
//                public void transferred(long num) {
//                    publishProgress((int) ((num / (float) totalSize) * 100));
//                }
//            };
//            String _fileType = FileType.VIDEO.equals(fileType) ? "video/mp4" : (FileType.IMAGE.equals(fileType) ? "image/jpeg" : "*/*");
//            return MyRestAdapter.getService().uploadFile(new CountingTypedFile(_fileType, file, listener), "/Mobile Uploads");
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            Logger.d(String.format("progress[%d]", values[0]));
//            //do something with values[0], its the percentage so you can easily do
//            //progressBar.setProgress(values[0]);
//        }
//    }

//    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
//        @Override
//        protected void onPreExecute() {
//            // setting progress bar to zero
//            dialog.show();
//            dialog.setProgress(0);
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... progress) {
//            // Making progress bar visible
//            //progressBar.setVisibility(View.VISIBLE);
//
//            // updating progress bar value
//            dialog.setProgress(progress[0]);
//
//            // updating percentage value
//            dialog.setTitle(String.valueOf(progress[0]) + "%");
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
//                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
//                        new AndroidMultiPartEntity.ProgressListener() {
//
//                            @Override
//                            public void transferred(long num) {
//                                publishProgress((int) ((num / (float) totalSize) * 100));
//                                dialog.setProgress((int) ((num / (float) totalSize) * 100));
//                            }
//                        });
//
//                File sourceFile = tempFile;
//
//                Charset chars = Charset.forName("UTF-8");
//
//                statusText = Utils.emoticonize(statusText);
//
//                entity.addPart("timeline_id", new StringBody(VMApp.mPref.userId().getOr("0")));
//                entity.addPart("recipient_id", new StringBody(""));
//                entity.addPart("text",
//                        new StringBody(statusText,chars));
//                entity.addPart("photos[]", new FileBody(sourceFile));
//
//
//                totalSize = entity.getContentLength();
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
//            Intent i = new Intent(PostPhotoActivity.this,MainActivity.class);
//            startActivity(i);
//            finish();
//
//        }
//
//    }

}