package co.aquario.socialkit.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import co.aquario.socialkit.R;
import co.aquario.socialkit.model.PostStory;
import co.aquario.socialkit.view.SquareImageView;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class PhotoGridProfileAdapter extends BaseAdapter {
    private Context context;
    private List<PostStory> posts = new ArrayList<>();

    public PhotoGridProfileAdapter(Context context, ArrayList<PostStory> posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        SquareImageView view = (SquareImageView) convertView;
        if (view == null) {
            view = new SquareImageView(context);
            view.setScaleType(CENTER_CROP);
        }

        if(posts.get(position).media != null) {
            // Get the image URL for the current position.
            String url = posts.get(position).media.getThumbUrl();
            //String url = MainApplication.IMAGE_ENDPOINT+"photos/2015/07/mTfn2_113855_4982a1e5676291e9c1a13d42a13ce79f.png";
            //Log.e("fixit " + position ,photos.get(position).media.getThumbUrl());

            // Trigger the download of the URL asynchronously into the image view.
            if (url != null)
                Picasso.with(context) //
                        .load(url) //
                        .placeholder(R.drawable.icon_vdomax) //
                        .error(R.drawable.icon_vdomax) //
                                //.resize(100,100)
                        .fit() //
                                //.centerCrop()
                        .tag(context) //
                        .into(view);
        }

        return view;
    }

    @Override public int getCount() {
        return posts.size();
    }

    @Override public PostStory getItem(int position) {
        return posts.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }
}
