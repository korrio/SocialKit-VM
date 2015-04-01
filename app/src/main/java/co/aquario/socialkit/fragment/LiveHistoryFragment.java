package co.aquario.socialkit.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import co.aquario.socialkit.fragment.main.VideoFragment;
import co.aquario.socialkit.model.Live;
import co.aquario.socialkit.util.Utils;

public class LiveHistoryFragment extends BaseFragment {

    private static String userId;

    AdapterListLiveFragment adapter;
    ArrayList<Live> artistList = new ArrayList<>();

    public static LiveHistoryFragment newInstance(String id) {
        LiveHistoryFragment mFragment = new LiveHistoryFragment();
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

        LiveHistoryFragment mFragment = this;

        aq.progress(dialog).ajax(url, JSONObject.class,mFragment, "getJson");

        Log.v("callsomething",url);
    }

    public void getJson(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        AQUtility.debug("jo", jo);
        Log.v("myhistory",jo.toString());
        if (jo != null) {
            String nameLive;
            JSONArray ja = jo.getJSONArray("history");
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


    GridLayoutManager manager;

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
                String urlLive = artistList.get(position).getUrlLive();

//                Log.d("Onclick_C",urlLive);
//                Bundle data = new Bundle();
//                data.putString("urlLive",urlLive);
//                VideoViewFragment newFragment = new VideoViewFragment();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.replace(R.id.fl_container, newFragment);
//                transaction.addToBackStack(null);
//                newFragment.setArguments(data);
//                transaction.commit();

                Intent i = new Intent(getActivity(), VitamioActivity.class);
                i.putExtra("id",urlLive);
                startActivity(i);

//                VideoViewFragment fragment = VideoViewFragment.newInstance("video","name",urlLive);
//                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.sub_container,
//                 fragment, "VIDEO_MAIN").addToBackStack(null).commit();
            }
        });

        return rootView;
    }




}
