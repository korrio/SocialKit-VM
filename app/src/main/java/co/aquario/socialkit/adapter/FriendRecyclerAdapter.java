package co.aquario.socialkit.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
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

import co.aquario.chatapp.ChatActivity;
import co.aquario.chatui.ChatUIActivity;
import co.aquario.chatui.ui.CircleTransform;
import co.aquario.socialkit.NewProfileActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.event.FollowRegisterEvent;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.User;
import co.aquario.socialkit.util.NotiUtils;
import co.aquario.socialkit.widget.RoundedTransformation;


public class FriendRecyclerAdapter extends RecyclerView.Adapter<FriendRecyclerAdapter.ViewHolder> {

    private static Activity mActivity;

    public ArrayList<User> list = new ArrayList<>();

    public OnItemClickListener mItemClickListener;
    public OnItemLongClickListener mItemLongClickListener;

    public FriendRecyclerAdapter(Activity mActivity, ArrayList<User> list) {
        FriendRecyclerAdapter.mActivity = mActivity;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        final View sView = mInflater.inflate(R.layout.item_friend, parent, false);

        ApiBus.getInstance().register(this);

        return new ViewHolder(sView, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent i = new Intent(mActivity, NewProfileActivity.class);
                i.putExtra("USER_ID", list.get(position).getId());
                mActivity.startActivity(i);
/*
                FeedFragment fragment = toolbar FeedFragment().newInstance(listStory.get(position).getId(), false);
                FragmentManager manager = ((AppCompatActivity) mActivity).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.sub_container, fragment).addToBackStack(null);
                transaction.commit();
                */
            }

            @Override
            public void onFollowClick(View view, int position) {
                Button button = (Button) view;
                Log.v("isFollowing:", list.get(position).getIsFollowing() + "");
                if (list.get(position).getIsFollowing()) {
                    toggleUnfollow(button);

                } else {
                    toggleFollowing(button);
                }

                ApiBus.getInstance().post(new FollowRegisterEvent(list.get(position).getId()));
                list.get(position).setIsFollowing(!list.get(position).getIsFollowing());

            }
        }, new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                final User user = list.get(position);

                final String imageProfile = user.getAvatar();
                final String nameProfile = user.getName();
                final String coverProfile = user.getCover();
                final String username = user.getUsername();
                final String userId = user.getId();

                String ImageProfileFillUrl = "https://www.vdomax.com/" + imageProfile + "";
                final String ImageCoverFillUrl = "https://www.vdomax.com/" + coverProfile + "";
                final Dialog dialog = new Dialog(mActivity, R.style.FullHeightDialog);
                dialog.setContentView(R.layout.dialog_followers);

                ImageView proFileImage;
                final ImageView coverImage;
                final TextView titleName;
                final TextView userName;
                final TextView nameAt;
                final TextView txtChat;
                final TextView audioChat;
                final TextView vdoChat;

                proFileImage = (ImageView) dialog.findViewById(R.id.avatar);
                titleName = (TextView) dialog.findViewById(R.id.name_title);
                coverImage = (ImageView) dialog.findViewById(R.id.cover);
                userName = (TextView) dialog.findViewById(R.id.name_username);
                nameAt = (TextView) dialog.findViewById(R.id.name_at);
                txtChat = (TextView) dialog.findViewById(R.id.txtChat);
                audioChat = (TextView) dialog.findViewById(R.id.voiceChat);
                vdoChat = (TextView) dialog.findViewById(R.id.vdoChat);

                nameAt.setText(nameProfile);
                userName.setText(username);
                titleName.setText(nameProfile);
                Picasso.with(mActivity)
                        .load(ImageProfileFillUrl)
                        .resize(200, 200)
                        .transform(new CircleTransform())
                        .into(proFileImage);

                Picasso.with(mActivity)
                        .load(ImageCoverFillUrl)
                        .into(coverImage);

                final int mUserId = Integer.parseInt(VMApp.mPref.userId().getOr("0"));

                vdoChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String roomName = "VM_" + mUserId + "_" + userId + "_" + System.currentTimeMillis();
                        NotiUtils.notifyUser(mActivity, 504, mUserId, Integer.parseInt(userId), roomName);
                        ChatUIActivity.connectToRoom(mActivity, roomName, true);
                    }
                });

                audioChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String roomName = "VM_" + mUserId + "_" + userId + "_" + System.currentTimeMillis();
                        NotiUtils.notifyUser(mActivity,505,mUserId, Integer.parseInt(userId), roomName);
                        ChatUIActivity.connectToRoom(mActivity, roomName, false);
                    }
                });

                txtChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ChatActivity.startChatActivity(mActivity, mUserId, Integer.parseInt(userId), 0);

//                        ChatWidgetFragmentClient fragment = ChatWidgetFragmentClient.newInstance(mUserId,userId,0);
//                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameFragment, fragment, "CHAT_MAIN").addToBackStack(null).commit();

                        dialog.dismiss();
                    }
                });


                dialog.show();
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
                .resize(200, 200)
                .transform(new RoundedTransformation(100, 4))
                .into(holder.avatar);

        holder.initButton(position,user.isFollowing, holder.btnFollow);

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

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView name;
        ImageView avatar;
        Button btnFollow;

        OnItemClickListener listener;
        OnItemLongClickListener longListener;
        boolean isFollowing = false;

        public ViewHolder(View view, OnItemClickListener listener, OnItemLongClickListener longListener) {
            super(view);

            this.listener = listener;
            this.longListener = longListener;

            name = (TextView) view.findViewById(R.id.profile_name);
            avatar = (ImageView) view.findViewById(R.id.profile_image);
            btnFollow = (Button) view.findViewById(R.id.btn_follow);

            avatar.setOnClickListener(this);
            avatar.setOnLongClickListener(this);
            btnFollow.setOnClickListener(this);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.profile_image:
                    listener.onItemClick(v, getLayoutPosition());
                    break;
                case R.id.btn_follow:
                    listener.onFollowClick(v, getLayoutPosition());
                    break;
            }
        }

        public void initButton(int position,boolean following, View v) {
            Button button = (Button) v;

            list.get(position).setIsFollowing(following);
            isFollowing = following;

            if (following) {
                toggleFollowing(button);
            } else {
                toggleUnfollow(button);
            }

            //isFollowing = !isFollowing;
        }


        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
                case R.id.profile_image:
                    longListener.onItemLongClick(v, getLayoutPosition());
                    break;
                case R.id.btn_follow:
                    longListener.onItemLongClick(v, getLayoutPosition());
                    break;
            }
            return true;
        }
    }
}