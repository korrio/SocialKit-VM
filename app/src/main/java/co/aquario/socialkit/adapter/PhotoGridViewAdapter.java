package co.aquario.socialkit.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import co.aquario.socialkit.R;
import co.aquario.socialkit.model.Photo;
import co.aquario.socialkit.widget.SquareImageView;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class PhotoGridViewAdapter extends BaseAdapter {
    private Context context;
    private List<Photo> photos = new ArrayList<>();

    public PhotoGridViewAdapter(Context context, ArrayList<Photo> photos) {
        this.context = context;
        this.photos = photos;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        SquareImageView view = (SquareImageView) convertView;
        if (view == null) {
            view = new SquareImageView(context);
            view.setScaleType(CENTER_CROP);
        }

        if(photos.get(position).media != null) {
            // Get the image URL for the current position.
            String url = photos.get(position).media.getThumbUrl();
            //String url = MainApplication.IMAGE_ENDPOINT+"photos/2015/07/mTfn2_113855_4982a1e5676291e9c1a13d42a13ce79f.png";
            //Log.e("fixit " + position ,photos.get(position).media.getThumbUrl());

            // Trigger the download of the URL asynchronously into the image view.
            if(url != null)
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
        return photos.size();
    }

    @Override public Photo getItem(int position) {
        return photos.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }
}
