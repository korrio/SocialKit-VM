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
import org.parceler.Parcels;

import java.util.ArrayList;

import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.activity.DragableActivity;
import co.aquario.socialkit.activity.NewDragableActivity;
import co.aquario.socialkit.adapter.VideoRecyclerAdapter;
import co.aquario.socialkit.model.Video;
import co.aquario.socialkit.util.PrefManager;
import co.aquario.socialkit.util.Utils;
import co.aquario.socialkit.widget.EndlessRecyclerOnScrollListener;

public class VideoFragment extends BaseFragment {

    public static final String KEYWORD_SEARCH = "KEYWORD_SEARCH";
    public static final String SORT_TYPE = "TYPE";
    //public static final String USER_ID = "USER_ID";
    VideoFragment fragment;
    //private String userId = "";
    private String sortType = "";
    private String keyword = "";
    private AQuery aq;
    private String url;
    private ArrayList<Video> list = new ArrayList<Video>();
    private VideoRecyclerAdapter adapterVideos;
    private GridLayoutManager manager;
    private PrefManager pref;

    public VideoFragment() {

    }

    public static VideoFragment newInstance(String keyword,String sortType){
        VideoFragment mFragment = new VideoFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(KEYWORD_SEARCH, keyword);
        mBundle.putString(SORT_TYPE, sortType);
        //mBundle.putString(USER_ID, userId);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    public static VideoFragment newInstance(String keyword,String sortType, ArrayList<Video> videoArrayList){
        VideoFragment mFragment = new VideoFragment();
        Bundle mBundle = new Bundle();
        mBundle.putParcelable("LIST", Parcels.wrap(videoArrayList));
        mBundle.putString(KEYWORD_SEARCH, keyword);
        mBundle.putString(SORT_TYPE, sortType);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sortType = getArguments().getString(SORT_TYPE);
            keyword = getArguments().getString(KEYWORD_SEARCH);
        }

        pref = VMApp.get(getActivity().getApplicationContext()).getPrefManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recyclerview_autofit_2col, container, false);

        aq = new AQuery(getActivity());
        fragment = this;

        RecyclerView recList = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recList.setHasFixedSize(true);
        if(Utils.isTablet(getActivity()))
            manager = new GridLayoutManager(getActivity(), 2);
        else
            manager = new GridLayoutManager(getActivity(), 1);
        recList.setLayoutManager(manager);

        adapterVideos = new VideoRecyclerAdapter(getActivity(),list);

        adapterVideos.SetOnItemClickListener(new VideoRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Video post = list.get(position);
                Intent i2;
                if(getActivity().getClass().getSimpleName().equals("NewDragableActivity"))
                    i2 = new Intent(getActivity(), DragableActivity.class);
                else
                    i2 = new Intent(getActivity(), NewDragableActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("obj", Parcels.wrap(post));

                i2.putExtras(bundle);
                startActivity(i2);
                //getActivity().finish();
            }
        });

        recList.setAdapter(adapterVideos);

        recList.addOnScrollListener(new EndlessRecyclerOnScrollListener(manager) {
            @Override
            public void onLoadMore(int page) {
                if (!sortType.equals("SEARCH")) {
                    String loadmoreUrl = "http://api.vdomax.com/search/video?sort=" + sortType + "&page=" + page;
                    Log.e("loadmoreurl", loadmoreUrl);
                    aq.ajax(loadmoreUrl, JSONObject.class, fragment, "getJson");
                }
            }
        });



        Log.e("sortType",sortType);

        if(!sortType.equals("SEARCH")) {
            url = "http://api.vdomax.com/search/video?sort="+sortType+"&page=1";
            Log.e("loadurl",url);
            aq.ajax(url, JSONObject.class, this, "getJson");
        } else {
            list = Parcels.unwrap(getArguments().getParcelable("LIST"));
            adapterVideos.notifyDataSetChanged();
        }


        return rootView;
    }

    public void getJson(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        AQUtility.debug("jo", jo);

        if (jo != null) {
            JSONArray ja = jo.getJSONArray("videos");
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
                    String pName = publisher.optString("name");
                    //JSONObject cover = publisher.optJSONObject("cover");
                    String pAvatar = publisher.optString("avatar_url");

                    int loveCount = obj.optInt("love_count");
                    int commentCount = obj.optInt("comment_count");
                    int shareCount = obj.optInt("share_count");

                    Video item = new Video("youtube",postId,title,shortMessage,youtubeId,text,timestamp,view,pUserId,pName,pAvatar,loveCount,commentCount,shareCount);
                    list.add(item);
                }
            }
            adapterVideos.notifyDataSetChanged();

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
