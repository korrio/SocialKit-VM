package co.aquario.socialkit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import co.aquario.socialkit.R;
import co.aquario.socialkit.model.Live;
import de.hdodenhof.circleimageview.CircleImageView;


public class LiveHistoryRecyclerAdapter extends RecyclerView.Adapter<LiveHistoryRecyclerAdapter.ViewHolder> {


    ArrayList<Live> list = new ArrayList<Live>();
    public static Context context;

    OnItemClickListener mItemClickListener;


    public LiveHistoryRecyclerAdapter(Context context, ArrayList<Live> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        final View sView = mInflater.inflate(R.layout.item_live, parent, false);
        return new ViewHolder(sView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Live item = list.get(position);

        holder.txt_title.setText(item.getNameLive());


        PrettyTime p = new PrettyTime();
        long agoLong = Integer.parseInt(item.getDate());
        Date timeAgo = new java.util.Date(agoLong * 1000);
        String ago = p.format(timeAgo);
        holder.txt_time.setText(ago);

        holder.txt_profile.setText(item.getNameLive());
        holder.txt_hours.setText(item.getHours());
        holder.txt_minutes.setText(item.getMinutes());



        Picasso.with(context)
                .load(item.getPhotoLive())
                .fit().centerCrop()
                .into(holder.image_live);

        Picasso.with(context)
                .load(item.getAvatar())
                .fit().centerCrop()
                .into(holder.image_profile);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView txt_title;
        TextView txt_time;
        TextView txt_profile;
        TextView txt_hours;
        TextView txt_minutes;

        ImageView image_live;
        CircleImageView image_profile;

        public ViewHolder(View view) {
            super(view);
            txt_title = (TextView) view.findViewById(R.id.txt_title);
            txt_time = (TextView) view.findViewById(R.id.txt_time);
            txt_profile = (TextView) view.findViewById(R.id.txt_profile);
            txt_hours = (TextView) view.findViewById(R.id.txt_hours);
            txt_minutes = (TextView) view.findViewById(R.id.txt_minutes);


            image_live = (ImageView) view.findViewById(R.id.image_live);
            image_profile = (CircleImageView) view.findViewById(R.id.image_profile);




            image_live.setOnClickListener(this);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.image_live:
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(v, getPosition());
                    }

            }

        }

    }


    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    /*
     * Snippet from http://stackoverflow.com/a/363692/1008278
     */
    public static int randInt(int min, int max) {
        final Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    /* ==========This Part is not necessary========= */

}