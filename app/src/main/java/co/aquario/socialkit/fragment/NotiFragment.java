package co.aquario.socialkit.fragment;

import android.app.Activity;
import android.content.Intent;
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
import co.aquario.chatapp.CallActivityLauncher;
import co.aquario.chatapp.ChatActivity;
import co.aquario.chatapp.event.request.NotiListEvent;
import co.aquario.chatapp.event.response.NotiListEventSuccess;
import co.aquario.chatapp.model.Noti;
import co.aquario.chatapp.picker.RecyclerItemClickListener;
import co.aquario.socialkit.MainActivity;
import co.aquario.socialkit.NewProfileActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
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
        rvComments.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        intentManage(Integer.parseInt(notiList.get(position).type),
                                Integer.parseInt(notiList.get(position).from_id),
                                Integer.parseInt(notiList.get(position).to_id));
                    }
                }));

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

    public void intentManage(int type,int postId,int fromId) {

        String customdata = "";
        if (type == TYPES_likeFeed) {
            Intent routeIntent = new Intent(getActivity(), MainActivity.class);
            routeIntent.putExtra("type", "post");
            routeIntent.putExtra("post_id", postId);
            startActivity(routeIntent);
        } else if (type == TYPES_commentFeed) {
            Intent routeIntent = new Intent(getActivity(), MainActivity.class);
            routeIntent.putExtra("type", "post");
            routeIntent.putExtra("post_id", postId);
            startActivity(routeIntent);
        } else if(type == TYPES_shareFeed) {
            Intent routeIntent = new Intent(getActivity(), MainActivity.class);
            routeIntent.putExtra("type", "post");
            routeIntent.putExtra("post_id", postId);
            startActivity(routeIntent);
        } else if (type == TYPES_liveNow) {
            return;
			/*
			 * toDetail = new Intent(ManagePush.this, PlayActivity.class);
			 * toDetail.putExtra("isPlay", "1"); toDetail.putExtra("roomId",
			 * fromName); toDetail.putExtra("roomTag", "0");
			 * startActivity(toDetail);
			 */

        } else if (type == TYPES_followedYou) {
            NewProfileActivity.startProfileActivity(getActivity(), fromId + "");

//			Intent profileIntent = new Intent(this, LandingActivity.class);
//			profileIntent.putExtra("type", "profile");
//			profileIntent.putExtra("user_id", fromId + "");
//			startActivity(profileIntent);

        } else if (type == TYPES_chatMessage || type == TYPES_chatSticker
                || type == TYPES_chatFile || type == TYPES_chatLocation) {

            ChatActivity.startChatActivity(getActivity(), Integer.parseInt(VMApp.mPref.userId().getOr("0")), fromId, 0);

        }
//        else if (type == TYPES_confCreate || type == TYPES_confJoin
//				|| type == TYPES_confInvite) {
//			// intent Lobby
//			toDetail = new Intent(PushManage.this,
//                    LandingActivity.class);
//			toDetail.putExtra("roomName", roomName);
//			startActivity(toDetail);
//
//		}
        else if (type == TYPES_chatFreeCall) {
            ChatActivity.startChatActivity(getActivity(), Integer.parseInt(VMApp.mPref.userId().getOr("0")) ,fromId,0);
            CallActivityLauncher.startCallActivity(getActivity(), customdata, false);
        } else if (type == TYPES_chatVideoCall) {
            ChatActivity.startChatActivity(getActivity(),Integer.parseInt(VMApp.mPref.userId().getOr("0")) ,fromId,0);
            CallActivityLauncher.startCallActivity(getActivity(), customdata, true);
        } else if (type == TYPES_chatInviteGroup) {
            ChatActivity.startChatActivity(getActivity(), Integer.parseInt(VMApp.mPref.userId().getOr("0")) ,fromId,0);
        }


    }

    public static int TYPES_likeFeed = 100;
    public static int TYPES_commentFeed = 101;
    public static int TYPES_shareFeed = 102;
    public static int TYPES_reportFeed = 103;
    public static int TYPES_liveNow = 200;
    public static int TYPES_followedYou = 300;
    public static int TYPES_chatMessage = 500;
    public static int TYPES_chatSticker = 501;
    public static int TYPES_chatFile = 502;
    public static int TYPES_chatLocation = 503;
    public static int TYPES_chatFreeCall = 504;
    public static int TYPES_chatVideoCall = 505;
    public static int TYPES_chatInviteGroup = 506;
    public static int TYPES_confInvite = 600;
    public static int TYPES_confCreate = 601;
    public static int TYPES_confJoin = 602;
    public static int TYPES_NotifyBadge = 700;

}
