package co.aquario.socialkit.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
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
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import co.aquario.socialkit.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Fragment implementation created to show a poster inside an ImageView widget.
 *
 * @author Pedro Vicente Gómez Sánchez.
 */
public class LIvePosterFragment extends Fragment implements ObservableScrollViewCallbacks {

    @InjectView(R.id.iv_thumbnail) ImageView thumbnailImageView;


    private String videoPosterThumbnail;
    private String posterTitle;
    TextView name;
    TextView video_title;
    TextView description;
    CircleImageView userProfile;
    boolean isFollowing = false;

    //Intent
    String nameTitle = "";
    String photoFile ="";
    String VideoYoutubeTitleUrl ="";
    String DescriptionUrl ="";
    String userId = "";
    Button btnFollow;
    /**
     * Override method used to initialize the fragment.
     */
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_youtube_detail, container, false);
        ButterKnife.inject(this, view);
        nameTitle = getActivity().getIntent().getExtras().getString("nameUser");
        photoFile = getActivity().getIntent().getExtras().getString("avatarUrl");
        VideoYoutubeTitleUrl = getActivity().getIntent().getExtras().getString("title");
        DescriptionUrl = getActivity().getIntent().getExtras().getString("desc");
        userId = getActivity().getIntent().getExtras().getString("userId");
        name = (TextView) view.findViewById(R.id.name);
        video_title = (TextView) view.findViewById(R.id.video_title);
        description = (TextView) view.findViewById(R.id.view);
        userProfile = (CircleImageView) view.findViewById(R.id.image_profile);
        btnFollow = (Button) view.findViewById(R.id.btn_follow);

        name.setText(nameTitle);
        video_title.setText(VideoYoutubeTitleUrl);
        description.setText(Html.fromHtml("<strong><em>" + DescriptionUrl + "</em></strong>"));


        Picasso.with(getActivity())
                .load(photoFile)
                .into(userProfile);

        Picasso.with(getActivity())
                .load(videoPosterThumbnail)
                .into(thumbnailImageView);

        ObservableScrollView scrollView = (ObservableScrollView) view.findViewById(R.id.scroll);
        scrollView.setScrollViewCallbacks(this);

        //VideoListFragment fragment = new VideoListFragment();
        LiveHistoryFragment fragment = LiveHistoryFragment.newInstance(userId);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.suggestion_container, fragment);
        transaction.commit();

        initButton();
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFollowing) {
                    btnFollow.setBackgroundResource(R.drawable.bg_unselected);
                    btnFollow.setTextColor(Color.parseColor("#ffffff"));

                    // clear state
                    btnFollow.setSelected(false);
                    btnFollow.setPressed(false);

                    btnFollow.setText(Html.fromHtml("&#x2713; FOLLOWING"));

                    // change state
                    btnFollow.setSelected(true);
                    btnFollow.setPressed(false);
                } else {
                    btnFollow.setSelected(false);
                    btnFollow.setPressed(false);


                    btnFollow.setText("+ FOLLOW");
                    btnFollow.setBackgroundResource(R.drawable.bg_selected);
                    btnFollow.setTextColor(Color.parseColor("#2C6497"));

                }

                isFollowing = !isFollowing;
            }
        });

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
    @OnClick(R.id.iv_thumbnail) void onThubmnailClicked() {
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

    public void initButton() {

        btnFollow.setBackgroundResource(R.drawable.bg_unselected);
        btnFollow.setTextColor(Color.parseColor("#ffffff"));

        // clear state
        btnFollow.setSelected(false);
        btnFollow.setPressed(false);

        btnFollow.setText(Html.fromHtml("&#x2713; FOLLOWING"));

        // change state
        btnFollow.setSelected(true);
        btnFollow.setPressed(false);

        isFollowing = !isFollowing;
    }
}
