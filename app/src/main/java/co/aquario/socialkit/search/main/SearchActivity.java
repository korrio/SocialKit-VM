package co.aquario.socialkit.search.main;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import co.aquario.socialkit.R;

/**
 * Created by Mac on 5/29/15.
 */
public class SearchActivity extends SwipeBaseActivity {
    @InjectView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @InjectView(R.id.no_shots)
    TextView mNoShots;
    String mQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);

        mQuery = getIntent().getStringExtra("query");
    }
}
