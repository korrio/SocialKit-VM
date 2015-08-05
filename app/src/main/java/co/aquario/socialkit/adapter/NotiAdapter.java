package co.aquario.socialkit.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import co.aquario.chatapp.model.Noti;
import co.aquario.socialkit.R;
import co.aquario.socialkit.widget.RoundedTransformation;


/**
 * Created by froger_mcs on 11.11.14.
 */
public class NotiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int itemsCount = 0;
    private int lastAnimatedPosition = -1;
    private int avatarSize;

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    ArrayList<Noti> mList = new ArrayList<Noti>();

    public NotiAdapter(Context context, ArrayList<Noti> mList) {
        this.context = context;
        this.mList = mList;
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.comment_avatar_size);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_noti, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        Noti noti = mList.get(position);

        runEnterAnimation(viewHolder.itemView, position);
        CommentViewHolder holder = (CommentViewHolder) viewHolder;

        holder.tvName.setText(noti.from_name);
        holder.tvComment.setText(noti.msg);

        holder.tvAgo.setText(noti.ago);

        if(noti.getAvatarUrl() != null)
        Picasso.with(context)
                .load(noti.getAvatarUrl())
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

    @Override
    public int getItemCount() {
        return mList.size();

    }

    public void updateItems() {
        itemsCount = mList.size();
        notifyDataSetChanged();
    }

//    public void addItem(String mCommentText,String avatar, String name) {
//        Noti noti = new Noti();
//        comment.setText(mCommentText);
//        User user = new User();
//        user.setAvatar(avatar);
//        user.setName(name);
//        comment.setUser(user);
//        mList.add(comment);
//        itemsCount++;
//        notifyItemInserted(itemsCount - 1);
//    }

    public void setAnimationsLocked(boolean animationsLocked) {
        this.animationsLocked = animationsLocked;
    }

    public void setDelayEnterAnimation(boolean delayEnterAnimation) {
        this.delayEnterAnimation = delayEnterAnimation;
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
