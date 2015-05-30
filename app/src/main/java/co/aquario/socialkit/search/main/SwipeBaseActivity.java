package co.aquario.socialkit.search.main;

import android.os.Bundle;
import android.view.MenuItem;

/**
 * Created by Mac on 5/29/15.
 */
public class SwipeBaseActivity extends me.imid.swipebacklayout.lib.app.SwipeBackActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpActionBar();
    }

    private void setUpActionBar() {
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#181818")));
//        getSupportActionBar().setLogo(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
