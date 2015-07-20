package co.aquario.socialkit.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
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
import co.aquario.socialkit.R;
import co.aquario.socialkit.model.CommentStory;
import co.aquario.socialkit.model.User;
import co.aquario.socialkit.widget.RoundedTransformation;
import co.aquario.socialkit.widget.URLImageParser;


/**
 * Created by froger_mcs on 11.11.14.
 */
public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int itemsCount = 0;
    private int lastAnimatedPosition = -1;
    private int avatarSize;

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    ArrayList<CommentStory> mList = new ArrayList<CommentStory>();

    public CommentAdapter(Context context, ArrayList<CommentStory> mList) {
        this.context = context;
        this.mList = mList;
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.comment_avatar_size);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        CommentStory comment = mList.get(position);

        runEnterAnimation(viewHolder.itemView, position);
        CommentViewHolder holder = (CommentViewHolder) viewHolder;
        holder.tvComment.setText(comment.text);
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
                URLImageParser parser = new URLImageParser(holder.tvComment, context,1);
                Spanned htmlSpan = Html.fromHtml(textEmoticonized, parser, null);

                holder.tvComment.setText(htmlSpan);
            }

        } else {
            holder.tvComment.setVisibility(View.GONE);
        }

        Picasso.with(context)
                .load(comment.user.getAvatarUrl())
                .centerCrop()
                .resize(avatarSize, avatarSize)
                .transform(new RoundedTransformation(avatarSize/2,4))
                .into(holder.ivUserAvatar);
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
