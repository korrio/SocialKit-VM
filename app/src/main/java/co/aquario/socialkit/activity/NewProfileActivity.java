package co.aquario.socialkit.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import co.aquario.socialkit.R;
import co.aquario.socialkit.fragment.main.FeedFragment;
import co.aquario.socialkit.fragment.main.FriendFragment;

/**
 * Created by Mac on 6/3/15.
 */
public class NewProfileActivity extends AppCompatActivity {
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appcompat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_camera);
        TextView titleView = (TextView)findViewById(R.id.toolbar_title);
        titleView.setText(getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

//    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setAllCaps(true);
        tabs.setShouldExpand(true);
        tabs.setFillViewport(true);
        tabs.setTextColor(getResources().getColor(R.color.white));

        //mSlidingTabLayout.setDistributeEvenly(true);

        //mSlidingTabLayout.setBackgroundResource(R.color.black_semi_transparent);

        //mSlidingTabLayout.setIndicatorColorResource(getResources().getColor(android.R.color.transparent));
        tabs.setDividerColor(getResources().getColor(android.R.color.transparent));
        pager = (ViewPager) findViewById(R.id.pager);

        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);


        tabs.setViewPager(pager);

        //final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
       // pager.setPageMargin(pageMargin);
        //pager.setOffscreenPageLimit(5);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_search)
            pager.setCurrentItem(1);
        else if (item.getItemId() == android.R.id.home)
            Log.i("onOptionsItemSelected", "|" + "home ==========" + "|");
        return super.onOptionsItemSelected(item);
    }

    /**************************************************
     * class
     ***************************************************/
    private class MyPagerAdapter extends FragmentPagerAdapter
    {
        private final String[] TITLES = { "Profile", "Followers","Followings","Friends","Pages"};


        public MyPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }


        @Override
        public Fragment getItem(int position)
        {
            String userId = "1926";

            Fragment[] listFragments = new Fragment[] {
                    new FeedFragment().newInstance(userId, false),
                    new FriendFragment().newInstance("FOLLOWER",userId),
                    new FriendFragment().newInstance("FOLLOWING",userId),
                    new FriendFragment().newInstance("FRIEND",userId),
                    new FriendFragment().newInstance("PAGE",userId)};

            return listFragments[position];

        }


        @Override
        public int getCount()
        {
            return TITLES.length;
        }


        @Override
        public CharSequence getPageTitle(int position)
        {
            return TITLES[position];
        }
    }


}
