package co.aquario.socialkit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import co.aquario.socialkit.R;
import co.aquario.socialkit.model.Video;
import co.aquario.socialkit.widget.RoundedTransformation;


public class VideoRecyclerAdapter extends RecyclerView.Adapter<VideoRecyclerAdapter.ViewHolder> {


    private Context context;
    private OnItemClickListener mItemClickListener;

    public ArrayList<Video> list = new ArrayList<Video>();

    public VideoRecyclerAdapter(Context context, ArrayList<Video> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        final View sView = mInflater.inflate(R.layout.item_crad_video, parent, false);
        return new ViewHolder(sView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Video item = list.get(position);

        holder.nLoveView.setText(item.getnLove() + "");
        holder.nCommentView.setText(item.getnComment() + "");
        holder.nShareView.setText(item.getnShare() + "");

        String viewCount = item.getView();
        if(viewCount.equals(""))
            viewCount = "0";

        if (item.getDesc() != null) {
            if (item.getDesc().trim().length() < 60)
                holder.videoDesc.setText(Html.fromHtml("" + item.getDesc() + ""));
            else
                holder.videoDesc.setText(Html.fromHtml("" + item.getDesc().substring(0, 60) + " ..." + ""));
        } else {
            holder.videoDesc.setVisibility(View.GONE);
        }

        if (item.getTitle() != null) {
            if (item.getTitle().trim().length() < 40)
                holder.videoTitle.setText(Html.fromHtml("" + item.getTitle() + ""));
            else
                holder.videoTitle.setText(Html.fromHtml("" + item.getTitle().substring(0, 40) + " ..." + ""));
        }

        holder.videoTitle.setText(item.getTitle());
        //mViewHolder.videoDesc.setText(Html.fromHtml(item.getDesc().substring(0, 60)));
        holder.TxtView.setText(Html.fromHtml("<b>" + viewCount + "+ views</b>"));

        holder.profileName.setText(item.getpName());


        //http://img.youtube.com/vi/QPjgMkx5KyY/0.jpg
        //http://img.youtube.com/vi/QPjgMkx5KyY/maxresdefault
        Picasso.with(context)
                .load("http://img.youtube.com/vi/" + item.getYoutubeId() + "/0.jpg")
                .into(holder.videoThumb);

        Picasso.with(context)
                .load(item.getpAvatar())
                .transform(new RoundedTransformation(50, 4))
                .resize(100, 100)
                .into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        ImageView avatar;
        TextView profileName;
        ImageView videoThumb;
        TextView videoTitle;
        TextView videoDesc;
        TextView TxtView;

        TextView nLoveView;
        TextView nCommentView;
        TextView nShareView;

        public ViewHolder(View view) {
            super(view);
            videoTitle = (TextView) view.findViewById(R.id.video_title);
            videoDesc = (TextView) view.findViewById(R.id.video_desc);
            videoThumb = (ImageView) view.findViewById(R.id.video_thumb);
            TxtView = (TextView) view.findViewById(R.id.view);

            avatar = (ImageView) view.findViewById(R.id.avatar);
            profileName = (TextView) view.findViewById(R.id.profile_name);

            nLoveView = (TextView) view.findViewById(R.id.number1);
            nCommentView = (TextView) view.findViewById(R.id.number2);
            nShareView = (TextView) view.findViewById(R.id.number3);

            avatar.setOnClickListener(this);
            videoThumb.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.video_thumb:
                case R.id.avatar:
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