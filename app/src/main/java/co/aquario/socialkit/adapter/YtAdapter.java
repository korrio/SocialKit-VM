package co.aquario.socialkit.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.services.youtube.model.SearchResult;
import com.squareup.picasso.Picasso;

import java.util.List;

import co.aquario.socialkit.R;

/**
 * This is the simple class that inflates ListView with the video data from keyword search
 * Created by ravikumar on 10/20/2014.
 */
public class YtAdapter extends BaseAdapter {

    private Activity mActivtiy = null;
    private List<SearchResult> mVideoList = null;
    private LayoutInflater mLayoutInflater = null;

    public YtAdapter(Activity iActivity) {
        mActivtiy = iActivity;
        mLayoutInflater = LayoutInflater.from(mActivtiy);
    }

    public void setmVideoList(List<SearchResult> mVideoList) {
        this.mVideoList = mVideoList;
    }


    @Override
    public int getCount() {
        return (mVideoList==null)?(0):(mVideoList.size());
    }

    @Override
    public SearchResult getItem(int i) {
        return (mVideoList!=null && mVideoList.size()>i)?(mVideoList.get(i)):(null);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder mHolder = null;
        if (view != null) {
            mHolder = (ViewHolder)view.getTag();
        } else {
            mHolder = new ViewHolder();
            view  = mLayoutInflater.inflate(R.layout.item_yt_video, null);
            mHolder.mVideoThumbnail = (ImageView)view.findViewById(R.id.video_thumbnail_imv);
            mHolder.mVideoTitleTxv = (TextView)view.findViewById(R.id.video_title_txv);
            mHolder.mVideoDescTxv = (TextView)view.findViewById(R.id.video_desc_txv);
            view.setTag(mHolder);
        }

        // Set the data
        SearchResult result = mVideoList.get(i);
        mHolder.mVideoTitleTxv.setText(result.getSnippet().getTitle());
        mHolder.mVideoDescTxv.setText(result.getSnippet().getDescription());


        //Load images
        Picasso.with(mActivtiy).load(result.getSnippet().getThumbnails().getMedium().getUrl()).into(mHolder.mVideoThumbnail);

        return view;
    }

    private class ViewHolder {
        private TextView mVideoTitleTxv = null;
        private TextView mVideoDescTxv = null;
        private ImageView mVideoThumbnail = null;
    }
}
