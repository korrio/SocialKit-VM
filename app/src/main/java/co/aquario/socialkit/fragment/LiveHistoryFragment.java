package co.aquario.socialkit.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.aquario.socialkit.R;
import co.aquario.socialkit.activity.VideoPlayerActivity;
import co.aquario.socialkit.activity.VitamioActivity;
import co.aquario.socialkit.adapter.LiveHistoryRecyclerAdapter;
import co.aquario.socialkit.model.Live;
import co.aquario.socialkit.util.Utils;

public class LiveHistoryFragment extends BaseFragment {

    String liveHistoryUrl = "http://api.vdomax.com/live/history";


    ArrayList<Live> list = new ArrayList<Live>();
    LiveHistoryRecyclerAdapter adapter;
    private GridLayoutManager manager;

    public AQuery aq;

    private MenuItem searchItem;
    private SearchRecentSuggestions suggestions;
    private SearchView searchView;

    Activity mActivity;
    RecyclerView recList;

    private static final String USER_ID = "USER_ID";
    private String userId = "";

    public static LiveHistoryFragment newInstance(String userId){
        LiveHistoryFragment mFragment = new LiveHistoryFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(USER_ID,userId);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(USER_ID);
        }

        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recyclerview_autofit_2col, container, false);
        recList = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        adapter = new LiveHistoryRecyclerAdapter(mActivity, list);

        recList.setHasFixedSize(true);
        if(Utils.isTablet(mActivity))
            manager = new GridLayoutManager(mActivity, 2);
        else
            manager = new GridLayoutManager(mActivity, 1);
        recList.setLayoutManager(manager);
        recList.setAdapter(adapter);

        adapter.SetOnItemClickListener(new LiveHistoryRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String userId = list.get(position).getUserId();
                String url = list.get(position).getUrlLive();
                String avatarUrl = list.get(position).getAvatar();
                String username = list.get(position).getNameLive();
                String name = list.get(position).getNameLive();
                String cover = avatarUrl;

                Intent i = new Intent(mActivity, VitamioActivity.class);
                i.putExtra("id", url);
                i.putExtra("name", name);
                i.putExtra("avatar", avatarUrl);
                i.putExtra("title", name);
                i.putExtra("cover", cover);
                i.putExtra("desc", username);
                i.putExtra("userId", userId);
                //startActivity(i);

                VideoPlayerActivity.startActivity(mActivity, url);
                //VitamioActivity.startActivity(mActivity, url,list.get(position));

            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        aq = new AQuery(mActivity);

        String url = liveHistoryUrl;
        if(!userId.equals("0"))
            url = liveHistoryUrl + "/" + userId;

        aq.ajax(url, JSONObject.class, this, "getJson");
    }

    public void getJson(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        AQUtility.debug("jo", jo);
        if (jo != null) {
            JSONArray ja = jo.getJSONArray("history");
            for (int i = 0; i < ja.length(); i++) {
                JSONObject obj = ja.getJSONObject(i);

                String userId = obj.optString("user_id");
                String  nameLive = obj.optString("username");
                String urlLive = obj.optString("url");
                String photoLive = obj.optString("thumb");
                String avatar = obj.optString("avatar");
                String date = obj.optString("date");

                JSONObject media = obj.getJSONObject("duration");

                String hours = media.optString("hours");
                String minutes = media.optString("minutes");
                String seconds = media.optString("seconds");

                Live liveList = new Live(urlLive,photoLive,nameLive,hours,minutes,seconds,null,avatar,date,userId);
                list.add(liveList);

            }
            adapter.notifyDataSetChanged();
            AQUtility.debug("done");

        } else {
            AQUtility.debug("error!");
        }
    }



}
