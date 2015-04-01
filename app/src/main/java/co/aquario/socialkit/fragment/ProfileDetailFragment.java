package co.aquario.socialkit.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import co.aquario.socialkit.R;
import co.aquario.socialkit.adapter.FeedAdapter;
import co.aquario.socialkit.event.GetUserProfileEvent;
import co.aquario.socialkit.event.GetUserProfileSuccessEvent;
import co.aquario.socialkit.event.LoadTimelineEvent;
import co.aquario.socialkit.event.LoadTimelineSuccessEvent;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.PostStory;
import co.aquario.socialkit.view.PullScrollView;
import co.aquario.socialkit.widget.RoundedTransformation;


public class ProfileDetailFragment extends BaseFragment implements View.OnClickListener {
    ImageView avatar;
    ImageView cover;
    TextView titleTv;
    TextView usernameTv;
    TextView bioTv;
    PullScrollView scrollView;

    TextView countPost;
    TextView countFollowing;
    TextView countFollower;
    TextView countFriend;

    Button btnFollow;

    String imageTitle;
    String nameTitle;
    String coverUrl;
    String userId;
    String username;

    public ArrayList<PostStory> list = new ArrayList<>();
    private FeedAdapter adapter;

    private boolean isHomeTimeline = false;
    private static final String TYPE = "";
    private static final int PER_PAGE = 20;

    public static String USER_ID = "userId";



    public static ProfileDetailFragment newInstance(String userId){
        ProfileDetailFragment mFragment = new ProfileDetailFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(USER_ID, userId);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(USER_ID);
        } else {
            userId = prefManager.userId().getOr("3");
        }

        Log.v("profileDetailuserId",userId + "");
    }

    @Override
    public void onResume() {
        super.onResume();
        ApiBus.getInstance().post(new GetUserProfileEvent(userId));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ApiBus.getInstance().post(new GetUserProfileEvent(userId));
        ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId), TYPE, 1, PER_PAGE, isHomeTimeline));
    }

    boolean isFollowing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_detail, container, false);
        //setContentView(R.layout.fragment_profile_detail);

        //Intent iin = getIntent();
        //Bundle b = iin.getExtras();

        /*
        userId = (String) b.get("userId");
        imageTitle = (String) b.get("avatarUrl");
        name = (String) b.get("nameUser");
        coverUrl = (String) b.get("cover");
        username = (String) b.get("username");
        */

        /*
        titleTv = (TextView) rootView.findViewById(R.id.name_title);
        usernameTv = (TextView) rootView.findViewById(R.id.name_username);
        avatarUrl = (CircularImageView) rootView.findViewById(R.id.avatarUrl);
        cover = (ImageView) rootView.findViewById(R.id.cover);
        */

        titleTv = (TextView) rootView.findViewById(R.id.user_name);
        usernameTv = (TextView) rootView.findViewById(R.id.user_username);
        bioTv = (TextView) rootView.findViewById(R.id.user_des);
        avatar = (ImageView) rootView.findViewById(R.id.user_avatar);
        cover = (ImageView) rootView.findViewById(R.id.user_cover);

        countPost = (TextView) rootView.findViewById(R.id.countPost);
        countFollowing = (TextView) rootView.findViewById(R.id.countFollowing);
        countFollower = (TextView) rootView.findViewById(R.id.countFollower);
        countFriend = (TextView) rootView.findViewById(R.id.countFriend);

        btnFollow = (Button) rootView.findViewById(R.id.btn_follow);
        btnFollow.setOnClickListener(this);

        scrollView = (PullScrollView) rootView.findViewById(R.id.scroll_view);
        RelativeLayout head = (RelativeLayout) rootView.findViewById(R.id.scroll_view_head);
        scrollView.setHeader(head);

        /*

        titleTv.setText(name);
        usernameTv.setText("@" + username);

        Picasso.with(getActivity())
                .load(coverUrl)
                .fit().centerCrop()
                .into(cover);

        Picasso.with(getActivity())
                .load(imageTitle)
                .into(avatarUrl);

                */

        adapter = new FeedAdapter(getActivity(), list);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.scroll);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //adapter.setLockedAnimations(true);
            }
        });

        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;

        if (isFollowing) {
            toggleUnfollow(button);
        } else {
            toggleFollowing(button);
        }

        isFollowing = !isFollowing;

        Log.v("You ","select: " + button.getText());
    }


    public void initButton(boolean following,View v) {
        Button button = (Button) v;

        isFollowing = following;

        if (following) {
            toggleFollowing(button);
        } else {
            toggleUnfollow(button);
        }

        //isFollowing = !isFollowing;
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

    @Subscribe
    public void onLoadProfile(GetUserProfileSuccessEvent event) {

        titleTv.setText(event.getUser().getName());
        usernameTv.setText("@" + event.getUser().getUsername());
        bioTv.setText(Html.fromHtml(event.getUser().getAbout()));

        Picasso.with(getActivity())
                .load(event.getUser().getCoverUrl())
                .fit().centerCrop()
                .into(cover);

        Picasso.with(getActivity())
                .load(event.getUser().getAvatarUrl())
                .centerCrop()
                .resize(100, 100)
                .transform(new RoundedTransformation(50, 4))
                .into(avatar);

        countPost.setText(event.getCount().post + "");
        countFollowing.setText(event.getCount().following + "");
        countFollower.setText(event.getCount().follower + "");
        countFriend.setText(event.getCount().friend + "");

        isFollowing = event.getUser().getIsFollowing();
        initButton(isFollowing,btnFollow);


    }

    @Subscribe
    public void onLoadTimelineSuccess(LoadTimelineSuccessEvent event) {
        list.addAll(event.getTimelineData().getPosts());
        adapter.notifyDataSetChanged();
        Log.e("itemCountAfterNotify", adapter.getItemCount() + "");
    }

}

