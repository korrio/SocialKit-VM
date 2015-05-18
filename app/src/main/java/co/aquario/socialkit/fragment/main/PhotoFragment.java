package co.aquario.socialkit.fragment.main;

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

import co.aquario.socialkit.MainApplication;
import co.aquario.socialkit.R;
import co.aquario.socialkit.activity.LoginActivity;
import co.aquario.socialkit.adapter.FeedAdapter;
import co.aquario.socialkit.event.LoadTimelineEvent;
import co.aquario.socialkit.event.LoadTimelineSuccessEvent;
import co.aquario.socialkit.event.LogoutEvent;
import co.aquario.socialkit.fragment.BaseFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.PostStory;
import co.aquario.socialkit.widget.EndlessRecyclerOnScrollListener;

/**
 * Created by Mac on 3/10/15.
 */
public class PhotoFragment extends BaseFragment {
    private static final String LOAD_TYPE = "TYPE";
    private static final String USER_ID = "USER_ID";

    //private String type = "";
    private String userId = "";

    ArrayList<PostStory> list = new ArrayList<>();

    FeedAdapter adapter;

    RecyclerView recyclerView;
    boolean refresh = false;

    private boolean isHomeTimeline = true;
    private String TYPE = "photo";
    private static final int PER_PAGE = 20;

    public static PhotoFragment newInstance(String text,String userId){
        PhotoFragment mFragment = new PhotoFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(LOAD_TYPE, text);
        mBundle.putString(USER_ID,userId);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = MainApplication.get(getActivity()).getPrefManager();
        if (getArguments() != null) {
            TYPE = getArguments().getString(LOAD_TYPE);
            userId = getArguments().getString(USER_ID);
        }
        if(!TYPE.equals("")) {
            if(userId.equals(""))
                userId = prefManager.userId().getOr("6");
            ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId), TYPE, 1, PER_PAGE, isHomeTimeline));
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.scroll);
        //recyclerView.setHasFixedSize(true);
        adapter = new FeedAdapter(getActivity(),list);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page) {
                Log.e("scrollBottom","laew na");
                refresh = true;
                ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId), TYPE, page, PER_PAGE, isHomeTimeline));
                //ApiBus.getInstance().post(new LoadFriendListEvent(type,Integer.parseInt(userId),current_page,15));
            }
        });




        /*
        //gridView = (ObservableGridView) rootView.findViewById(R.id.scroll);
        adapter = new FriendAdapter(getActivity(), liveChannelList);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), SlidingUpRecyclerViewActivity.class);
                i.putExtra("userId",liveChannelList.get(position).id);
                i.putExtra("avatar",liveChannelList.get(position).getAvatarUrl());
                i.putExtra("cover",liveChannelList.get(position).getCoverUrl());
                i.putExtra("name",liveChannelList.get(position).name);
                i.putExtra("username",liveChannelList.get(position).username);
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
            adapter.notifyDataSetChanged();

    }

    @Subscribe public void onLogout(LogoutEvent event) {
        MainApplication.logout();
        Intent login = new Intent(getActivity(), LoginActivity.class);
        startActivity(login);
        getActivity().finish();
    }
}
