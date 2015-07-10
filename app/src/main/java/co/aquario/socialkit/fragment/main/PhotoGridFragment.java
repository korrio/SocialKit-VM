package co.aquario.socialkit.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import co.aquario.socialkit.R;
import co.aquario.socialkit.adapter.PhotoGridViewAdapter;
import co.aquario.socialkit.event.LoadPhotoListEvent;
import co.aquario.socialkit.event.LoadPhotoListSuccessEvent;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.Photo;
import co.aquario.socialkit.widget.EndlessListOnScrollListener;
import me.iwf.photopicker.PhotoPagerActivity;

public class PhotoGridFragment extends BaseFragment {
    public ArrayList<Photo> listPhoto = new ArrayList<>();
    public ArrayList<String> urls = new ArrayList<>();
    public GridView gv;
    public PhotoGridViewAdapter adapter;

    public int currentPage = 1;
    public boolean isRefresh = false;
    public boolean isLoadding = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gridview_photo, container, false);

        adapter = new PhotoGridViewAdapter(getActivity(),listPhoto);
        gv = (GridView) rootView.findViewById(R.id.grid_view);
        gv.setAdapter(adapter);
        gv.setOnScrollListener(new EndlessListOnScrollListener(20,1) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                currentPage = page;
                isRefresh = false;
                //String loadMoreUrl = MainApplication.ENDPOINT + "/search/channel?sort=F&page="+page;
                if (!isLoadding) {
                    ApiBus.getInstance().post(new LoadPhotoListEvent(6,mSort,currentPage,20));
                    //aq.ajax(loadMoreUrl, JSONObject.class, fragment, "getJson");
                    Log.e("totalItemsCount", totalItemsCount + "");
                }

                isLoadding = true;
            }
        });
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                urls.clear();
                n = 0;
                for(int j = 0 ; j < listPhoto.size()  ; j++)  {
                    if(listPhoto.get(j).media != null) {
                        String url = listPhoto.get(j).media.getFullUrl();
                        if(url != null) {
                            urls.add(n,url);
                            n++;
                        }
                    }

                }

                Intent intent = new Intent(getActivity(), PhotoPagerActivity.class);

                intent.putExtra("current_item", i);
                intent.putStringArrayListExtra("photos", urls);

                startActivity(intent);
            }
        });

        return rootView;

    }

    public static final String SORT = "SORT";
    public String mSort;

    public static PhotoGridFragment newInstance(String sort){
        PhotoGridFragment mFragment = new PhotoGridFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(SORT, sort);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSort = getArguments().getString(SORT);
        }
        ApiBus.getInstance().post(new LoadPhotoListEvent(6,mSort,1,20));
    }

    int n = 0;
    @Subscribe
    public void onPhotoListDataResponseSuccess(LoadPhotoListSuccessEvent event) {
        if(event.getSortType().equals(mSort) && event.getPhotoListData() != null) {
            listPhoto.addAll(event.getPhotoListData().photos);
            adapter.notifyDataSetChanged();
        }
    }
}
