package co.aquario.socialkit;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.astuetz.PagerSlidingTabStrip;
import com.squareup.otto.Subscribe;

import co.aquario.socialkit.activity.LiveFragment;
import co.aquario.socialkit.event.FollowRegisterEvent;
import co.aquario.socialkit.event.GetUserProfileSuccessEvent;
import co.aquario.socialkit.fragment.LiveHistoryFragment;
import co.aquario.socialkit.fragment.main.FeedFragment;
import co.aquario.socialkit.fragment.main.FriendFragment;
import co.aquario.socialkit.fragment.main.PhotoGridProfileFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.Channel;
import co.aquario.socialkit.util.PrefManager;

/**
 * Created by Mac on 6/3/15.
 */
public class NewProfileActivity extends BaseActivity {
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;

    Button btnFollow;
    String userId;

    PrefManager pref;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appcompat);

        pref = getPref(getApplicationContext());

        if(getIntent() != null)
            userId = getIntent().getStringExtra("user_id");
        else
            userId = MainApplication.get(getApplicationContext()).getPrefManager().userId().getOr("0");

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



        /*
        btnFollow = (Button) findViewById(R.id.btn_follow);
        initButton(true, btnFollow);

        btnFollow.setOnClickListener(toolbar View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFollowing) {
                    toggleUnfollow(btnFollow);

                } else {
                    toggleFollowing(btnFollow);
                }
                ApiBus.getInstance().post(toolbar FollowRegisterEvent(userId));
                isFollowing = !isFollowing;
            }
        });
        */

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

    boolean isFollowing = true;

    public void toggleFollowing(Button v) {
        v.setTextColor(Color.parseColor("#ffffff"));
        //v.setText(Html.fromHtml("&#x2713; FOLLOWING"));
        v.setText(Html.fromHtml("FOLLOWING"));

        // change state
        v.setSelected(true);
        v.setPressed(false);

    }

    public void toggleUnfollow(Button v) {
        v.setTextColor(Color.parseColor("#2C6497"));
        v.setText("+ FOLLOW");

        // change state
        v.setSelected(false);
        v.setPressed(false);

    }

    public void initButton(boolean following, View v) {
        Button button = (Button) v;

        isFollowing = following;

        if (following) {
            toggleFollowing(button);
        } else {
            toggleUnfollow(button);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (!userId.equals(pref.userId().getOr("0"))) {
            getMenuInflater().inflate(R.menu.menu_profile, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_my_profile, menu);
        }



        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_follow:
                isFollowing = !isFollowing;
                ApiBus.getInstance().post(new FollowRegisterEvent(userId));
                if(isFollowing)
                    item.setTitle("FOLLOWING");
                else
                    item.setTitle("+ FOLLOW");
                break;
            case android.R.id.home:
                this.finish();
                break;

            default:
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter
    {
        private final String[] TITLES = { "Feed","Photo","Live History", "Followers","Followings","Friends"};


        public MyPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }


        @Override
        public Fragment getItem(int position)
        {
            Fragment[] listFragments = new Fragment[] {
                    new FeedFragment().newInstance(userId, false),
                    new PhotoGridProfileFragment().newInstance(userId),
                    new LiveHistoryFragment().newInstance(userId),
                    new FriendFragment().newInstance("FOLLOWER",userId),
                    new FriendFragment().newInstance("FOLLOWING",userId),
                    new FriendFragment().newInstance("FRIEND",userId),
                    //new FriendFragment().newInstance("PAGE",userId)
                    };

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

    private class LivePagerAdapter extends FragmentPagerAdapter
    {
        private final String[] TITLES = { "Live","Feed","Photo","Live History", "Followers","Followings","Friends"};
    private Channel channel;

        public LivePagerAdapter(FragmentManager fm,Channel channel)
        {
            super(fm);
            this.channel = channel;
        }


        @Override
        public Fragment getItem(int position)
        {


            Fragment[] listFragments = new Fragment[] {
                    new LiveFragment().newInstance(channel),
                    new FeedFragment().newInstance(userId, false),
                    new PhotoGridProfileFragment().newInstance(userId),
                    new LiveHistoryFragment().newInstance(userId),
                    new FriendFragment().newInstance("FOLLOWER",userId),
                    new FriendFragment().newInstance("FOLLOWING",userId),
                    new FriendFragment().newInstance("FRIEND",userId),
                    //new FriendFragment().newInstance("PAGE",userId)
            };

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

    @Subscribe public void onLoadProfile(GetUserProfileSuccessEvent event) {
        toolbar.setTitle(event.getUser().name);
        toolbar.setSubtitle("@" + event.getUser().username);
        Channel channel = new Channel(userId,event.getUser().name,event.getUser().username,event.getUser().getCoverUrl(),event.getUser().getAvatarUrl(),event.getUser().live,event.getUser().gender,event.getUser().online);

        if(event.getUser().isLive) {
            LivePagerAdapter adapter = new LivePagerAdapter(getSupportFragmentManager(),channel);
            pager.setAdapter(adapter);

        }

        tabs.setViewPager(pager);


    }


}
