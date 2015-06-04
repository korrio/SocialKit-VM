package co.aquario.socialkit.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import java.util.List;

import co.aquario.socialkit.R;
import co.aquario.socialkit.activity.MainActivity;
import co.aquario.socialkit.event.LoadPhotoListEvent;
import co.aquario.socialkit.event.LoadPhotoListSuccessEvent;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.Photo;

public class GalleryFragment extends BaseFragment {

    public static GalleryFragment newInstance(String text,String name, String desc){
        GalleryFragment mFragment = new GalleryFragment();
        Bundle mBundle = new Bundle();
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }


    public List<Photo> items;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_asym_gridview, container, false);



        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ApiBus.getInstance().postQueue(new LoadPhotoListEvent(6,"V",1,50));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((MainActivity) getActivity()).getToolbar().setVisibility(View.VISIBLE);
    }



    @Subscribe public void onLoadPhotoList(LoadPhotoListSuccessEvent event) {
        //items.addAll(event.getPhotoListData().photos);
        //adapter.notifyDataSetChanged();
    }
}
