package co.aquario.socialkit.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import co.aquario.socialkit.R;
import co.aquario.socialkit.model.Video;
import co.aquario.socialkit.widget.RoundedTransformation;


public class VideoAdapter extends BaseAdapter implements AdapterView.OnClickListener {

    private Context context;
    private OnItemClickListener mItemClickListener;

    public ArrayList<Video> list = new ArrayList<Video>();

    public VideoAdapter(Context context, ArrayList<Video> list) {
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
        return Long.parseLong(list.get(position).getPostId());
    }

    public class ViewHolder {

        ImageView avatar;
        TextView profileName;
        ImageView videoThumb;
        TextView videoTitle;
        TextView videoDesc;

        TextView view;

        public ViewHolder(View row) {
            videoTitle = (TextView) row.findViewById(R.id.video_title);
            videoDesc = (TextView) row.findViewById(R.id.video_desc);
            videoThumb = (ImageView) row.findViewById(R.id.video_thumb);
            view = (TextView) row.findViewById(R.id.view);

            avatar = (ImageView) row.findViewById(R.id.avatar);
            profileName = (TextView) row.findViewById(R.id.profile_name);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)  {


        ViewHolder mViewHolder = null;

        if(convertView == null) {

            LayoutInflater mInflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.item_videos,parent,false);

            mViewHolder = new ViewHolder(convertView);

            Video item = list.get(position);


            String viewCount = item.getView();
            if(viewCount.equals(""))
                viewCount = "0";

            if (item.getDesc() != null) {
                if (item.getDesc().trim().length() < 60)
                    mViewHolder.videoDesc.setText(Html.fromHtml("" + item.getDesc() + ""));
                else
                    mViewHolder.videoDesc.setText(Html.fromHtml("" + item.getDesc().substring(0, 60) + " ..." + ""));
            } else {
                mViewHolder.videoDesc.setVisibility(View.GONE);
            }

            mViewHolder.videoTitle.setText(item.getTitle());
            //mViewHolder.videoDesc.setText(Html.fromHtml(item.getDesc().substring(0, 60)));
            mViewHolder.view.setText(Html.fromHtml("<b>" + viewCount + "+ views</b>"));

            mViewHolder.profileName.setText(item.getpName());


            //http://img.youtube.com/vi/QPjgMkx5KyY/0.jpg
            //http://img.youtube.com/vi/QPjgMkx5KyY/maxresdefault
            Picasso.with(context)
                    .load("http://img.youtube.com/vi/" + item.getYoutubeId() + "/0.jpg")
                    .into(mViewHolder.videoThumb);

            Picasso.with(context)
                    .load(item.getpAvatar())
                    .transform(new RoundedTransformation(50, 4))
                    .resize(100, 100)
                    .into(mViewHolder.avatar);

            mViewHolder.videoThumb.setOnClickListener(this);

        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }





    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.video_thumb:
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(view);
                }
                break;

        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }




}

