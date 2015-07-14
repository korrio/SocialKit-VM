package co.aquario.socialkit.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import org.parceler.Parcels;

import java.util.ArrayList;

import co.aquario.socialkit.VMApplication;
import co.aquario.socialkit.R;
import co.aquario.socialkit.LoginActivity;
import co.aquario.socialkit.adapter.FriendRecyclerAdapter;
import co.aquario.socialkit.event.FollowUserSuccessEvent;
import co.aquario.socialkit.event.LoadFriendListEvent;
import co.aquario.socialkit.event.LoadFriendListSuccessEvent;
import co.aquario.socialkit.event.LogoutEvent;
import co.aquario.socialkit.event.UnfollowUserSuccessEvent;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.User;
import co.aquario.socialkit.util.Utils;
import co.aquario.socialkit.view.AutofitRecyclerView;
import co.aquario.socialkit.widget.EndlessRecyclerOnScrollListener;

/**
 * Created by Mac on 3/10/15.
 */
public class FriendSocialFragment extends BaseFragment {
    private static final String LOAD_TYPE = "TYPE";
    private static final String USER_ID = "USER_ID";
    ArrayList<User> list = new ArrayList<>();
    FriendRecyclerAdapter adapter2;
    RecyclerView recyclerView;
    GridLayoutManager manager;
    boolean refresh = false;
    private String type = "";
    private String userId = "";

    public static FriendSocialFragment newInstance(String type,String userId){
        FriendSocialFragment mFragment = new FriendSocialFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(LOAD_TYPE, type);
        mBundle.putString(USER_ID,userId);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    public static FriendSocialFragment newInstance(String type,String userId,ArrayList<User> list){
        FriendSocialFragment mFragment = new FriendSocialFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(LOAD_TYPE, type);
        mBundle.putString(USER_ID,userId);
        mBundle.putParcelable("LIST", Parcels.wrap(list));
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = VMApplication.get(getActivity()).getPrefManager();
        adapter2 = new FriendRecyclerAdapter(getActivity(),list);

        if (getArguments() != null) {
            //userId = prefManager.userId().getOr("0");
            type = getArguments().getString(LOAD_TYPE);
            userId = getArguments().getString(USER_ID);
            if(userId.equals(""))
                userId = prefManager.userId().getOr("0");
            if(!type.equals("SEARCH"))
                ApiBus.getInstance().post(new LoadFriendListEvent(type,Integer.parseInt(userId),1,100));
            else {
                list = Parcels.unwrap(getArguments().getParcelable("LIST")) ;
                adapter2.updateList(list);
                //emptyTv.setVisibility(View.GONE);
            }

        }
    }

    TextView emptyTv;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview_autofit, container, false);

        emptyTv = (TextView) rootView.findViewById(R.id.no_data);

        recyclerView = (AutofitRecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter2);
        if(Utils.isTablet(getActivity()))
            manager = new GridLayoutManager(getActivity(), 6);
        else
            manager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(manager);


        //LinearLayoutManager linearLayoutManager = toolbar LinearLayoutManager(getActivity());
        //linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(manager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.e("scrollBottom", "laew na");
                refresh = false;
                if(!type.equals("SEARCH"))
                    ApiBus.getInstance().post(new LoadFriendListEvent(type, Integer.parseInt(userId), current_page, 100));
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
                i.putExtra("avatarUrl",listStory.get(position).getAvatarUrl());
                i.putExtra("cover",listStory.get(position).getCoverUrl());
                i.putExtra("nameUser",listStory.get(position).nameUser);
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

    @Subscribe public void onLoadFriendListSuccess(LoadFriendListSuccessEvent event) {
        Log.e("MYTYPE",type);
        if(event.getType().equals(type)) {
            if(refresh)
                list.clear();
            list.addAll(event.getFriendListData().users);
            adapter2.updateList(list);
            emptyTv.setVisibility(View.GONE);
        }
    }

    @Subscribe public void onFollowSuccess(FollowUserSuccessEvent event) {

    }

    @Subscribe public void onUnfollowSuccess(UnfollowUserSuccessEvent event) {

    }

    @Subscribe public void onLogout(LogoutEvent event) {
        VMApplication.logout();
        Intent login = new Intent(getActivity(), LoginActivity.class);
        startActivity(login);
        getActivity().finish();
    }
}
