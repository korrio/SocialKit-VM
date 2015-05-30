package co.aquario.socialkit.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
public class FriendFragment extends BaseFragment {
    private static final String LOAD_TYPE = "TYPE";
    private static final String USER_ID = "USER_ID";
    ArrayList<User> list = new ArrayList<>();
    FriendRecyclerAdapter adapter2;
    RecyclerView recyclerView;
    GridLayoutManager manager;
    boolean refresh = false;
    private String type = "";
    private String userId = "";

    public static FriendFragment newInstance(String text,String userId){
        FriendFragment mFragment = new FriendFragment();
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
            type = getArguments().getString(LOAD_TYPE);
            userId = getArguments().getString(USER_ID);
        }
        if(!type.equals("")) {
            if(userId.equals(""))
                userId = prefManager.userId().getOr("0");
            ApiBus.getInstance().post(new LoadFriendListEvent(type,Integer.parseInt(userId),1,100));
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview_autofit, container, false);

        recyclerView = (AutofitRecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        adapter2 = new FriendRecyclerAdapter(getActivity(),list);
        recyclerView.setAdapter(adapter2);
        if(Utils.isTablet(getActivity()))
            manager = new GridLayoutManager(getActivity(), 6);
        else
            manager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(manager);


        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(manager) {
            @Override
            public void onLoadMore(int current_page) {
                refresh = false;
                ApiBus.getInstance().post(new LoadFriendListEvent(type, Integer.parseInt(userId), current_page, 100));
            }


        });

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
        }
    }

    @Subscribe public void onFollowSuccess(FollowUserSuccessEvent event) {

    }

    @Subscribe public void onUnfollowSuccess(UnfollowUserSuccessEvent event) {

    }

    @Subscribe public void onLogout(LogoutEvent event) {
        MainApplication.logout();
        Intent login = new Intent(getActivity(), LoginActivity.class);
        startActivity(login);
        getActivity().finish();
    }
}