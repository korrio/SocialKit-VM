package co.aquario.socialkit.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.aquario.socialkit.R;
import co.aquario.socialkit.adapter.AdapterLivieHistory;
import co.aquario.socialkit.model.Live;

public class LiveHistory extends ActionBarActivity {

    String url = "http://api.vdomax.com/live/history";

    ArrayList<Live> list = new ArrayList<Live>();
    AdapterLivieHistory activityLiveHistory;

    public AQuery aq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.histoy_live);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        aq = new AQuery(getApplicationContext());

        activityLiveHistory = new AdapterLivieHistory(getApplication(), list);

        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(getApplication());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);


        recList.setAdapter(activityLiveHistory);

        activityLiveHistory.SetOnItemClickListener(new AdapterLivieHistory.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getApplication(),""+position,Toast.LENGTH_SHORT).show();
                String userId = list.get(position).getUserId();
                String url = list.get(position).getUrlLive();
                String avatarUrl = list.get(position).getAvatar();
                String username = list.get(position).getNameLive();
                String name = list.get(position).getNameLive();

               Intent i = new Intent(getApplicationContext(),VitamioActivity.class);
                i.putExtra("id",url);
                i.putExtra("name",name);
                i.putExtra("avatar",avatarUrl);
                i.putExtra("title",name);
                i.putExtra("desc",username);
                i.putExtra("userId",userId);
                startActivity(i);

                /*
                Bundle data = new Bundle();
                VideoViewFragment oneFragment = new VideoViewFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                data.putString("urlLive", url);
                data.putString("avatar", avatarUrl);
                data.putString("username", username);
                data.putString("userId", userId);
                oneFragment.setArguments(data);
                transaction.add(R.id.fragment_container, oneFragment);
                transaction.commit();
                */


            }
        });


        aq.ajax(url, JSONObject.class, this, "getJson");


    }

    public void getJson(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        AQUtility.debug("jo", jo);
        Log.d("Check_Feed:", "Test1");
        if (jo != null) {
            JSONArray ja = jo.getJSONArray("result");
            for (int i = 0; i < ja.length(); i++) {
                JSONObject obj = ja.getJSONObject(i);

                Log.d("Cehci", obj.toString());

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
            activityLiveHistory.notifyDataSetChanged();
            AQUtility.debug("done");

        } else {
            AQUtility.debug("error!");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the search; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_live_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
