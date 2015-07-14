package co.aquario.socialkit.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import co.aquario.socialkit.VMApplication;
import co.aquario.socialkit.R;
import co.aquario.socialkit.LoginActivity;
import co.aquario.socialkit.adapter.FeedAdapter;
import co.aquario.socialkit.event.LoadTimelineEvent;
import co.aquario.socialkit.event.LoadTimelineSuccessEvent;
import co.aquario.socialkit.event.LogoutEvent;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.PostStory;
import co.aquario.socialkit.widget.EndlessRecyclerOnScrollListener;

/**
 * Created by Mac on 3/10/15.
 */
public class PhotoFragmentGrid extends BaseFragment {
    private static final String LOAD_TYPE = "TYPE";
    private static final String USER_ID = "USER_ID";

    //private String type = "";
    private String userId = "";

    ArrayList<PostStory> list = new ArrayList<>();

    FeedAdapter adapter2;

    RecyclerView recyclerView;
    boolean refresh = false;

    private boolean isHomeTimeline = true;
    private String TYPE = "photo";
    private static final int PER_PAGE = 20;

    public static PhotoFragmentGrid newInstance(){
        PhotoFragmentGrid mFragment = new PhotoFragmentGrid();
        Bundle mBundle = new Bundle();
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = VMApplication.get(getActivity()).getPrefManager();
        userId = prefManager.userId().getOr("0");
        ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId), TYPE, 1, PER_PAGE, isHomeTimeline));

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview_autofit_2col, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        adapter2 = new FeedAdapter(getActivity(),list);
        recyclerView.setAdapter(adapter2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page) {
                Log.e("scrollBottom","laew na");
                refresh = true;
                ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId), TYPE, page, PER_PAGE, isHomeTimeline));
                //ApiBus.getInstance().post(toolbar LoadFriendListEvent(type,Integer.parseInt(userId),current_page,15));
            }
        });




        /*
        //gridView = (ObservableGridView) rootView.findViewById(R.id.scroll);
        adapter = toolbar FriendAdapter(getActivity(), listStory);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(toolbar AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = toolbar Intent(getActivity(), SlidingUpRecyclerViewActivity.class);
                i.putExtra("userId",listStory.get(position).id);
                i.putExtra("avatar",listStory.get(position).getAvatarUrl());
                i.putExtra("cover",listStory.get(position).getCoverUrl());
                i.putExtra("name",listStory.get(position).name);
                i.putExtra("username",listStory.get(position).username);
                getActivity().startActivity(i);
            }
        });
        */

        return rootView;
    }

    /**
     * Save Fragment's State here
     */
    @Override
    protected void onSaveState(Bundle outState) {
        super.onSaveState(outState);
        // For example:
        //outState.putString("text", tvSample.getText().toString());
    }

    /**
     * Restore Fragment's State here
     */
    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        super.onRestoreState(savedInstanceState);
        // For example:
        //tvSample.setText(savedInstanceState.getString("text"));
    }

    @Subscribe public void onLoadTimelineSuccess(LoadTimelineSuccessEvent event) {
        Log.e("MYTYPE",TYPE);

        if(refresh)
            list.clear();
        list.addAll(event.getTimelineData().getPosts());
        adapter2.notifyDataSetChanged();

    }

    @Subscribe public void onLogout(LogoutEvent event) {
        VMApplication.logout();
        Intent login = new Intent(getActivity(), LoginActivity.class);
        startActivity(login);
        getActivity().finish();
    }
}
