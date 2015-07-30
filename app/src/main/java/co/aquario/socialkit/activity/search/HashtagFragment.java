package co.aquario.socialkit.activity.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import co.aquario.chatapp.picker.RecyclerItemClickListener;
import co.aquario.socialkit.R;
import co.aquario.socialkit.event.toolbar.TitleEvent;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.fragment.main.FeedFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.Hashtag;
import co.aquario.socialkit.util.PrefManager;


public class HashtagFragment extends BaseFragment {

    private static final String USER_ID = "USER_ID";
    private static final String TYPE = "TYPE";

    private String userId;
    private String notiType;

    @InjectView(R.id.contentRoot)
    LinearLayout contentRoot;
    @InjectView(R.id.rvComments)
    RecyclerView rvComments;

    ArrayList<Hashtag> listHashtag = new ArrayList<>();
    PrefManager pref;

    private HashtagAdapter hashtagAdapter;
    private int drawingStartLocation;

    public HashtagFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HashtagFragment newInstance(ArrayList<Hashtag> listHashtag) {
        HashtagFragment fragment = new HashtagFragment();
        Bundle args = new Bundle();
        args.putParcelable("LIST_HASHTAG", Parcels.wrap(listHashtag));
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            listHashtag = Parcels.unwrap(getArguments().getParcelable("LIST_HASHTAG"));
        }
        hashtagAdapter = new HashtagAdapter(getActivity(),listHashtag);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_noti, container, false);
        ButterKnife.inject(this,rootView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setHasFixedSize(true);

        rvComments.setAdapter(hashtagAdapter);
        rvComments.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvComments.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    hashtagAdapter.setAnimationsLocked(true);
                }
            }
        });
        rvComments.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
            @Override public void onItemClick(View view, int position) {
                String tag = listHashtag.get(position).tag;
                FeedFragment fragment = new FeedFragment().newInstance(tag);
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.sub_container, fragment).addToBackStack(null);
                transaction.commitAllowingStateLoss();
                ApiBus.getInstance().postQueue(new TitleEvent("#" + tag));
            }
        }));

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ApiBus.getInstance().postQueue(new NotiListEvent(Integer.parseInt(userId), 1));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
