package co.aquario.socialkit.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;

import co.aquario.socialkit.R;
import co.aquario.socialkit.activity.CommentsActivity;
import co.aquario.socialkit.activity.PhotoActivity;
import co.aquario.socialkit.activity.VideoViewNativeActivity;
import co.aquario.socialkit.activity.YoutubeActivity;
import co.aquario.socialkit.event.PhotoLoadEvent;
import co.aquario.socialkit.fragment.FeedFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.PostStory;
import co.aquario.socialkit.widget.RoundedTransformation;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private ArrayList<PostStory> list = new ArrayList<>();
    private static Activity mActivity;

    private OnItemClickListener mItemClickListener;
    private OnItemClickListener mItemLove;
    private OnItemClickListener mItemShare;

    private FeedFragment mFragment;

    public PhotoAdapter(Activity mActivity, ArrayList<PostStory> list) {
        this.mActivity = mActivity;
        this.list = list;

        ApiBus.getInstance().register(this);
    }

    public PhotoAdapter(Activity mActivity, ArrayList<PostStory> list, FeedFragment fragment) {
        this.mActivity = mActivity;
        this.list = list;
        mFragment = fragment;

        ApiBus.getInstance().register(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("FeedAdapter.viewType", viewType + "");
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        final View sView = mInflater.inflate(R.layout.item_feed_photo, parent, false);
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


        } else if(!checkNull(item.soundCloud) && !checkNull(item.clip) && !checkNull(item.youtube) && !checkNull(item.media)) {
            holder.nView.setVisibility(View.GONE);
            holder.thumb.setVisibility(View.GONE);
            holder.mediaLayout.setVisibility(View.GONE);
            holder.soundCloudLayout.setVisibility(View.GONE);
            holder.msg.setTextSize(18);
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


            mediaLayout = (RelativeLayout) view.findViewById(R.id.media_group);
            soundCloudLayout = (RelativeLayout) view.findViewById(R.id.soundcloud_group);

            trackTitle = (TextView) view.findViewById(R.id.track_title);
            btnPlay = (ImageButton) view.findViewById(R.id.btn_track_play);
            btnPlay.setOnClickListener(this);


            thumb.setOnClickListener(this);
            avatar.setOnClickListener(this);

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
                    mActivity.startActivity(intent);
                    mActivity.overridePendingTransition(0, 0);

                    break;
                case R.id.profile_name:
                case R.id.avatar:
                    //Intent intent = new Intent(mActivity, MainProfileFriends.class);
                    //mActivity.startActivity(intent);
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