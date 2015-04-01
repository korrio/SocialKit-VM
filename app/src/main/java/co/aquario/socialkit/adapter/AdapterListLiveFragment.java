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
import java.util.List;
import java.util.Random;

import co.aquario.socialkit.R;
import co.aquario.socialkit.model.Live;



public class AdapterListLiveFragment extends RecyclerView.Adapter<AdapterListLiveFragment.ViewHolder> {


    private OnItemClickListener mItemClickListener;

    private final Context context;

    private List<Live> list = new ArrayList<Live>();

    public AdapterListLiveFragment(Context context, ArrayList<Live> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        final View sView = mInflater.inflate(R.layout.item_list_live_card, parent, false);
        return new ViewHolder(sView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Live item = list.get(position);

        PrettyTime p = new PrettyTime();
        long agoLong = Integer.parseInt(item.getTimestamp());
        Date timeAgo = new java.util.Date((long) agoLong * 1000);
        String mydate = p.format(timeAgo);

        String string = item.getNameLive();
        String[] parts = string.split("-");
        String name = "";
        String timeDate = "";
        String date = "";
        String time = "";
        if (parts.length == 2) {
            name = parts[0]; // 004
            timeDate = parts[1]; // 034556
            String[] parts2 = timeDate.split("_");
            date = parts2[0];
            time = parts2[1];
        } else {
            name = parts[0]; // 004
            time = "";
            //date = parts[1] + "-" + parts[2] + "-" + parts[3]; // 034556
            //time = parts[4];
        }

        String durationStr = item.getHours() + ":" + item.getMinutes() + ":" + item.getSeconds();

        holder.title_live_name.setText(name);
        holder.title_date_name.setText(mydate);
        holder.text_time_name.setText(time);
        holder.text_duration_name.setText(durationStr);

        Picasso.with(context)
                .load(item.getPhotoLive())
                .fit().centerCrop()
                .into(holder.image_live);
        ;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        TextView title_live_name;
        TextView title_date_name;
        TextView text_time_name;
        TextView text_duration_name;
        ImageView image_live;

        public ViewHolder(View view) {
            super(view);

            title_live_name = (TextView) view.findViewById(R.id.title_live_name);
            title_date_name = (TextView) view.findViewById(R.id.title_date_name);
            text_time_name = (TextView) view.findViewById(R.id.text_time_name);
            text_duration_name = (TextView) view.findViewById(R.id.text_duration_name);
            image_live = (ImageView) view.findViewById(R.id.image_live);


            image_live.setOnClickListener(this);


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