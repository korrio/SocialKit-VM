package co.aquario.socialkit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import co.aquario.socialkit.R;
import co.aquario.socialkit.model.Channel;
import co.aquario.socialkit.widget.RoundedTransformation;
import de.hdodenhof.circleimageview.CircleImageView;


public class ChannelAdapter extends BaseAdapter {

    Context context;
    public ArrayList<Channel> list = new ArrayList<Channel>();

    public ChannelAdapter(Context context, ArrayList<Channel> list) {
        this.context = context;
        this.list = list;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = mInflater.inflate(R.layout.item_channel, parent, false);

        TextView name;

        ImageView liveCover;
        ImageView avatar;
        CircleImageView status;

        Channel channel = list.get(position);

        name = (TextView) row.findViewById(R.id.name);
        liveCover = (ImageView) row.findViewById(R.id.live_cover);
        avatar = (ImageView) row.findViewById(R.id.avatar);
        status = (CircleImageView) row.findViewById(R.id.status);

        if(channel.online) {
            status.setBackgroundColor(Color.parseColor("#66ff33"));
            Picasso.with(context)
                    .load(R.color.online)
                    .into(status);
        } else {
            status.setBackgroundColor(Color.parseColor("#aaaaaa"));
            Picasso.with(context)
                    .load(R.color.offline)
                    .into(status);
        }

        name.setText(channel.name);

        Picasso.with(context)
                .load(channel.liveCover)
                .placeholder(R.drawable.offline)
                .error(R.drawable.offline)
                .into(liveCover)
        ;

        Picasso.with(context)
                .load(channel.avatar)
                .transform(new RoundedTransformation(50, 4))
                .resize(100, 100)
                .into(avatar);

        return row;
    }

}