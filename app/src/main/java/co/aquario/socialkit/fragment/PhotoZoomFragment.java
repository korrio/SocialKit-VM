package co.aquario.socialkit.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import co.aquario.socialkit.R;
import co.aquario.socialkit.event.PhotoLoadEvent;
import co.aquario.socialkit.fragment.main.BaseFragment;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class PhotoZoomFragment extends BaseFragment {

    public static final String LOAD_URL = "LOAD_URL";
    Context context;
    ImageViewTouch imageView;
    TextView nameTv;
    TextView descTv;
    private String mUrl = "";
    private String mName = "";
    private String mDesc = "";

    public static PhotoZoomFragment newInstance(String text,String name, String desc){
        PhotoZoomFragment mFragment = new PhotoZoomFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(LOAD_URL, text);
        mBundle.putString("NAME", name);
        mBundle.putString("DESC", desc);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUrl = getArguments().getString(LOAD_URL);
            mName = getArguments().getString("NAME");
            mDesc = getArguments().getString("DESC");
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_zoom, container, false);

        context = getActivity();
        imageView = (ImageViewTouch) rootView.findViewById(R.id.image);
        nameTv = (TextView) rootView.findViewById(R.id.name);
        descTv = (TextView) rootView.findViewById(R.id.desc);

        ((AppCompatActivity) getActivity()).getSupportActionBar();

        if(!mName.equals(""))
            nameTv.setText(mName);
        if(!mDesc.equals(""))
            descTv.setText(mDesc);
        else
            descTv.setVisibility(View.GONE);

        if(!mUrl.equals(""))
            Picasso.with(context)
                    .load(mUrl)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            imageView.animate()
                                    .scaleX(1.f).scaleY(1.f)
                                    .setInterpolator(new OvershootInterpolator())
                                    .setDuration(400)
                                    .setStartDelay(200)
                                    .start();
                        }

                        @Override
                        public void onError() {
                        }
                    });

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //((MainActivity) getActivity()).getToolbar().setVisibility(View.VISIBLE);
    }

    @Subscribe public void onPhotoLoad(PhotoLoadEvent event) {
        Picasso.with(context)
                .load(event.getUrl())
                .into(imageView);
    }
}
