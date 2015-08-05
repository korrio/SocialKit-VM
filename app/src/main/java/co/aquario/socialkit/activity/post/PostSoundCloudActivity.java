package co.aquario.socialkit.activity.post;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.arabagile.typeahead.MentionAdapter;
import com.arabagile.typeahead.model.MentionUser;
import com.arabagile.typeahead.widget.TypeaheadTextView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.aquario.socialkit.MainActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.event.mention.LoadMentionListSuccessEvent;
import co.aquario.socialkit.event.mention.MentionListEvent;
import co.aquario.socialkit.handler.ApiBus;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

public class PostSoundCloudActivity extends Activity {

    String sid;
    String title;
    String subtitle;

    String thumbUrl;
    String statusText;

    ImageView thumb;
    TypeaheadTextView etStatus;
    Button btnPost;
    ProgressDialog dialog;

    TextView trackTitle;
    TextView trackUsername;
    Context context;

    View rootView;
    ImageView emojiButton;
    EmojiconsPopup popup;

    private Toolbar toolbar;
    void setupToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        if(toolbar != null) {
//            setSupportActionBar(toolbar);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setTitle("Post to Timeline");
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });


        }
    }

    @Subscribe
    public void onRequestMention(LoadMentionListSuccessEvent event) {
        List<MentionUser> users = new ArrayList<>();
        ArrayList<MentionUser> mentionList = event.response.mentions;
        for(int i = 0 ; i < mentionList.size() ; i++) {
            users.add(new MentionUser(mentionList.get(i).name, mentionList.get(i).username, mentionList.get(i).getAvatarUrl()));
        }
        MentionAdapter adapter = new MentionAdapter(this, R.layout.menu_user_mention, users);
        etStatus.setAdapter(adapter);
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
        setContentView(R.layout.activity_post_soundcloud);

        ApiBus.getInstance().post(new MentionListEvent(Integer.parseInt(VMApp.mPref.userId().getOr("0"))));

        rootView = findViewById(R.id.root_view);
        emojiButton = (ImageView) findViewById(R.id.emoji_btn);
        popup = new EmojiconsPopup(rootView, this);
        context = this;

        setupToolbar();
        prepareDialog();
        initTattoo();

        thumb = (ImageView) findViewById(R.id.img_track);
        trackTitle = (TextView) findViewById(R.id.track_title);
        trackUsername = (TextView) findViewById(R.id.tv_username);
        etStatus = (TypeaheadTextView) findViewById(R.id.et_box);
        btnPost = (Button) findViewById(R.id.button_recent);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postSoundCloud();
            }
        });

        if(getIntent()!=null) {
            sid = getIntent().getStringExtra("soundcloud_uri");
            title = getIntent().getStringExtra("soundcloud_title");
            subtitle = getIntent().getStringExtra("soundcloud_subtitle");
            thumbUrl = getIntent().getStringExtra("artwork_url");

            Picasso.with(this).load(thumbUrl).into(thumb);
            trackTitle.setText(title);
            trackUsername.setText(subtitle);
        }



    }

    void initTattoo() {
        popup.setSizeForSoftKeyboard();
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                btnPost.append(emojicon.getEmoji());
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                etStatus.dispatchKeyEvent(event);
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
                etStatus.append(emojicon.getEmoji());
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                etStatus.dispatchKeyEvent(event);
            }
        });


        emojiButton.setOnClickListener(new View.OnClickListener() {

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
                        etStatus.setFocusableInTouchMode(true);
                        etStatus.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(btnPost, InputMethodManager.HIDE_IMPLICIT_ONLY);
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

    public String url = "https://www.vdomax.com/ajax.php?t=post&a=new&user_id=6&token=123456&user_pass=039a726ac0aeec3dde33e45387a7d4ac";
    public long totalSize;

    public void postSoundCloud() {
        statusText = etStatus.getText().toString()
                .replace("\n", "%0A");
        //statusText.toString().trim().replaceAll("\\s+", " ");

        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(statusText);
        boolean found = matcher.find();
        boolean isWhitespace = statusText.matches("^\\s*$");

        if (statusText.length() == 0 || statusText.trim().equals("") || found || isWhitespace) {
            //etStatus.setError("กรุณาพิมพ์ข้อความก่อนส่ง");
            Log.e("YEAH", statusText.length() + " " + statusText.trim() + " " + found + " " + isWhitespace);
        }

        statusText = etStatus.getText().toString();

        String fromUserId = VMApp.mPref.userId().getOr("0");
        uploadPost(statusText, fromUserId, "");
        //new UploadFileToServer().execute();
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
        if (id == R.id.action_sort) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId){
        iconToBeChanged.setImageResource(drawableResourceId);
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
        Map<String, Object> params = new HashMap<String, Object>();
        //TypedFile typedFile = new TypedFile("multipart/form-data", file);

        params.put("timeline_id", fromUserId);
        params.put("recipient_id", toUserId);
        params.put("text", text);
        params.put("soundcloud_title",title);
        params.put("soundcloud_uri",sid);

        AQuery aq = new AQuery(getApplicationContext());
        aq.progress(dialog).ajax(url, params, JSONObject.class, this, "uploadCb");
    }

    public void uploadCb(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        Log.e("hahahaha", jo.toString(4));
        if(jo.optInt("status") == 200) {
            Intent backIntent = new Intent(context, MainActivity.class
            );
            //setResult(-1, backIntent);
            startActivity(backIntent);
            finish();
        }

    }

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
//                //File sourceFile = tempFile;
//
//                PrefManager pref = VMApp.get(getApplicationContext()).getPrefManager();
//                String userId = pref.userId().getOr("1301");
//
//                statusText = Utils.emoticonize(statusText);
//
//                Charset chars = Charset.forName("UTF-8");
//                entity.addPart("timeline_id", new StringBody(userId));
//                entity.addPart("recipient_id", new StringBody(""));
//                entity.addPart("text",
//                        new StringBody(statusText,chars));
//                entity.addPart("soundcloud_title",
//                        new StringBody(title,chars));
//                entity.addPart("soundcloud_uri",
//                        new StringBody(sid,chars));
//                //entity.addPart("photos[]", toolbar FileBody(sourceFile));
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
//            Log.e("HEYHEY", "Response from server: " + result);
//
//            // showing the server response in an alert dialog
//            dialog.dismiss();
//            Intent i = new Intent(PostSoundCloudActivity.this,MainActivity.class);
//            startActivity(i);
//            finish();
//            //showAlert(result);
//            super.onPostExecute(result);
//        }
//
//    }
}
