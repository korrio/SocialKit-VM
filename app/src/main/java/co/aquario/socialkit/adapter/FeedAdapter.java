package co.aquario.socialkit.adapter;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
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
import org.parceler.Parcels;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import co.aquario.socialkit.R;
import co.aquario.socialkit.activity.CommentsActivity;
import co.aquario.socialkit.activity.YoutubeDragableActivity;
import co.aquario.socialkit.fragment.PhotoZoomFragment;
import co.aquario.socialkit.fragment.main.FeedFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.PostStory;
import co.aquario.socialkit.model.Video;
import co.aquario.socialkit.widget.RoundedTransformation;
import co.aquario.socialkit.widget.URLImageParser;


public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private static Activity mActivity;
    private final Map<Integer, Integer> likesCount = new HashMap<>();
    private final Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimations = new HashMap<>();
    private final ArrayList<Integer> likedPositions = new ArrayList<>();
    public boolean isHomeTimeline;
    private ArrayList<PostStory> list = new ArrayList<>();
    private OnItemClickListener mItemClickListener;
    private OnItemClickListener mItemLove;
    private OnItemClickListener mItemShare;
    private FeedFragment mFragment;

    public FeedAdapter(Activity mActivity, ArrayList<PostStory> list) {
        FeedAdapter.mActivity = mActivity;
        this.list = list;

        ApiBus.getInstance().register(this);
    }

    public FeedAdapter(Activity mActivity, ArrayList<PostStory> list, FeedFragment fragment, boolean isHomeTimeline) {
        FeedAdapter.mActivity = mActivity;
        this.list = list;
        this.isHomeTimeline = isHomeTimeline;
        mFragment = fragment;

        ApiBus.getInstance().register(this);
    }

    public void add(PostStory item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(PostStory item) {
        int position = list.indexOf(item);
        list.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public int getItemViewType(int position) {

        String postType = list.get(position).type;
        //Log.e("getItemViewType",position + ":" + postType);
        switch (postType) {
            case "text":
                return 0;
            case "tattoo":
                return 1;
            case "photo":
                if (list.get(position).media.type.equals("album"))
                    return 21;
                else
                    return 2;
            case "clip":
                return 3;
            case "youtube":
                return 4;
            case "soundcloud":
                return 5;
            case "ppv":
                return 6;
            case "live":
                return 7;
            case "map":
                return 8;
            case "ad":
                return 9;
            default:
                return 0;
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Log.e("FeedAdapter.viewType", viewType + "");
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        View sView = null;
        switch (viewType) {
            case 0:
                sView = mInflater.inflate(R.layout.item_feed_text, parent, false);
                break;
            case 1:
                sView = mInflater.inflate(R.layout.item_feed_tattoo, parent, false);
                break;
            case 2:
                sView = mInflater.inflate(R.layout.item_feed_photo, parent, false);
                break;
            case 21:
                sView = mInflater.inflate(R.layout.item_feed_photo, parent, false);
                break;
            case 3:
                sView = mInflater.inflate(R.layout.item_feed_clip, parent, false);
                break;
            case 4:
                sView = mInflater.inflate(R.layout.item_feed_clip, parent, false);
                break;
            case 5:
                sView = mInflater.inflate(R.layout.item_feed_soundcloud, parent, false);
                break;
            case 6:
                sView = mInflater.inflate(R.layout.item_feed_clip, parent, false);
                break;
            case 7:
                sView = mInflater.inflate(R.layout.item_feed_live, parent, false);
                break;
            case 8:
                sView = mInflater.inflate(R.layout.item_feed_map, parent, false);
                break;
            case 9:
                break;
            default:
                sView = mInflater.inflate(R.layout.item_feed, parent, false);
                break;

        }

        return new ViewHolder(sView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PostStory item = list.get(position);

        if(item.isLoved)
            holder.btnLove.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_love_vm_red,0,0,0);

        //if(item.isShared)
            //holder.btnShare.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_share,0,0,0);

        PrettyTime p = new PrettyTime();
        long agoLong = Integer.parseInt(item.time);
        Date timeAgo = new java.util.Date(agoLong * 1000);
        String ago = p.format(timeAgo);

        holder.name.setText(item.author.name);
        holder.ago.setText(item.timestamp);
        holder.nLove.setText(item.loveCount + "");
        holder.nComment.setText(item.commentCount + "");
        holder.nShare.setText(item.shareCount + "");
        holder.ago.setText(ago);


        Picasso.with(mActivity)
                .load(item.author.getAvatarPath())
                .centerCrop()
                .resize(100, 100)
                .transform(new RoundedTransformation(50, 4))
                .into(holder.avatar);

        if (isNull(item.text)) {
            holder.msg.setVisibility(View.GONE);
        } else {
            if (item.text.trim().length() < 200)
                holder.msg.setText(Html.fromHtml("" + item.text + ""));
            else
                holder.msg.setText(Html.fromHtml("" + item.text.substring(0, 200) + " ..." + ""));
        }

        if (item.type.equals("text")) {

            holder.msg.setTextSize(18.0f);

            /*
            URLImageParser parser = new URLImageParser(holder.msg, mActivity,2);
            Spanned htmlSpan = Html.fromHtml(textEmoticonized, parser, null);
            holder.msg.setText(htmlSpan);
            */


        } else if (item.type.equals("tattoo")) {
            holder.msg.setVisibility(View.GONE);
            Picasso.with(mActivity)
                    .load(item.tattooUrl)
                            //.resize(100,100)
                    .fit().centerInside()
                    .into(holder.thumb);

        } else if (item.type.equals("photo")) {
            Picasso.with(mActivity)
                    .load(item.media.getThumbUrl())
                    .error(R.drawable.default_offline)
                    .fit().centerCrop()
                    .into(holder.thumb);
            Picasso.with(mActivity).load(R.drawable.ic_photo).into(holder.typeIcon);
            holder.nView.setText(item.view + " views");

        } else if (item.type.equals("clip")) {

            Picasso.with(mActivity)
                    .load(item.clip.thumb)
                    .error(R.drawable.default_offline)
                    .fit().centerCrop()
                    .into(holder.thumb);

            Picasso.with(mActivity).load(R.drawable.ic_clip).into(holder.typeIcon);
            holder.nView.setText(item.view + " views");

        } else if (item.type.equals("youtube")) {

            Picasso.with(mActivity)
                    .load(item.youtube.thumbnail)
                    .error(R.drawable.default_offline)
                    .fit().centerCrop()
                    .into(holder.thumb);

            Picasso.with(mActivity).load(R.drawable.ic_yt).into(holder.typeIcon);
            holder.nView.setText(item.view + " views");
        } else if (item.type.equals("soundcloud")) {

            if (item.soundCloud.title.trim().length() < 40)
                holder.trackTitle.setText(Html.fromHtml("" + Html.fromHtml("" + item.soundCloud.title + "") + ""));
            else
                holder.trackTitle.setText(Html.fromHtml("" + Html.fromHtml(item.soundCloud.title.substring(0, 40)) + " ..." + ""));

            holder.nView.setText(item.view + " listens");

            //holder.trackTitle.setText(item.soundCloud.title);
            //holder.trackSubtitle.setText(item.soundCloud.trackId);
//            holder.mediaLayout.setVisibility(View.GONE);


        } else if (item.type.equals("ppv")) {
            Picasso.with(mActivity)
                    .load(item.clip.thumb)
                    .error(R.drawable.default_offline)
                    .fit().centerCrop()
                    .into(holder.thumb);

            Picasso.with(mActivity).load(R.drawable.ic_clip).into(holder.typeIcon);
            holder.nView.setText(item.view + " views");
        } else if (item.type.equals("live")) {
            Picasso.with(mActivity)
                    .load(item.author.liveCover)
                    .error(R.drawable.default_offline)
                    .fit().centerCrop()
                    .into(holder.thumb);
        } else if (item.type.equals("map")) {
            String mapUrl = null;
            try {
                mapUrl = "https://maps.googleapis.com/maps/api/staticmap?center=" + URLEncoder.encode(item.google_map_name, "UTF-8") + "&zoom=16&size=400x300&maptype=roadmap";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Picasso.with(mActivity)
                    .load(mapUrl)
                    .error(R.drawable.default_offline)
                    .fit().centerCrop()
                    .into(holder.thumb);

            //Log.e("mapUrl",mapUrl);



        }


        if (item.comment != null) {
            if (item.comment.size() >= 1) {

                if (item.comment.get(0).getmEmoticonizedText() != null) {
                    URLImageParser parser = new URLImageParser(holder.tvComment1, mActivity,1);
                    Spanned htmlSpan = Html.fromHtml(item.comment.get(0).getmEmoticonizedText(), parser, null);
                    holder.tvComment1.setText(htmlSpan);
                }


                //holder.tvComment1.setText(item.comment.get(0).getText());
                holder.tvName1.setText(item.comment.get(0).getUser().getName());
                Picasso.with(mActivity)
                        .load(item.comment.get(0).getUser().getAvatarUrl())
                        .centerCrop()
                        .resize(100, 100)
                        .transform(new RoundedTransformation(50, 4))
                        .into(holder.ivUserAvatar1);
            } else {
                holder.comment1.setVisibility(View.GONE);
                holder.comment2.setVisibility(View.GONE);
            }

            if (item.comment.size() >= 2) {
                if (item.comment.get(1).getmEmoticonizedText() != null) {
                    URLImageParser parser = new URLImageParser(holder.tvComment2, mActivity,1);
                    Spanned htmlSpan = Html.fromHtml(item.comment.get(1).getmEmoticonizedText(), parser, null);
                    holder.tvComment2.setText(htmlSpan);
                }
                // holder.tvComment2.setText(item.comment.get(1).getText());
                holder.tvName2.setText(item.comment.get(1).getUser().getName());
                Picasso.with(mActivity)
                        .load(item.comment.get(1).getUser().getAvatarUrl())
                        .centerCrop()
                        .resize(100, 100)
                        .transform(new RoundedTransformation(50, 4))
                        .into(holder.ivUserAvatar2);
            } else {
                holder.comment2.setVisibility(View.GONE);
            }

        } else {
            holder.comment1.setVisibility(View.GONE);
            holder.comment2.setVisibility(View.GONE);
        }

    }

    public void updateBadge(int position) {

    }

    private boolean isNull(Object obj) {
        return obj == null;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void OnItemLoveClick(final OnItemClickListener mItemLove) {

        this.mItemLove = mItemLove;
    }

    public void OnItemShareClick(final OnItemClickListener mItemShare) {
        this.mItemShare = mItemShare;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLoveClick {
        void onItemClick(View view, int position);
    }


    public interface OnItemShareClick {
        void onItemClick(View view, int position);
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
        TextView trackSubtitle;
        ImageButton btnPlay;

        //Comment_view
        ImageView ivUserAvatar1;
        TextView tvName1;
        TextView tvComment1;

        ImageView ivUserAvatar2;
        TextView tvName2;
        TextView tvComment2;

        View comment1;
        View comment2;

        RelativeLayout feedThumb;


        public ViewHolder(View view) {
            super(view);


            feedThumb = (RelativeLayout) view.findViewById(R.id.feed_thumb_group);

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
            trackSubtitle = (TextView) view.findViewById(R.id.track_sub_title);
            btnPlay = (ImageButton) view.findViewById(R.id.btn_track_play);

            comment1 = view.findViewById(R.id.comment_view_1);
            comment2 = view.findViewById(R.id.comment_view_2);

            ivUserAvatar1 = (ImageView) comment1.findViewById(R.id.ivUserAvatar);
            tvName1 = (TextView) comment1.findViewById(R.id.tvName);
            tvComment1 = (TextView) comment1.findViewById(R.id.tvComment);

            ivUserAvatar2 = (ImageView) comment2.findViewById(R.id.ivUserAvatar);
            tvName2 = (TextView) comment2.findViewById(R.id.tvName);
            tvComment2 = (TextView) comment2.findViewById(R.id.tvComment);

            thumb.setOnClickListener(this);
            avatar.setOnClickListener(this);
            btnComment.setOnClickListener(this);
            btnLove.setOnClickListener(this);
            btnShare.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int clickedPos = getPosition();
            if (!isHomeTimeline)
                clickedPos--;

            PostStory post = list.get(clickedPos);
            String postType = post.type;


            switch (v.getId()) {
                case R.id.thumb:
                    if (postType.equals("photo")) {
                        String url = post.media.getThumbUrl();
                        String name = post.author.name;

                        String text = post.text;
                        if(text == null)
                            text = "";
                        else {
                            text = Html.fromHtml("" + list.get(clickedPos).text + "").toString();
                            if (text.trim().length() >= 200)
                                text = text.substring(0, 200);
                        }


                        //PhotoZoomFragment fragment = new PhotoZoomFragment();



                        PhotoZoomFragment fragment = new PhotoZoomFragment().newInstance(url,name,text);
                        FragmentManager manager = ((ActionBarActivity) mActivity).getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.sub_container, fragment).addToBackStack(null);
                        transaction.commit();

                        /*
                        Intent i = new Intent(mActivity, PhotoActivity.class);
                        i.putExtra("url", url);
                        i.putExtra("name", name);
                        i.putExtra("desc", text);
                        mActivity.startActivity(i);
                        */



                    } else if (postType.equals("clip")) {

                        /*
                        Intent i = new Intent(mActivity, VideoViewNativeActivity.class);
                        i.putExtra("url", post.clip.url);
                        i.putExtra("userId", post.author.id);
                        i.putExtra("avatar", post.author.getAvatarPath());
                        i.putExtra("cover", post.author.getCoverPath());
                        i.putExtra("name", post.author.name);
                        i.putExtra("username", post.author.username);
                        mActivity.startActivity(i);
                        */



                        /*
                        Intent i = new Intent(mActivity,VitamioActivity.class);
                        i.putExtra("id",post.clip.url);
                        i.putExtra("name",post.author.name);
                        i.putExtra("avatar",post.author.getAvatarPath());
                        i.putExtra("cover",post.author.getCoverPath());
                        i.putExtra("title",post.text);
                        i.putExtra("desc","@"+post.author.username);
                        i.putExtra("userId",post.author.id);
                        mActivity.startActivity(i);

                        */

                       // VideoPlayerActivity.startActivity(mActivity,post.clip.url);

                    } else if (postType.equals("youtube")) {


                        //String location = "https://www.youtube.com/watch?v=SvDMZFfwmgo";
                        //String location = "https://www.youtube.com/watch?v=" + post.youtube.id;

                        //String location = post.youtube.id;
                        //if (YouTubeData.isYouTubeUrl(location)) {
                            //Intent i = new Intent(mActivity, VideoTrailerPlayerActivity.class);
                            //i.putExtra(VideoTrailerPlayerActivity.LOCATION, post.youtube.id);
                            //mActivity.startActivity(i);
                        //}

                        //Intent lVideoIntent = new Intent(null, Uri.parse("ytv://" + post.youtube.id), mActivity, OpenYouTubePlayerActivity.class);
                        //mActivity.startActivity(lVideoIntent);



    /*
                        Intent i = new Intent(mActivity, YoutubeActivity.class);
                        i.putExtra("id", post.youtube.id);
                        i.putExtra("title", post.youtube.title);
                        i.putExtra("desc", post.youtube.desc);
                        i.putExtra("name", post.author.name);
                        i.putExtra("avatar", post.author.getAvatarPath());
                        i.putExtra("ago", post.getAgoText());

                        mActivity.startActivity(i);
                        */

                        Video item = new Video(post.postId, post.youtube.title, post.youtube.desc, post.youtube.id, post.text, post.timestamp, post.view, post.author.id, post.author.name, post.author.getAvatarPath(), post.loveCount, post.commentCount, post.shareCount);

                        Intent i2 = new Intent(mActivity, YoutubeDragableActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("obj", Parcels.wrap(item));

                        i2.putExtras(bundle);
                        mActivity.startActivity(i2);




                    } else if (postType.equals("soundcloud")) {
                        if (mFragment != null) {
                            mFragment.playTrack(post.soundCloud.streamUrl,post.soundCloud.title);
                            //Log.e("heysoundcloud", post.soundCloud.streamUrl);
                        }
                    } else if(postType.equals("live")) {

                        /*
                        Intent i = new Intent(mActivity,VideoViewFragment.class);
                        i.putExtra("url", post.author.liveUrl);
                        i.putExtra("userId", post.author.id);
                        i.putExtra("avatar", post.author.getAvatarPath());
                        i.putExtra("cover", post.author.getCoverPath());
                        i.putExtra("name", post.author.name);
                        i.putExtra("username", post.author.username);
                        mActivity.startActivity(i);

                        */

                        //VideoPlayerActivity.startActivity(mActivity,post.author.liveUrl);
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
                    //intent.putParcelableArrayListExtra(CommentsActivity.ARG_COMMENT_LIST, Parcels.wrap(post.comment));
                    intent.putExtra("POST_ID", post.postId);
                    mActivity.startActivity(intent);
                    mActivity.overridePendingTransition(0, 0);

                    break;
                case R.id.profile_name:
                case R.id.avatar:
                    FeedFragment fragment = new FeedFragment().newInstance(post.author.id, false);
                    FragmentManager manager = ((ActionBarActivity) mActivity).getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.sub_container, fragment).addToBackStack(null);
                    transaction.commit();
                    break;
                case R.id.btn_love:
                    int oldLoveCount = 0;
                    if(Integer.parseInt(nLove.getText().toString()) == post.loveCount && !post.isLoved) {
                        oldLoveCount = post.loveCount;
                        oldLoveCount++;
                        btnLove.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_love_vm_red,0,0,0);

                    }
                    else {
                        oldLoveCount = Integer.parseInt(nLove.getText().toString());
                        oldLoveCount--;
                        btnLove.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                    }

                    post.isLoved = !post.isLoved;

                    nLove.setText(oldLoveCount + "");
                    if (mItemLove != null) {
                        mItemLove.onItemClick(v, getPosition());
                    }
                    break;
                case R.id.btn_share:
                    int oldShareCount = 0;
                    if(Integer.parseInt(nShare.getText().toString()) == post.shareCount && !post.isShared) {
                        oldShareCount = post.shareCount;
                        oldShareCount++;
                        btnShare.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_share,0,0,0);
                    }
                    else {
                        oldShareCount = Integer.parseInt(nShare.getText().toString());
                        oldShareCount--;
                        btnShare.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                    }
                    nShare.setText(oldShareCount + "");
                    if (mItemShare != null) {
                        mItemShare.onItemClick(v, getPosition());
                    }
                    break;
                case R.id.btn_track_play:
                    if (mFragment != null) {
                        mFragment.playTrack(post.soundCloud.streamUrl,post.soundCloud.title);
                        //Log.e("heysoundcloud", post.soundCloud.streamUrl);
                    }
                    break;



            }
        }

    }
}