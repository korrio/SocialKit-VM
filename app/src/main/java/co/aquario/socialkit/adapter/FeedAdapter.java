package co.aquario.socialkit.adapter;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import co.aquario.socialkit.BaseActivity;
import co.aquario.socialkit.NewProfileActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.activity.CommentsActivity;
import co.aquario.socialkit.activity.DragableActivity;
import co.aquario.socialkit.fragment.main.FeedFragment;
import co.aquario.socialkit.fragment.main.LiveFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.interfaces.TagClick;
import co.aquario.socialkit.model.Channel;
import co.aquario.socialkit.model.PostStory;
import co.aquario.socialkit.model.Video;
import co.aquario.socialkit.util.Utils;
import co.aquario.socialkit.widget.RoundedTransformation;
import co.aquario.socialkit.widget.TagSelectingTextview;
import co.aquario.socialkit.widget.URLImageParser;
import me.iwf.photopicker.PhotoPagerActivity;


public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> implements TagClick {

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
            holder.btnLove.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_red,0,0,0);

        if(item.isShared)
            holder.btnShare.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_share_blue,0,0,0);

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

        holder.btnShare.setText(item.shareCount + "");
        holder.btnLove.setText(item.loveCount + "");
        holder.btnComment.setText(item.commentCount + "");


        Picasso.with(mActivity)
                .load(item.author.getAvatarPath())
                .centerCrop()
                .resize(200, 200)
                .transform(new RoundedTransformation(100, 4))
                .into(holder.avatar);


        if (isNull(item.text)) {
            holder.msg.setVisibility(View.GONE);
        } else {
            holder.msg.setMovementMethod(LinkMovementMethod.getInstance());
            /*
            if (item.text.trim().length() < 200)
                holder.msg.setText(Html.fromHtml("" + Utils.bbcode(item.text) + ""));
            else
                holder.msg.setText(Html.fromHtml("" + Utils.bbcode(item.text).substring(0, 200) + " ..." + ""));
                */

            TagSelectingTextview mTagSelectingTextview = new TagSelectingTextview();

            String hastTagColorBlue = "#5BCFF2", hastTagColorRed = "#FF0000",
                    hastTagColorYellow = "#FFFF00", hastTagColorGreen = "#014a01", hashtagColorIndigo500 = "#3f51b5",
                    testText, currentHashTagColor;

            int hashTagHyperLinkEnabled = 1;
            int hashTagHyperLinkDisabled = 0;

            holder.msg.setText(mTagSelectingTextview.addClickablePart(
                            Utils.bbcode(item.text), this, hashTagHyperLinkDisabled, hashtagColorIndigo500),
                    TextView.BufferType.SPANNABLE);
        }

        if (item.type.equals("text")) {

            holder.msg.setTextSize(18.0f);

            /*
            URLImageParser parser = toolbar URLImageParser(holder.msg, mActivity,2);
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
                    //.load(item.media.getFullUrl())
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
                    .placeholder(R.drawable.default_offline)
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

    @Override
    public void clickedTag(String tag) {
        if(tag.startsWith("@")) {
            FeedFragment fragment = new FeedFragment().newInstance(tag.substring(1), false);
            FragmentManager manager = ((BaseActivity) mActivity).getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.sub_container, fragment).addToBackStack(null);
            transaction.commitAllowingStateLoss();
        } else if(tag.startsWith("#")) {
            FeedFragment fragment = new FeedFragment().newInstance(tag.substring(1));
            FragmentManager manager = ((BaseActivity) mActivity).getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.sub_container, fragment).addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }

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

        Toolbar toolbar;


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

            toolbar = (Toolbar) view.findViewById(R.id.card_toolbar);
            if (toolbar != null) {
                //inflate your menu
                toolbar.inflateMenu(R.menu.menu_card);
                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        return true;
                    }
                });
            }

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
                    switch (postType) {
                        case "photo":
                            String url = post.media.getFullUrl();
                            String name = post.author.name;

                            String text = post.text;
                            if(text == null)
                                text = "";
                            else {
                                text = Html.fromHtml("" + list.get(clickedPos).text + "").toString();
                                if (text.trim().length() >= 200)
                                    text = text.substring(0, 200);
                            }

                            ArrayList<String> urls = new ArrayList<>();
                            urls.add(0,url);

                            Intent intent = new Intent(mActivity, PhotoPagerActivity.class);

                            intent.putExtra("current_item", 1);
                            intent.putStringArrayListExtra("photos", urls);

                            mActivity.startActivity(intent);

                            /*
                            PhotoZoomFragment fragment = new PhotoZoomFragment().newInstance(url,name,text);
                            FragmentManager manager = ((AppCompatActivity) mActivity).getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.add(R.id.sub_container, fragment).addToBackStack(null);
                            transaction.commit();
                            */
                            break;
                        case "clip":
                            //String sample = "https://stream-1.vdomax.com/vod/__definst__/mp4:110559/110559_720p.mp4/playlist.m3u8";
                            String clipURL = "http://stream-1.vdomax.com:1935/vod/__definst__/mp4:"+post.clip.id+"/"+post.clip.id+"_720p.mp4/playlist.m3u8";
                            Log.e("fromFeedAdapter", clipURL);

                            Video clip = new Video("clip",post.postId, post.author.name, "@"+post.author.username, clipURL, post.text, post.timestamp, post.view, post.author.id, post.author.name, post.author.getAvatarPath(), post.loveCount, post.commentCount, post.shareCount);

                            Intent intentClip = new Intent(mActivity, DragableActivity.class);
                            Bundle bundleClip = new Bundle();
                            bundleClip.putParcelable("obj", Parcels.wrap(clip));

                            intentClip.putExtras(bundleClip);
                            mActivity.startActivity(intentClip);
                            break;
                        case "youtube":
                            Video item = new Video("youtube",post.postId, post.youtube.title, post.youtube.desc, post.youtube.id, post.text, post.timestamp, post.view, post.author.id, post.author.name, post.author.getAvatarPath(), post.loveCount, post.commentCount, post.shareCount);

                            Intent i2 = new Intent(mActivity, DragableActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("obj", Parcels.wrap(item));

                            i2.putExtras(bundle);
                            mActivity.startActivity(i2);
                            break;
                        case "soundcloud":
                            if (mFragment != null) {
                                mFragment.playTrack(post.soundCloud.streamUrl,post.soundCloud.title);
                                //Log.e("heysoundcloud", post.soundCloud.streamUrl);
                            }
                            break;
                        case "live":
                            String userId = post.author.id;
                            String liveName = post.author.name;
                            String username = post.author.username;
                            String avatar = post.author.getAvatarPath();
                            String cover = post.author.getCoverPath();
                            String liveCover = post.author.liveCover;
                            String gender = "male";
                            boolean liveStatus = true;

                            Channel channel = new Channel(userId, liveName, username, cover, avatar, liveCover, "male", liveStatus);

                            LiveFragment liveFragment = LiveFragment.newInstance(channel);
                            ((AppCompatActivity) mActivity).getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, liveFragment, "WATCH_LIVE_MAIN").addToBackStack(null).commit();
                            break;
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
                case R.id.profile_avatar:
                    Intent i = new Intent(mActivity, NewProfileActivity.class);
                    i.putExtra("USER_ID",post.author.id);
                    mActivity.startActivity(i);
                    /*
                    FeedFragment fragment = new FeedFragment().newInstance(post.author.id, false);
                    FragmentManager manager = ((AppCompatActivity) mActivity).getSupportFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.sub_container, fragment).addToBackStack(null);
                    transaction.commit();
                    */
                    break;
                case R.id.btn_love:
                    int oldLoveCount = 0;
                    if(Integer.parseInt(nLove.getText().toString()) == post.loveCount && !post.isLoved) {
                        oldLoveCount = post.loveCount;
                        oldLoveCount++;
                        btnLove.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_red,0,0,0);

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
                        btnShare.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_share_blue,0,0,0);
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