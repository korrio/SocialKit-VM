package co.aquario.socialkit.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.adapter.ChannelAdapter;
import co.aquario.socialkit.model.Channel;
import co.aquario.socialkit.util.Utils;
import co.aquario.socialkit.widget.EndlessListOnScrollListener;


public class ChannelFragment extends BaseFragment {

    private static final String TITLE = "TITLE";
    private static final String TAB = "TAB";
    public ChannelFragment fragment;
    String endpoint = "http://api.vdomax.com";
    // tab 0
    String liveChannelUrl = endpoint + "/live/now?a=1";
    // tab 1
    String channelUrl = endpoint + "/search/channel?page=1&sort=F" + "&limit=100";
    int currentPage;
    boolean isRefresh = false;
    boolean isLoadding = false;
    private ArrayList<Channel> liveChannelList = new ArrayList<Channel>();
    private ArrayList<Channel> mostFollowerList = new ArrayList<Channel>();
    private ChannelAdapter channelAdapter;
    private GridView mGridView;
    private AQuery aq;
    private SwipeRefreshLayout swipeLayout;
    private int tabNo;

    public static ChannelFragment newInstance(String tabTitle,int tabNo) {
        ChannelFragment mFragment = new ChannelFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(TITLE, tabTitle);
        mBundle.putInt(TAB, tabNo);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tabNo = getArguments().getInt(TAB);
            //getActivity().setTitle(getArguments().getString(TITLE));
        } else {
            tabNo = 0;
            //getActivity().setTitle("Channels");
        }
        fragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_channel, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        aq = new AQuery(getActivity(), rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                String suffix = "&user_id=" + VMApp.mPref.userId().getOr("0");
                if (tabNo == 0) {
                    aq.ajax(liveChannelUrl + suffix, JSONObject.class, fragment, "getJson");
                    Log.e("liveChannelUrl", liveChannelUrl);
                }
                else {
                    aq.ajax(channelUrl + suffix, JSONObject.class, fragment, "getJson");
                    Log.e("channelUrl", channelUrl);
                }
                swipeLayout.setRefreshing(false);
            }
        });

        if(tabNo == 0)
            channelAdapter = new ChannelAdapter(getActivity(), liveChannelList);
        else
            channelAdapter = new ChannelAdapter(getActivity(), mostFollowerList);

        mGridView = (GridView) view.findViewById(R.id.grid);

        int col = (Utils.isTablet(getActivity().getApplicationContext())) ? 2 : 1;

        mGridView.setNumColumns(col);

//        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                Channel c = liveChannelList.get(position);
//
//                if (tabNo == 0) {
//
//                    c = liveChannelList.get(position);
//
////                    LiveFragment fragment = LiveFragment.newInstance(liveChannelList.get(position));
////                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "WATCH_LIVE_MAIN").addToBackStack(null).commit();
//
//
//                    /*
//                    // intent to profile page with live streaming
//                    Intent i = toolbar Intent(getActivity(), VitamioActivity.class);
//                    i.putExtra("id", "rtmp://150.107.31.6:1935/live/" + liveChannelList.get(position).username);
//                    i.putExtra("name", liveChannelList.get(position).name);
//                    i.putExtra("avatar", liveChannelList.get(position).getAvatarUrl());
//                    i.putExtra("cover", liveChannelList.get(position).getCoverUrl());
//                    i.putExtra("title", liveChannelList.get(position).name);
//                    i.putExtra("desc", "@" + liveChannelList.get(position).username);
//                    i.putExtra("userId", liveChannelList.get(position).id);
//                    startActivity(i);
//                    */
//                } else if (tabNo == 1) {
//                    // intent to profile page w/o live streaming
////                    LiveFragment fragment = LiveFragment.newInstance(mostFollowerList.get(position));
////                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "WATCH_LIVE_MAIN").addToBackStack(null).commit();
//
//                    c = mostFollowerList.get(position);
//
//
//                }
//
//
//
//                String liveURL = "https://stream-1.vdomax.com/live/"+c.username+"/playlist.m3u8";
//                long unixTime = System.currentTimeMillis() / 1000L;
//
//                Video clip = new Video("clip","", c.name, "@"+c.username,liveURL ,"", unixTime + "", "168", c.id, c.name, c.getAvatarUrl(), 0, 0, 0);
//
//                Intent intentClip = new Intent(getActivity(), LiveDragableActivity.class);
//                Bundle bundleClip = new Bundle();
//                bundleClip.putParcelable("obj", Parcels.wrap(clip));
//
//                intentClip.putExtras(bundleClip);
//                getActivity().startActivity(intentClip);
//
//            }
//        });
        mGridView.setAdapter(channelAdapter);
        mGridView.setOnScrollListener(new EndlessListOnScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                currentPage = page;
                isRefresh = false;
                String loadMoreUrl = endpoint + "/search/channel?sort=F&page=" + page + "&limit=100";
                if (!isLoadding) {
                    aq.ajax(loadMoreUrl, JSONObject.class, fragment, "getJson");
                    Log.e("loadMoreUrl", loadMoreUrl);
                }

                isLoadding = true;
            }
        });

        aq = new AQuery(getActivity());
        if(tabNo == 0) {
            aq.ajax(liveChannelUrl, JSONObject.class, this, "getJsonLive");
            Log.e("liveChannelUrl",liveChannelUrl);
        } else {
            aq.ajax(channelUrl  , JSONObject.class, this, "getJsonMostFollower");
            Log.e("channelUrl", channelUrl);
        }

    }

    public void getJsonLive(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        AQUtility.debug("jo", jo);
        if (jo != null) {
            if (isRefresh) {
                //mostFollowerList.clear();
                liveChannelList.clear();
            }

            isRefresh = false;

            JSONArray ja = jo.optJSONArray("channels");
            for (int i = 0; i < ja.length(); i++) {
                JSONObject obj = ja.optJSONObject(i);

                String userId = obj.optString("id");
                String name = obj.optString("name");
                String username = obj.optString("username");
                String avatar = obj.optString("avatar_url");
                String cover = obj.optString("cover_url");
                String liveCover = obj.optString("live_cover");
                String gender = obj.optString("gender");
                if(gender == null)
                    gender = "male";
                boolean liveStatus = obj.optBoolean("status");

                Channel channel = new Channel(userId, name, username, cover, avatar, liveCover, gender, liveStatus);

                channel.isFollowing = obj.optBoolean("is_following");
                if(obj.optJSONObject("count") != null)
                    channel.totalFollower = obj.optJSONObject("count").optInt("follower") + "";
                else
                    channel.totalFollower = "";
                    liveChannelList.add(channel);

            }
            channelAdapter.notifyDataSetChanged();
            swipeLayout.setRefreshing(false);
            isLoadding = false;
            AQUtility.debug("done");

        } else {
            AQUtility.debug("error!");
        }
    }

    public void getJsonMostFollower(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        AQUtility.debug("jo", jo);
        if (jo != null) {
            if (isRefresh) {
                mostFollowerList.clear();
                //liveChannelList.clear();
            }

            isRefresh = false;

            JSONArray ja = jo.optJSONArray("channels");
            for (int i = 0; i < ja.length(); i++) {
                JSONObject obj = ja.optJSONObject(i);

                String userId = obj.optString("id");
                String name = obj.optString("name");
                String username = obj.optString("username");
                String avatar = obj.optString("avatar_url");
                String cover = obj.optString("cover_url");
                String liveCover = obj.optString("live_cover");
                String gender = obj.optString("gender");
                if(gender == null)
                    gender = "male";
                boolean liveStatus = obj.optBoolean("online");

                Channel channel = new Channel(userId, name, username, cover, avatar, liveCover, gender, liveStatus);
                mostFollowerList.add(channel);

            }
            channelAdapter.notifyDataSetChanged();
            swipeLayout.setRefreshing(false);
            isLoadding = false;
            AQUtility.debug("done");

        } else {
            AQUtility.debug("error!");
        }
    }


}
