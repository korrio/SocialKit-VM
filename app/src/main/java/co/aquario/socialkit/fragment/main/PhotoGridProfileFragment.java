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
import co.aquario.socialkit.adapter.PhotoGridProfileAdapter;
import co.aquario.socialkit.event.LoadTimelineEvent;
import co.aquario.socialkit.event.LoadTimelineSuccessEvent;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.PostStory;
import co.aquario.socialkit.widget.EndlessListOnScrollListener;
import me.iwf.photopicker.PhotoPagerActivity;

public class PhotoGridProfileFragment extends BaseFragment {
    public ArrayList<PostStory> listStory = new ArrayList<>();
    public ArrayList<String> urls = new ArrayList<>();
    public GridView gv;
    public PhotoGridProfileAdapter adapter;

    public int currentPage = 1;
    public boolean isRefresh = false;
    public boolean isLoadding = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gridview_photo, container, false);

        adapter = new PhotoGridProfileAdapter(getActivity(), listStory);
        gv = (GridView) rootView.findViewById(R.id.grid_view);
        gv.setAdapter(adapter);
        gv.setOnScrollListener(new EndlessListOnScrollListener(20,1) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                currentPage = page;
                isRefresh = false;
                //String loadMoreUrl = MainApplication.ENDPOINT + "/search/channel?sort=F&page="+page;
                if (!isLoadding) {
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
                n=0;

                for(int j = 0 ; j < listStory.size()  ; j++)  {
                    if(listStory.get(j).media != null) {
                        String url = listStory.get(j).media.getFullUrl();
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

    public static final String USER_ID = "USER_ID";
    public String userId;

    public static PhotoGridProfileFragment newInstance(String userId){
        PhotoGridProfileFragment mFragment = new PhotoGridProfileFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(USER_ID, userId);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(USER_ID);
        }
        ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId), "photo", 1, 20, false));

    }

    int n = 0;
    @Subscribe public void onLoadTimelineSuccess(LoadTimelineSuccessEvent event) {
        if(isRefresh)
            listStory.clear();
        isRefresh = false;
        listStory.addAll(event.getTimelineData().getPosts());
        adapter.notifyDataSetChanged();
        isLoadding = false;
       // myTitanicTextView.setVisibility(View.GONE);

    }
}
