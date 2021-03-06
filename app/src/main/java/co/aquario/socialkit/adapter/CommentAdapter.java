package co.aquario.socialkit.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import co.aquario.socialkit.BaseActivity;
import co.aquario.socialkit.NewProfileActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.event.toolbar.TitleEvent;
import co.aquario.socialkit.fragment.main.FeedFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.interfaces.TagClick;
import co.aquario.socialkit.model.CommentStory;
import co.aquario.socialkit.model.User;
import co.aquario.socialkit.util.Utils;
import co.aquario.socialkit.widget.RoundedTransformation;
import co.aquario.socialkit.widget.TagSelectingTextview;
import co.aquario.socialkit.widget.URLImageParser;


/**
 * Created by froger_mcs on 11.11.14.
 */
public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements TagClick {

    private Activity mActivity;
    //private Context context;
    private int itemsCount = 0;
    private int lastAnimatedPosition = -1;
    private int avatarSize;

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    ArrayList<CommentStory> mList = new ArrayList<CommentStory>();



    public CommentAdapter(Activity mActivity, ArrayList<CommentStory> mList) {
        this.mActivity = mActivity;
        this.mList = mList;
        avatarSize = mActivity.getResources().getDimensionPixelSize(R.dimen.comment_avatar_size);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mActivity.getApplicationContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        final CommentStory comment = mList.get(position);

        runEnterAnimation(viewHolder.itemView, position);
        CommentViewHolder holder = (CommentViewHolder) viewHolder;




        if(comment.text != null) {
            holder.tvComment.setMovementMethod(LinkMovementMethod.getInstance());
            TagSelectingTextview mTagSelectingTextview = new TagSelectingTextview();

            String hastTagColorBlue = "#5BCFF2", hastTagColorRed = "#FF0000",
                    hastTagColorYellow = "#FFFF00", hastTagColorGreen = "#014a01", hashtagColorIndigo500 = "#3f51b5",
                    testText, currentHashTagColor;

            int hashTagHyperLinkEnabled = 1;
            int hashTagHyperLinkDisabled = 0;

            holder.tvComment.setText(mTagSelectingTextview.addClickablePart(
                            Utils.bbcode(comment.text), this, hashTagHyperLinkDisabled, hashtagColorIndigo500),
                    TextView.BufferType.SPANNABLE);
        }


        //holder.tvComment.setText(comment.text);
        holder.tvName.setText(Html.fromHtml(comment.user.getName()));

        if(comment.time != null) {
            PrettyTime p = new PrettyTime();
            long agoLong = Integer.parseInt(comment.time);
            Date timeAgo = new java.util.Date(agoLong * 1000);
            String ago = p.format(timeAgo);

            holder.tvAgo.setText(ago);
        }


        String textEmoticonized = comment.emoticonized;

        if (comment.text != null) {

            if (textEmoticonized != null) {
                URLImageParser parser = new URLImageParser(holder.tvComment, mActivity.getApplicationContext(),1);
                Spanned htmlSpan = Html.fromHtml(textEmoticonized, parser, null);

                holder.tvComment.setText(htmlSpan);
            }

        } else {
            holder.tvComment.setVisibility(View.GONE);
        }

        Picasso.with(mActivity.getApplicationContext())
                .load(comment.user.getAvatarUrl())
                .centerCrop()
                .resize(avatarSize, avatarSize)
                .transform(new RoundedTransformation(avatarSize/2,4))
                .into(holder.ivUserAvatar);

        holder.ivUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mActivity, NewProfileActivity.class);
                i.putExtra("USER_ID",comment.user.id);
                mActivity.startActivity(i);
            }
        });
    }

    private void runEnterAnimation(View view, int position) {
        if (animationsLocked) return;

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(100);
            view.setAlpha(0.f);
            view.animate()
                    .translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? 20 * (position) : 0)
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(300)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = true;
                        }
                    })
                    .start();
        }
    }

    public void setAnimationsLocked(boolean animationsLocked) {
        this.animationsLocked = animationsLocked;
    }

    public void setDelayEnterAnimation(boolean delayEnterAnimation) {
        this.delayEnterAnimation = delayEnterAnimation;
    }

    @Override
    public int getItemCount() {
        return mList.size();

    }

    public void updateItems() {
        itemsCount = mList.size();
        notifyDataSetChanged();
    }

    public void addItem(String mCommentText,String avatar, String name) {
        CommentStory comment = new CommentStory();
        comment.text = mCommentText;
        User user = new User();
        user.setAvatar(avatar);
        user.setName(name);
        comment.user = user;
        mList.add(comment);
        itemsCount++;
        notifyItemInserted(itemsCount - 1);
    }

    @Override
    public void clickedTag(String tag) {
        if(mActivity != null) {
            if(tag.startsWith("@")) {
                //NewProfileActivity.startProfileActivity(mActivity,tag.substring(1));
                FeedFragment fragment = new FeedFragment().newInstance(tag.substring(1), false);
                FragmentManager manager = ((BaseActivity) mActivity).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.sub_container, fragment).addToBackStack(null);
                transaction.commitAllowingStateLoss();
                ApiBus.getInstance().postQueue(new TitleEvent(tag));
            } else if(tag.startsWith("#")) {
                FeedFragment fragment = new FeedFragment().newInstance(tag.substring(1));
                FragmentManager manager = ((BaseActivity) mActivity).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.sub_container, fragment).addToBackStack(null);
                transaction.commitAllowingStateLoss();
                ApiBus.getInstance().postQueue(new TitleEvent(tag));
            }
        }
    }


    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.ivUserAvatar)
        ImageView ivUserAvatar;
        @InjectView(R.id.tvComment)
        TextView tvComment;
        @InjectView(R.id.tvName)
        TextView tvName;
        @InjectView(R.id.tvAgo)
        TextView tvAgo;

        public CommentViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }
}
