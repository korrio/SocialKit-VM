package co.aquario.socialkit.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.api.services.youtube.model.SearchResult;

import java.util.List;

import co.aquario.socialkit.R;
import co.aquario.socialkit.adapter.YtAdapter;
import co.aquario.socialkit.connections.ServerResponseListener;
import co.aquario.socialkit.connections.ServiceTask;
import co.aquario.socialkit.util.AppUtils;


public class SearchYoutubeActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener, ServerResponseListener {

    private EditText mYtVideoEdt = null;
    private Button mYtVideoBtn = null;
    private ListView mYtVideoLsv = null;

    private YtAdapter mYtAdapter = null;
    private ServiceTask mYtServiceTask = null;
    private ProgressDialog mLoadingDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_youtube);
        initializeViews();
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.yt_video_btn:
                final String keyWord = mYtVideoEdt.getText().toString().trim();
                if (keyWord.length() > 0) {

                    // Service to search video
                    mYtServiceTask = new ServiceTask(SEARCH_VIDEO);
                    mYtServiceTask.setmServerResponseListener(this);
                    //mYtServiceTask.execute(new String[]{keyWord});
                    mYtServiceTask.execute(keyWord);
                } else {
                    AppUtils.showToast("Empty field");
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(SearchYoutubeActivity.this,PostYoutubeActivity.class);
        SearchResult result = mYtAdapter.getItem(i);
        intent.putExtra("yid",result.getId().getVideoId());
        intent.putExtra("title",result.getSnippet().getTitle());
        intent.putExtra("desc",result.getSnippet().getDescription());
        intent.putExtra("thumb",result.getSnippet().getThumbnails().getMedium().getUrl());
        startActivity(intent);
    }

    @Override
    public void prepareRequest(Object... objects) {
        // Parse the response based upon type of request
        Integer reqCode = (Integer) objects[0];

        if(reqCode==null || reqCode == 0)
            throw new NullPointerException("Request Code's value is Invalid.");
        String dialogMsg = null;
        switch (reqCode)
        {
            case SEARCH_VIDEO:
                dialogMsg = SEARCH_VIDEO_MSG;
                break;
        }

        if (mLoadingDialog != null && !mLoadingDialog.isShowing())
            mLoadingDialog = ProgressDialog.show(this, DIALOG_TITLE, dialogMsg, true, true);
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

        if(reqCode==null || reqCode == 0)
            throw new NullPointerException("Request Code's value is Invalid.");

        switch (reqCode) {
            case SEARCH_VIDEO:

                if (mYtAdapter == null) {
                    mYtAdapter = new YtAdapter(this);
                    mYtAdapter.setmVideoList((List<SearchResult>) objects[1]);
                    mYtVideoLsv.setAdapter(mYtAdapter);
                } else {
                    mYtAdapter.setmVideoList((List<SearchResult>) objects[1]);
                    mYtAdapter.notifyDataSetChanged();
                }

                break;
        }
    }
}
