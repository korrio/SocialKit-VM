package co.aquario.chatapp.picker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.api.services.youtube.model.SearchResult;

import java.util.List;

import co.aquario.socialkit.R;
import co.aquario.socialkit.activity.post.PostYoutubeActivity;
import co.aquario.socialkit.search.youtube.ServerResponseListener;
import co.aquario.socialkit.search.youtube.ServiceTask;
import co.aquario.socialkit.search.youtube.YtSearchResultAdapter;


public class YoutubePickerActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, ServerResponseListener {

    boolean executing = false;
    private EditText mYtVideoEdt = null;
    private Button mYtVideoBtn = null;
    private ListView mYtVideoLsv = null;
    private YtSearchResultAdapter mYtSearchResultAdapter = null;
    private ServiceTask mYtServiceTask = null;
    private ProgressDialog mLoadingDialog = null;
    private Context context;
    private SearchView mSearchView;

    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_youtube);

        if(getIntent() !=null) {
            from = getIntent().getAction();
            Log.e("from", from);
        }

        // youtube api v3
        // https://www.googleapis.com/youtube/v3/videos?part=snippet&chart=mostPopular&regionCode=th&key={YOUR_API_KEY}

        initializeViews();
        initSearchAsync();
        context = this;
    }

    private void initSearchBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_search);

        mSearchView = (SearchView) toolbar.getMenu().findItem(R.id.action_search).getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 0) {
                    // Service to search video
                    mYtServiceTask.execute(query);
                    return true;
                } else {
                    return false;
                }


            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }

        });




        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Method that initializes views from the activity's content layout
     */
    private void initializeViews() {
        mYtVideoEdt = (EditText) findViewById(R.id.yt_video_edt);
        mYtVideoBtn = (Button) findViewById(R.id.yt_video_btn);
        mYtVideoLsv = (ListView) findViewById(R.id.yt_video_lsv);

        mYtVideoBtn.setOnClickListener(this);
        mYtVideoLsv.setOnItemClickListener(this);
    }

    private void initSearchAsync() {
        // Service to search video
        mYtServiceTask = new ServiceTask(1);
        mYtServiceTask.setmServerResponseListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.yt_video_btn:
                final String keyWord = mYtVideoEdt.getText().toString().trim();
                if (keyWord.length() > 0) {



                    //mYtServiceTask.execute(toolbar String[]{keyWord});
                    if(!executing) {
                        executing = true;
                        mYtServiceTask.execute(keyWord);
                    }



                } else {
                    //Utils.showToast("Empty field");
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        SearchResult result = mYtSearchResultAdapter.getItem(i);
        Intent intent;
        if (from.equals("post")) {
            intent = new Intent(YoutubePickerActivity.this, PostYoutubeActivity.class);

            intent.putExtra("yid", result.getId().getVideoId());
            intent.putExtra("title", result.getSnippet().getTitle());
            intent.putExtra("desc", result.getSnippet().getDescription());
            intent.putExtra("thumb", result.getSnippet().getThumbnails().getMedium().getUrl());
            startActivity(intent);
            finish();
        } else {
            intent = new Intent();
            intent.putExtra("yid", result.getId().getVideoId());
            intent.putExtra("title", result.getSnippet().getTitle());
            intent.putExtra("desc", result.getSnippet().getDescription());
            intent.putExtra("thumb", result.getSnippet().getThumbnails().getMedium().getUrl());
            //i.putExtra("LOCATION",marker.)
            setResult(-1, intent);
            finish();
        }


    }

    @Override
    public void prepareRequest(Object... objects) {

        executing = false;
        // Parse the response based upon type of request
        Integer reqCode = (Integer) objects[0];

        if (reqCode == null || reqCode == 0)
            throw new NullPointerException("Request Code's value is Invalid.");
        String dialogMsg = null;
        switch (reqCode) {
            case 1:
                dialogMsg = "Search Video";
                break;
        }

        if (mLoadingDialog != null && !mLoadingDialog.isShowing())
            mLoadingDialog = ProgressDialog.show(this, "Loading", dialogMsg, true, true);
    }

    @Override
    public void goBackground(Object... objects) {

    }

    @Override
    public void completedRequest(Object... objects) {

        // Dismiss the dialog
        if (mLoadingDialog != null && mLoadingDialog.isShowing())
            mLoadingDialog.dismiss();

        // Parse the response based upon type of request
        Integer reqCode = (Integer) objects[0];

        if (reqCode == null || reqCode == 0)
            throw new NullPointerException("Request Code's value is Invalid.");

        switch (reqCode) {
            case 1:

                if (mYtSearchResultAdapter == null) {
                    mYtSearchResultAdapter = new YtSearchResultAdapter(this);
                    mYtSearchResultAdapter.setmVideoList((List<SearchResult>) objects[1]);
                    mYtVideoLsv.setAdapter(mYtSearchResultAdapter);
                } else {
                    mYtSearchResultAdapter.setmVideoList((List<SearchResult>) objects[1]);
                    mYtSearchResultAdapter.notifyDataSetChanged();
                }

                break;
        }
    }
}
