package co.aquario.socialkit.adapter.view;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

import co.aquario.socialkit.fragment.FeedFragment;
import co.aquario.socialkit.fragment.FriendFragment;

public class TimelinePagerAdapter extends FragmentPagerAdapter {

    public TimelinePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment;



        switch (position) {
            case 0:
                return new FeedFragment().newInstance("");
            case 1:
                return FriendFragment.newInstance("FOLLOWING", "6");
            case 2:
                return FriendFragment.newInstance("FOLLOWER", "6");
            case 3:
                return FriendFragment.newInstance("FRIEND", "6");
            default:
                return FeedFragment.newInstance("");
        }

    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 6;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return "12 Posts".toUpperCase(l);
            case 1:
                return "17 Followers".toUpperCase(l);
            case 2:
                return "25 Following".toUpperCase(l);
            case 3:
                return "15 Friends".toUpperCase(l);
            case 4:
                return "55 Loves".toUpperCase(l);
            case 5:
                return "5 Groups".toUpperCase(l);
        }
        return null;
    }
}
