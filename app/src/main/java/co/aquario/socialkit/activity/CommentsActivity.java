package co.aquario.socialkit.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import co.aquario.socialkit.MainApplication;
import co.aquario.socialkit.R;
import co.aquario.socialkit.adapter.CommentsAdapter;
import co.aquario.socialkit.event.GetStoryEvent;
import co.aquario.socialkit.event.GetStorySuccessEvent;
import co.aquario.socialkit.event.PostCommentEvent;
import co.aquario.socialkit.event.PostCommentSuccessEvent;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.CommentStory;
import co.aquario.socialkit.util.PrefManager;
import co.aquario.socialkit.util.Utils;
import co.aquario.socialkit.view.SendCommentButton;
import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;


public class CommentsActivity extends ActionBarActivity implements SendCommentButton.OnSendClickListener {
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
    public static final String ARG_COMMENT_LIST = "arg_comment_list";

    @Optional
    //@InjectView(R.id.toolbar)
            //Toolbar toolbar;

            //public Toolbar getToolbar() {
            //return toolbar;
            //}
            String statusText;
    @InjectView(R.id.contentRoot)
    LinearLayout contentRoot;
    @InjectView(R.id.rvComments)
    RecyclerView rvComments;
    @InjectView(R.id.llAddComment)
    LinearLayout llAddComment;
    @InjectView(R.id.comment_box)
    EmojiconEditText etComment;
    @InjectView(R.id.btnSendComment)
    SendCommentButton btnSendComment;

    private CommentsAdapter commentsAdapter;
    private int drawingStartLocation;

    ArrayList<CommentStory> commentList;

    PrefManager pref;
    String avatar;
    String name;
    String postId;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.inject(this);

        final View rootView = findViewById(R.id.root_view);
        final ImageView emojiButton = (ImageView) findViewById(R.id.emoji_btn);
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);

        ApiBus.getInstance().register(this);

        postId = getIntent().getStringExtra("POST_ID");
        ApiBus.getInstance().post(new GetStoryEvent(postId));
        //commentList = getIntent().getParcelableArrayListExtra(ARG_COMMENT_LIST);
        pref = MainApplication.get(this).getPrefManager();
        userId = pref.userId().getOr("3");
        avatar = pref.avatar().getOr("TEST");
        name = pref.name().getOr("TEST");

        if(commentList == null)
            commentList  = new ArrayList<CommentStory>();

        setupComments();
        setupSendCommentButton();



        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        if (savedInstanceState == null) {
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });
        }

        popup.setSizeForSoftKeyboard();
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {

                etComment.append(emojicon.getEmoji());
            }
        });

        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                etComment.dispatchKeyEvent(event);
            }
        });

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
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
                etComment.append(emojicon.getEmoji());
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                etComment.dispatchKeyEvent(event);
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
                        etComment.setFocusableInTouchMode(true);
                        etComment.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(etComment, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                    }
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else{
                    popup.dismiss();
                }
            }
        });

        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.v("emojiText", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setupComments() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setHasFixedSize(true);

        commentsAdapter = new CommentsAdapter(this,commentList);
        rvComments.setAdapter(commentsAdapter);
        rvComments.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvComments.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    commentsAdapter.setAnimationsLocked(true);
                }
            }
        });
    }

    private void setupSendCommentButton() {
        btnSendComment.setOnSendClickListener(this);
    }

    private void startIntroAnimation() {
        //ViewCompat.setElevation(getToolbar(), 0);
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawingStartLocation);
        llAddComment.setTranslationY(200);

        contentRoot.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //ViewCompat.setElevation(getToolbar(), Utils.dpToPx(8));
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {
        commentsAdapter.updateItems();
        llAddComment.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(200)
                .start();

    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId){
        iconToBeChanged.setImageResource(drawableResourceId);
    }

    @Override
    public void onBackPressed() {

        //ViewCompat.setElevation(getToolbar(), 0);
        contentRoot.animate()
                .translationY(Utils.getScreenHeight(this))
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        CommentsActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }

    @Override
    public void onSendClickListener(View v) {

        if (validateComment()) {

            String commentText = etComment.getText().toString();
            commentText = Utils.emoticonize(commentText);

            ApiBus.getInstance().post(new PostCommentEvent(commentText,userId,postId));

            commentsAdapter.addItem(commentText,avatar,name);
            commentsAdapter.setAnimationsLocked(false);
            commentsAdapter.setDelayEnterAnimation(false);

            int someHeight;
            if(rvComments.getChildAt(0) == null)
                someHeight = 0;
            else
                someHeight = rvComments.getChildAt(0).getHeight();

            rvComments.smoothScrollBy(0, someHeight * commentsAdapter.getItemCount());

            etComment.setText(null);
            btnSendComment.setCurrentState(SendCommentButton.STATE_DONE);
        }
    }

    @Subscribe public void onPostCommentSuccess(PostCommentSuccessEvent event) {
        //ApiBus.getInstance().post(new RefreshEvent());
        Toast.makeText(getApplicationContext(),"Success comment",Toast.LENGTH_SHORT).show();
    }

    @Subscribe public void onGetStorySuccess(GetStorySuccessEvent event) {
        if(event.getPost().comment != null) {
            commentList.addAll(event.getPost().comment);
            animateContent();
        }

        //commentsAdapter.notifyDataSetChanged();
    }

    private boolean validateComment() {
        if (TextUtils.isEmpty(etComment.getText())) {
            btnSendComment.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }
        return true;
    }
}