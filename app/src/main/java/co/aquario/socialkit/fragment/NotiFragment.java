package co.aquario.socialkit.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import co.aquario.chatapp.event.request.NotiListEvent;
import co.aquario.chatapp.event.response.NotiListEventSuccess;
import co.aquario.chatapp.model.Noti;
import co.aquario.socialkit.R;
import co.aquario.socialkit.adapter.NotiAdapter;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.util.PrefManager;


public class NotiFragment extends BaseFragment {

    private static final String USER_ID = "USER_ID";
    private static final String TYPE = "TYPE";
    
    private String userId;
    private String notiType;

    @InjectView(R.id.contentRoot)
    LinearLayout contentRoot;
    @InjectView(R.id.rvComments)
    RecyclerView rvComments;

    ArrayList<Noti> notiList = new ArrayList<>();
    PrefManager pref;

    private NotiAdapter notiAdapter;
    private int drawingStartLocation;

    public NotiFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static NotiFragment newInstance(String userId,String notiType) {
        NotiFragment fragment = new NotiFragment();
        Bundle args = new Bundle();
        args.putString(USER_ID, userId);
        args.putString(TYPE, notiType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(USER_ID);
            notiType = getArguments().getString(TYPE);
        }
        notiAdapter = new NotiAdapter(getActivity(),notiList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_noti, container, false);
        ButterKnife.inject(this,rootView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setHasFixedSize(true);

        rvComments.setAdapter(notiAdapter);
        rvComments.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvComments.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    notiAdapter.setAnimationsLocked(true);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ApiBus.getInstance().postQueue(new NotiListEvent(Integer.parseInt(userId), 1));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Subscribe public void onLoadNotiListSuccess(NotiListEventSuccess event) {
        if(event.data.data != null)
            notiList.addAll(event.data.data);
        notiAdapter.updateItems();
    }

}
