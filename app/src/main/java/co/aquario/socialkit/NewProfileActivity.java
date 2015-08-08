package co.aquario.socialkit;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.squareup.otto.Subscribe;

import co.aquario.chatapp.ChatActivity;
import co.aquario.socialkit.event.GetFriendProfileEvent;
import co.aquario.socialkit.event.GetFriendProfileSuccessEvent;
import co.aquario.socialkit.fragment.LiveHistoryFragment;
import co.aquario.socialkit.fragment.SettingFragment;
import co.aquario.socialkit.fragment.main.FeedFragment;
import co.aquario.socialkit.fragment.main.LiveFragment;
import co.aquario.socialkit.fragment.main.PhotoGridProfileFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.Channel;
import co.aquario.socialkit.util.PrefManager;

public class NewProfileActivity extends BaseActivity {
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;

    String userId;
    String username = "";

    PrefManager pref;
    Toolbar toolbar;
    Activity mActivity;

    public static void startProfileActivity(Activity mActivity, String username,boolean isUsername) {
        Intent i = new Intent(mActivity,NewProfileActivity.class);
        i.putExtra("USERNAME", username);
        i.putExtra("IS_USERNAME", isUsername);
        i.putExtra("IS_LIVE", false);
        mActivity.startActivity(i);
    }

    public static void startProfileActivity(Activity mActivity, String userId) {
        Intent i = new Intent(mActivity,NewProfileActivity.class);
        i.putExtra("USER_ID", userId);
        i.putExtra("IS_USERNAME", false);
        i.putExtra("IS_LIVE", false);
        mActivity.startActivity(i);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_appcompat);

//        CustomActivityOnCrash.setShowErrorDetails(true);
//        CustomActivityOnCrash.setRestartActivityClass(MainActivity.class);
//        CustomActivityOnCrash.install(this);

        mActivity = this;

        pref = getPref(getApplicationContext());

        if(getIntent() != null) {
            if(getIntent().getExtras().getBoolean("IS_USERNAME")) {
                userId = getIntent().getExtras().getString("USERNAME");
            } else {
                userId = getIntent().getExtras().getString("USER_ID");
            }
        } else {
            userId = pref.userId().getOr("0");
        }

        ApiBus.getInstance().post(new GetFriendProfileEvent(userId));


        setupToolbar();

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setAllCaps(false);
        tabs.setShouldExpand(true);
        tabs.setFillViewport(true);
        tabs.setTextColor(getResources().getColor(android.R.color.white));

        //mSlidingTabLayout.setDistributeEvenly(true);

        //mSlidingTabLayout.setBackgroundResource(R.color.black_semi_transparent);

        //mSlidingTabLayout.setIndicatorColorResource(getResources().getColor(android.R.color.transparent));
        tabs.setDividerColor(getResources().getColor(android.R.color.transparent));
        pager = (ViewPager) findViewById(R.id.pager);




        //final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
       // pager.setPageMargin(pageMargin);
        //pager.setOffscreenPageLimit(5);
    }



    void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(toolbar != null) {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            toolbar.getMenu().clear();
            if (!userId.equals(pref.userId().getOr("0"))) {
                toolbar.inflateMenu(R.menu.menu_profile);
            } else {
                toolbar.inflateMenu(R.menu.menu_my_profile);
            }

            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_message:
                            ChatActivity.startChatActivity(mActivity, Integer.parseInt(pref.userId().getOr("0")), Integer.parseInt(userId), 0);
                            break;
                        case R.id.action_edit_profile:
                            //EditProfileFragment editProfileFragment = EditProfileFragment.newInstance();
                            SettingFragment settingFragment = SettingFragment.newInstance(pref.userId().getOr("0"));
                            FragmentManager manager = getSupportFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.add(R.id.sub_container, settingFragment).addToBackStack(null);
                            transaction.commit();
                            break;
                        case android.R.id.home:
                            finish();
                            break;

                        default:
                            break;

                    }
                    return false;
                }
            });

            //toolbar.getMenu().clear();

        }


    }


    private class MyPagerAdapter extends FragmentPagerAdapter
    {
        private final String[] TITLES = { "FEED","PHOTO","HISTORY"
               // , "Followers","Followings","Friends","Pages"
        };


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
                    //new FriendFragment().newInstance("FOLLOWER",userId),
                    //new FriendFragment().newInstance("FOLLOWING",userId),
                    //new FriendFragment().newInstance("FRIEND",userId),
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
        private final String[] TITLES = { "LIVE","FEED","PHOTO","HISTORY"
                //, "Followers","Followings","Friends"
        };
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
                  //  new FriendFragment().newInstance("FOLLOWER",userId),
                   // new FriendFragment().newInstance("FOLLOWING",userId),
                   // new FriendFragment().newInstance("FRIEND",userId),
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

    @Subscribe public void onLoadProfile(GetFriendProfileSuccessEvent event) {
        if(toolbar != null) {
            toolbar.setTitle(event.getUser().name);
            toolbar.setSubtitle("@" + event.getUser().username);

        }

        Channel channel = new Channel(userId,event.getUser().name,event.getUser().username,event.getUser().getCoverUrl(),event.getUser().getAvatarUrl(),event.getUser().live,event.getUser().gender,event.getUser().online);

        if(event.getUser().isLive) {
            LivePagerAdapter adapter = new LivePagerAdapter(getSupportFragmentManager(),channel);
            pager.setAdapter(adapter);

        } else {
            MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
            pager.setAdapter(adapter);
        }

        tabs.setViewPager(pager);


    }


}
