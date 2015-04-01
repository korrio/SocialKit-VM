package co.aquario.socialkit.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.aquario.socialkit.MainApplication;
import co.aquario.socialkit.R;
import co.aquario.socialkit.activity.YoutubeDragableActivity;
import co.aquario.socialkit.adapter.VideoRecyclerAdapter;
import co.aquario.socialkit.fragment.BaseFragment;
import co.aquario.socialkit.model.Video;
import co.aquario.socialkit.util.PrefManager;
import co.aquario.socialkit.util.Utils;
import co.aquario.socialkit.widget.EndlessRecyclerOnScrollListener;

public class VideoFragment extends BaseFragment {

    public static final String KEYWORD_SEARCH = "KEYWORD_SEARCH";
    public static final String SORT_TYPE = "SORT_TYPE";
    public static final String USER_ID = "USER_ID";

    private String userId = "";
    private String sortType = "";
    private String keyword = "";

    private AQuery aq;
    private String url;
    private ArrayList<Video> list = new ArrayList<Video>();
    private VideoRecyclerAdapter adapterVideos;
    private GridLayoutManager manager;

    public VideoFragment() {

    }

    char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    public static VideoFragment newInstance(String keyword,String sortType,String userId){
        VideoFragment mFragment = new VideoFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(KEYWORD_SEARCH, keyword);
        mBundle.putString(SORT_TYPE, sortType);
        mBundle.putString(USER_ID, userId);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    private PrefManager pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            keyword = getArguments().getString(KEYWORD_SEARCH);
            sortType = getArguments().getString(SORT_TYPE);
            userId = getArguments().getString(USER_ID);
        }
        if(keyword.equals(""))
            keyword = "epic";
        pref = MainApplication.get(getActivity().getApplicationContext()).getPrefManager();
    }

    VideoFragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview_autofit_2col, container, false);

        aq = new AQuery(getActivity());
        adapterVideos = new VideoRecyclerAdapter(getActivity(), list);
        fragment = this;

        RecyclerView recList = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recList.setHasFixedSize(true);
        if(Utils.isTablet(getActivity()))
            manager = new GridLayoutManager(getActivity(), 2);
        else
            manager = new GridLayoutManager(getActivity(), 1);
        recList.setLayoutManager(manager);
        recList.setAdapter(adapterVideos);

        recList.setOnScrollListener(new EndlessRecyclerOnScrollListener(manager) {
            @Override
            public void onLoadMore(int page) {
                String loadmoreUrl = "http://api.vdomax.com/search/video/"+alphabet[page]+"?from="+page+"&limit=20";
                Log.e("loadmoreurl",loadmoreUrl);
                aq.ajax(loadmoreUrl, JSONObject.class, fragment, "getJson");
            }
        });

        adapterVideos.SetOnItemClickListener(new VideoRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Video post = list.get(position);
                String idYoutube = post.getYoutubeId();
                String title = post.getTitle();
                String description = post.getDesc();
                String avatarUrl = post.getpAvatar();
                String profileName = post.getpName();
                String viewCount = post.getView();
                String userId = post.getpUserId();

                Intent i2 = new Intent(getActivity(), YoutubeDragableActivity.class);
                i2.putExtra("userId",userId);
                i2.putExtra("name",profileName);
                i2.putExtra("avatar",avatarUrl);
                i2.putExtra("id",idYoutube);
                i2.putExtra("title",title);
                i2.putExtra("desc",description);
                i2.putExtra("view",viewCount);

                startActivity(i2);
            }
        });



        url = "http://api.vdomax.com/search/video/"+keyword+"?from=0&limit=20";
        Log.e("loadurl",url);
        aq.ajax(url, JSONObject.class, this, "getJson");

        return rootView;
    }

    public void getJson(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        AQUtility.debug("jo", jo);

        if (jo != null) {
            JSONArray ja = jo.getJSONArray("result");
            for (int i = 0; i < ja.length(); i++) {
                JSONObject obj = ja.getJSONObject(i);

                String mediaType = obj.optString("media_type");

                if(mediaType.equals("youtube")) {
                    String postId = obj.optString("post_id");
                    String title = obj.optString("youtube_title");
                    String desc = obj.optString("youtube_description");
                    String youtubeId = obj.optString("youtube_video_id");
                    String text = obj.optString("text");
                    String timestamp = obj.optString("time");
                    String view = obj.optString("view");


                    String shortMessage;
                    if (desc.length() > 60)
                        shortMessage = desc.substring(0, 60);
                    else
                        shortMessage = desc;

                    JSONObject publisher = obj.optJSONObject("publisher");
                    String pUserId = publisher.optString("id");
                    String pName = publisher.optString("username");
                    //JSONObject cover = publisher.optJSONObject("cover");
                    String pAvatar = publisher.optString("avatar_url");

                    Video item = new Video(postId,title,shortMessage,youtubeId,text,timestamp,view,pUserId,pName,pAvatar);
                    list.add(item);
                }
            }
            adapterVideos.notifyDataSetChanged();
            Log.v("list.size()",list.size() + "");

            AQUtility.debug("done");

        } else {
            AQUtility.debug("error!");
        }
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

}
