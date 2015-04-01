package co.aquario.socialkit.fragment;

import android.graphics.Color;
import android.os.Bundle;
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

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.aquario.socialkit.R;
import co.aquario.socialkit.event.GetUserProfileEvent;
import co.aquario.socialkit.event.GetUserProfileSuccessEvent;
import co.aquario.socialkit.fragment.main.VideoFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.widget.RoundedTransformation;

/**
 * Fragment implementation created to show a poster inside an ImageView widget.
 *
 * @author Pedro Vicente Gómez Sánchez.
 */
public class DragableBottomFragment extends BaseFragment implements ObservableScrollViewCallbacks,View.OnClickListener {

    @InjectView(R.id.iv_thumbnail)
    ImageView thumbnailImageView;

    private String videoPosterThumbnail;
    private String posterTitle;
    TextView nameUser;
    TextView videoTitle;
    TextView videoView;
    ImageView avatar;
    boolean isFollowing = false;

    //Intent
    String name = "";
    String avatarUrl = "";
    String title = "";
    String desc = "";
    String userId = "";
    String countView = "";
    Button btnFollow;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getActivity().getIntent().getExtras().getString("userId");
        name = getActivity().getIntent().getExtras().getString("name");
        avatarUrl = getActivity().getIntent().getExtras().getString("avatar");
        title = getActivity().getIntent().getExtras().getString("title");
        desc = getActivity().getIntent().getExtras().getString("desc");

        countView = getActivity().getIntent().getExtras().getString("view");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ApiBus.getInstance().post(new GetUserProfileEvent(userId));
    }

    @Subscribe
    public void onLoadProfile(GetUserProfileSuccessEvent event) {
        isFollowing = event.getUser().getIsFollowing();
        initButton(isFollowing, btnFollow);
    }

    /**
     * Override method used to initialize the fragment.
     */
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
        btnFollow.setOnClickListener(this);

        nameUser.setText(Html.fromHtml(name));
        videoTitle.setText(Html.fromHtml(title));

        //String[] parts;
        String keyword = "video";
        if(title != null) {
            //parts = title.split(" ");
            keyword = name;
        }

        //videoView.setText(Html.fromHtml("<strong><em>" + desc + "</em></strong>"));
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

        ObservableScrollView scrollView = (ObservableScrollView) view.findViewById(R.id.scroll);
        scrollView.setScrollViewCallbacks(this);

        VideoFragment fragment = VideoFragment.newInstance(keyword,"name",userId);
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
                ProfileDetailFragment fragment = new ProfileDetailFragment().newInstance(userId);
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container, fragment).addToBackStack(null);
                transaction.commit();
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

        }

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
