package co.aquario.socialkit.adapter;

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

import java.util.List;

import co.aquario.socialkit.R;
import co.aquario.socialkit.activity.PostSoundCloudActivity;
import co.aquario.socialkit.soundcloud.Track;

/**
 * Created by kevin on 2/22/15.
 */
public class TracksAdapter extends RecyclerView.Adapter<TracksAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView titleTextView;
        private final ImageView thumbImageView;
        private final ImageButton shareTrackBtn;

        ViewHolder(View v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.track_title);
            thumbImageView = (ImageView) v.findViewById(R.id.track_thumbnail);
            shareTrackBtn = (ImageButton) v.findViewById(R.id.btn_track_share);
            v.setOnClickListener(this);

            shareTrackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String artworkURL = mTracks.get(getPosition()).getArtworkURL();
                    String trackTitle = mTracks.get(getPosition()).getTitle();
                    String trackUri = mTracks.get(getPosition()).getUri();

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
                .inflate(R.layout.item_track, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Track track = mTracks.get(i);
        viewHolder.titleTextView.setText(track.getTitle());
        Picasso.with(mContext).load(track.getArtworkURL()).into(viewHolder.thumbImageView);
    }

    @Override
    public int getItemCount() {
        return mTracks.size();
    }

}
