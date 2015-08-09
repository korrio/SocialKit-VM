package co.aquario.socialkit.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.facebook.share.widget.LikeView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.inthecheesefactory.lib.fblike.widget.FBLikeView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.aquario.socialkit.NewProfileActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.activity.post.PostCommentsActivity;
import co.aquario.socialkit.event.GetUserProfileEvent;
import co.aquario.socialkit.event.GetUserProfileSuccessEvent;
import co.aquario.socialkit.event.PostShareEvent;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.fragment.main.VideoFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.Video;
import co.aquario.socialkit.widget.RoundedTransformation;

/**
 * Fragment implementation created to show a poster inside an ImageView widget.
 *
 * @author Pedro Vicente Gómez Sánchez.
 */
public class YoutubeDetailFragment extends BaseFragment implements ObservableScrollViewCallbacks, View.OnClickListener {

    @InjectView(R.id.iv_thumbnail)
    ImageView thumbnailImageView;
    TextView nameUser;
    TextView videoTitle;
    TextView videoView;
    ImageView avatar;
    boolean isFollowing = false;
    TextView loveCountView;
    TextView commentCountView;
    TextView shareCountView;
    TextView countFollowerView;
    //Intent
    String postId = "";
    String name = "";
    String avatarUrl = "";
    String title = "";
    String desc = "";
    String userId = "";
    String countView = "";
    Button btnFollow;
    int countLove;
    int countComment;
    int countShare;
    private String videoPosterThumbnail;
    private String posterTitle;
    private Video video;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        video = Parcels.unwrap((Parcelable) getActivity().getIntent().getExtras().get("obj"));

        postId = video.getPostId();
        userId = video.getpUserId();
        name = video.getpName();
        avatarUrl = video.getpAvatar();
        title = video.getTitle();
        desc = video.getDesc();
        countView = video.getView();
        countLove = video.getnLove();
        countComment = video.getnComment();
        countShare = video.getnShare();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ApiBus.getInstance().post(new GetUserProfileEvent(userId));
    }

    @Subscribe
    public void onLoadProfile(GetUserProfileSuccessEvent event) {
        isFollowing = event.getUser().getIsFollowing();
        countFollowerView.setText(event.getCount().follower + " followers");
        initButton(isFollowing, btnFollow);
    }

    Button btnLove;
    Button btnComment;
    Button btnShare;

    FBLikeView fbLikeBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_youtube_detail, container, false);
        ButterKnife.inject(this, view);

        nameUser = (TextView) view.findViewById(R.id.name);
        videoTitle = (TextView) view.findViewById(R.id.video_title);
        videoView = (TextView) view.findViewById(R.id.view);
        avatar = (ImageView) view.findViewById(R.id.myavatar);
        btnFollow = (Button) view.findViewById(R.id.btn_follow);
        countFollowerView = (TextView) view.findViewById(R.id.countFollower);

        loveCountView = (TextView) view.findViewById(R.id.number1);
        commentCountView = (TextView) view.findViewById(R.id.number2);
        shareCountView = (TextView) view.findViewById(R.id.number3);

        btnFollow.setOnClickListener(this);

        String shareUrl = "https://www.vdomax.com/share/" + postId;

        btnLove = (Button) view.findViewById(R.id.btn_love);
        btnComment = (Button) view.findViewById(R.id.btn_comment);
        btnShare = (Button) view.findViewById(R.id.btn_share);

        fbLikeBtn = (FBLikeView) view.findViewById(R.id.fbLikeView1);
        fbLikeBtn.getLikeView().setObjectIdAndType(shareUrl, LikeView.ObjectType.OPEN_GRAPH);

        btnLove.setOnClickListener(this);
        btnComment.setOnClickListener(this);
        btnShare.setOnClickListener(this);

        btnLove.setText(countLove + "");
        btnComment.setText(countComment + "");
        btnShare.setText(countShare + "");

        loveCountView.setText(countLove + "");
        commentCountView.setText(countComment + "");
        shareCountView.setText(countShare + "");

        nameUser.setText(Html.fromHtml(name));
        videoTitle.setText(Html.fromHtml(title));

        String keyword = "";
        videoView.setText(countView + " views");

        Picasso.with(getActivity())
                .load(avatarUrl)
                .centerCrop()
                .resize(100, 100)
                .transform(new RoundedTransformation(50, 4))
                .into(avatar);

        avatar.setOnClickListener(this);

        Picasso.with(getActivity())
                .load(videoPosterThumbnail)
                .into(thumbnailImageView);

        ObservableScrollView scrollView = (ObservableScrollView) view.findViewById(R.id.rvFeed);
        scrollView.setScrollViewCallbacks(this);

        VideoFragment fragment = VideoFragment.newInstance(keyword,"N");
        //LiveHistoryFragment fragment = LiveHistoryFragment.newInstance(userId);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.suggestion_container, fragment);
        transaction.commit();



        return view;
    }


    /**
     * Show the poster image in the thumbnailImageView widget.
     */
    public void setPoster(String videoPosterThumbnail) {
        this.videoPosterThumbnail = videoPosterThumbnail;
    }

    /**
     * Store the poster title to show it when the thumbanil view is clicked.
     */
    public void setPosterTitle(String posterTitle) {
        this.posterTitle = posterTitle;
    }

    /**
     * Method triggered when the iv_thumbnail widget is clicked. This method shows a toast with the
     * poster information.
     */
    @OnClick(R.id.iv_thumbnail)
    void onThubmnailClicked() {
        Toast.makeText(getActivity(), posterTitle, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onScrollChanged(int i, boolean b, boolean b2) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myavatar:

                Intent i = new Intent(getActivity(), NewProfileActivity.class);
                i.putExtra("user_id",userId);
                getActivity().startActivity(i);


                break;
            case R.id.btn_follow:
                Button button = (Button) v;

                if (isFollowing) {
                    toggleUnfollow(button);
                } else {
                    toggleFollowing(button);
                }

                isFollowing = !isFollowing;

                Log.v("You ", "select: " + button.getText());
                break;
            case R.id.btn_love:
                int oldLoveCount = 0;
                if(Integer.parseInt(btnLove.getText().toString()) == countLove) {
                    oldLoveCount = countLove;
                    oldLoveCount++;
                    btnLove.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_red,0,0,0);

                }
                else {
                    oldLoveCount = Integer.parseInt(btnLove.getText().toString());
                    oldLoveCount--;
                    btnLove.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                }

                //post.isLoved = !post.isLoved;

                btnLove.setText(oldLoveCount + "");
                break;
            case R.id.btn_comment:
                final Intent intent = new Intent(getActivity(), PostCommentsActivity.class);
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                intent.putExtra(PostCommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
                //intent.putParcelableArrayListExtra(CommentsActivity.ARG_COMMENT_LIST, Parcels.wrap(post.comment));
                Log.i("postIdComment",postId);
                intent.putExtra("POST_ID", postId);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(0, 0);
                //getActivity().finish();
                break;

            case R.id.btn_share:

                Log.e("postId",postId+"");
                buildShare2Dialog();
                break;

        }

    }

    public void buildShare2Dialog() {
        final CharSequence[] items = {"FACEBOOK", "VDOMAX"};



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Share to");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == 0) {
                    shareToFacebook(postId + "");
                    //recordVideo();
                } else if (i == 1) {
                    ApiBus.getInstance().postQueue(new PostShareEvent(userId, postId + ""));
                    //pickVideo();
                } else if (i == 2) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void shareToFacebook(String postId){

        String url = "https://graph.facebook.com/me/feed";

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("message", "กำลังเล่นสนุกอยู่ที่ VDOMAX (Live Streaming) https://goo.gl/jBF5cI");
        //params.put("link","https://www.vdomax.com/story/16693");
        //https://www.vdomax.com/story/195854
        params.put("link","https://www.vdomax.com/story/" + postId);

        //Simply put a byte[] to the params, AQuery will detect it and treat it as a multi-part post
        //byte[] data = getImageData(getResources().getDrawable(R.drawable.com_parse_ui_facebook_login_logo));
        //params.put("source", data);

        //Alternatively, put a File or InputStream instead of byte[]
        //File file = getImageFile();
        //params.put("source", file);

        AQuery aq = new AQuery(getActivity());
        aq.auth(VMApp.getFacebookHandle(getActivity())).ajax(url, params, JSONObject.class, this, "shareFb");
    }

    public void initButton(boolean following, View v) {
        Button button = (Button) v;

        isFollowing = following;

        if (following) {
            toggleFollowing(button);
        } else {
            toggleUnfollow(button);
        }


    }

    public void toggleFollowing(Button v) {
        v.setTextColor(Color.parseColor("#ffffff"));
        //v.setText(Html.fromHtml("&#x2713; FOLLOWING"));
        v.setText(Html.fromHtml("FOLLOWING"));

        // change state
        v.setSelected(true);
        v.setPressed(false);

    }

    public void toggleUnfollow(Button v) {
        v.setTextColor(Color.parseColor("#2C6497"));
        v.setText("+ FOLLOW");

        // change state
        v.setSelected(false);
        v.setPressed(false);

    }
}
