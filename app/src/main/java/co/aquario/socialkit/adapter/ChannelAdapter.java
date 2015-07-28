package co.aquario.socialkit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import co.aquario.socialkit.R;
import co.aquario.socialkit.event.FollowRegisterEvent;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.Channel;
import co.aquario.socialkit.widget.RoundedTransformation;


public class ChannelAdapter extends BaseAdapter {

    Context context;
    public ArrayList<Channel> list = new ArrayList<Channel>();

    public ChannelAdapter(Context context, ArrayList<Channel> list) {
        this.context = context;
        this.list = list;
        ApiBus.getInstance().register(this);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) Integer.parseInt(list.get(position).id);
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



        View row = mInflater.inflate(R.layout.item_channel, parent, false);

        TextView name;
        TextView follower;

        Button btnFollow;

        ImageView liveCover;
        ImageView avatar;
        ImageView status;

        Channel channel = list.get(position);

        name = (TextView) row.findViewById(R.id.name);
        follower = (TextView) row.findViewById(R.id.followerTv);

        liveCover = (ImageView) row.findViewById(R.id.live_cover);
        avatar = (ImageView) row.findViewById(R.id.avatar);
        status = (ImageView) row.findViewById(R.id.status);

        btnFollow = (Button) row.findViewById(R.id.btn_follow);
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                Log.v("isFollowing:", list.get(position).isFollowing + "");
                if (list.get(position).isFollowing) {
                    toggleUnfollow(button);

                } else {
                    toggleFollowing(button);
                }

                ApiBus.getInstance().post(new FollowRegisterEvent(list.get(position).id));
                list.get(position).setIsFollowing(!list.get(position).isFollowing);
            }
        });

        initButton(position,channel.isFollowing,btnFollow);


        if(channel.online) {
            status.setBackgroundColor(Color.parseColor("#66ff33"));
            Picasso.with(context)
                    .load(R.color.online)
                    .resize(10,10)
                    .transform(new RoundedTransformation(5, 4))
                    .into(status);
        } else {
            status.setBackgroundColor(Color.parseColor("#aaaaaa"));
            Picasso.with(context)
                    .load(R.color.offline)
                    .resize(10, 10)
                    .transform(new RoundedTransformation(5, 4))
                    .into(status);
        }

        name.setText(Html.fromHtml(channel.name));
        if(channel.totalFollower != null)
            follower.setText(channel.totalFollower + " followers");

        Picasso.with(context)
                .load(channel.liveCover)
                .placeholder(R.drawable.default_offline)
                .error(R.drawable.default_offline)
                .into(liveCover)
        ;

        Picasso.with(context)
                .load(channel.avatar)
                .resize(200, 200)
                .transform(new RoundedTransformation(100, 4))
                .into(avatar);



        return row;
    }

    public OnItemClickListener mItemClickListener;
    boolean isFollowing = false;

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

}