package co.aquario.socialkit.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import co.aquario.socialkit.R;
import co.aquario.socialkit.activity.VitamioActivity;
import co.aquario.socialkit.adapter.AdapterListLiveFragment;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.model.Live;
import co.aquario.socialkit.util.Utils;

public class LiveHistoryListFragment extends BaseFragment {

    private static String userId;

    AdapterListLiveFragment adapter;
    ArrayList<Live> artistList = new ArrayList<>();
    GridLayoutManager manager;

    public static LiveHistoryListFragment newInstance(String id) {
        LiveHistoryListFragment mFragment = new LiveHistoryListFragment();
        //userId = id;
        Bundle mBundle = new Bundle();
        mBundle.putString("USER_ID", id);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString("USER_ID");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AQuery aq = new AQuery(getActivity());
        String url = "http://api.vdomax.com/live/history/" + userId;

        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Downloading live history");
        dialog.setMessage("กำลังดาวน์โหลดประวัติการถ่ายทอดสด..");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setIndeterminate(false);
        dialog.setMax(100);

        LiveHistoryListFragment mFragment = this;

        aq.progress(dialog).ajax(url, JSONObject.class,mFragment, "getJson");

        Log.v("callsomething",url);
    }

    public void getJson(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        AQUtility.debug("jo", jo);
        if (jo != null) {
            String nameLive;
            JSONArray ja = jo.getJSONArray("history");
            if(ja != null)
            for (int i = 0; i < ja.length(); i++) {
                JSONObject obj = ja.optJSONObject(i);

                nameLive = obj.optString("nameUser");

                String urlLive = obj.optString("url");
                String photoLive = obj.optString("thumb");
                long timestamp = obj.optLong("date");

                JSONObject media = obj.optJSONObject("duration");

                String hours = media.optString("hours");
                String minutes = media.optString("minutes");
                String seconds = media.optString("seconds");

                Live liveList = new Live(urlLive,photoLive,nameLive,hours,minutes,seconds,Long.toString(timestamp),null,null,null);
                artistList.add(liveList);

            }


            adapter.notifyDataSetChanged();

            AQUtility.debug("done");

        } else {
            AQUtility.debug("error!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_live, container, false);

        adapter = new AdapterListLiveFragment(getActivity(), artistList);


        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);
        if(Utils.isTablet(getActivity()))
            manager = new GridLayoutManager(getActivity(), 2);
        else
            manager = new GridLayoutManager(getActivity(), 1);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        adapter.SetOnItemClickListener(new AdapterListLiveFragment.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Live liveItem = artistList.get(position);

                Intent i = new Intent(getActivity(),VitamioActivity.class);
                i.putExtra("id",liveItem.getUrlLive());
                i.putExtra("name",liveItem.getNameLive());
                i.putExtra("avatar",liveItem.getAvatar());
                i.putExtra("cover",liveItem.getPhotoLive());
                i.putExtra("title",liveItem.getNameLive());
                i.putExtra("desc","@"+liveItem.getNameLive());
                i.putExtra("userId",liveItem.getUserId());
                getActivity().startActivity(i);
                //getActivity().finish();

            }
        });

        return rootView;
    }




}
