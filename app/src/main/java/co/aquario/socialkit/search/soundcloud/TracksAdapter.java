package co.aquario.socialkit.search.soundcloud;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import co.aquario.socialkit.activity.post.PostSoundCloudActivity;

/**
 * Created by kevin on 2/22/15.
 */
public class TracksAdapter extends RecyclerView.Adapter<TracksAdapter.ViewHolder> {

    private List<Track> mTracks;
    private Context mContext;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    public TracksAdapter(Context context, List<Track> tracks) {
        mTracks = tracks;
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
        Track track = mTracks.get(i);
        holder.titleTextView.setText(track.mTitle);
        Picasso.with(mContext).load(track.user.avatarUrl).into(holder.thumbImageView);
        Picasso.with(mContext).load(track.getArtworkURL()).into(holder.imgTrack);


        holder.tvDuration.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(track.duration),
                TimeUnit.MILLISECONDS.toSeconds(track.duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(track.duration))
        ));

        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.tvPlaycount.setText(track.playbackCount);

        holder.tvUsername.setText(track.user.username);
        holder.tvPermalink.setText(track.user.permalink);
    }

    @Override
    public int getItemCount() {
        return mTracks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

            v.setOnClickListener(this);

            shareTrackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String artworkURL = mTracks.get(getPosition()).getArtworkURL();
                    String trackTitle = mTracks.get(getPosition()).mTitle;
                    String trackUri = mTracks.get(getPosition()).uri;

                    Log.e("myTrackUri", trackUri);

                    Intent i = new Intent(mContext, PostSoundCloudActivity.class);
                    i.putExtra("soundcloud_uri", trackUri);
                    i.putExtra("soundcloud_title", trackTitle);
                    i.putExtra("artwork_url", artworkURL);
                    mContext.startActivity(i);
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                if (v.getId() != R.id.btn_track_share)
                    mOnItemClickListener.onItemClick(null, v, getPosition(), 0);
            }
        }
    }

}
