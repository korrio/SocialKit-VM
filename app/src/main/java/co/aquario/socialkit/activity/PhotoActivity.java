package co.aquario.socialkit.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import co.aquario.socialkit.BaseActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.event.PhotoLoadEvent;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class PhotoActivity extends BaseActivity {
    Context context;
    ImageViewTouch imageFeedBig;
   TextView nameTitle;
    TextView desc;

    public static final String LOAD_URL = "LOAD_URL";
    private String mUrl = "";
    private String name = "";
    private String descUrl = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_photo_zoom);

        imageFeedBig = (ImageViewTouch) findViewById(R.id.image);
        nameTitle = (TextView) findViewById(R.id.name);
        desc = (TextView) findViewById(R.id.desc);

        mUrl = getIntent().getStringExtra("url");
        name = getIntent().getStringExtra("name");
        descUrl = getIntent().getStringExtra("desc");

        nameTitle.setText(name);
        desc.setText(descUrl);

        if(mUrl != null)
            Picasso.with(context)
                    .load(mUrl)
                    .into(imageFeedBig);
    }

    @Subscribe
    public void onPhotoLoad(PhotoLoadEvent event) {
        Toast.makeText(this,event.getUrl(),Toast.LENGTH_SHORT).show();
//        Picasso.with(context)
//                .load(event.getUrl())
//                .into(imageFeedBig);
    }

}
