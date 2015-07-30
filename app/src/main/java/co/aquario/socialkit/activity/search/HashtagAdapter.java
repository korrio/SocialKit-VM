package co.aquario.socialkit.activity.search;

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

import org.jsoup.Jsoup;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import co.aquario.socialkit.R;
import co.aquario.socialkit.model.Hashtag;


/**
 * Created by froger_mcs on 11.11.14.
 */
public class HashtagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int itemsCount = 0;
    private int lastAnimatedPosition = -1;
    private int avatarSize;

    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;

    ArrayList<Hashtag> mList = new ArrayList<>();

    public HashtagAdapter(Context context, ArrayList<Hashtag> mList) {
        this.context = context;
        this.mList = mList;
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.comment_avatar_size);
    }

    //private final View.OnClickListener mOnClickListener = new MyOnClickListener();


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_hashtag, parent, false);
        //view.setOnClickListener(mOnClickListener);
        return new CommentViewHolder(view);
    }

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        Hashtag hashtag = mList.get(position);

        runEnterAnimation(viewHolder.itemView, position);
        CommentViewHolder holder = (CommentViewHolder) viewHolder;



        holder.tvName.setText("#" + html2text(hashtag.tag));
        if(!hashtag.num.equals("1"))
            holder.tvComment.setText(hashtag.num + " posts");
        else
            holder.tvComment.setText(hashtag.num + " post");

        PrettyTime p = new PrettyTime();
        long agoLong = Integer.parseInt(hashtag.ago);
        Date timeAgo = new java.util.Date(agoLong * 1000);
        String ago = p.format(timeAgo);

        holder.tvAgo.setText(ago);

        Picasso.with(context)
                .load(R.drawable.ic_whatshot_black_24dp)
                //.centerCrop()
                //.resize(avatarSize, avatarSize)
                        //.transform(new RoundedTransformation(avatarSize/2,4))
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
