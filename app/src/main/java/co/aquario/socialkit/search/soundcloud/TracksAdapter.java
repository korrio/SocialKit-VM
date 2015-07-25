package co.aquario.socialkit.search.soundcloud;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import co.aquario.socialkit.R;

/**
 * Created by kevin on 2/22/15.
 */
public class TracksAdapter extends RecyclerView.Adapter<TracksAdapter.ViewHolder> {

    private List<MusicTrack> mMusicTracks;
    private Context mContext;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    public TracksAdapter(Context context, List<MusicTrack> musicTracks) {
        mMusicTracks = musicTracks;
        mContext = context;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_sc_track, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        MusicTrack musicTrack = mMusicTracks.get(i);
        holder.titleTextView.setText(musicTrack.mTitle);
        Picasso.with(mContext).load(musicTrack.user.avatarUrl).into(holder.thumbImageView);
        Picasso.with(mContext).load(musicTrack.getArtworkURL()).into(holder.imgTrack);


        holder.tvDuration.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(musicTrack.duration),
                TimeUnit.MILLISECONDS.toSeconds(musicTrack.duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(musicTrack.duration))
        ));

        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.tvPlaycount.setText(musicTrack.playbackCount);

        holder.tvUsername.setText(musicTrack.user.username);
        holder.tvPermalink.setText(musicTrack.user.permalink);
    }

    @Override
    public int getItemCount() {
        return mMusicTracks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final ImageView thumbImageView;
        private final ImageButton shareTrackBtn;

        TextView tvUsername, tvPermalink, tvDuration, tvPlaycount;
        ImageView imgTrack;

        ViewHolder(View v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.track_title);
            thumbImageView = (ImageView) v.findViewById(R.id.track_thumbnail);
            shareTrackBtn = (ImageButton) v.findViewById(R.id.btn_track_share);

            tvUsername = (TextView) v.findViewById(R.id.tv_username);
            tvPermalink = (TextView) v.findViewById(R.id.tv_time);

            tvDuration = (TextView) v.findViewById(R.id.tv_duration);
            tvPlaycount = (TextView) v.findViewById(R.id.tv_playcount);

            imgTrack = (ImageView) v.findViewById(R.id.img_track);


        }

    }

}
