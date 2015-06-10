package co.aquario.socialkit.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import co.aquario.socialkit.R;
import co.aquario.socialkit.event.FollowRegisterEvent;
import co.aquario.socialkit.fragment.main.FeedFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.User;
import co.aquario.socialkit.widget.RoundedTransformation;


public class FriendRecyclerAdapter extends RecyclerView.Adapter<FriendRecyclerAdapter.ViewHolder> {

    private static Activity mActivity;

    public ArrayList<User> list = new ArrayList<>();

    public OnItemClickListener mItemClickListener;

    public FriendRecyclerAdapter(Activity mActivity, ArrayList<User> list) {
        FriendRecyclerAdapter.mActivity = mActivity;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        final View sView = mInflater.inflate(R.layout.item_friend, parent, false);

        ApiBus.getInstance().register(this);

        return new ViewHolder(sView,new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                FeedFragment fragment = new FeedFragment().newInstance(list.get(position).getId(), false);
                FragmentManager manager = ((AppCompatActivity) mActivity).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.sub_container, fragment).addToBackStack(null);
                transaction.commit();
            }

            @Override
            public void onFollowClick(View view, int position) {
                Button button = (Button) view;
                Log.v("isFollowing:",list.get(position).getIsFollowing() + "");
                if (list.get(position).getIsFollowing()) {
                    toggleUnfollow(button);

                } else {
                    toggleFollowing(button);
                }

                ApiBus.getInstance().post(new FollowRegisterEvent(list.get(position).getId()));
                list.get(position).setIsFollowing(!list.get(position).getIsFollowing());

            }
        });
    }

    public void updateList(ArrayList<User> data) {
        list = data;
        notifyDataSetChanged();
    }

    public void addItem(int position, User data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        User user = list.get(position);
        holder.name.setText(Html.fromHtml(user.getName()));

        Picasso.with(mActivity)
                .load(user.getAvatarUrl())
                .centerCrop()
                .resize(100, 100)
                .transform(new RoundedTransformation(50, 4))
                .into(holder.avatar);

        holder.initButton(user.getIsFollowing(), holder.btnFollow);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public void toggleFollowing(Button v) {
        v.setTextColor(Color.parseColor("#ffffff"));
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

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onFollowClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        ImageView avatar;
        Button btnFollow;

        OnItemClickListener listener;
        boolean isFollowing = false;

        public ViewHolder(View view, OnItemClickListener listener) {
            super(view);

            this.listener = listener;

            name = (TextView) view.findViewById(R.id.profile_name);
            avatar = (ImageView) view.findViewById(R.id.profile_image);
            btnFollow = (Button) view.findViewById(R.id.btn_follow);

            avatar.setOnClickListener(this);
            btnFollow.setOnClickListener(this);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.profile_image:
                    listener.onItemClick(v, getPosition());
                    break;
                case R.id.btn_follow:
                    listener.onFollowClick(v, getPosition());
                    break;
            }
        }

        public void initButton(boolean following, View v) {
            Button button = (Button) v;

            list.get(getPosition()).setIsFollowing(following);
            isFollowing = following;

            if (following) {
                toggleFollowing(button);
            } else {
                toggleUnfollow(button);
            }

            //isFollowing = !isFollowing;
        }



    }
}