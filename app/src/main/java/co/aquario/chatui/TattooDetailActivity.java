package co.aquario.chatui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import co.aquario.chatui.adapter.TattooStoreDetailAdapter;
import co.aquario.chatui.model.TattooStore;
import co.aquario.socialkit.R;

public class TattooDetailActivity extends ActionBarActivity {


    TattooStoreDetailAdapter adapterTattooStroe;
    ImageView sticker;
    TextView title_vdomax;
    TextView name_sticker;
    TextView date;
    TextView price;
    GridView gridView;

    TattooStore tattoo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tattoo_detail);

        sticker = (ImageView) findViewById(R.id.sticker);
        title_vdomax = (TextView) findViewById(R.id.title_vdomax);
        name_sticker = (TextView) findViewById(R.id.name_sticker);
        date = (TextView) findViewById(R.id.date);
        price = (TextView) findViewById(R.id.price);
        gridView = (GridView) findViewById(R.id.gridView);


        if(getIntent() != null) {
            tattoo = Parcels.unwrap(getIntent().getBundleExtra("bundle").getParcelable("tattoo"));
            title_vdomax.setText(tattoo.getCreate_by_name());
            name_sticker.setText(tattoo.getItem_set_name());

            String path = tattoo.getImageLogo();

            Picasso.with(getApplicationContext())
                    .load(path)
                    .into(sticker);

            adapterTattooStroe = new TattooStoreDetailAdapter(getApplicationContext(),tattoo.getTitle_vdomax());
            gridView.setAdapter(adapterTattooStroe);
        }



    }


}

