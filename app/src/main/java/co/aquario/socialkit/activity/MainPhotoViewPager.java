
package co.aquario.socialkit.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.astuetz.PagerSlidingTabStrip;

import co.aquario.socialkit.R;
import co.aquario.socialkit.adapter.FragmentPagerAdapterPhotoGrid;

public class MainPhotoViewPager extends ActionBarActivity {

    private final String ARG_SELECTED_LAYOUT_ID = "selectedLayoutId";


    private int mSelectedLayoutId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_photo_viewpager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new FragmentPagerAdapterPhotoGrid(getSupportFragmentManager()));

        PagerSlidingTabStrip slidingTabLayout = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        slidingTabLayout.setAllCaps(false);
        slidingTabLayout.setShouldExpand(true);
        slidingTabLayout.setIndicatorColorResource(R.color.indigo_700);
        slidingTabLayout.setDividerColor(getResources().getColor(android.R.color.transparent));
        slidingTabLayout.setViewPager(pager);

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_SELECTED_LAYOUT_ID, mSelectedLayoutId);
    }
}
