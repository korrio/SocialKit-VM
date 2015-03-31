package co.aquario.socialkit.adapter;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import co.aquario.socialkit.R;
import co.aquario.socialkit.activity.CommentsActivity;
import co.aquario.socialkit.activity.PhotoActivity;
import co.aquario.socialkit.fragment.VideoViewFragment;
import co.aquario.socialkit.activity.VideoViewNativeActivity;
import co.aquario.socialkit.activity.YoutubeActivity;
import co.aquario.socialkit.event.PhotoLoadEvent;
import co.aquario.socialkit.fragment.FeedFragment;
import co.aquario.socialkit.fragment.ProfileDetailFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.PostStory;
import co.aquario.socialkit.widget.RoundedTransformation;


public class FeedAdapterPhotoList extends RecyclerView.Adapter<FeedAdapterPhotoList.ViewHolder> {

    private final Map<Integer, Integer> likesCount = new HashMap<>();
    private final Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimations = new HashMap<>();
    private final ArrayList<Integer> likedPositions = new ArrayList<>();

    private ArrayList<PostStory> list = new ArrayList<>();
    private static Activity mActivity;

    private OnItemClickListener mItemClickListener;
    private OnItemClickListener mItemLove;
    private OnItemClickListener mItemShare;

    private FeedFragment mFragment;

    public FeedAdapterPhotoList(Activity mActivity, ArrayList<PostStory> list) {
        this.mActivity = mActivity;
        this.list = list;

        ApiBus.getInstance().register(this);
    }

    public FeedAdapterPhotoList(Activity mActivity, ArrayList<PostStory> list, FeedFragment fragment) {
        this.mActivity = mActivity;
        this.list = list;
        mFragment = fragment;

        ApiBus.getInstance().register(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("FeedAdapter.viewType", viewType + "");
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        final View sView = mInflater.inflate(R.layout.item_feed, parent, false);
        return new ViewHolder(sView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PostStory item = list.get(position);

        holder.soundCloudLayout.setVisibility(View.GONE);
        holder.trackTitle.setVisibility(View.GONE);
        holder.btnPlay.setVisibility(View.GONE);

        holder.typeIcon.setVisibility(View.VISIBLE);
        holder.nView.setVisibility(View.VISIBLE);

        holder.thumb.setVisibility(View.GONE);
        holder.mediaLayout.setVisibility(View.VISIBLE);
        holder.soundCloudLayout.setVisibility(View.VISIBLE);

        PrettyTime p = new PrettyTime();
        long agoLong = Integer.parseInt(item.time);
        Date timeAgo = new Date(agoLong * 1000);
        String ago = p.format(timeAgo);

        holder.name.setText(item.author.name);
        holder.ago.setText(item.timestamp);
        holder.nLove.setText(item.loveCount + "");
        holder.nComment.setText(item.commentCount + "");
        holder.nShare.setText(item.shareCount + "");
        holder.ago.setText(ago);

        if (item.text != null) {
            if (item.text.trim().length() < 200)
                holder.msg.setText(Html.fromHtml("" + item.text + ""));
            else
                holder.msg.setText(Html.fromHtml("" + item.text.substring(0, 200) + " ..." + ""));
        } else {
            holder.msg.setVisibility(View.GONE);
        }

        Picasso.with(mActivity)
                .load(item.author.getAvatarPath())
                .centerCrop()
                .resize(100, 100)
                .transform(new RoundedTransformation(50, 4))
                .into(holder.avatar);

        if (checkNull(item.media)) {
            holder.thumb.setVisibility(View.VISIBLE);
            Picasso.with(mActivity)
                    .load(item.media.getThumbUrl())

                    .error(R.drawable.offline)
                    .fit().centerCrop()
                    .into(holder.thumb);
            Picasso.with(mActivity).load(R.drawable.ic_photo).into(holder.typeIcon);
            holder.nView.setText(item.view + " views");
            //holder.nView.setVisibility(View.GONE);
            //holder.typeIcon.setVisibility(View.GONE);
        } else if (checkNull(item.youtube)) {
            holder.thumb.setVisibility(View.VISIBLE);
            Picasso.with(mActivity)
                    .load(item.youtube.thumbnail)

                    .error(R.drawable.offline)
                    .fit().centerCrop()
                    .into(holder.thumb);
            Picasso.with(mActivity).load(R.drawable.ic_yt).into(holder.typeIcon);
            holder.nView.setText(item.view + " views");
        } else if (checkNull(item.clip)) {
            holder.thumb.setVisibility(View.VISIBLE);
            Picasso.with(mActivity)
                    .load(item.clip.thumb)
                    .error(R.drawable.offline)
                    .fit().centerCrop()
                    .into(holder.thumb);
            Picasso.with(mActivity).load(R.drawable.ic_clip).into(holder.typeIcon);
            holder.nView.setText(item.view + " views");


        } else if (checkNull(item.soundCloud)) {

            holder.soundCloudLayout.setVisibility(View.VISIBLE);
            holder.trackTitle.setVisibility(View.VISIBLE);
            holder.btnPlay.setVisibility(View.VISIBLE);
            Picasso.with(mActivity)
                    .load(R.drawable.ic_soundcloud)
                    .fit().centerCrop()
                            //.resize(128,128)
                    //.centerInside()
                            //.error(R.drawable.offline)
                    .into(holder.thumb);

            holder.typeIcon.setVisibility(View.GONE);
            holder.nView.setVisibility(View.GONE);

            holder.trackTitle.setText(item.soundCloud.title);

            //holder.soundCloudLayout.setVisibility(View.VISIBLE);
            //holder.mediaLayout.setVisibility(View.GONE);


            //Picasso.with(mActivity).load(R.drawable.ic_sound).into(holder.typeIcon);


        }  else if(!checkNull(item.soundCloud) && !checkNull(item.clip) && !checkNull(item.youtube) && !checkNull(item.media)) {
            holder.nView.setVisibility(View.GONE);
            holder.thumb.setVisibility(View.GONE);
            holder.mediaLayout.setVisibility(View.GONE);
            holder.soundCloudLayout.setVisibility(View.GONE);
            holder.msg.setTextSize(18);
        }

        if(item.type.equals("live")){
            holder.thumb.setVisibility(View.VISIBLE);
            holder.mediaLayout.setVisibility(View.VISIBLE);
            holder.msg.setTextSize(18);
            Picasso.with(mActivity)
                    .load(item.author.liveCover)
                    .error(R.drawable.offline)
                    .fit().centerCrop()
                    .into(holder.thumb);
        }

    }

    private boolean checkNull(Object obj) {
        return obj != null;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        TextView ago;
        TextView nLove;
        TextView nComment;
        TextView nShare;
        TextView msg;
        ImageView avatar;
        ImageView thumb;
        TextView nView;
        ImageView typeIcon;

        Button btnLove;
        Button btnComment;
        Button btnShare;

        RelativeLayout mediaLayout;
        RelativeLayout soundCloudLayout;

        TextView trackTitle;
        ImageButton btnPlay;


        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.profile_name);
            ago = (TextView) view.findViewById(R.id.ago);
            nLove = (TextView) view.findViewById(R.id.number1);
            nComment = (TextView) view.findViewById(R.id.number2);
            nShare = (TextView) view.findViewById(R.id.number3);
            msg = (TextView) view.findViewById(R.id.text);
            avatar = (ImageView) view.findViewById(R.id.profile_avatar);
            thumb = (ImageView) view.findViewById(R.id.thumb);
            nView = (TextView) view.findViewById(R.id.view);
            typeIcon = (ImageView) view.findViewById(R.id.ic_type);

            btnComment = (Button) view.findViewById(R.id.btn_comment);
            btnLove = (Button) view.findViewById(R.id.btn_love);
            btnShare = (Button) view.findViewById(R.id.btn_share);

            mediaLayout = (RelativeLayout) view.findViewById(R.id.media_group);
            soundCloudLayout = (RelativeLayout) view.findViewById(R.id.soundcloud_group);

            trackTitle = (TextView) view.findViewById(R.id.track_title);
            btnPlay = (ImageButton) view.findViewById(R.id.btn_track_play);
            btnPlay.setOnClickListener(this);


            thumb.setOnClickListener(this);
            avatar.setOnClickListener(this);
            btnComment.setOnClickListener(this);
            btnLove.setOnClickListener(this);
            btnShare.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            PostStory post = list.get(getPosition());
            String postType = post.type;


            switch (v.getId()) {
                case R.id.thumb:
                    if (postType.equals("photo")) {
                        String url = list.get(getPosition()).media.getThumbUrl();
                        String name = list.get(getPosition()).author.name;
                        String desc = list.get(getPosition()).text;

                        Intent i = new Intent(mActivity, PhotoActivity.class);
                        i.putExtra("url", url);
                        i.putExtra("name",name);
                        i.putExtra("desc",desc);
                        mActivity.startActivity(i);

                        PhotoLoadEvent event = new PhotoLoadEvent(url);
                        ApiBus.getInstance().post(event);



                    } else if (postType.equals("clip")) {

                        Intent i = new Intent(mActivity, VideoViewNativeActivity.class);
                        i.putExtra("url", post.clip.url);
                        i.putExtra("userId", post.author.id);
                        i.putExtra("avatar", post.author.getAvatarPath());
                        i.putExtra("cover", post.author.getCoverPath());
                        i.putExtra("name", post.author.name);
                        i.putExtra("username", post.author.username);
                        mActivity.startActivity(i);

                    } else if (postType.equals("youtube")) {

                        Intent i = new Intent(mActivity, YoutubeActivity.class);
                        i.putExtra("id", post.youtube.id);
                        i.putExtra("title", post.youtube.title);
                        i.putExtra("desc", post.youtube.desc);
                        i.putExtra("name", post.author.name);
                        i.putExtra("avatar", post.author.getAvatarPath());
                        i.putExtra("ago", post.getAgoText());
                        mActivity.startActivity(i);

                    } else if (postType.equals("soundcloud")) {
                        if (mFragment != null) {
                            mFragment.playTrack(post.soundCloud.streamUrl,post.soundCloud.title);
                            Log.e("heysoundcloud", post.soundCloud.streamUrl);
                        }
                    } else if(postType.equals("live")) {
                        Intent i = new Intent(mActivity,VideoViewFragment.class);
                        i.putExtra("url", post.author.liveUrl);
                        i.putExtra("userId", post.author.id);
                        i.putExtra("avatar", post.author.getAvatarPath());
                        i.putExtra("cover", post.author.getCoverPath());
                        i.putExtra("name", post.author.name);
                        i.putExtra("username", post.author.username);
                        mActivity.startActivity(i);
                    }
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, getPosition());
                    }
                    break;
                case R.id.btn_comment:

                    final Intent intent = new Intent(mActivity, CommentsActivity.class);
                    int[] startingLocation = new int[2];
                    v.getLocationOnScreen(startingLocation);
                    intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
                    intent.putParcelableArrayListExtra(CommentsActivity.ARG_COMMENT_LIST,post.comment);
                    intent.putExtra("POST_ID",post.postId);
                    mActivity.startActivity(intent);
                    mActivity.overridePendingTransition(0, 0);

                    /*
                    TimelineDataEvent event = new TimelineDataEvent(list.get(getPosition()));
                    ApiBus.getInstance().post(event);

                    Intent i = new Intent(mActivity, CommentActivity.class);
                    i.putExtra("postId", list.get(getPosition()).postId);
                    mActivity.startActivity(i);
                    */
                    break;
                case R.id.profile_name:
                case R.id.avatar:
                    ProfileDetailFragment fragment = new ProfileDetailFragment().newInstance(post.author.id);
                    FragmentManager manager = ((ActionBarActivity) mActivity).getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.sub_container, fragment).addToBackStack(null);
                    transaction.commit();
                    break;
                case R.id.btn_love:

                    if (mItemLove != null) {
                        mItemLove.onItemClick(v, getPosition());
                    }
                    break;
                case R.id.btn_share:
                    if (mItemShare != null) {
                        mItemShare.onItemClick(v, getPosition());
                    }
                    break;
                case R.id.btn_track_play:
                    if (mFragment != null) {
                        mFragment.playTrack(post.soundCloud.streamUrl,post.soundCloud.title);
                        Log.e("heysoundcloud", post.soundCloud.streamUrl);
                    }
                    break;



            }
        }

    }

    /*

    private void updateLikesCounter(ViewHolder holder, boolean animated) {
        holder.getPosition()
        int currentLikesCount = Integer.parseInt() + 1
        String likesCountText = context.getResources().getQuantityString(
                R.plurals.likes_count, currentLikesCount, currentLikesCount
        );

        if (animated) {
            holder.tsLikesCounter.setText(likesCountText);
        } else {
            holder.tsLikesCounter.setCurrentText(likesCountText);
        }

        likesCount.put(holder.getPosition(), currentLikesCount);
    }

    private void updateHeartButton(final ViewHolder holder, boolean animated) {
        if (animated) {
            if (!likeAnimations.containsKey(holder)) {
                AnimatorSet animatorSet = new AnimatorSet();
                likeAnimations.put(holder, animatorSet);

                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.btnLike, "rotation", 0f, 360f);
                rotationAnim.setDuration(300);
                rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.btnLike, "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.btnLike, "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                bounceAnimY.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        holder.btnLike.setImageResource(R.drawable.ic_heart_red);
                    }
                });

                animatorSet.play(rotationAnim);
                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        resetLikeAnimationState(holder);
                    }
                });

                animatorSet.start();
            }
        } else {
            if (likedPositions.contains(holder.getPosition())) {
                holder.btnLike.setImageResource(R.drawable.ic_heart_red);
            } else {
                holder.btnLike.setImageResource(R.drawable.ic_heart_outline_grey);
            }
        }
    }

    */


    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemLoveClick {
        public void onItemClick(View view, int position);
    }

    public void OnItemLoveClick(final OnItemClickListener mItemLove) {

        this.mItemLove = mItemLove;
    }


    public interface OnItemShareClick {
        public void onItemClick(View view, int position);
    }

    public void OnItemShareClick(final OnItemClickListener mItemShare) {
        this.mItemShare = mItemShare;
    }
}